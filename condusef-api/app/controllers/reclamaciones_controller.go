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

func ObtenerReclamacionPorFolio(c echo.Context) error {
	folio := c.QueryParam("folio")

	reclamacion, err := repositories.ObtenerReclamacionPorFolio(folio)
	if err != nil {
		return errors.New(err.Error())
	}

	if reclamacion == (models.ReclamacionResponse{}) {
		return c.JSON(http.StatusNotFound, "Records not found")
	}

	return c.JSON(http.StatusOK, reclamacion)
}

func CrearReclamacion(c echo.Context) error {
	reclamacion := &models.Reclamacion{}

	if err := c.Bind(reclamacion); err != nil {
		return c.String(http.StatusInternalServerError, err.Error())
	}

	claims := c.Get("user").(*jwt.Token).Claims.(*services.CustomClaims)
	reclamacion.IdEmpresa = claims.Empresa.Id

	err := repositories.CrearReclamacion(reclamacion)
	if err != nil {
		return c.String(http.StatusInternalServerError, err.Error())
	}

	return c.JSON(http.StatusCreated, "")
}

func ModificarReclamacion(c echo.Context) error {
	reclamacion := &models.Reclamacion{}

	if err := c.Bind(reclamacion); err != nil {
		return c.String(http.StatusInternalServerError, err.Error())
	}

	fmt.Println(reclamacion)
	err := repositories.ModificarReclamacion(reclamacion)
	if err != nil {
		return c.JSON(http.StatusInternalServerError, "")
	}

	return c.JSON(http.StatusOK, "")
}

// EliminarReclamacion
func EliminarReclamacion(c echo.Context) error {
	folio := c.Param("folio")
	if folio == "" {
		return c.JSON(http.StatusInternalServerError, "")
	}

	err := repositories.EliminarReclamacion(folio)
	if err != nil {
		return c.JSON(http.StatusInternalServerError, "")
	}

	return c.JSON(http.StatusOK, "")
}
