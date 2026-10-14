package repositories

import (
	"condusef-api/app/models"
	"condusef-api/app/utils"

	"encoding/json"
	"fmt"
	"os"
	"strings"
)

// AutenticarLoginGlobal
func LoginGlobal(username, password string, ipUsuario string) (*models.LoginGlobalResponse, error) {
	encryptedPass, err := utils.GetAESEncrypted(password)
	if err != nil {
		return nil, err
	}

	var payload = strings.NewReader(
		fmt.Sprintf(`
			{ 
			  "usuario": "%s", 
			  "contrasena": "%s", 
			  "idUsuario": "0", 
			  "ipUsuario": "%s",
			  "idSesion": "0",
			  "estatus": "0",
			  "idAplicativo":"59"
			}`, username, encryptedPass, ipUsuario))

	var url = fmt.Sprintf(`http://%s:%s/AdminSegWS/rest/capacontrol/loginGlobal/`,
		os.Getenv("ASP_LOGIN_HOST"),
		os.Getenv("ASP_LOGIN_PORT"))

	resp, err := utils.HTTPRequest("POST", url, "", payload)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()

	var response models.LoginGlobalResponse
	err = json.NewDecoder(resp.Body).Decode(&response)
	if err != nil {
		return nil, err
	}
	if response.IdUsuario != "" && response.MessageEstatus == "" {
		fmt.Print("revisar permisos")
		var url = fmt.Sprintf(`http://%s:%s/AdminSegWS/rest/permisos/consulta/%s`,
			os.Getenv("ASP_LOGIN_HOST"),
			os.Getenv("ASP_LOGIN_PORT"), username)

		resp, err := utils.HTTPRequest("POST", url, "", strings.NewReader(""))
		if err != nil {
			return nil, err
		}
		defer resp.Body.Close()

		var permisosResponse models.PermisosResponse
		err = json.NewDecoder(resp.Body).Decode(&permisosResponse)
		if err != nil {
			return nil, err
		}
		valid := false
		for _, resultado := range permisosResponse.SeTransaccionResponse.Resultados {
			if resultado.Nombre == "MNU_CONDUSEF" {
				valid = true
			}
		}
		if !valid {
			response.MessageEstatus = "Usuario sin privilegios para App Condusef"
			response.CodeEstatus = "-1"
			//si el usuario no tiene permisos provocar logout
			LogoutGlobal(response.IdUsuario, username, ipUsuario)
		}
	}

	return &response, nil
}

// LogoutGlobal
func LogoutGlobal(id, username string, ipUsuario string) (*models.LoginGlobalResponse, error) {
	var payload = strings.NewReader(
		fmt.Sprintf(`
			{ 
			  "usuario": "%s",
			  "idUsuario": "%s", 
			  "ipUsuario": "%s",
			  "idSesion": "0",
			  "estatus": "1",
			  "idAplicativo":"59"
			}`, username, id, ipUsuario))

	var url = fmt.Sprintf(`http://%s:%s/AdminSegWS/rest/capacontrol/logoutGlobal`,
		os.Getenv("ASP_LOGIN_HOST"),
		os.Getenv("ASP_LOGIN_PORT"))

	resp, err := utils.HTTPRequest("POST", url, "", payload)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()

	var response models.LoginGlobalResponse
	err = json.NewDecoder(resp.Body).Decode(&response)
	if err != nil {
		return nil, err
	}

	return &response, nil
}
