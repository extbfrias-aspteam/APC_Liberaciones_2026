package controllers

import (
	"condusef-api/app/repositories"
	"errors"
	"net/http"

	"github.com/labstack/echo/v4"
)

// ObtenerEmpresas
func ObtenerEmpresas(c echo.Context) error {
	empresas, err := repositories.ObtenerEmpresas()
	if err != nil {
		return errors.New(err.Error())
	}

	return c.JSON(http.StatusOK, empresas)
}
