package utils

import (
	"bytes"
	"crypto/aes"
	"crypto/cipher"
	"crypto/tls"
	"crypto/x509"
	"embed"
	"encoding/base64"
	"errors"
	"fmt"
	"io"
	"io/fs"
	"log"
	"math/rand"
	"net/http"
	"os"
	"sync"
	"time"

	"github.com/aidarkhanov/nanoid/v2"
	"github.com/fatih/color"
	"github.com/oklog/ulid/v2"
	uuid "github.com/satori/go.uuid"
	"github.com/speps/go-hashids/v2"
)

/*
	Numbers
*/

// RandNumber
func RandNumber() int {
	x1 := rand.NewSource(time.Now().UnixNano())
	n := rand.New(x1)

	return n.Int()
}

/*
	String Ids
*/

// GetReferenceID
func GetReferenceID() string {
	t := time.Now()
	entropy := ulid.Monotonic(rand.New(rand.NewSource(t.UnixNano())), 0)
	ulid := ulid.MustNew(ulid.Timestamp(t), entropy)

	hd := hashids.NewData()
	hd.Salt = ulid.String()
	hd.Alphabet = "abcdefghijklmnopqrstuvwxyz1234567890"
	hd.MinLength = 15
	h, _ := hashids.NewWithData(hd)
	e, _ := h.Encode([]int{
		RandNumber(), RandNumber(), RandNumber(), RandNumber()})
	vid := fmt.Sprintf("%s-%s-%s-2024", e[:3], (e[len(e)-7:])[:4], e[len(e)-3:])

	return vid
}

// NewNanoId
func NewNanoId() string {
	id, _ := nanoid.New()
	id2, _ := nanoid.GenerateString(nanoid.DefaultAlphabet, 22)
	return fmt.Sprintf("%s%s", id, id2)
}

// NewUlid
func NewUlid() string {
	t := time.Now().Unix()
	entropy := ulid.Monotonic(rand.New(rand.NewSource(t)), 0)
	newUlid := ulid.MustNew(ulid.Timestamp(time.Now()), entropy)
	return newUlid.String()
}

// NewUUID
func NewUUID() string {
	uid := uuid.NewV5(uuid.NewV4(), "com.nerdbrains.harmony")
	return uid.String()
}

/*
	Prints
*/

// SuccessMessage
func SuccessMessage(message string) {
	color.Set(color.Bold)
	log.Println(" ", color.HiGreenString("•"),
		message)
}

// NoticeMessage
func NoticeMessage(message string) {
	color.Set(color.Bold)
	log.Println(" ", color.HiBlueString("•"),
		message)
}

// ErrorMessage
func ErrorMessage(short, long string) {
	color.Set(color.Bold)
	log.Println(" ", color.HiRedString("•"),
		short,
		color.HiRedString("\terror ="),
		long)
}

// WarningMessage
func WarningMessage(short, long string) {
	color.Set(color.Bold)
	log.Println(" ", color.YellowString("•"),
		short,
		color.YellowString("\twarning ="),
		long)
}

/*
	Encryption
*/

// GetAESEncrypted encrypts given text in AES 256 CBC
func GetAESEncrypted(plaintext string) (string, error) {
	key, _ := base64.StdEncoding.DecodeString(os.Getenv("AES_KEY"))
	iv, _ := base64.StdEncoding.DecodeString(os.Getenv("AES_INIT_VECTOR"))

	var plainTextBlock []byte
	length := len(plaintext)

	if length%16 != 0 {
		extendBlock := 16 - (length % 16)
		plainTextBlock = make([]byte, length+extendBlock)
		copy(plainTextBlock[length:], bytes.Repeat([]byte{uint8(extendBlock)}, extendBlock))
	} else {
		plainTextBlock = make([]byte, length)
	}

	copy(plainTextBlock, plaintext)
	block, err := aes.NewCipher(key)
	if err != nil {
		return "", err
	}

	ciphertext := make([]byte, len(plainTextBlock))
	mode := cipher.NewCBCEncrypter(block, iv)
	mode.CryptBlocks(ciphertext, plainTextBlock)

	str := base64.StdEncoding.EncodeToString(ciphertext)

	return str, nil
}

/*
	HTTP Request
*/

//go:embed certs/*.pem
var embeddedCACerts embed.FS

var (
	httpClientOnce sync.Once
	httpClient     *http.Client
)

func condusefHTTPClient() *http.Client {
	httpClientOnce.Do(func() {
		transport := http.DefaultTransport.(*http.Transport).Clone()
		transport.TLSClientConfig = condusefTLSConfig()
		httpClient = &http.Client{
			Timeout:   30 * time.Second,
			Transport: transport,
		}
	})
	return httpClient
}

func condusefTLSConfig() *tls.Config {
	pool, err := x509.SystemCertPool()
	if err != nil || pool == nil {
		pool = x509.NewCertPool()
	}

	loaded := appendEmbeddedCACerts(pool)
	if caPath := os.Getenv("CONDUSEF_CA_CERT"); caPath != "" {
		pemBytes, readErr := os.ReadFile(caPath)
		if readErr != nil {
			WarningMessage("No se pudo leer CONDUSEF_CA_CERT", readErr.Error())
		} else if !pool.AppendCertsFromPEM(pemBytes) {
			WarningMessage("CONDUSEF_CA_CERT no contiene un certificado PEM válido", caPath)
		} else {
			NoticeMessage("CA adicional cargada para CONDUSEF: " + caPath)
			loaded++
		}
	}

	if loaded == 0 {
		WarningMessage("No se cargó ninguna CA adicional para CONDUSEF", "se usará solo el almacén del sistema")
		return nil
	}

	return &tls.Config{RootCAs: pool}
}

func appendEmbeddedCACerts(pool *x509.CertPool) int {
	loaded := 0
	err := fs.WalkDir(embeddedCACerts, "certs", func(path string, d fs.DirEntry, walkErr error) error {
		if walkErr != nil || d.IsDir() {
			return walkErr
		}
		pemBytes, readErr := embeddedCACerts.ReadFile(path)
		if readErr != nil {
			WarningMessage("No se pudo leer la CA embebida", readErr.Error())
			return nil
		}
		if !pool.AppendCertsFromPEM(pemBytes) {
			WarningMessage("La CA embebida no contiene un certificado PEM válido", path)
			return nil
		}
		NoticeMessage("CA embebida cargada para CONDUSEF: " + path)
		loaded++
		return nil
	})
	if err != nil {
		WarningMessage("No se pudieron cargar las CA embebidas", err.Error())
	}
	return loaded
}

// HTTPRequest
func HTTPRequest(method, url, token string, payload io.Reader) (*http.Response, error) {
	req, err := http.NewRequest(method, url, payload)
	if err != nil {
		return nil, err
	}
	req.Header.Add("Content-Type", "application/json")
	if token != "" {
		req.Header.Add("Authorization", token)
	}

	return condusefHTTPClient().Do(req)
}

/*
	Ids
*/

// obtenerUrlCondusef
func UrlTokenCondusef(schema string) (string, error) {
	if schema == "redeco" {
		return fmt.Sprintf("%s/auth/users/token/", os.Getenv("CONDUSEF_URL_BASE")), nil
	} else if schema == "reune" {
		return fmt.Sprintf("%s/auth/users/token/", os.Getenv("CONDUSEF_URL_REUNE")), nil
	} else {
		return "", errors.New("invalid schema in urlTokenCondusef")
	}
}

// KeyTokenCondusef
func KeyTokenCondusef(idEmpresa int, username, schema string) (string, error) {
	if schema == "" {
		return "", errors.New("invalid schema in keyTokenCondusef")
	}

	return fmt.Sprintf("Emp%d_%s_%s_token", idEmpresa, username, schema), nil
}

/*
	Archivos
*/

func FileExist(path string) bool {
	if _, err := os.Stat(path); os.IsNotExist(err) {
		return false
	}
	return true
}
