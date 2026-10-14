package controllers

import (
	"condusef-api/app/models"
	"condusef-api/app/repositories"
	"condusef-api/app/services"
	"condusef-api/app/utils"
	"errors"
	"fmt"
	"os"
	"strconv"

	"net/http"

	"github.com/golang-jwt/jwt/v5"
	"github.com/labstack/echo/v4"
)

func ObtenerPaginadoREUNE(c echo.Context) error {
	offset, _ := strconv.Atoi(c.QueryParam("offset"))
	limit, _ := strconv.Atoi(c.QueryParam("limit"))
	filter := c.QueryParam("filter")
	numTrim, _ := strconv.Atoi(c.QueryParam("num_trim"))

	claims := c.Get("user").(*jwt.Token).Claims.(*services.CustomClaims)

	registros, total, err := repositories.ObtenerPaginadoREUNE(numTrim, offset, limit, filter, claims.Empresa.Id)
	if err != nil {
		c.JSON(http.StatusInternalServerError, errors.New(err.Error()))
	}

	response := models.PagedResponse{}
	response.Total = total
	response.Items = registros

	return c.JSON(http.StatusOK, response)
}

func ObtenerRegistrosREUNESinReportar(c echo.Context) error {
	numTrim, err := strconv.Atoi(c.QueryParam("num_trim"))
	if err != nil {
		return c.String(http.StatusBadRequest, err.Error())
	}
	claims := c.Get("user").(*jwt.Token).Claims.(*services.CustomClaims)

	registros, err := repositories.ObtenerRegistrosREUNESinReportar(numTrim, claims.Empresa.Id)
	if err != nil {
		return errors.New(err.Error())
	}

	return c.JSON(http.StatusOK, registros)
}

func ReportarREUNE(c echo.Context) error {
	params := &models.ReportarParams{}
	response := &models.ResponseREUNE{}

	if err := c.Bind(params); err != nil {
		return c.String(http.StatusInternalServerError, err.Error())
	}
	// Se genera el tokenKey
	claims := c.Get("user").(*jwt.Token).Claims.(*services.CustomClaims)
	tokenKey, err := utils.KeyTokenCondusef(1, claims.Nombre, "reune")
	if err != nil {
		return c.JSON(http.StatusInternalServerError, err.Error())
	}
	// Se obtiene el token
	token, err := services.L_STORAGE.ObtenerToken(tokenKey)
	if err != nil {
		return c.JSON(http.StatusInternalServerError, err.Error())
	}
	if token == "" {
		return c.String(http.StatusInternalServerError, "El token ha expirado o no fue encontrado, actualice su token para continuar")
	}

	consultas, err := repositories.ObtenerConsultasPorFolios(params.Folios, 1)
	if err != nil {
		return errors.New(err.Error())
	} else if len(consultas) > 0 {
		consultasEnv, err := repositories.ObtenerConsultasPorFoliosEnv(params.Folios, 1)

		if err != nil {
			return errors.New(err.Error())
		} else if len(consultasEnv) > 0 {
			response.Consultas = EnviarApiCondusef(consultasEnv, fmt.Sprintf("%s/reune/consultas/general", os.Getenv("CONDUSEF_URL_REUNE")), token)
		}
		if response.Consultas.Success {
			repositories.MarcarConsultasReportadas(params.Folios)
		} else {
			response.Consultas.Data = consultas
		}
	}

	reclamaciones, err := repositories.ObtenerReclamacionesPorFolios(params.Folios, 1)
	if err != nil {
		return errors.New(err.Error())
	} else if len(reclamaciones) > 0 {
		reclamacionesEnv, err := repositories.ObtenerReclamacionesPorFoliosEnv(params.Folios, 1)

		if err != nil {
			return errors.New(err.Error())
		} else if len(reclamacionesEnv) > 0 {
			response.Reclamaciones = EnviarApiCondusef(reclamacionesEnv, fmt.Sprintf("%s/reune/reclamaciones/general", os.Getenv("CONDUSEF_URL_REUNE")), token)
		}
		if response.Reclamaciones.Success {
			repositories.MarcarReclamacionesReportadas(params.Folios)
		} else {
			response.Reclamaciones.Data = reclamaciones
		}
	}

	aclaraciones, err := repositories.ObtenerAclaracionesPorFolios(params.Folios, 1)
	if err != nil {
		return errors.New(err.Error())
	} else if len(aclaraciones) > 0 {
		aclaracionesEnv, err := repositories.ObtenerAclaracionesPorFoliosEnv(params.Folios, 1)

		if err != nil {
			return errors.New(err.Error())
		} else if len(aclaracionesEnv) > 0 {
			response.Aclaraciones = EnviarApiCondusef(aclaracionesEnv, fmt.Sprintf("%s/reune/aclaraciones/general", os.Getenv("CONDUSEF_URL_REUNE")), token)
		}
		if response.Aclaraciones.Success {
			repositories.MarcarAclaracionesReportadas(params.Folios)
		} else {
			response.Aclaraciones.Data = aclaraciones
		}
	}

	return c.JSON(http.StatusOK, response)
}
