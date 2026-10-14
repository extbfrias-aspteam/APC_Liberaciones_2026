package models

type Folio struct {
	ReferenceID string `json:"referenceID"`
}

type Consecutivo struct {
	Id          string `gorm:"column:id; primary_key" json:"id"`
	Mask        string `gorm:"column:mask; primary_key" json:"mask"`
	Consecutivo int    `gorm:"column:consecutivo" json:"consecutivo"`
}

func (Consecutivo) TableName() string {
	return "redeco.cat_consecutivos"
}
