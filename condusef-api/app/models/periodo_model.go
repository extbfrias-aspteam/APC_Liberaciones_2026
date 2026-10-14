package models

import "time"

type Periodo struct {
	Mes          int        `gorm:"column:mes; primary_key" json:"mes"`
	Anio         int        `gorm:"column:anio; primary_key" json:"anio"`
	FechaInicial string     `gorm:"column:fecha_inicial" json:"fecha_inicial"`
	FechaFinal   string     `gorm:"column:fecha_final" json:"fecha_final"`
	Activo       bool       `gorm:"column:activo" json:"activo"`
	FechaCierre  *time.Time `gorm:"column:fecha_cierre" json:"fecha_cierre"`
	IdEmpresa    *int       `gorm:"column:id_empresa" json:"id_empresa"`
}
