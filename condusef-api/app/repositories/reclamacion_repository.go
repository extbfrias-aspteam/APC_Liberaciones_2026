package repositories

import (
	"condusef-api/app/database"
	"condusef-api/app/models"
	"errors"
	"strings"
)

func ObtenerReclamacionPorFolio(folio string) (models.ReclamacionResponse, error) {
	var reclamacion models.Reclamacion

	if result := database.DB.First(&reclamacion, "folio = ?", folio); result.Error != nil {
		return models.ReclamacionResponse{}, result.Error
	}

	return ConvertToReclamacionResponse(reclamacion), nil
}

func ObtenerReclamacionesPorFolios(folios []string, id_empresa int) ([]models.ReclamacionResponse, error) {
	var reclamaciones []models.Reclamacion

	if result := database.DB.Preload("Empresa").Where("id_empresa = ? AND folio IN ?", id_empresa, folios).Find(&reclamaciones); result.Error != nil {
		return nil, result.Error
	}

	return ConvertToReclamacionResponses(reclamaciones), nil
}

func ObtenerReclamacionesPorFoliosEnv(folios []string, id_empresa int) ([]models.ReclamacionResponseEnv, error) {
	var reclamaciones []models.Reclamacion

	if result := database.DB.Preload("Empresa").Where("id_empresa = ? AND folio IN ?", id_empresa, folios).Find(&reclamaciones); result.Error != nil {
		return nil, result.Error
	}

	return ConvertToReclamacionResponsesEnv(reclamaciones), nil
}

func ConvertToReclamacionResponses(reclamaciones []models.Reclamacion) []models.ReclamacionResponse {
	var responses []models.ReclamacionResponse
	for _, reclamacion := range reclamaciones {
		response := ConvertToReclamacionResponse(reclamacion)
		responses = append(responses, response)
	}
	return responses
}

func ConvertToReclamacionResponsesEnv(reclamaciones []models.Reclamacion) []models.ReclamacionResponseEnv {
	var responses []models.ReclamacionResponseEnv
	for _, reclamacion := range reclamaciones {
		response := ConvertToReclamacionResponseEnv(reclamacion)
		responses = append(responses, response)
	}
	return responses
}

func ConvertToReclamacionResponse(reclamacion models.Reclamacion) models.ReclamacionResponse {

    var importeAbonado *float64
    if strings.EqualFold(reclamacion.RecMonetario, "NO") {
        importeAbonado = nil
    } else {
        importeAbonado = reclamacion.RecImporteAbonado 
    }

    return models.ReclamacionResponse{
        ID:                     reclamacion.ID,
        RecTrimestre:           reclamacion.RecTrimestre,
        RecNumero:              reclamacion.RecNumero,
        RecFolioAtencion:       reclamacion.RecFolioAtencion,
        RecEstadoConPend:       reclamacion.RecEstadoConPend,
        RecFechaReclamacion:    reclamacion.RecFechaReclamacion,
        RecFechaAtencion:       reclamacion.RecFechaAtencion,
        RecMedioRecepcionCanal: reclamacion.RecMedioRecepcionCanal,
        RecProductoServicio:    reclamacion.RecProductoServicio,
        RecCausaMotivo:         reclamacion.IdCausa,
        RecFechaResolucion:     reclamacion.RecFechaResolucion,
        RecFechaNotifiUsuario:  reclamacion.RecFechaNotifiUsuario,
        RecEntidadFederativa:   reclamacion.RecEntidadFederativa,
        RecCodigoPostal:        reclamacion.RecCodigoPostal,
        RecMunicipioAlcaldia:   reclamacion.RecMunicipioAlcaldia,
        RecLocalidad:           reclamacion.RecLocalidad,
        RecColonia:             reclamacion.RecColonia,
        RecMonetario:           reclamacion.RecMonetario,
        RecMontoReclamado:      reclamacion.RecMontoReclamado,
        RecImporteAbonado:      importeAbonado, // <--- regla aplicada
        RecFechaAbonoImporte:   reclamacion.RecFechaAbonoImporte,
        RecPori:                reclamacion.RecPori,
        RecTipoPersona:         reclamacion.RecTipoPersona,
        RecSexo:                reclamacion.RecSexo,
        RecEdad:                reclamacion.RecEdad,
        RecSentidoResolucion:   reclamacion.RecSentidoResolucion,
        RecNivelAtencion:       reclamacion.RecNivelAtencion,
        RecFolioCondusef:       reclamacion.RecFolioCondusef,
        RecReversa:             reclamacion.RecReversa,
        Sucursal:               reclamacion.Sucursal,
        IdEmpresa:              reclamacion.IdEmpresa,
        RecSector:              reclamacion.Empresa.Sector,
        RecDenominacion:        reclamacion.Empresa.Denominacion,
    }
}

func ConvertToReclamacionResponseEnv(reclamacion models.Reclamacion) models.ReclamacionResponseEnv {

    var importeAbonado *float64
    if strings.EqualFold(reclamacion.RecMonetario, "NO") {
        importeAbonado = nil
    } else {
        importeAbonado = reclamacion.RecImporteAbonado
    }

    return models.ReclamacionResponseEnv{
        RecTrimestre:           reclamacion.RecTrimestre,
        RecNumero:              reclamacion.RecNumero,
        RecFolioAtencion:       reclamacion.RecFolioAtencion,
        RecEstadoConPend:       reclamacion.RecEstadoConPend,
        RecFechaReclamacion:    reclamacion.RecFechaReclamacion,
        RecFechaAtencion:       reclamacion.RecFechaAtencion,
        RecMedioRecepcionCanal: reclamacion.RecMedioRecepcionCanal,
        RecProductoServicio:    reclamacion.RecProductoServicio,
        RecCausaMotivo:         agregarCeros(reclamacion.IdCausa),
        RecFechaResolucion:     reclamacion.RecFechaResolucion,
        RecFechaNotifiUsuario:  reclamacion.RecFechaNotifiUsuario,
        RecEntidadFederativa:   reclamacion.RecEntidadFederativa,
        RecCodigoPostal:        reclamacion.RecCodigoPostal,
        RecMunicipioAlcaldia:   reclamacion.RecMunicipioAlcaldia,
        RecLocalidad:           reclamacion.RecLocalidad,
        RecColonia:             reclamacion.RecColonia,
        RecMonetario:           reclamacion.RecMonetario,
        RecMontoReclamado:      reclamacion.RecMontoReclamado,
        RecImporteAbonado:      importeAbonado, 
        RecFechaAbonoImporte:   reclamacion.RecFechaAbonoImporte,
        RecPori:                reclamacion.RecPori,
        RecTipoPersona:         reclamacion.RecTipoPersona,
        RecSexo:                reclamacion.RecSexo,
        RecEdad:                reclamacion.RecEdad,
        RecSentidoResolucion:   reclamacion.RecSentidoResolucion,
        RecNivelAtencion:       reclamacion.RecNivelAtencion,
        RecFolioCondusef:       reclamacion.RecFolioCondusef,
        RecReversa:             reclamacion.RecReversa,
        RecSector:              reclamacion.Empresa.Sector,
        RecDenominacion:        reclamacion.Empresa.Denominacion,
    }
}

func CrearReclamacion(reclamacion *models.Reclamacion) error {
	var omit []string
	if len(reclamacion.RecFechaAtencion) < 1 {
		omit = append(omit, "RecFechaAtencion")
	}
	if len(reclamacion.RecFechaResolucion) < 1 {
		omit = append(omit, "RecFechaResolucion")
	}
	if len(reclamacion.RecFechaNotifiUsuario) < 1 {
		omit = append(omit, "RecFechaNotifiUsuario")
	}
	if len(reclamacion.RecFechaAbonoImporte) < 1 {
		omit = append(omit, "RecFechaAbonoImporte")
	}
	if len(omit) > 0 {
		if result := database.DB.Omit(omit...).Create(&reclamacion); result.Error != nil {
			return result.Error
		}
	} else if result := database.DB.Create(&reclamacion); result.Error != nil {
		return result.Error
	}

	return nil
}

func ModificarReclamacion(reclamacion *models.Reclamacion) error {
	var omit []string
	if len(reclamacion.RecFechaAtencion) < 1 {
		omit = append(omit, "RecFechaAtencion")
	}
	if len(reclamacion.RecFechaResolucion) < 1 {
		omit = append(omit, "RecFechaResolucion")
	}
	if len(reclamacion.RecFechaNotifiUsuario) < 1 {
		omit = append(omit, "RecFechaNotifiUsuario")
	}
	if len(reclamacion.RecFechaAbonoImporte) < 1 {
		omit = append(omit, "RecFechaAbonoImporte")
	}
	result := database.DB. Model(&models.Reclamacion{}).Omit(omit...).Where("id = ?", reclamacion.ID).Updates(map[string]interface{}{
		"rec_numero":                reclamacion.RecNumero,
		"folio":                     reclamacion.RecFolioAtencion,
		"estatus":                   reclamacion.RecEstadoConPend,
		"rec_fecha_reclamacion":     reclamacion.RecFechaReclamacion,
		"rec_fecha_atencion":        reclamacion.RecFechaAtencion,
		"rec_medio_recepcion_canal": reclamacion.RecMedioRecepcionCanal,
		"id_producto":               reclamacion.RecProductoServicio,
		"id_causa":                  reclamacion.IdCausa,
		"rec_fecha_resolucion":      reclamacion.RecFechaResolucion,
		"rec_fecha_notifi_usuario":  reclamacion.RecFechaNotifiUsuario,
		"id_rec_entidad_federativa": reclamacion.RecEntidadFederativa,
		"rec_codigo_postal":         reclamacion.RecCodigoPostal,
		"id_rec_municipio_alcaldia": reclamacion.RecMunicipioAlcaldia,
		"id_rec_localidad":          reclamacion.RecLocalidad,
		"id_rec_colonia":            reclamacion.RecColonia,
		"rec_monetario":             reclamacion.RecMonetario,
		"rec_monto_reclamado":       reclamacion.RecMontoReclamado,
		"rec_importe_abonado":       reclamacion.RecImporteAbonado,
		"rec_fecha_abono_importe":   reclamacion.RecFechaAbonoImporte,
		"rec_pori":                  reclamacion.RecPori,
		"rec_tipo_persona":          reclamacion.RecTipoPersona,
		"rec_sexo":                  reclamacion.RecSexo,
		"rec_edad":                  reclamacion.RecEdad,
		"rec_sentido_resolucion":    reclamacion.RecSentidoResolucion,
		"id_nivel_atencion":         reclamacion.RecNivelAtencion,
		"rec_folio_condusef":        reclamacion.RecFolioCondusef,
		"rec_reversa":               reclamacion.RecReversa,
		"sucursal":                  reclamacion.Sucursal,
		//"rec_operacion_extranjero":  reclamacion.RecOperacionExtranjero,
	})

	if result.Error != nil {
		return result.Error
	}

	if result.RowsAffected == 0 {
		return errors.New("ningún registro fue afectado")
	}

	return nil
}
func MarcarReclamacionesReportadas(folios []string) error {
	result := database.DB.Model(&models.Reclamacion{}).Where("folio IN ?", folios).Update("reportado", true)

	if result.Error != nil {
		return result.Error
	}

	if result.RowsAffected == 0 {
		return errors.New("ningún registro fue afectado")
	}

	return nil
}

// EliminarReclamacion
func EliminarReclamacion(folio string) error {
	if result := database.DB.Delete(models.Reclamacion{}, "folio = ?", folio); result.Error != nil {
		return result.Error
	}

	return nil
}
