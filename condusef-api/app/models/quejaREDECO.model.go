package models

type QuejaCondusef struct {
	Sector            string  `gorm:"column:sector" json:"QuejasSector"`
	Denominacion      string  `gorm:"column:denominacion" json:"QuejasDenominacion"`
	NumMes            int     `gorm:"column:num_mes" json:"QuejasNoMes"`
	NumQuejas         int     `gorm:"column:num_quejas" json:"QuejasNum"`
	NumFolio          string  `gorm:"column:num_folio" json:"QuejasFolio"`
	FechaRecepcion    string  `gorm:"column:fecha_recepcion" json:"QuejasFecRecepcion"`
	IdMedioRecepcion  int     `gorm:"column:id_medio_recepcion" json:"QuejasMedio"`
	IdNivelAtencion   int     `gorm:"column:id_nivel_atencion" json:"QuejasNivelAT"`
	IdProducto        string  `gorm:"column:id_producto" json:"QuejasProducto"`
	IdCausa           string  `gorm:"column:id_causa" json:"QuejasCausa"`
	Pori              string  `gorm:"column:pori" json:"QuejasPORI"`
	Estatus           int     `gorm:"column:estatus" json:"QuejasEstatus"`
	IdEntidadFed      int     `gorm:"column:id_entidad_fed" json:"QuejasEstados"`
	IdMunicipio       int     `gorm:"column:id_municipio" json:"QuejasMunId"`
	IdLocalidad       *int    `gorm:"column:id_localidad" json:"QuejasLocId"`
	IdColonia         int     `gorm:"column:id_colonia" json:"QuejasColId"`
	CodigoPostal      int     `gorm:"column:cod_postal" json:"QuejasCP"`
	IdTipoPersona     int     `gorm:"column:id_tipo_persona" json:"QuejasTipoPersona"`
	PersonaSexo       *string `gorm:"column:persona_sexo" json:"QuejasSexo"`
	PersonaEdad       *int    `gorm:"column:persona_edad" json:"QuejasEdad"`
	FechaResolucion   *string `gorm:"column:fecha_resolucion" json:"QuejasFecResolucion"`
	FechaNotificacion *string `gorm:"column:fecha_notificacion" json:"QuejasFecNotificacion"`
	IdRespuestaRes    *int    `gorm:"column:id_respuesta_res" json:"QuejasRespuesta"`
	NumPenalizacion   *int    `gorm:"column:num_penalizacion" json:"QuejasNumPenal"`
	IdPenalizacion    *int    `gorm:"column:id_penalizacion" json:"QuejasPenalizacion"`
}
