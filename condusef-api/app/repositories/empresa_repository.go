package repositories

import (
	"condusef-api/app/database"
	"condusef-api/app/models"
)

// ObtenerEmpresas
func ObtenerEmpresas() ([]models.Empresa, error) {
	var empresas []models.Empresa

	sql := `SELECT id, nombre, sector, denominacion 
			  FROM redeco.cat_empresas ORDER BY id`

	if result := database.DB.Raw(sql).Scan(&empresas); result.Error != nil {
		return nil, result.Error
	}

	return empresas, nil
}

func ObtenerEmpresaPorId(id int) (models.Empresa, error) {
	var empresa models.Empresa

	sql := `SELECT id, nombre, sector, denominacion 
			  FROM redeco.cat_empresas WHERE id = ?`

	if result := database.DB.Raw(sql, id).Scan(&empresa); result.Error != nil {
		return models.Empresa{}, result.Error
	}

	return empresa, nil
}
