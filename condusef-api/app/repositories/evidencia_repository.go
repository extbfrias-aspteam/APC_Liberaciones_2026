package repositories

import (
	"condusef-api/app/database"
	"condusef-api/app/models"
)

// CrearEvidencia
func CrearEvidencia(evidencia *models.Evidencia) error {
	if result := database.DB.Create(&evidencia); result.Error != nil {
		return result.Error
	}

	return nil
}

// ObtenerEvidenciasPorFolio
func ObtenerEvidenciasPorFolio(folio string) ([]models.Evidencia, error) {
	var evidencias []models.Evidencia

	sql := `SELECT num_folio, nombre_doc, id, timezone('America/Mexico_City', fecha_creacion) fecha_creacion
	          FROM redeco.evidencias where num_folio = ?`

	if result := database.DB.Raw(sql, folio).Find(&evidencias); result.Error != nil {
		return nil, result.Error
	}

	return evidencias, nil
}
