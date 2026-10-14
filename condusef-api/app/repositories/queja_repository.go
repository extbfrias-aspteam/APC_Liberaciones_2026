package repositories

import (
	"condusef-api/app/database"
	"condusef-api/app/models"

	"errors"
)

// ObtenerQuejasPaginado
func ObtenerQuejasPaginado(numMes, idEmpresa, offset, limit int, filter string) ([]models.Queja, int64, error) {
	var quejas []models.Queja
	var total int64

	sql := `SELECT q.nombre_cliente, q.correo_cliente, q.telefono_cliente, q.sucursal, q.num_credito, q.despacho_cobranza, q.num_mes, q.num_quejas, q.num_folio, TO_CHAR(q.fecha_recepcion, 'dd/mm/yyyy') as fecha_recepcion, 
	               q.id_medio_recepcion, q.id_nivel_atencion, q.id_producto, p.producto, q.id_causa, c.descripcion as causa, q.pori, q.estatus, 
				   q.id_entidad_fed, q.id_municipio, q.id_localidad, q.id_colonia, q.cod_postal, id_tipo_persona, q.persona_sexo, q.persona_edad, TO_CHAR(q.fecha_resolucion, 'dd/mm/yyyy') as fecha_resolucion, 
				   TO_CHAR(q.fecha_notificacion, 'dd/mm/yyyy') as fecha_notificacion, q.id_respuesta_res, q.num_penalizacion, q.id_penalizacion, q.reportado, q.id_empresa
			  FROM redeco.quejas q 
				INNER JOIN redeco.cat_productos p ON p.id = q.id_producto
				INNER JOIN redeco.cat_causas c ON c.id = q.id_causa and c.id_producto = q.id_producto
			 WHERE q.num_mes = ? AND id_empresa = ? LIMIT ? OFFSET ?`

	if result := database.DB.Raw(sql, numMes, idEmpresa, limit, (offset-1)*limit).Scan(&quejas); result.Error != nil {
		return nil, 0, result.Error
	}

	database.DB.Model(&models.Queja{}).Where("num_mes = ?", numMes).Count(&total)

	return quejas, total, nil
}

// ObtenerQuejaPorFolio
func ObtenerQuejaPorFolio(folio string) (models.QuejaCondusef, error) {
	var queja models.QuejaCondusef

	sql := `SELECT q.num_mes, q.num_quejas, q.num_folio, TO_CHAR(q.fecha_recepcion, 'dd/mm/yyyy') as fecha_recepcion, q.id_medio_recepcion, q.id_nivel_atencion, q.id_producto,
				   q.id_causa, q.pori, q.estatus, q.id_entidad_fed, q.id_municipio, q.id_localidad, q.id_colonia, q.cod_postal, q.id_tipo_persona, q.persona_sexo, q.persona_edad,
				   TO_CHAR(q.fecha_resolucion, 'dd/mm/yyyy') as fecha_resolucion, TO_CHAR(q.fecha_notificacion, 'dd/mm/yyyy') as fecha_notificacion, q.id_respuesta_res, q.num_penalizacion,
				   q.id_penalizacion, e.sector, e.denominacion
			FROM redeco.quejas q
				INNER JOIN redeco.cat_empresas e on e.id = q.id_empresa
			WHERE q.num_folio = ?`

	if result := database.DB.Raw(sql, folio).Find(&queja); result.Error != nil {
		return models.QuejaCondusef{}, result.Error
	}

	return queja, nil
}

func ObtenerQuejasPorFolio(folios []string, id_empresa int) ([]models.QuejaCondusef, error) {
	var quejas []models.QuejaCondusef

	sql := `SELECT q.num_mes, q.num_quejas, q.num_folio, TO_CHAR(q.fecha_recepcion, 'dd/mm/yyyy') as fecha_recepcion, q.id_medio_recepcion, q.id_nivel_atencion, q.id_producto, 
				   q.id_causa, q.pori, q.estatus, q.id_entidad_fed, q.id_municipio, q.id_localidad, q.id_colonia, q.cod_postal, q.id_tipo_persona, q.persona_sexo, q.persona_edad, 
				   TO_CHAR(q.fecha_resolucion, 'dd/mm/yyyy') as fecha_resolucion, TO_CHAR(q.fecha_notificacion, 'dd/mm/yyyy') as fecha_notificacion, q.id_respuesta_res, q.num_penalizacion, 
				   q.id_penalizacion, e.sector, e.denominacion
			FROM redeco.quejas q
				INNER JOIN redeco.cat_empresas e on e.id = q.id_empresa 
			WHERE q.id_empresa = ? AND q.num_folio IN (?)`

	if result := database.DB.Raw(sql, id_empresa, folios).Find(&quejas); result.Error != nil {
		return []models.QuejaCondusef{}, result.Error
	}

	return quejas, nil
}

// ObtenerQuejasSinReportar
func ObtenerQuejasSinReportar(numMes, idEmpresa int) ([]models.QuejaCondusef, error) {
	var quejas []models.QuejaCondusef

	sql := `SELECT q.num_mes, q.num_quejas, q.num_folio, TO_CHAR(q.fecha_recepcion, 'dd/mm/yyyy') as fecha_recepcion, q.id_medio_recepcion, q.id_nivel_atencion, q.id_producto, 
				   q.id_causa, q.pori, q.estatus, q.id_entidad_fed, q.id_municipio, q.id_localidad, q.id_colonia, q.cod_postal, q.id_tipo_persona, q.persona_sexo, q.persona_edad, 
				   TO_CHAR(q.fecha_resolucion, 'dd/mm/yyyy') as fecha_resolucion, TO_CHAR(q.fecha_notificacion, 'dd/mm/yyyy') as fecha_notificacion, q.id_respuesta_res, q.num_penalizacion, 
				   q.id_penalizacion, e.sector, e.denominacion
			FROM redeco.quejas q 
				INNER JOIN redeco.cat_empresas e on e.id = q.id_empresa 
			WHERE q.num_mes = ? AND q.reportado = false AND q.id_empresa = ?`

	if result := database.DB.Raw(sql, numMes, idEmpresa).Find(&quejas); result.Error != nil {
		return nil, result.Error
	}

	return quejas, nil
}

// CrearQueja
func CrearQueja(queja *models.Queja) error {
	if result := database.DB.Create(&queja); result.Error != nil {
		return result.Error
	}

	return nil
}

// ModificarQueja
func ModificarQueja(queja *models.Queja) error {
	result := database.DB.Model(models.Queja{}).Where("num_folio = ?", queja.NumFolio).Updates(map[string]interface{}{
		"nombre_cliente":     queja.NombreCliente,
		"correo_cliente":     queja.CorreoCliente,
		"telefono_cliente":   queja.TelefonoCliente,
		"sucursal":           queja.Sucursal,
		"num_credito":        queja.NumCredito,
		"despacho_cobranza":  queja.DespachoCobranza,
		"num_mes":            queja.NumMes,
		"num_quejas":         queja.NumQuejas,
		"fecha_recepcion":    queja.FechaRecepcion,
		"id_medio_recepcion": queja.IdMedioRecepcion,
		"id_nivel_atencion":  queja.IdNivelAtencion,
		"id_producto":        queja.IdProducto,
		"id_causa":           queja.IdCausa,
		"pori":               queja.Pori,
		"estatus":            queja.Estatus,
		"id_entidad_fed":     queja.IdEntidadFed,
		"id_municipio":       queja.IdMunicipio,
		"id_localidad":       queja.IdLocalidad,
		"id_colonia":         queja.IdColonia,
		"cod_postal":         queja.CodPostal,
		"id_tipo_persona":    queja.IdTipoPersona,
		"persona_sexo":       queja.PersonaSexo,
		"persona_edad":       queja.PersonaEdad,
		"fecha_resolucion":   queja.FechaResolucion,
		"fecha_notificacion": queja.FechaNotificacion,
		"id_respuesta_res":   queja.IdRespuestaRes,
		"num_penalizacion":   queja.NumPenalizacion,
		"id_penalizacion":    queja.IdPenalizacion,
	})

	if result.Error != nil {
		return result.Error
	}

	if result.RowsAffected == 0 {
		return errors.New("ningún registro fue afectado")
	}

	return nil
}

// EliminarQueja
func EliminarQueja(folio string) error {
	if result := database.DB.Delete(models.Queja{}, "num_folio = ?", folio); result.Error != nil {
		return result.Error
	}

	return nil
}

// ModificarEstadoReportadoPorFolio
func ModificarEstadoReportadoPorFolio(folios []string, value bool) error {
	result := database.DB.Model(models.Queja{}).Where("num_folio in ?", folios).Updates(map[string]interface{}{
		"reportado": value,
	})

	if result.Error != nil {
		return result.Error
	}

	if result.RowsAffected == 0 {
		return errors.New("ningun registro fue afectado")
	}

	return nil
}
