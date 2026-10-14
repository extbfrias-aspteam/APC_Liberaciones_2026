package controllers

import (
	"condusef-api/app/models"
	"condusef-api/app/repositories"
	"condusef-api/app/services"
	"errors"
	"fmt"

	"net/http"

	"github.com/golang-jwt/jwt/v5"
	"github.com/labstack/echo/v4"
)

func ObtenerAclaracionPorFolio(c echo.Context) error {
	folio := c.QueryParam("folio")

	aclaracion, err := repositories.ObtenerAclaracionPorFolio(folio)
	if err != nil {
		return errors.New(err.Error())
	}

	if aclaracion == (models.AclaracionResponse{}) {
		return c.JSON(http.StatusNotFound, "Records not found")
	}

	return c.JSON(http.StatusOK, aclaracion)
}

func CrearAclaracion(c echo.Context) error {
	aclaracion := &models.Aclaracion{}

	if err := c.Bind(aclaracion); err != nil {
		return c.String(http.StatusInternalServerError, err.Error())
	}

	claims := c.Get("user").(*jwt.Token).Claims.(*services.CustomClaims)
	aclaracion.IdEmpresa = claims.Empresa.Id

	err := repositories.CrearAclaracion(aclaracion)
	if err != nil {
		return c.String(http.StatusInternalServerError, err.Error())
	}

	return c.JSON(http.StatusCreated, "")
}

func ModificarAclaracion(c echo.Context) error {
	aclaracion := &models.Aclaracion{}

	if err := c.Bind(aclaracion); err != nil {
		return c.String(http.StatusInternalServerError, err.Error())
	}

	fmt.Println(aclaracion)
	err := repositories.ModificarAclaracion(aclaracion)
	if err != nil {
		return c.JSON(http.StatusInternalServerError, "")
	}

	return c.JSON(http.StatusOK, "")
}

// EliminarAclaracion
func EliminarAclaracion(c echo.Context) error {
	folio := c.Param("folio")
	if folio == "" {
		return c.JSON(http.StatusInternalServerError, "")
	}

	err := repositories.EliminarAclaracion(folio)
	if err != nil {
		return c.JSON(http.StatusInternalServerError, "")
	}

	return c.JSON(http.StatusOK, "")
}
