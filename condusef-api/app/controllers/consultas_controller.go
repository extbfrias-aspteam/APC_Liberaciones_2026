package controllers

import (
	"condusef-api/app/models"
	"condusef-api/app/repositories"
	"condusef-api/app/services"
	"encoding/json"
	"errors"
	"strings"

	"net/http"

	"github.com/golang-jwt/jwt/v5"
	"github.com/labstack/echo/v4"
)

func ObtenerConsultaPorFolio(c echo.Context) error {
	folio := c.QueryParam("folio")

	consulta, err := repositories.ObtenerConsultaPorFolio(folio)
	if err != nil {
		return errors.New(err.Error())
	}

	if consulta == (models.ConsultaResponse{}) {
		return c.JSON(http.StatusNotFound, "Records not found")
	}

	// Serializar manualmente el JSON
	jsonData, err := json.Marshal(consulta)
	if err != nil {
		return err
	}

	// Reemplazar el \u0026 con &
	jsonString := strings.ReplaceAll(string(jsonData), `\u0026`, "&")

	// Enviar la respuesta usando c.Blob
	return c.Blob(http.StatusOK, echo.MIMEApplicationJSONCharsetUTF8, []byte(jsonString))
}

func CrearConsulta(c echo.Context) error {
	consulta := &models.Consulta{}

	if err := c.Bind(consulta); err != nil {
		return c.String(http.StatusInternalServerError, err.Error())
	}
	claims := c.Get("user").(*jwt.Token).Claims.(*services.CustomClaims)
	consulta.IdEmpresa = claims.Empresa.Id

	err := repositories.CrearConsulta(consulta)
	if err != nil {
		return c.String(http.StatusInternalServerError, err.Error())
	}

	return c.JSON(http.StatusCreated, "")
}

func ModificarConsulta(c echo.Context) error {
	consulta := &models.Consulta{}

	if err := c.Bind(consulta); err != nil {
		return c.String(http.StatusInternalServerError, err.Error())
	}

	err := repositories.ModificarConsulta(consulta)
	if err != nil {
		return c.JSON(http.StatusInternalServerError, "")
	}

	return c.JSON(http.StatusOK, "")
}

// EliminarConsulta
func EliminarConsulta(c echo.Context) error {
	folio := c.Param("folio")
	if folio == "" {
		return c.JSON(http.StatusInternalServerError, "")
	}

	err := repositories.EliminarConsulta(folio)
	if err != nil {
		return c.JSON(http.StatusInternalServerError, "")
	}

	return c.JSON(http.StatusOK, "")
}
