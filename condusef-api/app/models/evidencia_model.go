package models

import "time"

type Evidencia struct {
	Id            string    `gorm:"column:id; primary_key" json:"id"`
	NumFolio      string    `gorm:"column:num_folio" json:"numFolio"`
	NombreDoc     string    `gorm:"column:nombre_doc" json:"nombreDoc"`
	FechaCreacion time.Time `gorm:"column:fecha_creacion" json:"fechaCreacion"`
}

func (Evidencia) TableName() string {
	return "redeco.evidencias"
}
