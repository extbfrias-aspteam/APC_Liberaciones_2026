package services

import (
	"condusef-api/app/models"

	"os"
	"time"

	"github.com/golang-jwt/jwt/v5"
)

type CustomClaims struct {
	Id          string         `json:"id"`
	Nombre      string         `json:"nombre"`
	NumEmpleado string         `json:"num_empleado"`
	Empresa     models.Empresa `json:"empresa"`
	jwt.RegisteredClaims
}

// Valid implements jwt.Claims
func (*CustomClaims) Valid() error {
	panic("unimplemented")
}

// JWTGenerateToken
func JWTGenerateToken(t *models.AuthUser) (string, error) {
	JWTSecret := os.Getenv("JWT_SECRET")
	secret := []byte(JWTSecret)
	claims := &CustomClaims{
		Id:          t.Id,
		Nombre:      t.Nombre,
		NumEmpleado: t.NumEmpleado,
		Empresa:     t.Empresa,
		RegisteredClaims: jwt.RegisteredClaims{
			ExpiresAt: jwt.NewNumericDate(time.Now().Add(time.Minute * 120)),
		},
	}

	obj := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	token, error := obj.SignedString(secret)
	if error != nil {
		return "", error
	}

	return token, nil
}
