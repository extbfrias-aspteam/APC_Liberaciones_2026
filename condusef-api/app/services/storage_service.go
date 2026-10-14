package services

import (
	"condusef-api/app/utils"
	"context"
	"fmt"
	"io"
	"os"
	"time"

	"cloud.google.com/go/storage"
)

var G_STORAGE *GoogleStorage

// InitGoogleStorage
func InitGoogleStorage() error {
	existCredentialsFile := utils.FileExist("application_default_credentials.json")
	if existCredentialsFile {
		os.Setenv("GOOGLE_APPLICATION_CREDENTIALS", "application_default_credentials.json")
	}

	client, err := storage.NewClient(context.Background())
	if err != nil {
		return err
	}

	G_STORAGE = &GoogleStorage{
		Client:     client,
		BucketName: os.Getenv("GSTORAGE_BUCKET_NAME"),
		ProjectID:  os.Getenv("GSTORAGE_PROJECT_ID"),
	}

	return nil
}

// GoogleStorage
type GoogleStorage struct {
	Client     *storage.Client
	ProjectID  string
	BucketName string
	UploadPath string
}

// Upload sube un archivo a un bucket a partir de un reader
func (c *GoogleStorage) Upload(reader io.Reader, object string) error {
	if c == nil {
		return fmt.Errorf("google storage not initialized error")
	}

	ctx := context.Background()

	ctx, cancel := context.WithTimeout(ctx, time.Second*50)
	defer cancel()

	wc := c.Client.Bucket(c.BucketName).Object(c.UploadPath + object).NewWriter(ctx)
	if _, err := io.Copy(wc, reader); err != nil {
		return fmt.Errorf("upload error, io.Copy: %v", err)
	}
	if err := wc.Close(); err != nil {
		return fmt.Errorf("upload error, writer.Close: %v", err)
	}

	return nil
}

// UploadFromFile sube un archivo a un bucket a partir de un file
func (c *GoogleStorage) UploadFromFile(file *os.File, object string) error {
	if c == nil {
		return fmt.Errorf("google storage not initialized error")
	}

	return c.Upload(file, object)
}

// Download descarga un objeto de un bucket
func (c *GoogleStorage) Download(object, destFileName string) error {
	if c == nil {
		return fmt.Errorf("google storage not initialized error")
	}

	ctx := context.Background()

	client, err := storage.NewClient(ctx)
	if err != nil {
		return fmt.Errorf("download error, storage.NewClient: %w", err)
	}
	defer client.Close()

	ctx, cancel := context.WithTimeout(ctx, time.Second*50)
	defer cancel()

	f, err := os.Create(destFileName)
	if err != nil {
		return fmt.Errorf("download error, os.Create: %w", err)
	}

	rc, err := client.Bucket(c.BucketName).Object(object).NewReader(ctx)
	if err != nil {
		return fmt.Errorf("download error, Object(%q).NewReader: %w", object, err)
	}
	defer rc.Close()

	if _, err := io.Copy(f, rc); err != nil {
		return fmt.Errorf("download error, io.Copy: %w", err)
	}

	if err = f.Close(); err != nil {
		return fmt.Errorf("download error, f.Close: %w", err)
	}

	return nil
}
