package controllers

import (
	"condusef-api/app/models"
	"condusef-api/app/repositories"
	"condusef-api/app/services"
	"condusef-api/app/utils"
	"net"

	"fmt"
	"net/http"
	"strconv"

	"github.com/labstack/echo/v4"
)

// Autenticar
func Autenticar(c echo.Context) error {
	username := c.FormValue("username")
	password := c.FormValue("password")
	idEmpresa, err := strconv.Atoi(c.FormValue("idEmpresa"))
	if err != nil {
		return c.JSON(http.StatusBadRequest,
			models.JsonResponse{
				Status:  "error",
				Message: "Es necesario enviar todos los campos",
			})
	}

	if username == "" || password == "" {
		return c.JSON(http.StatusBadRequest,
			models.JsonResponse{
				Status:  "error",
				Message: "Es necesario enviar todos los campos",
			})
	}

	empresa, err := repositories.ObtenerEmpresaPorId(idEmpresa)
	if err != nil {
		return c.JSON(http.StatusBadRequest,
			models.JsonResponse{
				Status:  "error",
				Message: "Los datos de la empresa no son válidos",
			})
	}
	// Obtener IP priorizando X-Forwarded-For
	ip := c.Request().Header.Get("X-Forwarded-For")
	r := c.Request()
	if ip == "" {
		// Luego revisa X-Real-IP
		ip = r.Header.Get("X-Real-IP")
	}
	if ip == "" {
		// Finalmente usa RemoteAddr
		ip, _, _ = net.SplitHostPort(r.RemoteAddr)
	}
	resp, err := repositories.LoginGlobal(username, password, ip)
	if err != nil {
		return c.JSON(http.StatusInternalServerError, err.Error())
	}

	fmt.Printf("Log Login: %+v\n", resp)

	fmt.Printf("Log CodeEstatus: %+v\n", resp.CodeEstatus)

	fmt.Printf("USUARIO", username)

	if resp.CodeEstatus == "0" {
		authUser := models.AuthUser{
			Id:          resp.IdUsuario,
			NumEmpleado: resp.Numempleado,
			Nombre:      username,
			Empresa:     empresa,
		}

		jwtToken, jwtErr := services.JWTGenerateToken(&authUser)
		if jwtErr != nil {
			utils.ErrorMessage("util.JWTGenerateToken error", jwtErr.Error())
			return c.JSON(http.StatusInternalServerError, models.JsonResponse{Status: "error", Message: jwtErr.Error()})
		}

		return c.JSON(
			http.StatusOK,
			models.JsonResponse{Status: "success", Data: jwtToken})
	}

	return c.JSON(
		http.StatusOK,
		models.JsonResponse{Status: "error", Message: fmt.Sprintf(`Error code %s: %s`, resp.CodeEstatus, resp.MessageEstatus)})
}

// CerrarSesion
func CerrarSesion(c echo.Context) error {
	user := &models.AuthUser{}
	if err := c.Bind(user); err != nil {
		return c.JSON(http.StatusBadRequest, err.Error())
	}
	/*
		// Leer la variable de entorno
		hostStr := os.Getenv("AMBIENTE")

		// Convertir el valor a entero
		host, err := strconv.Atoi(hostStr)
		if err != nil {
			return c.JSON(http.StatusBadRequest,
				models.JsonResponse{
					Status:  "error",
					Message: "Hubo un error al leer las variables de entorno",
				})
		}

		if host == 3 {
	*/
	// Obtener IP priorizando X-Forwarded-For
	ip := c.Request().Header.Get("X-Forwarded-For")
	r := c.Request()
	if ip == "" {
		// Luego revisa X-Real-IP
		ip = r.Header.Get("X-Real-IP")
	}
	if ip == "" {
		// Finalmente usa RemoteAddr
		ip, _, _ = net.SplitHostPort(r.RemoteAddr)
	}

	resp, err := repositories.LogoutGlobal(user.Id, user.Nombre, ip)
	if err != nil {
		return c.JSON(http.StatusBadRequest,
			models.JsonResponse{
				Status:  "error",
				Message: "Hubo un error al leer las variables de entorno",
			})
	}

	if resp.CodeEstatus == "0" {
		return c.JSON(
			http.StatusOK,
			models.JsonResponse{Status: "success"})
	}

	return c.JSON(
		http.StatusOK,
		models.JsonResponse{Status: "error", Message: fmt.Sprintf(`Error code %s: %s`, resp.CodeEstatus, resp.MessageEstatus)})
	/*}

	return c.JSON(
		http.StatusOK,
		models.JsonResponse{Status: "success"})
	*/

}
