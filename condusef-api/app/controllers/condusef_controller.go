package controllers

import (
	"condusef-api/app/models"
	"condusef-api/app/repositories"
	"condusef-api/app/services"
	"condusef-api/app/utils"
	"errors"
	"io"

	"encoding/json"
	"fmt"
	"net/http"
	"os"
	"strings"

	"github.com/golang-jwt/jwt/v5"
	"github.com/labstack/echo/v4"
)

// ObtenerTokenCONDUSEF
func ObtenerTokenCONDUSEF(c echo.Context) error {
	user := &models.LoginCondusefRequest{}
	if err := c.Bind(user); err != nil {
		return c.JSON(http.StatusBadRequest, err.Error())
	}

	if user.Username == "" || user.Password == "" || user.Schema == "" {
		return c.JSON(http.StatusBadRequest, "query parameter not found")
	}

	var payload = strings.NewReader(
		fmt.Sprintf(`{ "username": "%s", "password": "%s" }`, user.Username, user.Password))

	var url, err = utils.UrlTokenCondusef(user.Schema)
	if err != nil {
		return c.JSON(http.StatusInternalServerError, err.Error())
	}

	// Se realiza la petición http
	var data models.LoginCondusefResponse
	resp, err := utils.HTTPRequest("GET", url, "", payload)
	if err != nil {
		return c.JSON(http.StatusInternalServerError, "Hubo un error al obtener el token: "+err.Error())
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		return c.JSON(resp.StatusCode, resp.Body)
	}

	json.NewDecoder(resp.Body).Decode(&data)

	// Se genera el tokenKey
	claims := c.Get("user").(*jwt.Token).Claims.(*services.CustomClaims)
	tokenKey, err := utils.KeyTokenCondusef(claims.Empresa.Id, claims.Nombre, user.Schema)
	if err != nil {
		return c.JSON(http.StatusInternalServerError, err.Error())
	}

	// Se guarda el token en el local storage
	err = services.L_STORAGE.GuardarToken(tokenKey, data.User.TokenAccess)
	if err != nil {
		return c.JSON(http.StatusInternalServerError, err.Error())
	}

	return c.JSON(http.StatusOK, data)
}

// ObtenerQuejasCONDUSEF
func ObtenerQuejasCONDUSEF(c echo.Context) error {
	anio := c.QueryParam("anio")
	mes := c.QueryParam("mes")
	if anio == "" || mes == "" {
		return c.JSON(http.StatusBadRequest, "query parameter not found")
	}

	// Se genera el tokenKey
	claims := c.Get("user").(*jwt.Token).Claims.(*services.CustomClaims)
	tokenKey, err := utils.KeyTokenCondusef(claims.Empresa.Id, claims.Nombre, "redeco")
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

	// Se hace el request hacia condusef
	resp, err := utils.HTTPRequest("GET", fmt.Sprintf("%s/redeco/quejas/?year=%s&month=%s", os.Getenv("CONDUSEF_URL_BASE"), anio, mes), token, nil)
	if err != nil {
		return c.JSON(resp.StatusCode, err.Error())
	}
	defer resp.Body.Close()

	// Se regresa la respuesta directa de condusef
	var data interface{}
	err = json.NewDecoder(resp.Body).Decode(&data)
	if err != nil {
		return c.JSON(http.StatusInternalServerError, err.Error())
	}

	return c.JSON(resp.StatusCode, data)
}

// ReportarQuejasCondusef
func ReportarQuejasCondusef(c echo.Context) error {
	params := &models.ReportarParams{}
	response := &models.ResponseREDECO{}

	if err := c.Bind(params); err != nil {
		return c.String(http.StatusInternalServerError, err.Error())
	}
	// Se genera el tokenKey
	claims := c.Get("user").(*jwt.Token).Claims.(*services.CustomClaims)
	tokenKey, err := utils.KeyTokenCondusef(claims.Empresa.Id, claims.Nombre, "redeco")
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

	quejas, err := repositories.ObtenerQuejasPorFolio(params.Folios, claims.Empresa.Id)
	if err != nil {
		return errors.New(err.Error())
	} else if len(quejas) > 0 {
		response.Quejas = EnviarApiCondusef(quejas, fmt.Sprintf("%s/redeco/quejas", os.Getenv("CONDUSEF_URL_BASE")), token)
		if response.Quejas.Success {
			repositories.ModificarEstadoReportadoPorFolio(params.Folios, true)
		} else {
			response.Quejas.Data = quejas
		}
	}

	return c.JSON(http.StatusOK, response)
}

// EliminarQuejaCondusef
func EliminarQuejaCondusef(c echo.Context) error {
	folio := c.QueryParam("folio")
	if folio == "" {
		return c.JSON(http.StatusBadRequest, "query parameter not found")
	}

	// Se genera el tokenKey
	claims := c.Get("user").(*jwt.Token).Claims.(*services.CustomClaims)
	tokenKey, err := utils.KeyTokenCondusef(claims.Empresa.Id, claims.Nombre, "redeco")
	if err != nil {
		return c.JSON(http.StatusInternalServerError, err.Error())
	}

	// Se obtiene el token
	token, err := services.L_STORAGE.ObtenerToken(tokenKey)
	if err != nil {
		return c.JSON(http.StatusInternalServerError, err.Error())
	}
	if token == "" {
		return c.JSON(http.StatusInternalServerError, "El token ha expirado o no fue encontrado, actualice su token para continuar")
	}

	// Se hace el request hacia condusef
	resp, err := utils.HTTPRequest("DELETE", fmt.Sprintf("%s/redeco/quejas/?quejaFolio=%s", os.Getenv("CONDUSEF_URL_BASE"), folio), token, nil)
	if err != nil {
		return c.JSON(resp.StatusCode, err.Error())
	}
	defer resp.Body.Close()

	// Se regresa la respuesta directa de condusef
	var data interface{}
	err = json.NewDecoder(resp.Body).Decode(&data)
	if err != nil {
		return c.JSON(http.StatusInternalServerError, err.Error())
	}

	return c.JSON(resp.StatusCode, data)
}

// AsignarTokenCondusef
func AsignarTokenCondusef(c echo.Context) error {
	key := c.QueryParam("key")
	token := c.QueryParam("token")
	if key == "" || token == "" {
		return c.JSON(http.StatusBadRequest, "query parameter not found")
	}

	services.L_STORAGE.GuardarToken(key, token)

	return c.JSON(http.StatusOK, "")
}

type Solicitud interface{}

func EnviarApiCondusef(solicitud Solicitud, url string, token string) models.ApiResponse {
	payload, err := json.Marshal(solicitud)
	if err != nil {
		return models.ApiResponse{
			Success: false,
			Status:  "500 Internal Server Error",
			Errors:  map[string][]string{"default": {fmt.Sprintf("%s", err)}},
			Message: "Error generando JSON",
		}
	}

	resp, err := utils.HTTPRequest("POST", url, token, strings.NewReader(string(payload)))

	if err != nil {
		return models.ApiResponse{
			Success: false,
			Status:  "500 Internal Server Error",
			Errors:  map[string][]string{"default": {fmt.Sprintf("%s", err)}},
			Message: "Error enviando solicitud al servidor de Condusef",
		}
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		bodyBytes, err := io.ReadAll(resp.Body)
		if err != nil {
			return models.ApiResponse{
				Success: false,
				Status:  resp.Status,
				Errors:  map[string][]string{"default": {fmt.Sprintf("Error leyendo respuesta del servidor: %s", err)}},
				Message: "Error leyendo respuesta del servidor",
			}
		}

		var errorResponse models.ErroresCondusef
		if err := json.Unmarshal(bodyBytes, &errorResponse); err == nil && errorResponse.Message != "" {
			return models.ApiResponse{
				Success: false,
				Status:  resp.Status,
				Errors:  errorResponse.Errors,
				Message: errorResponse.Message,
			}
		}

		var simpleError models.ErrorResponse
		if err := json.Unmarshal(bodyBytes, &simpleError); err == nil && simpleError.Error != "" {
			return models.ApiResponse{
				Success: false,
				Status:  resp.Status,
				Errors:  map[string][]string{"default": {simpleError.Error}},
				Message: simpleError.Error,
			}
		}

		var msgResponse models.MsgResponse
		if err := json.Unmarshal(bodyBytes, &msgResponse); err == nil && msgResponse.Msg != "" {
			return models.ApiResponse{
				Success: false,
				Status:  resp.Status,
				Message: msgResponse.Msg,
			}
		}

		return models.ApiResponse{
			Success: false,
			Status:  resp.Status,
			Errors:  map[string][]string{"default": {string(bodyBytes)}},
			Message: "Error recibido del servidor",
		}
	}

	var successResp models.SuccessResponse
	if err := json.NewDecoder(resp.Body).Decode(&successResp); err == nil {
		var responseData interface{}
		if len(successResp.QuejasEnviadas) > 0 {
			responseData = successResp.QuejasEnviadas
		} else if len(successResp.ConsultasEnviadas) > 0 {
			responseData = successResp.ConsultasEnviadas
		} else if len(successResp.AclaracionesEnviadas) > 0 {
			responseData = successResp.AclaracionesEnviadas
		} else if len(successResp.ReclamacionesEnviadas) > 0 {
			responseData = successResp.ReclamacionesEnviadas
		} else {
			responseData = successResp
		}
		return models.ApiResponse{
			Success: true,
			Status:  "200 OK",
			Message: successResp.Message,
			Data:    responseData,
		}
	}

	return models.ApiResponse{
		Success: false,
		Status:  "500 Internal Server Error",
		Errors:  map[string][]string{"default": {"No se pudo decodificar la respuesta"}},
		Message: "Error decodificando respuesta exitosa",
	}
}
