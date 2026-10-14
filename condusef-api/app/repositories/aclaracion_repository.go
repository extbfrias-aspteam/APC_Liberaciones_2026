package repositories

import (
	"condusef-api/app/database"
	"condusef-api/app/models"
	"errors"
)

func ConvertToAclaracionResponse(aclaracion models.Aclaracion) models.AclaracionResponse {
	return models.AclaracionResponse{
		ID:                            aclaracion.ID,
		AclaracionTrimestre:           aclaracion.AclaracionTrimestre,
		AclaracionNumero:              aclaracion.AclaracionNumero,
		AclaracionFolioAtencion:       aclaracion.AclaracionFolioAtencion,
		AclaracionEstadoConPend:       aclaracion.AclaracionEstadoConPend,
		AclaracionFechaAclaracion:     aclaracion.AclaracionFechaAclaracion,
		AclaracionFechaAtencion:       aclaracion.AclaracionFechaAtencion,
		AclaracionMedioRecepcionCanal: aclaracion.AclaracionMedioRecepcionCanal,
		AclaracionProductoServicio:    aclaracion.IdProducto,
		AclaracionCausaMotivo:         aclaracion.IdCausa,
		AclaracionFechaResolucion:     aclaracion.AclaracionFechaResolucion,
		AclaracionFechaNotifiUsuario:  aclaracion.AclaracionFechaNotifiUsuario,
		AclaracionEntidadFederativa:   aclaracion.IdAclaracionEntidadFederativa,
		AclaracionCodigoPostal:        aclaracion.AclaracionCodigoPostal,
		AclaracionMunicipioAlcaldia:   aclaracion.IdAclaracionMunicipioAlcaldia,
		AclaracionLocalidad:           aclaracion.IdAclaracionLocalidad,
		AclaracionColonia:             aclaracion.IdAclaracionColonia,
		AclaracionMonetario:           aclaracion.AclaracionMonetario,
		AclaracionMontoReclamado:      aclaracion.AclaracionMontoReclamado,
		AclaracionPori:                aclaracion.AclaracionPori,
		AclaracionTipoPersona:         aclaracion.AclaracionTipoPersona,
		AclaracionSexo:                aclaracion.AclaracionSexo,
		AclaracionEdad:                aclaracion.AclaracionEdad,
		AclaracionNivelAtencion:       aclaracion.AclaracionNivelAtencion,
		AclaracionFolioCondusef:       aclaracion.AclaracionFolioCondusef,
		AclaracionReversa:             aclaracion.AclaracionReversa,
		AclaracionOperacionExtranjero: aclaracion.AclaracionOperacionExtranjero,
		Sucursal:                      aclaracion.Sucursal,
		IdEmpresa:                     aclaracion.IdEmpresa,
		AclaracionSector:              aclaracion.Empresa.Sector,
		AclaracionDenominacion:        aclaracion.Empresa.Denominacion,
	}
}

func ConvertToAclaracionResponseEnv(aclaracion models.Aclaracion) models.AclaracionResponseEnv {
	return models.AclaracionResponseEnv{
		AclaracionTrimestre:           aclaracion.AclaracionTrimestre,
		AclaracionNumero:              aclaracion.AclaracionNumero,
		AclaracionFolioAtencion:       aclaracion.AclaracionFolioAtencion,
		AclaracionEstadoConPend:       aclaracion.AclaracionEstadoConPend,
		AclaracionFechaAclaracion:     aclaracion.AclaracionFechaAclaracion,
		AclaracionFechaAtencion:       aclaracion.AclaracionFechaAtencion,
		AclaracionMedioRecepcionCanal: aclaracion.AclaracionMedioRecepcionCanal,
		AclaracionProductoServicio:    aclaracion.IdProducto,
		AclaracionCausaMotivo:         agregarCeros(aclaracion.IdCausa),
		AclaracionFechaResolucion:     aclaracion.AclaracionFechaResolucion,
		AclaracionFechaNotifiUsuario:  aclaracion.AclaracionFechaNotifiUsuario,
		AclaracionEntidadFederativa:   aclaracion.IdAclaracionEntidadFederativa,
		AclaracionCodigoPostal:        aclaracion.AclaracionCodigoPostal,
		AclaracionMunicipioAlcaldia:   aclaracion.IdAclaracionMunicipioAlcaldia,
		AclaracionLocalidad:           aclaracion.IdAclaracionLocalidad,
		AclaracionColonia:             aclaracion.IdAclaracionColonia,
		AclaracionMonetario:           aclaracion.AclaracionMonetario,
		AclaracionMontoReclamado:      aclaracion.AclaracionMontoReclamado,
		AclaracionPori:                aclaracion.AclaracionPori,
		AclaracionTipoPersona:         aclaracion.AclaracionTipoPersona,
		AclaracionSexo:                aclaracion.AclaracionSexo,
		AclaracionEdad:                aclaracion.AclaracionEdad,
		AclaracionNivelAtencion:       aclaracion.AclaracionNivelAtencion,
		AclaracionFolioCondusef:       aclaracion.AclaracionFolioCondusef,
		AclaracionReversa:             aclaracion.AclaracionReversa,
		AclaracionOperacionExtranjero: aclaracion.AclaracionOperacionExtranjero,
		IdEmpresa:                     aclaracion.IdEmpresa,
		AclaracionSector:              aclaracion.Empresa.Sector,
		AclaracionDenominacion:        aclaracion.Empresa.Denominacion,
	}
}

func ObtenerAclaracionPorFolio(folio string) (models.AclaracionResponse, error) {
	var aclaracion models.Aclaracion

	if result := database.DB.Preload("Empresa").First(&aclaracion, "folio = ?", folio); result.Error != nil {
		return models.AclaracionResponse{}, result.Error
	}

	return ConvertToAclaracionResponse(aclaracion), nil
}

// Obtener Aclaraciones por folios y convertir a una lista de AclaracionResponse
func ObtenerAclaracionesPorFolios(folios []string, id_empresa int) ([]models.AclaracionResponse, error) {
	var aclaraciones []models.Aclaracion

	if result := database.DB.Preload("Empresa").Where("id_empresa = ? AND folio IN ?", id_empresa, folios).Find(&aclaraciones); result.Error != nil {
		return nil, result.Error
	}

	return ConvertToAclaracionResponses(aclaraciones), nil
}

// Obtener Aclaraciones por folios y convertir a una lista de AclaracionResponse
func ObtenerAclaracionesPorFoliosEnv(folios []string, id_empresa int) ([]models.AclaracionResponseEnv, error) {
	var aclaraciones []models.Aclaracion

	if result := database.DB.Preload("Empresa").Where("id_empresa = ? AND folio IN ?", id_empresa, folios).Find(&aclaraciones); result.Error != nil {
		return nil, result.Error
	}

	return ConvertToAclaracionResponsesEnv(aclaraciones), nil
}

// Convertir una lista de Aclaracion a una lista de AclaracionResponse
func ConvertToAclaracionResponses(aclaraciones []models.Aclaracion) []models.AclaracionResponse {
	var responses []models.AclaracionResponse
	for _, aclaracion := range aclaraciones {
		response := ConvertToAclaracionResponse(aclaracion)
		responses = append(responses, response)
	}
	return responses
}

// Convertir una lista de Aclaracion a una lista de AclaracionResponse
func ConvertToAclaracionResponsesEnv(aclaraciones []models.Aclaracion) []models.AclaracionResponseEnv {
	var responses []models.AclaracionResponseEnv
	for _, aclaracion := range aclaraciones {
		response := ConvertToAclaracionResponseEnv(aclaracion)
		responses = append(responses, response)
	}
	return responses
}

func CrearAclaracion(aclaracion *models.Aclaracion) error {
	var omit []string
	if len(aclaracion.AclaracionFechaAtencion) < 1 {
		omit = append(omit, "AclaracionFechaAtencion")
	}
	if len(aclaracion.AclaracionFechaAclaracion) < 1 {
		omit = append(omit, "AclaracionFechaAclaracion")
	}
	if len(aclaracion.AclaracionFechaResolucion) < 1 {
		omit = append(omit, "AclaracionFechaResolucion")
	}
	if len(aclaracion.AclaracionFechaNotifiUsuario) < 1 {
		omit = append(omit, "AclaracionFechaNotifiUsuario")
	}
	if len(omit) > 0 {
		if result := database.DB.Omit(omit...).Create(&aclaracion); result.Error != nil {
			return result.Error
		}
	} else if result := database.DB.Create(&aclaracion); result.Error != nil {
		return result.Error
	}

	return nil
}

func ModificarAclaracion(aclaracion *models.Aclaracion) error {
	var omit []string
	if len(aclaracion.AclaracionFechaAtencion) < 1 {
		omit = append(omit, "AclaracionFechaAtencion")
	}
	if len(aclaracion.AclaracionFechaAclaracion) < 1 {
		omit = append(omit, "AclaracionFechaAclaracion")
	}
	if len(aclaracion.AclaracionFechaResolucion) < 1 {
		omit = append(omit, "AclaracionFechaResolucion")
	}
	if len(aclaracion.AclaracionFechaNotifiUsuario) < 1 {
		omit = append(omit, "AclaracionFechaNotifiUsuario")
	}
	result := database.DB.Model(models.Aclaracion{}).Where("id = ?", aclaracion.ID).Omit(omit...).Updates(map[string]interface{}{
		"periodo":                          aclaracion.AclaracionTrimestre,
		"aclaracion_numero":                aclaracion.AclaracionNumero,
		"folio":                            aclaracion.AclaracionFolioAtencion,
		"estatus":                          aclaracion.AclaracionEstadoConPend,
		"aclaracion_fecha_reclamacion":     aclaracion.AclaracionFechaAclaracion,
		"aclaracion_fecha_atencion":        aclaracion.AclaracionFechaAtencion,
		"aclaracion_medio_recepcion_canal": aclaracion.AclaracionMedioRecepcionCanal,
		"id_producto":                      aclaracion.IdProducto,
		"id_causa":                         aclaracion.IdCausa,
		"aclaracion_fecha_resolucion":      aclaracion.AclaracionFechaResolucion,
		"aclaracion_fecha_notifi_usuario":  aclaracion.AclaracionFechaNotifiUsuario,
		"id_aclaracion_entidad_federativa": aclaracion.IdAclaracionEntidadFederativa,
		"aclaracion_codigo_postal":         aclaracion.AclaracionCodigoPostal,
		"id_aclaracion_municipio_alcaldia": aclaracion.IdAclaracionMunicipioAlcaldia,
		"id_aclaracion_localidad":          aclaracion.IdAclaracionLocalidad,
		"id_aclaracion_colonia":            aclaracion.IdAclaracionColonia,
		"aclaracion_monetario":             aclaracion.AclaracionMonetario,
		"aclaracion_monto_reclamado":       aclaracion.AclaracionMontoReclamado,
		"aclaracion_pori":                  aclaracion.AclaracionPori,
		"aclaracion_tipo_persona":          aclaracion.AclaracionTipoPersona,
		"aclaracion_sexo":                  aclaracion.AclaracionSexo,
		"aclaracion_edad":                  aclaracion.AclaracionEdad,
		"id_nivel_atencion":                aclaracion.AclaracionNivelAtencion,
		"aclaracion_folio_condusef":        aclaracion.AclaracionFolioCondusef,
		"aclaracion_reversa":               aclaracion.AclaracionReversa,
		"aclaracion_operacion_extranjero":  aclaracion.AclaracionOperacionExtranjero,
		"sucursal":                         aclaracion.Sucursal,
	})

	if result.Error != nil {
		return result.Error
	}

	if result.RowsAffected == 0 {
		return errors.New("ningún registro fue afectado")
	}

	return nil
}

func MarcarAclaracionesReportadas(folios []string) error {
	result := database.DB.Model(&models.Aclaracion{}).Where("folio IN ?", folios).Update("reportado", true)

	if result.Error != nil {
		return result.Error
	}

	if result.RowsAffected == 0 {
		return errors.New("ningún registro fue afectado")
	}

	return nil
}

// EliminarAclaracion
func EliminarAclaracion(folio string) error {
	if result := database.DB.Delete(models.Aclaracion{}, "folio = ?", folio); result.Error != nil {
		return result.Error
	}

	return nil
}
