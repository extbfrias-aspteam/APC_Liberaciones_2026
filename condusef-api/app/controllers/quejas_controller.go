package controllers

import (
	"condusef-api/app/models"
	"condusef-api/app/repositories"
	"condusef-api/app/services"

	"errors"
	"net/http"
	"strconv"
	"strings"

	"github.com/golang-jwt/jwt/v5"
	"github.com/labstack/echo/v4"
)

func ObtenerQuejasPaginado(c echo.Context) error {
	offset, _ := strconv.Atoi(c.QueryParam("offset"))
	limit, _ := strconv.Atoi(c.QueryParam("limit"))
	filter := c.QueryParam("filter")
	numMes, _ := strconv.Atoi(c.QueryParam("num_mes"))
	claims := c.Get("user").(*jwt.Token).Claims.(*services.CustomClaims)
	idEmpresa := claims.Empresa.Id

	quejas, total, err := repositories.ObtenerQuejasPaginado(numMes, idEmpresa, offset, limit, filter)
	if err != nil {
		c.JSON(http.StatusInternalServerError, errors.New(err.Error()))
	}

	response := models.PagedResponse{}
	response.Total = total
	response.Items = quejas

	return c.JSON(http.StatusOK, response)
}

// ObtenerQuejaPorFolio
func ObtenerQuejaPorFolio(c echo.Context) error {
	folio := c.QueryParam("num_folio")

	queja, err := repositories.ObtenerQuejaPorFolio(folio)
	if err != nil {
		return errors.New(err.Error())
	}

	if queja == (models.QuejaCondusef{}) {
		return c.JSON(http.StatusNotFound, "Records not found")
	}

	return c.JSON(http.StatusOK, queja)
}

// ObtenerQuejasSinReportar
func ObtenerQuejasSinReportar(c echo.Context) error {
	numTrim, err := strconv.Atoi(c.QueryParam("num_mes"))
	if err != nil {
		return c.String(http.StatusBadRequest, err.Error())
	}

	claims := c.Get("user").(*jwt.Token).Claims.(*services.CustomClaims)
	idEmpresa := claims.Empresa.Id

	quejas, err := repositories.ObtenerQuejasSinReportar(numTrim, idEmpresa)
	if err != nil {
		return errors.New(err.Error())
	}

	return c.JSON(http.StatusOK, quejas)
}

// ModificarEstadoReportadoPorFolio
func ModificarEstadoReportadoPorFolio(c echo.Context) error {
	folios := c.QueryParam("folios")
	if folios == "" {
		return c.String(http.StatusBadRequest, "")
	}
	foliosClean := strings.Replace(folios, ",", " ", -1)
	foliosArray := strings.Fields(foliosClean)

	reportado, err := strconv.ParseBool(c.QueryParam("reportado"))
	if err != nil {
		return c.String(http.StatusBadRequest, err.Error())
	}

	err = repositories.ModificarEstadoReportadoPorFolio(foliosArray, reportado)
	if err != nil {
		return c.JSON(http.StatusInternalServerError, "")
	}

	return c.JSON(http.StatusNoContent, "")
}

// CrearQueja
func CrearQueja(c echo.Context) error {
	queja := &models.Queja{}
	if err := c.Bind(queja); err != nil {
		return c.String(http.StatusInternalServerError, err.Error())
	}

	claims := c.Get("user").(*jwt.Token).Claims.(*services.CustomClaims)
	queja.IdEmpresa = claims.Empresa.Id

	err := repositories.CrearQueja(queja)
	if err != nil {
		return c.String(http.StatusInternalServerError, err.Error())
	}

	return c.JSON(http.StatusCreated, "")
}

// ModificarQueja
func ModificarQueja(c echo.Context) error {
	queja := &models.Queja{}

	if err := c.Bind(queja); err != nil {
		return c.String(http.StatusInternalServerError, err.Error())
	}

	err := repositories.ModificarQueja(queja)
	if err != nil {
		return c.JSON(http.StatusInternalServerError, "")
	}

	return c.JSON(http.StatusOK, "")
}

// EliminarQueja
func EliminarQueja(c echo.Context) error {
	folio := c.Param("folio")
	if folio == "" {
		return c.JSON(http.StatusInternalServerError, "")
	}

	err := repositories.EliminarQueja(folio)
	if err != nil {
		return c.JSON(http.StatusInternalServerError, "")
	}

	return c.JSON(http.StatusOK, "")
}
