package models

type Empresa struct {
	Id           int    `gorm:"column:id;primaryKey;autoIncrement" json:"id"`
	Nombre       string `gorm:"column:nombre;not null" json:"nombre"`
	Sector       string `gorm:"column:sector;not null" json:"sector"`
	Denominacion string `gorm:"column:denominacion;not null" json:"denominacion"`
}

func (Empresa) TableName() string {
	return "redeco.cat_empresas"
}
