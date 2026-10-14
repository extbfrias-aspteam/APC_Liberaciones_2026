package models

type JsonResponse struct {
	Status  string      `json:"status"`
	Message string      `json:"message"`
	Data    interface{} `json:"data"`
}

// Local models
type (
	LoginRequest struct {
		Username string `json:"username"`
		Password string `json:"password"`
	}

	LoginGlobalResponse struct {
		Numempleado    string `json:"numempleado"`
		IdUsuario      string `json:"idUsuario"`
		MessageEstatus string `json:"messageEstatus"`
		CodeEstatus    string `json:"codeEstatus"`
	}

	AuthUser struct {
		Id          string  `json:"id"`
		NumEmpleado string  `json:"num_empleado"`
		Nombre      string  `json:"nombre"`
		Empresa     Empresa `json:"empresa"`
	}
)

// Condusef models
type (
	LoginCondusefRequest struct {
		Username string `json:"username"`
		Password string `json:"password"`
		Schema   string `json:"schema"`
	}

	LoginCondusefResponse struct {
		Msg  string           `json:"msg"`
		User AuthUserCondusef `json:"user"`
	}

	AuthUserCondusef struct {
		TokenAccess string `json:"token_access"`
		Username    string `json:"username"`
	}
)
