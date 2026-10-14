package models

type Queja struct {
	NombreCliente     string  `gorm:"column:nombre_cliente" json:"nombreCliente"`
	CorreoCliente     *string `gorm:"column:correo_cliente" json:"correoCliente"`
	TelefonoCliente   *string `gorm:"column:telefono_cliente" json:"telefonoCliente"`
	Sucursal          int     `gorm:"column:sucursal" json:"sucursal"`
	NumCredito        *string `gorm:"column:num_credito" json:"numCredito"`
	DespachoCobranza  *string `gorm:"column:despacho_cobranza" json:"despachoCobranza"`
	NumMes            int     `gorm:"column:num_mes" json:"numMes"`
	NumQuejas         int     `gorm:"column:num_quejas" json:"numQuejas"`
	NumFolio          string  `gorm:"column:num_folio; primary_key" json:"numFolio"`
	FechaRecepcion    string  `gorm:"column:fecha_recepcion" json:"fechaRecepcion"`
	IdMedioRecepcion  int     `gorm:"column:id_medio_recepcion" json:"idMedioRecepcion"`
	IdNivelAtencion   int     `gorm:"column:id_nivel_atencion" json:"idNivelAtencion"`
	IdProducto        string  `gorm:"column:id_producto" json:"idProducto"`
	Producto          string  `gorm:"->" json:"producto"`
	IdCausa           string  `gorm:"column:id_causa" json:"idCausa"`
	Causa             string  `gorm:"->" json:"causa"`
	Pori              string  `gorm:"column:pori" json:"pori"`
	Estatus           int     `gorm:"column:estatus" json:"estatus"`
	IdEntidadFed      int     `gorm:"column:id_entidad_fed" json:"idEntidadFed"`
	IdMunicipio       int     `gorm:"column:id_municipio" json:"idMunicipio"`
	IdLocalidad       *int    `gorm:"column:id_localidad" json:"idLocalidad"`
	IdColonia         int     `gorm:"column:id_colonia" json:"idColonia"`
	CodPostal         int     `gorm:"column:cod_postal" json:"codPostal"`
	IdTipoPersona     int     `gorm:"column:id_tipo_persona" json:"idTipoPersona"`
	PersonaSexo       *string `gorm:"column:persona_sexo" json:"personaSexo"`
	PersonaEdad       *int    `gorm:"column:persona_edad" json:"personaEdad"`
	FechaResolucion   *string `gorm:"column:fecha_resolucion" json:"fechaResolucion"`
	FechaNotificacion *string `gorm:"column:fecha_notificacion" json:"fechaNotificacion"`
	IdRespuestaRes    *int    `gorm:"column:id_respuesta_res" json:"idRespuestaRes"`
	NumPenalizacion   *int    `gorm:"column:num_penalizacion" json:"numPenalizacion"`
	IdPenalizacion    *int    `gorm:"column:id_penalizacion" json:"idPenalizacion"`
	Reportado         bool    `gorm:"column:reportado" json:"reportado"`
	IdEmpresa         int     `gorm:"column:id_empresa" json:"idEmpresa"`
}

func (Queja) TableName() string {
	return "redeco.quejas"
}

type PagedResponse struct {
	Total int64       `json:"total"`
	Items interface{} `json:"items"`
}
