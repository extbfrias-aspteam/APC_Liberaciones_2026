package models

import (
	"encoding/json"
)

type Aclaracion struct {
	ID                            int      `gorm:"column:id;primaryKey;autoIncrement" json:"Id"`
	AclaracionTrimestre           int      `gorm:"column:periodo;not null" json:"AclaracionTrimestre"`
	AclaracionNumero              int      `gorm:"column:aclaracion_numero;not null" json:"AclaracionNumero"`
	AclaracionFolioAtencion       string   `gorm:"column:folio;not null" json:"AclaracionFolioAtencion"`
	AclaracionEstadoConPend       int      `gorm:"column:estatus;not null" json:"AclaracionEstadoConPend"`
	AclaracionFechaAclaracion     string   `gorm:"column:aclaracion_fecha_reclamacion;not null" json:"AclaracionFechaAclaracion"`
	AclaracionFechaAtencion       string   `gorm:"column:aclaracion_fecha_atencion" json:"AclaracionFechaAtencion"`
	AclaracionMedioRecepcionCanal int      `gorm:"column:aclaracion_medio_recepcion_canal;not null" json:"AclaracionMedioRecepcionCanal"`
	IdProducto                    string   `gorm:"column:id_producto;not null" json:"AclaracionProductoServicio"`
	IdCausa                       string   `gorm:"column:id_causa;not null" json:"AclaracionCausaMotivo"`
	AclaracionFechaResolucion     string   `gorm:"column:aclaracion_fecha_resolucion" json:"AclaracionFechaResolucion"`
	AclaracionFechaNotifiUsuario  string   `gorm:"column:aclaracion_fecha_notifi_usuario" json:"AclaracionFechaNotifiUsuario"`
	IdAclaracionEntidadFederativa int      `gorm:"column:id_aclaracion_entidad_federativa;not null" json:"AclaracionEntidadFederativa"`
	AclaracionCodigoPostal        *int     `gorm:"column:aclaracion_codigo_postal" json:"AclaracionCodigoPostal"`
	IdAclaracionMunicipioAlcaldia *int     `gorm:"column:id_aclaracion_municipio_alcaldia" json:"AclaracionMunicipioAlcaldia"`
	IdAclaracionLocalidad         *int     `gorm:"column:id_aclaracion_localidad" json:"AclaracionLocalidad"`
	IdAclaracionColonia           *int     `gorm:"column:id_aclaracion_colonia" json:"AclaracionColonia"`
	AclaracionMonetario           string   `gorm:"column:aclaracion_monetario;not null" json:"AclaracionMonetario"`
	AclaracionMontoReclamado      *float64 `gorm:"column:aclaracion_monto_reclamado" json:"AclaracionMontoReclamado"`
	AclaracionPori                string   `gorm:"column:aclaracion_pori;not null" json:"AclaracionPori"`
	AclaracionTipoPersona         int      `gorm:"column:aclaracion_tipo_persona;not null" json:"AclaracionTipoPersona"`
	AclaracionSexo                string   `gorm:"column:aclaracion_sexo" json:"AclaracionSexo"`
	AclaracionEdad                int      `gorm:"column:aclaracion_edad" json:"AclaracionEdad"`
	AclaracionNivelAtencion       int      `gorm:"column:id_nivel_atencion" json:"AclaracionNivelAtencion"`
	AclaracionFolioCondusef       string   `gorm:"column:aclaracion_folio_condusef" json:"AclaracionFolioCondusef"`
	AclaracionReversa             int      `gorm:"column:aclaracion_reversa" json:"AclaracionReversa"`
	AclaracionOperacionExtranjero string   `gorm:"column:aclaracion_operacion_extranjero;not null" json:"AclaracionOperacionExtranjero"`
	Sucursal                      int      `gorm:"column:sucursal;not null" json:"Sucursal"`
	IdEmpresa                     int      `gorm:"column:id_empresa" json:"IdEmpresa"`
	Reportado                     bool     `gorm:"column:reportado" json:"Reportado"`
	Empresa                       Empresa  `gorm:"foreignKey:IdEmpresa" json:"-"`
}

type AclaracionResponse struct {
	ID                            int      `json:"Id"`
	AclaracionTrimestre           int      `json:"AclaracionTrimestre"`
	AclaracionNumero              int      `json:"AclaracionNumero"`
	AclaracionFolioAtencion       string   `json:"AclaracionFolioAtencion"`
	AclaracionEstadoConPend       int      `json:"AclaracionEstadoConPend"`
	AclaracionFechaAclaracion     string   `json:"AclaracionFechaAclaracion"`
	AclaracionFechaAtencion       string   `json:"AclaracionFechaAtencion"`
	AclaracionMedioRecepcionCanal int      `json:"AclaracionMedioRecepcionCanal"`
	AclaracionProductoServicio    string   `json:"AclaracionProductoServicio"`
	AclaracionCausaMotivo         string   `json:"AclaracionCausaMotivo"`
	AclaracionFechaResolucion     string   `json:"AclaracionFechaResolucion"`
	AclaracionFechaNotifiUsuario  string   `json:"AclaracionFechaNotifiUsuario"`
	AclaracionEntidadFederativa   int      `json:"AclaracionEntidadFederativa"`
	AclaracionCodigoPostal        *int     `json:"AclaracionCodigoPostal"`
	AclaracionMunicipioAlcaldia   *int     `json:"AclaracionMunicipioAlcaldia"`
	AclaracionLocalidad           *int     `json:"AclaracionLocalidad"`
	AclaracionColonia             *int     `json:"AclaracionColonia"`
	AclaracionMonetario           string   `json:"AclaracionMonetario"`
	AclaracionMontoReclamado      *float64 `json:"AclaracionMontoReclamado"`
	AclaracionPori                string   `json:"AclaracionPori"`
	AclaracionTipoPersona         int      `json:"AclaracionTipoPersona"`
	AclaracionSexo                string   `json:"AclaracionSexo"`
	AclaracionEdad                int      `json:"AclaracionEdad"`
	AclaracionNivelAtencion       int      `json:"AclaracionNivelAtencion"`
	AclaracionFolioCondusef       string   `json:"AclaracionFolioCondusef"`
	AclaracionReversa             int      `json:"AclaracionReversa"`
	AclaracionOperacionExtranjero string   `json:"AclaracionOperacionExtranjero"`
	Sucursal                      int      `json:"Sucursal"`
	IdEmpresa                     int      `json:"IdEmpresa"`
	AclaracionSector              string   `json:"AclaracionSector"`
	AclaracionDenominacion        string   `json:"AclaracionDenominacion"`
}

type AclaracionResponseEnv struct {
	AclaracionTrimestre           int      `json:"AclaracionTrimestre"`
	AclaracionNumero              int      `json:"AclaracionNumero"`
	AclaracionFolioAtencion       string   `json:"AclaracionFolioAtencion"`
	AclaracionEstadoConPend       int      `json:"AclaracionEstadoConPend"`
	AclaracionFechaAclaracion     string   `json:"AclaracionFechaAclaracion"`
	AclaracionFechaAtencion       string   `json:"AclaracionFechaAtencion"`
	AclaracionMedioRecepcionCanal int      `json:"AclaracionMedioRecepcionCanal"`
	AclaracionProductoServicio    string   `json:"AclaracionProductoServicio"`
	AclaracionCausaMotivo         string   `json:"AclaracionCausaMotivo"`
	AclaracionFechaResolucion     string   `json:"AclaracionFechaResolucion"`
	AclaracionFechaNotifiUsuario  string   `json:"AclaracionFechaNotifiUsuario"`
	AclaracionEntidadFederativa   int      `json:"AclaracionEntidadFederativa"`
	AclaracionCodigoPostal        *int     `json:"AclaracionCodigoPostal"`
	AclaracionMunicipioAlcaldia   *int     `json:"AclaracionMunicipioAlcaldia"`
	AclaracionLocalidad           *int     `json:"AclaracionLocalidad"`
	AclaracionColonia             *int     `json:"AclaracionColonia"`
	AclaracionMonetario           string   `json:"AclaracionMonetario"`
	AclaracionMontoReclamado      *float64 `json:"AclaracionMontoReclamado"`
	AclaracionPori                string   `json:"AclaracionPori"`
	AclaracionTipoPersona         int      `json:"AclaracionTipoPersona"`
	AclaracionSexo                string   `json:"AclaracionSexo"`
	AclaracionEdad                int      `json:"AclaracionEdad"`
	AclaracionNivelAtencion       int      `json:"AclaracionNivelAtencion"`
	AclaracionFolioCondusef       string   `json:"AclaracionFolioCondusef"`
	AclaracionReversa             int      `json:"AclaracionReversa"`
	AclaracionOperacionExtranjero string   `json:"AclaracionOperacionExtranjero"`
	IdEmpresa                     int      `json:"IdEmpresa"`
	AclaracionSector              string   `json:"AclaracionSector"`
	AclaracionDenominacion        string   `json:"AclaracionDenominacion"`
}

// MarshalJSON personaliza la codificación JSON de la estructura
func (a AclaracionResponse) MarshalJSON() ([]byte, error) {
	type Alias AclaracionResponse
	alias := Alias(a)

	alias.AclaracionFechaAclaracion    = formatToDDMMYYYY(a.AclaracionFechaAclaracion)
	alias.AclaracionFechaAtencion      = formatToDDMMYYYY(a.AclaracionFechaAtencion)
	alias.AclaracionFechaResolucion    = formatToDDMMYYYY(a.AclaracionFechaResolucion)
	alias.AclaracionFechaNotifiUsuario = formatToDDMMYYYY(a.AclaracionFechaNotifiUsuario)

	temp := map[string]interface{}{
		"Id":                            alias.ID,
		"AclaracionTrimestre":           alias.AclaracionTrimestre,
		"AclaracionNumero":              alias.AclaracionNumero,
		"AclaracionFolioAtencion":       alias.AclaracionFolioAtencion,
		"AclaracionEstadoConPend":       alias.AclaracionEstadoConPend,
		"AclaracionFechaAclaracion":     nilIfEmpty(alias.AclaracionFechaAclaracion),
		"AclaracionFechaAtencion":       nilIfEmpty(alias.AclaracionFechaAtencion),
		"AclaracionMedioRecepcionCanal": alias.AclaracionMedioRecepcionCanal,
		"AclaracionProductoServicio":    alias.AclaracionProductoServicio,
		"AclaracionCausaMotivo":         alias.AclaracionCausaMotivo,
		"AclaracionFechaResolucion":     nilIfEmpty(alias.AclaracionFechaResolucion),
		"AclaracionFechaNotifiUsuario":  nilIfEmpty(alias.AclaracionFechaNotifiUsuario),
		"AclaracionEntidadFederativa":   alias.AclaracionEntidadFederativa,
		"AclaracionMunicipioAlcaldia":   alias.AclaracionMunicipioAlcaldia,
		"AclaracionCodigoPostal":        alias.AclaracionCodigoPostal,
		"AclaracionColonia":             alias.AclaracionColonia,
		"AclaracionLocalidad":           alias.AclaracionLocalidad,
		"AclaracionMonetario":           alias.AclaracionMonetario,
		"AclaracionMontoReclamado":      alias.AclaracionMontoReclamado,
		"AclaracionPori":                alias.AclaracionPori,
		"AclaracionTipoPersona":         alias.AclaracionTipoPersona,
		"AclaracionSexo":                alias.AclaracionSexo,
		"AclaracionEdad":                alias.AclaracionEdad,
		"AclaracionNivelAtencion":       nilIfZero(alias.AclaracionNivelAtencion),
		"AclaracionFolioCondusef":       nilIfEmpty(alias.AclaracionFolioCondusef),
		"AclaracionReversa":             alias.AclaracionReversa,
		"AclaracionOperacionExtranjero": alias.AclaracionOperacionExtranjero,
		"Sucursal":                      alias.Sucursal,
		"AclaracionSector":              alias.AclaracionSector,
		"AclaracionDenominacion":        alias.AclaracionDenominacion,
	}

	return json.Marshal(temp)
}

// UnmarshalJSON personaliza la decodificación JSON de la estructura
func (a *AclaracionResponse) UnmarshalJSON(data []byte) error {
	type Alias AclaracionResponse
	aux := &struct{ *Alias }{Alias: (*Alias)(a)}
	if err := json.Unmarshal(data, &aux); err != nil {
		return err
	}
	a.AclaracionFechaAclaracion    = formatToYYYYMMDD(a.AclaracionFechaAclaracion)
	a.AclaracionFechaAtencion      = formatToYYYYMMDD(a.AclaracionFechaAtencion)
	a.AclaracionFechaResolucion    = formatToYYYYMMDD(a.AclaracionFechaResolucion)
	a.AclaracionFechaNotifiUsuario = formatToYYYYMMDD(a.AclaracionFechaNotifiUsuario)
	return nil
}

// MarshalJSON personaliza la codificación JSON de la estructura
func (a AclaracionResponseEnv) MarshalJSON() ([]byte, error) {
	type Alias AclaracionResponseEnv
	alias := Alias(a)

	alias.AclaracionFechaAclaracion    = formatToDDMMYYYY(a.AclaracionFechaAclaracion)
	alias.AclaracionFechaAtencion      = formatToDDMMYYYY(a.AclaracionFechaAtencion)
	alias.AclaracionFechaResolucion    = formatToDDMMYYYY(a.AclaracionFechaResolucion)
	alias.AclaracionFechaNotifiUsuario = formatToDDMMYYYY(a.AclaracionFechaNotifiUsuario)

	temp := map[string]interface{}{
		"AclaracionTrimestre":           alias.AclaracionTrimestre,
		"AclaracionNumero":              alias.AclaracionNumero,
		"AclaracionFolioAtencion":       alias.AclaracionFolioAtencion,
		"AclaracionEstadoConPend":       alias.AclaracionEstadoConPend,
		"AclaracionFechaAclaracion":     nilIfEmpty(alias.AclaracionFechaAclaracion),
		"AclaracionFechaAtencion":       nilIfEmpty(alias.AclaracionFechaAtencion),
		"AclaracionMedioRecepcionCanal": alias.AclaracionMedioRecepcionCanal,
		"AclaracionProductoServicio":    alias.AclaracionProductoServicio,
		"AclaracionCausaMotivo":         alias.AclaracionCausaMotivo,
		"AclaracionFechaResolucion":     nilIfEmpty(alias.AclaracionFechaResolucion),
		"AclaracionFechaNotifiUsuario":  nilIfEmpty(alias.AclaracionFechaNotifiUsuario),
		"AclaracionEntidadFederativa":   alias.AclaracionEntidadFederativa,
		"AclaracionMunicipioAlcaldia":   alias.AclaracionMunicipioAlcaldia,
		"AclaracionCodigoPostal":        alias.AclaracionCodigoPostal,
		"AclaracionColonia":             alias.AclaracionColonia,
		"AclaracionLocalidad":           alias.AclaracionLocalidad,
		"AclaracionMonetario":           alias.AclaracionMonetario,
		"AclaracionMontoReclamado":      alias.AclaracionMontoReclamado,
		"AclaracionPori":                alias.AclaracionPori,
		"AclaracionTipoPersona":         alias.AclaracionTipoPersona,
		"AclaracionSexo":                alias.AclaracionSexo,
		"AclaracionEdad":                alias.AclaracionEdad,
		"AclaracionNivelAtencion":       nilIfZero(alias.AclaracionNivelAtencion),
		"AclaracionFolioCondusef":       nilIfEmpty(alias.AclaracionFolioCondusef),
		"AclaracionReversa":             alias.AclaracionReversa,
		"AclaracionOperacionExtranjero": alias.AclaracionOperacionExtranjero,
		"AclaracionSector":              alias.AclaracionSector,
		"AclaracionDenominacion":        alias.AclaracionDenominacion,
	}

	return json.Marshal(temp)
}

// UnmarshalJSON personaliza la decodificación JSON de la estructura
func (a *AclaracionResponseEnv) UnmarshalJSON(data []byte) error {
	type Alias AclaracionResponseEnv
	aux := &struct{ *Alias }{Alias: (*Alias)(a)}
	if err := json.Unmarshal(data, &aux); err != nil {
		return err
	}
	a.AclaracionFechaAclaracion    = formatToYYYYMMDD(a.AclaracionFechaAclaracion)
	a.AclaracionFechaAtencion      = formatToYYYYMMDD(a.AclaracionFechaAtencion)
	a.AclaracionFechaResolucion    = formatToYYYYMMDD(a.AclaracionFechaResolucion)
	a.AclaracionFechaNotifiUsuario = formatToYYYYMMDD(a.AclaracionFechaNotifiUsuario)
	return nil
}

