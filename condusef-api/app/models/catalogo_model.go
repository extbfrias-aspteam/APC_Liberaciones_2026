package models

import "time"

type Producto struct {
	Id            string `gorm:"column:id; primary_key" json:"productId"`
	TipoOperacion string `gorm:"column:tipo_operacion" json:"tipo_operacion"`
	Producto      string `gorm:"column:producto" json:"product"`
	Subproducto   string `gorm:"column:subproducto" json:"subproduct"`
	Institucion   string `gorm:"column:institucion" json:"institucion"`
}

type Causa struct {
	Id          string `gorm:"column:id; primary_key" json:"causaId"`
	IdProducto  string `gorm:"column:id_producto" json:"productoId"`
	Descripcion string `gorm:"column:descripcion" json:"causa"`
	Institucion string `gorm:"column:institucion" json:"institucion"`
}

type EntidadFed struct {
	Id     int    `gorm:"column:id; primary_key" json:"claveEdo"`
	Nombre string `gorm:"column:nombre" json:"estado"`
}

type Municipio struct {
	IdEntidadFed int    `gorm:"column:id_entidad_fed; primary_key" json:"estadoId"`
	IdMunicipio  int    `gorm:"column:id_municipio; primary_key" json:"municipioId"`
	Municipio    string `gorm:"column:municipio" json:"municipio"`
}

type Colonia struct {
	IdEntidadFed int    `gorm:"column:id_entidad_fed; primary_key" json:"estadoId"`
	IdMunicipio  int    `gorm:"column:id_municipio; primary_key" json:"municipioId"`
	Municipio    string `gorm:"column:municipio; primary_key" json:"municipio"`
	IdColonia    int    `gorm:"column:id_colonia; primary_key" json:"coloniaId"`
	Colonia      string `gorm:"column:colonia" json:"colonia"`
	IdLocalidad  int    `gorm:"column:id_localidad; primary_key" json:"tipoLocalidadId"`
	Localidad    string `gorm:"column:localidad" json:"tipoLocalidad"`
}

type CodigoPostal struct {
	IdEntidadFed int `gorm:"column:id_entidad_fed; primary_key" json:"estadoId"`
	Cp           int `gorm:"column:cp; primary_key" json:"codigo_sepomex"`
}

type MedioRecepcion struct {
	Id     int    `gorm:"column:id; primary_key" json:"medioId"`
	Nombre string `gorm:"column:nombre" json:"medioDsc"`
}

type NivelAtencion struct {
	Id     int    `gorm:"column:id; primary_key" json:"nivelDeAtencionId"`
	Nombre string `gorm:"column:nombre" json:"nivelDeAtencionDsc"`
}

const dateFormat = "02/01/2006"
const dbDateFormat = "2006-01-02"

// Helper function to format date from yyyy-mm-dd to dd/mm/yyyy
func formatToDDMMYYYY(dateStr string) string {
	if dateStr == "" {
		return ""
	}
	parsedDate, err := time.Parse(time.RFC3339, dateStr)
	if err != nil {
		return dateStr
	}
	return parsedDate.Format(dateFormat)
}

// Helper function to format date from dd/mm/yyyy to yyyy-mm-dd
func formatToYYYYMMDD(dateStr string) string {
	if dateStr == "" {
		return ""
	}
	parsedDate, err := time.Parse(dateFormat, dateStr)
	if err != nil {
		return dateStr
	}
	return parsedDate.Format(dbDateFormat)
}
