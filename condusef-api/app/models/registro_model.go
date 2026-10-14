package models

type Registro struct {
	ID          int    `gorm:"column:id;primary_key" json:"id"`
	Folio       string `gorm:"column:folio" json:"folio"`
	ProductoID  string `gorm:"column:id_producto" json:"id_producto"`
	CausaID     int    `gorm:"column:id_causa" json:"id_causa"`
	Tipo        int    `gorm:"column:tipo" json:"tipo"`
	Reportado   bool   `gorm:"column:reportado" json:"reportado"`
	Periodo     int    `gorm:"column:periodo" json:"periodo"`
	Producto    string `gorm:"column:producto" json:"producto"`
	Subproducto string `gorm:"column:subproducto" json:"subproducto"`
	Estatus     int    `gorm:"column:estatus" json:"estatus"`
}

type ReportarParams struct {
	Folios []string `json:"folios"`
}
type ResponseREDECO struct {
	Quejas ApiResponse `json:"quejas"`
}
type ResponseREUNE struct {
	Consultas     ApiResponse `json:"consultas"`
	Reclamaciones ApiResponse `json:"reclamaciones"`
	Aclaraciones  ApiResponse `json:"aclaraciones"`
}
type ErroresCondusef struct {
	Errors  map[string][]string `json:"errors"`
	Message string              `json:"message"`
}
type ErrorResponse struct {
	Error string `json:"error"`
}
type MsgResponse struct {
	Msg string `json:"msg"`
}
type ApiResponse struct {
	Success bool                `json:"success"`
	Status  string              `json:"status"`
	Errors  map[string][]string `json:"errors"`
	Message string              `json:"message,omitempty"`
	Data    interface{}         `json:"data,omitempty"`
}
type SuccessResponse struct {
	Message               string   `json:"message"`
	NumeroTotalDeEnvios   int      `json:"Número total de envios"`
	QuejasEnviadas        []string `json:"Quejas enviadas,omitempty"`
	ConsultasEnviadas     []string `json:"Consultas enviadas,omitempty"`
	AclaracionesEnviadas  []string `json:"Aclaraciones enviadas,omitempty"`
	ReclamacionesEnviadas []string `json:"Reclamaciones enviadas,omitempty"`
}

type PermisosResultado struct {
	ID                  int    `json:"id"`
	Seestatus           string `json:"seestatus"`
	UsuarioCreacion     int    `json:"usuarioCreacion"`
	FechaCreacion       int64  `json:"fechaCreacion"`
	UsuarioModificacion *int   `json:"usuarioModificacion"`
	FechaModificacion   *int64 `json:"fechaModificacion"`
	Nombre              string `json:"nombre"`
	Descripcion         string `json:"descripcion"`
}

type Permisos struct {
	CodeEstatus    string              `json:"codeEstatus"`
	MessageEstatus string              `json:"messageEstatus"`
	Resultados     []PermisosResultado `json:"resultados"`
}

type PermisosResponse struct {
	SeTransaccionResponse Permisos    `json:"seTransaccionResponse"`
	Usuario               interface{} `json:"usuario,omitempty"`
	ContrasenaUsuario     interface{} `json:"contrasenaUsuario,omitempty"`
}

func nilIfZero(value int) interface{} {
	if value == 0 {
		return nil
	}
	return value
}
func nilIfZeroFloat64(num float64) interface{} {
	if num == 0.0 {
		return nil
	}
	return num
}
func nilIfZeroInt(num int) interface{} {
	if num == 0 {
		return nil
	}
	return num
}
func nilIfEmpty(value string) interface{} {
	if value == "" {
		return nil
	}
	return value
}
