package controllers

import (
	"condusef-api/app/models"
	"condusef-api/app/repositories"
	"condusef-api/app/services"

	"errors"
	"net/http"
	"strconv"

	"github.com/golang-jwt/jwt/v5"
	"github.com/labstack/echo/v4"
)

// ObtenerPeriodos
func ObtenerPeriodos(c echo.Context) error {
	schema := c.QueryParam("schema")
	if schema == "" {
		return c.JSON(http.StatusBadRequest, "query parameter not found")
	}

	idEmpresa := c.Get("user").(*jwt.Token).Claims.(*services.CustomClaims).Empresa.Id

	periodos, err := repositories.ObtenerPeriodos(schema, idEmpresa)
	if err != nil {
		return errors.New(err.Error())
	}

	return c.JSON(http.StatusOK, periodos)
}

// ObtenerPeriodoActivo
func ObtenerPeriodoActivo(c echo.Context) error {
	schema := c.QueryParam("schema")
	if schema == "" {
		return c.JSON(http.StatusBadRequest, "query parameter not found")
	}

	idEmpresa := c.Get("user").(*jwt.Token).Claims.(*services.CustomClaims).Empresa.Id

	periodo, err := repositories.ObtenerPeriodoActivo(schema, idEmpresa)
	if err != nil {
		return c.JSON(http.StatusInternalServerError, err.Error())
	}

	if periodo == (models.Periodo{}) {
		return c.String(http.StatusNotFound, "Ningún periodo ha sido encontrado")
	}

	return c.JSON(http.StatusOK, periodo)
}

// CrearPeriodo
func CrearPeriodo(c echo.Context) error {
	periodo := &models.Periodo{}
	if err := c.Bind(periodo); err != nil {
		return c.JSON(http.StatusBadRequest, err.Error())
	}

	schema := c.QueryParam("schema")
	if schema == "" {
		return c.JSON(http.StatusBadRequest, "query parameter not found")
	}

	idEmpresa := c.Get("user").(*jwt.Token).Claims.(*services.CustomClaims).Empresa.Id

	// Si el periodo viene marcado como activo se valida si existe
	// un periodo activo
	if periodo.Activo {
		activo, _ := repositories.ObtenerPeriodoActivo(schema, idEmpresa)
		if activo != (models.Periodo{}) {
			return c.JSON(http.StatusInternalServerError, "Ya existe un periodo abierto")
		}
	}

	periodo.IdEmpresa = &idEmpresa

	err := repositories.CrearPeriodo(schema, periodo)
	if err != nil {
		return c.JSON(http.StatusInternalServerError, err.Error())
	}

	return c.JSON(http.StatusCreated, "")
}

// CerrarPeriodo
func CerrarPeriodo(c echo.Context) error {
	schema := c.QueryParam("schema")
	if schema == "" {
		return c.JSON(http.StatusBadRequest, "query parameter not found")
	}

	mes, err := strconv.Atoi(c.QueryParam("mes"))
	if err != nil {
		return c.JSON(http.StatusBadRequest, err.Error())
	}

	anio, err := strconv.Atoi(c.QueryParam("anio"))
	if err != nil {
		return c.JSON(http.StatusBadRequest, err.Error())
	}

	idEmpresa := c.Get("user").(*jwt.Token).Claims.(*services.CustomClaims).Empresa.Id

	err = repositories.CerrarPeriodo(schema, idEmpresa, mes, anio)
	if err != nil {
		return c.JSON(http.StatusInternalServerError, err.Error())
	}

	return c.JSON(http.StatusOK, "")
}

// AbrirPeriodo
func AbrirPeriodo(c echo.Context) error {
	schema := c.QueryParam("schema")
	if schema == "" {
		return c.JSON(http.StatusBadRequest, "query parameter not found")
	}

	mes, err := strconv.Atoi(c.QueryParam("mes"))
	if err != nil {
		return c.JSON(http.StatusBadRequest, err.Error())
	}

	anio, err := strconv.Atoi(c.QueryParam("anio"))
	if err != nil {
		return c.JSON(http.StatusBadRequest, err.Error())
	}

	idEmpresa := c.Get("user").(*jwt.Token).Claims.(*services.CustomClaims).Empresa.Id

	// Se valida si existe un periodo activo
	activo, _ := repositories.ObtenerPeriodoActivo(schema, idEmpresa)
	if activo != (models.Periodo{}) {
		return c.JSON(http.StatusInternalServerError, "Ya existe un periodo abierto")
	}

	err = repositories.AbrirPeriodo(schema, idEmpresa, mes, anio)
	if err != nil {
		return c.JSON(http.StatusInternalServerError, err.Error())
	}

	return c.JSON(http.StatusOK, "")
}
