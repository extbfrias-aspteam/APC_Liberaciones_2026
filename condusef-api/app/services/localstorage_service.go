package services

import (
	"github.com/sopherapps/go-scdb/scdb"
)

var maxKeys uint64 = 1_000_000
var redundantBlocks uint16 = 1
var poolCapacity uint64 = 10
var compactionInterval uint32 = 1_800

var L_STORAGE *LocalStorage

func InitLocalstorage() error {
	store, err := scdb.New(
		"db",
		&maxKeys,
		&redundantBlocks,
		&poolCapacity,
		&compactionInterval,
		false)
	if err != nil {
		return err
	}

	L_STORAGE = &LocalStorage{
		Store: store,
	}

	return nil
}

// LocalStorage
type LocalStorage struct {
	Store *scdb.Store
}

// GuardarToken
func (c *LocalStorage) GuardarToken(key, token string) error {
	err := c.Store.Set([]byte(key), []byte(token), nil)
	if err != nil {
		return err
	}

	return nil
}

// ObtenerToken
func (c *LocalStorage) ObtenerToken(key string) (string, error) {
	token, err := c.Store.Get([]byte(key))
	if err != nil {
		return "", err
	}

	return string(token), nil
}

func (c *LocalStorage) Dispose() {
	_ = c.Store.Close()
}
