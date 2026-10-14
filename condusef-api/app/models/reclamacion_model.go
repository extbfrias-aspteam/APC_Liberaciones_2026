package models

import (
	"encoding/json"
	 "strings"
)

type Reclamacion struct {
	ID                     int     `gorm:"column:id;primaryKey;autoIncrement" json:"id"`
	RecTrimestre           int     `gorm:"column:periodo;not null" json:"RecTrimestre"`
	RecNumero              int     `gorm:"column:rec_numero;not null" json:"RecNumero"`
	RecFolioAtencion       string  `gorm:"column:folio;not null" json:"RecFolioAtencion"`
	RecEstadoConPend       int     `gorm:"column:estatus;not null" json:"RecEstadoConPend"`
	RecFechaReclamacion    string  `gorm:"column:rec_fecha_reclamacion;not null" json:"RecFechaReclamacion"`
	RecFechaAtencion       string  `gorm:"column:rec_fecha_atencion" json:"RecFechaAtencion"`
	RecMedioRecepcionCanal int     `gorm:"column:rec_medio_recepcion_canal;not null" json:"RecMedioRecepcionCanal"`
	RecProductoServicio    string  `gorm:"column:id_producto;not null" json:"RecProductoServicio"`
	IdCausa                string  `gorm:"column:id_causa;not null" json:"RecCausaMotivo"`
	RecFechaResolucion     string  `gorm:"column:rec_fecha_resolucion" json:"RecFechaResolucion"`
	RecFechaNotifiUsuario  string  `gorm:"column:rec_fecha_notifi_usuario" json:"RecFechaNotifiUsuario"`
	RecEntidadFederativa   int     `gorm:"column:id_rec_entidad_federativa;not null" json:"RecEntidadFederativa"`
	RecCodigoPostal        int     `gorm:"column:rec_codigo_postal" json:"RecCodigoPostal"`
	RecMunicipioAlcaldia   int     `gorm:"column:id_rec_municipio_alcaldia;not null" json:"RecMunicipioAlcaldia"`
	RecLocalidad           int     `gorm:"column:id_rec_localidad" json:"RecLocalidad"`
	RecColonia             int     `gorm:"column:id_rec_colonia" json:"RecColonia"`
	RecMonetario           string  `gorm:"column:rec_monetario;not null" json:"RecMonetario"`
	RecMontoReclamado      float64 `gorm:"column:rec_monto_reclamado" json:"RecMontoReclamado"`
	RecImporteAbonado *float64 `gorm:"column:rec_importe_abonado" json:"RecImporteAbonado,omitempty"`
	RecFechaAbonoImporte   string  `gorm:"column:rec_fecha_abono_importe" json:"RecFechaAbonoImporte"`
	RecPori                string  `gorm:"column:rec_pori;not null" json:"RecPori"`
	RecTipoPersona         int     `gorm:"column:rec_tipo_persona;not null" json:"RecTipoPersona"`
	RecSexo                string  `gorm:"column:rec_sexo;not null" json:"RecSexo"`
	RecEdad                int     `gorm:"column:rec_edad" json:"RecEdad"`
	RecSentidoResolucion   int     `gorm:"column:rec_sentido_resolucion" json:"RecSentidoResolucion"`
	RecNivelAtencion       int     `gorm:"column:id_nivel_atencion" json:"RecNivelAtencion"`
	RecFolioCondusef       string  `gorm:"column:rec_folio_condusef" json:"RecFolioCondusef"`
	RecReversa 				int `gorm:"column:rec_reversa" json:"RecReversa"`
	Sucursal               int     `gorm:"column:sucursal;not null" json:"Sucursal"`
	IdEmpresa              int     `gorm:"column:id_empresa" json:"idEmpresa"`
	Empresa                Empresa `gorm:"foreignKey:IdEmpresa" json:"-"`
	//RecOperacionExtranjero string  `gorm:"column:rec_operacion_extranjero;not null" json:"RecOperacionExtranjero"`
}

type ReclamacionResponse struct {
	ID                     int     `json:"id"`
	RecTrimestre           int     `json:"RecTrimestre"`
	RecDenominacion        string  `json:"RecDenominacion"`
	RecNumero              int     `json:"RecNumero"`
	RecFolioAtencion       string  `json:"RecFolioAtencion"`
	RecEstadoConPend       int     `json:"RecEstadoConPend"`
	RecFechaReclamacion    string  `json:"RecFechaReclamacion"`
	RecFechaAtencion       string  `json:"RecFechaAtencion"`
	RecMedioRecepcionCanal int     `json:"RecMedioRecepcionCanal"`
	RecProductoServicio    string  `json:"RecProductoServicio"`
	RecCausaMotivo         string  `json:"RecCausaMotivo"`
	RecFechaResolucion     string  `json:"RecFechaResolucion"`
	RecFechaNotifiUsuario  string  `json:"RecFechaNotifiUsuario"`
	RecEntidadFederativa   int     `json:"RecEntidadFederativa"`
	RecCodigoPostal        int     `json:"RecCodigoPostal"`
	RecMunicipioAlcaldia   int     `json:"RecMunicipioAlcaldia"`
	RecLocalidad           int     `json:"RecLocalidad"`
	RecColonia             int     `json:"RecColonia"`
	RecMonetario           string  `json:"RecMonetario"`
	RecMontoReclamado      float64 `json:"RecMontoReclamado"`
   	RecImporteAbonado *float64 `json:"RecImporteAbonado,omitempty"`
	RecFechaAbonoImporte   string  `json:"RecFechaAbonoImporte"`
	RecPori                string  `json:"RecPori"`
	RecTipoPersona         int     `json:"RecTipoPersona"`
	RecSexo                string  `json:"RecSexo"`
	RecEdad                int     `json:"RecEdad"`
	RecSentidoResolucion   int     `json:"RecSentidoResolucion"`
	RecNivelAtencion       int     `json:"RecNivelAtencion"`
	RecFolioCondusef       string  `json:"RecFolioCondusef"`
	RecReversa             int     `json:"RecReversa"`
	Sucursal               int     `json:"Sucursal"`
	IdEmpresa              int     `json:"idEmpresa"`
	RecSector              string  `json:"RecSector"`
	//RecOperacionExtranjero string  `json:"RecOperacionExtranjero"`
}

type ReclamacionResponseEnv struct {
	RecTrimestre           int     `json:"RecTrimestre"`
	RecDenominacion        string  `json:"RecDenominacion"`
	RecNumero              int     `json:"RecNumero"`
	RecFolioAtencion       string  `json:"RecFolioAtencion"`
	RecEstadoConPend       int     `json:"RecEstadoConPend"`
	RecFechaReclamacion    string  `json:"RecFechaReclamacion"`
	RecFechaAtencion       string  `json:"RecFechaAtencion"`
	RecMedioRecepcionCanal int     `json:"RecMedioRecepcionCanal"`
	RecProductoServicio    string  `json:"RecProductoServicio"`
	RecCausaMotivo         string  `json:"RecCausaMotivo"`
	RecFechaResolucion     string  `json:"RecFechaResolucion"`
	RecFechaNotifiUsuario  string  `json:"RecFechaNotifiUsuario"`
	RecEntidadFederativa   int     `json:"RecEntidadFederativa"`
	RecCodigoPostal        int     `json:"RecCodigoPostal"`
	RecMunicipioAlcaldia   int     `json:"RecMunicipioAlcaldia"`
	RecLocalidad           int     `json:"RecLocalidad"`
	RecColonia             int     `json:"RecColonia"`
	RecMonetario           string  `json:"RecMonetario"`
	RecMontoReclamado      float64 `json:"RecMontoReclamado"`
	RecImporteAbonado *float64 `json:"RecImporteAbonado,omitempty"`
	RecFechaAbonoImporte   string  `json:"RecFechaAbonoImporte"`
	RecPori                string  `json:"RecPori"`
	RecTipoPersona         int     `json:"RecTipoPersona"`
	RecSexo                string  `json:"RecSexo"`
	RecEdad                int     `json:"RecEdad"`
	RecSentidoResolucion   int     `json:"RecSentidoResolucion"`
	RecNivelAtencion       int     `json:"RecNivelAtencion"`
	RecFolioCondusef       string  `json:"RecFolioCondusef"`
	RecReversa             int     `json:"RecReversa"`
	// IdEmpresa              int     `json:"idEmpresa"`
	RecSector              string `json:"RecSector"`
	//RecOperacionExtranjero string `json:"RecOperacionExtranjero"`
}

func (Reclamacion) TableName() string {
	return "reune.reclamaciones"
}

// MarshalJSON personaliza la codificación JSON de la estructura Reclamacion
func (r ReclamacionResponse) MarshalJSON() ([]byte, error) {
	type Alias ReclamacionResponse
	alias := Alias(r)

	// Convertir las fechas al formato deseado
	alias.RecFechaReclamacion = formatToDDMMYYYY(r.RecFechaReclamacion)
	alias.RecFechaAtencion = formatToDDMMYYYY(r.RecFechaAtencion)
	alias.RecFechaResolucion = formatToDDMMYYYY(r.RecFechaResolucion)
	alias.RecFechaNotifiUsuario = formatToDDMMYYYY(r.RecFechaNotifiUsuario)
	alias.RecFechaAbonoImporte = formatToDDMMYYYY(r.RecFechaAbonoImporte)

	  var importeAbonado any
    if strings.EqualFold(alias.RecMonetario, "NO") {
        importeAbonado = nil
    } else {
       
        importeAbonado = alias.RecImporteAbonado
    }

	temp := map[string]interface{}{
		"ID":                     alias.ID,
		"RecDenominacion":        alias.RecDenominacion,
		"RecNumero":              alias.RecNumero,
		"RecFolioAtencion":       alias.RecFolioAtencion,
		"RecEstadoConPend":       alias.RecEstadoConPend,
		"RecTrimestre":           alias.RecTrimestre,
		"RecFechaReclamacion":    alias.RecFechaReclamacion,
		"RecFechaAtencion":       nilIfEmpty(alias.RecFechaAtencion),
		"RecMedioRecepcionCanal": alias.RecMedioRecepcionCanal,
		"RecProductoServicio":    alias.RecProductoServicio,
		"RecCausaMotivo":         alias.RecCausaMotivo,
		"RecFechaResolucion":     nilIfEmpty(alias.RecFechaResolucion),
		"RecFechaNotifiUsuario":  nilIfEmpty(alias.RecFechaNotifiUsuario),
		"RecEntidadFederativa":   alias.RecEntidadFederativa,
		"RecCodigoPostal":        nilIfZeroInt(alias.RecCodigoPostal),
		"RecMunicipioAlcaldia":   alias.RecMunicipioAlcaldia,
		"RecLocalidad":           nilIfZeroInt(alias.RecLocalidad),
		"RecColonia":             nilIfZeroInt(alias.RecColonia),
		"RecMonetario":           alias.RecMonetario,
		"RecMontoReclamado":      nilIfZeroFloat64(alias.RecMontoReclamado),
		"RecImporteAbonado":      importeAbonado,
		"RecFechaAbonoImporte":   nilIfEmpty(alias.RecFechaAbonoImporte),
		"RecPori":                alias.RecPori,
		"RecTipoPersona":         alias.RecTipoPersona,
		"RecSexo":                nilIfEmpty(alias.RecSexo),
		"RecEdad":                nilIfZeroInt(alias.RecEdad),
		"RecSentidoResolucion":   nilIfZeroInt(alias.RecSentidoResolucion),
		"RecNivelAtencion":       nilIfZeroInt(alias.RecNivelAtencion),
		"RecFolioCondusef":       nilIfEmpty(alias.RecFolioCondusef),
		"RecReversa":             alias.RecReversa,
		"Sucursal":               alias.Sucursal,
		"RecSector":              alias.RecSector,
		//"RecOperacionExtranjero": alias.RecOperacionExtranjero,
	}

	return json.Marshal(temp)
}

// UnmarshalJSON personaliza la decodificación JSON de la estructura Reclamacion
func (r *ReclamacionResponse) UnmarshalJSON(data []byte) error {
	type Alias ReclamacionResponse
	aux := &struct {
		*Alias
	}{
		Alias: (*Alias)(r),
	}
	if err := json.Unmarshal(data, &aux); err != nil {
		return err
	}

	// Convertir las fechas de vuelta al formato de la base de datos
	r.RecFechaReclamacion = formatToYYYYMMDD(r.RecFechaReclamacion)
	r.RecFechaAtencion = formatToYYYYMMDD(r.RecFechaAtencion)
	r.RecFechaResolucion = formatToYYYYMMDD(r.RecFechaResolucion)
	r.RecFechaNotifiUsuario = formatToYYYYMMDD(r.RecFechaNotifiUsuario)
	r.RecFechaAbonoImporte = formatToYYYYMMDD(r.RecFechaAbonoImporte)

	return nil
}

// MarshalJSON personaliza la codificación JSON de la estructura Reclamacion
func (r ReclamacionResponseEnv) MarshalJSON() ([]byte, error) {
	type Alias ReclamacionResponseEnv
	alias := Alias(r)

	// Convertir las fechas al formato deseado
	alias.RecFechaReclamacion = formatToDDMMYYYY(r.RecFechaReclamacion)
	alias.RecFechaAtencion = formatToDDMMYYYY(r.RecFechaAtencion)
	alias.RecFechaResolucion = formatToDDMMYYYY(r.RecFechaResolucion)
	alias.RecFechaNotifiUsuario = formatToDDMMYYYY(r.RecFechaNotifiUsuario)
	alias.RecFechaAbonoImporte = formatToDDMMYYYY(r.RecFechaAbonoImporte)

	  var importeAbonado any
    if strings.EqualFold(alias.RecMonetario, "NO") {
        importeAbonado = nil
    } else {
        importeAbonado = alias.RecImporteAbonado
    }


	temp := map[string]interface{}{
		"RecDenominacion":        alias.RecDenominacion,
		"RecNumero":              alias.RecNumero,
		"RecFolioAtencion":       alias.RecFolioAtencion,
		"RecEstadoConPend":       alias.RecEstadoConPend,
		"RecTrimestre":           alias.RecTrimestre,
		"RecFechaReclamacion":    alias.RecFechaReclamacion,
		"RecFechaAtencion":       nilIfEmpty(alias.RecFechaAtencion),
		"RecMedioRecepcionCanal": alias.RecMedioRecepcionCanal,
		"RecProductoServicio":    alias.RecProductoServicio,
		"RecCausaMotivo":         alias.RecCausaMotivo,
		"RecFechaResolucion":     nilIfEmpty(alias.RecFechaResolucion),
		"RecFechaNotifiUsuario":  nilIfEmpty(alias.RecFechaNotifiUsuario),
		"RecEntidadFederativa":   alias.RecEntidadFederativa,
		"RecCodigoPostal":        nilIfZeroInt(alias.RecCodigoPostal),
		"RecMunicipioAlcaldia":   alias.RecMunicipioAlcaldia,
		"RecLocalidad":           nilIfZeroInt(alias.RecLocalidad),
		"RecColonia":             nilIfZeroInt(alias.RecColonia),
		"RecMonetario":           alias.RecMonetario,
		"RecMontoReclamado":      nilIfZeroFloat64(alias.RecMontoReclamado),
		"RecImporteAbonado":   	   importeAbonado,
		"RecFechaAbonoImporte":   nilIfEmpty(alias.RecFechaAbonoImporte),
		"RecPori":                alias.RecPori,
		"RecTipoPersona":         alias.RecTipoPersona,
		"RecSexo":                nilIfEmpty(alias.RecSexo),
		"RecEdad":                nilIfZeroInt(alias.RecEdad),
		"RecSentidoResolucion":   nilIfZeroInt(alias.RecSentidoResolucion),
		"RecNivelAtencion":       nilIfZeroInt(alias.RecNivelAtencion),
		"RecFolioCondusef":       nilIfEmpty(alias.RecFolioCondusef),
		"RecReversa":             alias.RecReversa,
		"RecSector":              alias.RecSector,
		//"RecOperacionExtranjero": alias.RecOperacionExtranjero,
	}

	return json.Marshal(temp)
}

// UnmarshalJSON personaliza la decodificación JSON de la estructura Reclamacion
func (r *ReclamacionResponseEnv) UnmarshalJSON(data []byte) error {
	type Alias ReclamacionResponseEnv
	aux := &struct {
		*Alias
	}{
		Alias: (*Alias)(r),
	}
	if err := json.Unmarshal(data, &aux); err != nil {
		return err
	}

	// Convertir las fechas de vuelta al formato de la base de datos
	r.RecFechaReclamacion = formatToYYYYMMDD(r.RecFechaReclamacion)
	r.RecFechaAtencion = formatToYYYYMMDD(r.RecFechaAtencion)
	r.RecFechaResolucion = formatToYYYYMMDD(r.RecFechaResolucion)
	r.RecFechaNotifiUsuario = formatToYYYYMMDD(r.RecFechaNotifiUsuario)
	r.RecFechaAbonoImporte = formatToYYYYMMDD(r.RecFechaAbonoImporte)

	return nil
}
