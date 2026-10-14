package controllers

import (
	"condusef-api/app/models"
	"condusef-api/app/repositories"
	"condusef-api/app/services"

	"errors"
	"fmt"
	"net/http"
	"strconv"

	"github.com/golang-jwt/jwt/v5"
	"github.com/labstack/echo/v4"
)

// ObtenerProductos
func ObtenerProductos(c echo.Context) error {
	schema := c.QueryParam("schema")
	if schema == "" {
		return c.JSON(http.StatusBadRequest, "query parameter not found")
	}

	claims := c.Get("user").(*jwt.Token).Claims.(*services.CustomClaims)

	productos, err := repositories.ObtenerProductos(schema, claims.Empresa.Id)
	if err != nil {
		return errors.New(err.Error())
	}

	return c.JSON(http.StatusOK, productos)
}

// ObtenerCausas
func ObtenerCausas(c echo.Context) error {
	schema := c.QueryParam("schema")
	idProducto := c.QueryParam("idProducto")
	tipo := c.QueryParam("tipo")

	if schema == "" || idProducto == "" {
		return c.JSON(http.StatusBadRequest, "query parameter not found")
	}

	causas, err := repositories.ObtenerCausas(schema, idProducto, tipo)
	if err != nil {
		return errors.New(err.Error())
	}

	return c.JSON(http.StatusOK, causas)
}

// ObtenerEntidadesFed
func ObtenerEntidadesFed(c echo.Context) error {
	entidadesFed, err := repositories.ObtenerEntidadesFed()
	if err != nil {
		return errors.New(err.Error())
	}

	return c.JSON(http.StatusOK, entidadesFed)
}

// ObtenerMunicipios
func ObtenerMunicipios(c echo.Context) error {
	idEntidadFed, err := strconv.Atoi(c.QueryParam("idEntidadFed"))
	if err != nil {
		return c.String(http.StatusBadRequest, err.Error())
	}

	cp, err := strconv.Atoi(c.QueryParam("cp"))
	if err != nil {
		cp = 0
	}

	municipios, err := repositories.ObtenerMunicipios(idEntidadFed, cp)
	if err != nil {
		return errors.New(err.Error())
	}

	return c.JSON(http.StatusOK, municipios)
}

// ObtenerColonias
func ObtenerColonias(c echo.Context) error {
	cp, err := strconv.Atoi(c.QueryParam("cp"))
	if err != nil {
		return c.String(http.StatusBadRequest, err.Error())
	}

	colonias, err := repositories.ObtenerColonias(cp)
	if err != nil {
		return errors.New(err.Error())
	}

	return c.JSON(http.StatusOK, colonias)
}

// ObtenerCodigosPostales
func ObtenerCodigosPostales(c echo.Context) error {
	idEntidadFed, err := strconv.Atoi(c.QueryParam("idEntidadFed"))
	if err != nil {
		return c.String(http.StatusBadRequest, err.Error())
	}

	codigos, err := repositories.ObtenerCodigosPostales(idEntidadFed)
	if err != nil {
		return errors.New(err.Error())
	}

	return c.JSON(http.StatusOK, codigos)
}

// ObtenerMediosRecepcion
func ObtenerMediosRecepcion(c echo.Context) error {
	schema := c.QueryParam("schema")
	if schema == "" {
		return c.JSON(http.StatusBadRequest, "query parameter not found")
	}

	mediosRecepcion, err := repositories.ObtenerMediosRecepcion(schema)
	if err != nil {
		return errors.New(err.Error())
	}

	return c.JSON(http.StatusOK, mediosRecepcion)
}

// ObtenerNivelesAtencion
func ObtenerNivelesAtencion(c echo.Context) error {
	schema := c.QueryParam("schema")
	if schema == "" {
		return c.JSON(http.StatusBadRequest, "query parameter not found")
	}

	nivelesAtencion, err := repositories.ObtenerNivelesAtencion(schema)
	if err != nil {
		return errors.New(err.Error())
	}

	return c.JSON(http.StatusOK, nivelesAtencion)
}

// ObtenerNumFolio
func ObtenerNumFolio(c echo.Context) error {
	id := c.QueryParam("id")
	if id == "" {
		return c.JSON(http.StatusBadRequest, "query parameter not found")
	}

	claims := c.Get("user").(*jwt.Token).Claims.(*services.CustomClaims)

	consecutivo, err := services.GetConsecutivo(id, claims.Empresa.Id)
	if err != nil {
		fmt.Println(err.Error())
		return c.JSON(http.StatusInternalServerError, err.Error())
	}

	folio := &models.Folio{
		ReferenceID: consecutivo,
	}

	return c.JSON(http.StatusOK, folio)
}
