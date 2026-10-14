package models

import (
	"encoding/json"
	"strings"
)

type Consulta struct {
	ID                      int     `gorm:"column:id;primaryKey;autoIncrement" json:"Id"`
	ConsultasTrim           int     `gorm:"column:periodo;not null" json:"ConsultasTrim"`
	NumConsultas            int     `gorm:"column:num_consultas;not null" json:"NumConsultas"`
	ConsultasFolio          string  `gorm:"column:folio;not null" json:"ConsultasFolio"`
	ConsultasEstatusCon     int     `gorm:"column:estatus;not null" json:"ConsultasEstatusCon"`
	ConsultasFecAten        string  `gorm:"column:consultas_fec_aten" json:"ConsultasFecAten"`
	IdEstados               int     `gorm:"column:id_estados;not null" json:"EstadosId"`
	ConsultasFecRecepcion   string  `gorm:"column:consultas_fec_recepcion;not null" json:"ConsultasFecRecepcion"`
	IdMedios                int     `gorm:"column:id_medios;not null" json:"MediosId"`
	IdProducto              string  `gorm:"column:id_producto;not null" json:"Producto"`
	IdCausa                 string  `gorm:"column:id_causa;not null" json:"CausaId"`
	ConsultasCp             int     `gorm:"column:consultas_cp" json:"ConsultasCP"`
	IdConsultasMpio         int     `gorm:"column:id_consultas_mpio;not null" json:"ConsultasMpioId"`
	IdConsultasLoc          int     `gorm:"column:id_consultas_loc" json:"ConsultasLocId"`
	IdConsultasCol          int     `gorm:"column:id_consultas_col" json:"ConsultasColId"`
	IdConsultasCatNivelAten int     `gorm:"column:id_consultascatnivelaten" json:"ConsultascatnivelatenId"`
	ConsultasPori           string  `gorm:"column:consultas_pori;not null" json:"ConsultasPori"`
	Sucursal                int     `gorm:"column:sucursal;not null" json:"Sucursal"`
	IdEmpresa               int     `gorm:"column:id_empresa" json:"idEmpresa"`
	Empresa                 Empresa `gorm:"foreignKey:IdEmpresa" json:"-"`
}

type ConsultaResponse struct {
	ID                      int    `json:"Id"`
	ConsultasTrim           int    `json:"ConsultasTrim"`
	NumConsultas            int    `json:"NumConsultas"`
	ConsultasFolio          string `json:"ConsultasFolio"`
	ConsultasEstatusCon     int    `json:"ConsultasEstatusCon"`
	ConsultasFecAten        string `json:"ConsultasFecAten"`
	EstadosId               int    `json:"EstadosId"`
	ConsultasFecRecepcion   string `json:"ConsultasFecRecepcion"`
	MediosId                int    `json:"MediosId"`
	Producto                string `json:"Producto"`
	CausaId                 string `json:"CausaId"`
	ConsultasCP             int    `json:"ConsultasCP"`
	ConsultasMpioId         int    `json:"ConsultasMpioId"`
	ConsultasLocId          int    `json:"ConsultasLocId"`
	ConsultasColId          int    `json:"ConsultasColId"`
	ConsultascatnivelatenId int    `json:"ConsultascatnivelatenId"`
	ConsultasPori           string `json:"ConsultasPori"`
	Sucursal                int    `json:"Sucursal"`
	IdEmpresa               int    `json:"idEmpresa"`
	ConsultasSector         string `json:"Sector"`
	ConsultasDenominacion   string `json:"InstitucionClave"`
}

type ConsultaResponseEnv struct {
	ConsultasTrim           int    `json:"ConsultasTrim"`
	NumConsultas            int    `json:"NumConsultas"`
	ConsultasFolio          string `json:"ConsultasFolio"`
	ConsultasEstatusCon     int    `json:"ConsultasEstatusCon"`
	ConsultasFecAten        string `json:"ConsultasFecAten"`
	EstadosId               int    `json:"EstadosId"`
	ConsultasFecRecepcion   string `json:"ConsultasFecRecepcion"`
	MediosId                int    `json:"MediosId"`
	Producto                string `json:"Producto"`
	CausaId                 string `json:"CausaId"`
	ConsultasCP             int    `json:"ConsultasCP"`
	ConsultasMpioId         int    `json:"ConsultasMpioId"`
	ConsultasLocId          int    `json:"ConsultasLocId"`
	ConsultasColId          int    `json:"ConsultasColId"`
	ConsultascatnivelatenId int    `json:"ConsultascatnivelatenId"`
	ConsultasPori           string `json:"ConsultasPori"`
	// IdEmpresa               int    `json:"idEmpresa"`
	ConsultasSector       string `json:"Sector"`
	ConsultasDenominacion string `json:"InstitucionClave"`
}

func (Consulta) TableName() string {
	return "reune.consultas"
}

// MarshalJSON personaliza la codificación JSON de la estructura
func (c ConsultaResponse) MarshalJSON() ([]byte, error) {
	type Alias ConsultaResponse
	alias := Alias(c)

	alias.ConsultasFecAten = formatToDDMMYYYY(c.ConsultasFecAten)
	alias.ConsultasFecRecepcion = formatToDDMMYYYY(c.ConsultasFecRecepcion)

	temp := map[string]interface{}{
		"Id":                      alias.ID,
		"ConsultasTrim":           alias.ConsultasTrim,
		"NumConsultas":            alias.NumConsultas,
		"ConsultasFolio":          alias.ConsultasFolio,
		"ConsultasEstatusCon":     alias.ConsultasEstatusCon,
		"ConsultasFecAten":        nilIfEmpty(alias.ConsultasFecAten),
		"EstadosId":               alias.EstadosId,
		"ConsultasFecRecepcion":   alias.ConsultasFecRecepcion,
		"MediosId":                alias.MediosId,
		"Producto":                alias.Producto,
		"CausaId":                 alias.CausaId,
		"ConsultasCP":             alias.ConsultasCP,
		"ConsultasMpioId":         alias.ConsultasMpioId,
		"ConsultasLocId":          alias.ConsultasLocId,
		"ConsultasColId":          alias.ConsultasColId,
		"ConsultascatnivelatenId": nilIfZero(alias.ConsultascatnivelatenId),
		"ConsultasPori":           alias.ConsultasPori,
		"Sucursal":                alias.Sucursal,
		"Sector":                  alias.ConsultasSector,
		"InstitucionClave":        alias.ConsultasDenominacion,
	}

	// Marshal the data as JSON
	jsonData, err := json.Marshal(temp)
	if err != nil {
		return nil, err
	}

	// Convert the JSON to a string and replace the escaped ampersand
	jsonString := string(jsonData)
	jsonString = strings.ReplaceAll(jsonString, `\u0026`, "&")

	// Convert back to a byte slice and return
	return []byte(jsonString), nil
}

// UnmarshalJSON personaliza la decodificación JSON de la estructura
func (c *ConsultaResponse) UnmarshalJSON(data []byte) error {
	type Alias ConsultaResponse
	aux := &struct {
		*Alias
	}{
		Alias: (*Alias)(c),
	}
	if err := json.Unmarshal(data, &aux); err != nil {
		return err
	}

	c.ConsultasFecAten = formatToYYYYMMDD(c.ConsultasFecAten)
	c.ConsultasFecRecepcion = formatToYYYYMMDD(c.ConsultasFecRecepcion)

	return nil
}

// MarshalJSON personaliza la codificación JSON de la estructura
func (c ConsultaResponseEnv) MarshalJSON() ([]byte, error) {
	type Alias ConsultaResponseEnv
	alias := Alias(c)

	alias.ConsultasFecAten = formatToDDMMYYYY(c.ConsultasFecAten)
	alias.ConsultasFecRecepcion = formatToDDMMYYYY(c.ConsultasFecRecepcion)

	temp := map[string]interface{}{
		// "Id":                      alias.ID,
		"ConsultasTrim":           alias.ConsultasTrim,
		"NumConsultas":            alias.NumConsultas,
		"ConsultasFolio":          alias.ConsultasFolio,
		"ConsultasEstatusCon":     alias.ConsultasEstatusCon,
		"ConsultasFecAten":        nilIfEmpty(alias.ConsultasFecAten),
		"EstadosId":               alias.EstadosId,
		"ConsultasFecRecepcion":   alias.ConsultasFecRecepcion,
		"MediosId":                alias.MediosId,
		"Producto":                alias.Producto,
		"CausaId":                 alias.CausaId,
		"ConsultasCP":             alias.ConsultasCP,
		"ConsultasMpioId":         alias.ConsultasMpioId,
		"ConsultasLocId":          alias.ConsultasLocId,
		"ConsultasColId":          alias.ConsultasColId,
		"ConsultascatnivelatenId": nilIfZero(alias.ConsultascatnivelatenId),
		"ConsultasPori":           alias.ConsultasPori,
		// "Sucursal":                alias.Sucursal,
		"Sector":           alias.ConsultasSector,
		"InstitucionClave": alias.ConsultasDenominacion,
	}

	// Marshal the data as JSON
	jsonData, err := json.Marshal(temp)
	if err != nil {
		return nil, err
	}

	// Convert the JSON to a string and replace the escaped ampersand
	jsonString := string(jsonData)
	jsonString = strings.ReplaceAll(jsonString, `\u0026`, "&")

	// Convert back to a byte slice and return
	return []byte(jsonString), nil
}

// UnmarshalJSON personaliza la decodificación JSON de la estructura
func (c *ConsultaResponseEnv) UnmarshalJSON(data []byte) error {
	type Alias ConsultaResponseEnv
	aux := &struct {
		*Alias
	}{
		Alias: (*Alias)(c),
	}
	if err := json.Unmarshal(data, &aux); err != nil {
		return err
	}

	c.ConsultasFecAten = formatToYYYYMMDD(c.ConsultasFecAten)
	c.ConsultasFecRecepcion = formatToYYYYMMDD(c.ConsultasFecRecepcion)

	return nil
}
