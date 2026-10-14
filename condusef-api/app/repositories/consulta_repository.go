package repositories

import (
	"condusef-api/app/database"
	"condusef-api/app/models"
	"errors"

	"fmt"
)

func ConvertToConsultaResponse(consulta models.Consulta) models.ConsultaResponse {
	return models.ConsultaResponse{
		ID:                      consulta.ID,
		ConsultasTrim:           consulta.ConsultasTrim,
		NumConsultas:            consulta.NumConsultas,
		ConsultasFolio:          consulta.ConsultasFolio,
		ConsultasEstatusCon:     consulta.ConsultasEstatusCon,
		ConsultasFecAten:        consulta.ConsultasFecAten,
		EstadosId:               consulta.IdEstados,
		ConsultasFecRecepcion:   consulta.ConsultasFecRecepcion,
		MediosId:                consulta.IdMedios,
		Producto:                consulta.IdProducto,
		CausaId:                 consulta.IdCausa,
		ConsultasCP:             consulta.ConsultasCp,
		ConsultasMpioId:         consulta.IdConsultasMpio,
		ConsultasLocId:          consulta.IdConsultasLoc,
		ConsultasColId:          consulta.IdConsultasCol,
		ConsultascatnivelatenId: consulta.IdConsultasCatNivelAten,
		ConsultasPori:           consulta.ConsultasPori,
		Sucursal:                consulta.Sucursal,
		IdEmpresa:               consulta.IdEmpresa,
		ConsultasSector:         consulta.Empresa.Sector,
		ConsultasDenominacion:   consulta.Empresa.Denominacion,
	}
}

func ConvertToConsultaResponseEnv(consulta models.Consulta) models.ConsultaResponseEnv {
	return models.ConsultaResponseEnv{
		ConsultasTrim:           consulta.ConsultasTrim,
		NumConsultas:            consulta.NumConsultas,
		ConsultasFolio:          consulta.ConsultasFolio,
		ConsultasEstatusCon:     consulta.ConsultasEstatusCon,
		ConsultasFecAten:        consulta.ConsultasFecAten,
		EstadosId:               consulta.IdEstados,
		ConsultasFecRecepcion:   consulta.ConsultasFecRecepcion,
		MediosId:                consulta.IdMedios,
		Producto:                consulta.IdProducto,
		CausaId:                 agregarCeros(consulta.IdCausa),
		ConsultasCP:             consulta.ConsultasCp,
		ConsultasMpioId:         consulta.IdConsultasMpio,
		ConsultasLocId:          consulta.IdConsultasLoc,
		ConsultasColId:          consulta.IdConsultasCol,
		ConsultascatnivelatenId: consulta.IdConsultasCatNivelAten,
		ConsultasPori:           consulta.ConsultasPori,
		// IdEmpresa:               consulta.IdEmpresa,
		ConsultasSector:       consulta.Empresa.Sector,
		ConsultasDenominacion: consulta.Empresa.Denominacion,
	}
}

func agregarCeros(cadena string) string {
	return fmt.Sprintf("%0*s", 4, cadena)
}

func ObtenerConsultaPorFolio(folio string) (models.ConsultaResponse, error) {
	var consulta models.Consulta

	if result := database.DB.Preload("Empresa").First(&consulta, "folio = ?", folio); result.Error != nil {
		return models.ConsultaResponse{}, result.Error
	}

	return ConvertToConsultaResponse(consulta), nil
}

// Obtener Consultas por folios y convertir a una lista de ConsultaResponse
func ObtenerConsultasPorFolios(folios []string, id_empresa int) ([]models.ConsultaResponse, error) {
	var consultas []models.Consulta

	if result := database.DB.Preload("Empresa").Where("id_empresa = ? AND folio IN ?", id_empresa, folios).Find(&consultas); result.Error != nil {
		return nil, result.Error
	}

	return ConvertToConsultaResponses(consultas), nil
}

func ObtenerConsultasPorFoliosEnv(folios []string, id_empresa int) ([]models.ConsultaResponseEnv, error) {
	var consultas []models.Consulta

	if result := database.DB.Preload("Empresa").Where("id_empresa = ? AND folio IN ?", id_empresa, folios).Find(&consultas); result.Error != nil {
		return nil, result.Error
	}

	return ConvertToConsultaResponsesEnv(consultas), nil
}

// Convertir una lista de Consulta a una lista de ConsultaResponse
func ConvertToConsultaResponses(consultas []models.Consulta) []models.ConsultaResponse {
	var responses []models.ConsultaResponse
	for _, consulta := range consultas {
		response := ConvertToConsultaResponse(consulta)
		responses = append(responses, response)
	}
	return responses
}

// Convertir una lista de Consulta a una lista de ConsultaResponse
func ConvertToConsultaResponsesEnv(consultas []models.Consulta) []models.ConsultaResponseEnv {
	var responses []models.ConsultaResponseEnv
	for _, consulta := range consultas {
		response := ConvertToConsultaResponseEnv(consulta)
		responses = append(responses, response)
	}
	return responses
}

func CrearConsulta(consulta *models.Consulta) error {
	if len(consulta.ConsultasFecAten) < 1 {
		if result := database.DB.Omit("ConsultasFecAten").Create(&consulta); result.Error != nil {
			return result.Error
		}
	} else if result := database.DB.Create(&consulta); result.Error != nil {
		return result.Error
	}

	return nil
}

func ModificarConsulta(consulta *models.Consulta) error {
	var omit []string
	if len(consulta.ConsultasFecAten) < 1 {
		omit = append(omit, "ConsultasFecAten")
	}
	result := database.DB.Model(models.Consulta{}).Omit(omit...).Where("id = ?", consulta.ID).Updates(map[string]interface{}{
		"folio":                    consulta.ConsultasFolio,
		"sucursal":                 consulta.Sucursal,
		"periodo":                  consulta.ConsultasTrim,
		"num_consultas":            consulta.NumConsultas,
		"consultas_fec_recepcion":  consulta.ConsultasFecRecepcion,
		"id_medios":                consulta.IdMedios,
		"id_consultascatnivelaten": consulta.IdConsultasCatNivelAten,
		"id_producto":              consulta.IdProducto,
		"id_causa":                 consulta.IdCausa,
		"consultas_pori":           consulta.ConsultasPori,
		"id_estados":               consulta.IdEstados,
		"id_consultas_mpio":        consulta.IdConsultasMpio,
		"id_consultas_loc":         consulta.IdConsultasLoc,
		"id_consultas_col":         consulta.IdConsultasCol,
		"consultas_cp":             consulta.ConsultasCp,
		"consultas_fec_aten":       consulta.ConsultasFecAten,
		"estatus":                  consulta.ConsultasEstatusCon,
	})

	if result.Error != nil {
		return result.Error
	}

	if result.RowsAffected == 0 {
		return errors.New("ningún registro fue afectado")
	}

	return nil
}

func MarcarConsultasReportadas(folios []string) error {
	result := database.DB.Model(&models.Consulta{}).Where("folio IN ?", folios).Update("reportado", true)

	if result.Error != nil {
		return result.Error
	}

	if result.RowsAffected == 0 {
		return errors.New("ningún registro fue afectado")
	}

	return nil
}

// EliminarConsulta
func EliminarConsulta(folio string) error {
	if result := database.DB.Delete(models.Consulta{}, "folio = ?", folio); result.Error != nil {
		return result.Error
	}

	return nil
}
