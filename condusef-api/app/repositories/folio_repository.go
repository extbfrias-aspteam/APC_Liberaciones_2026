package repositories

import (
	"condusef-api/app/database"
	"condusef-api/app/models"

	"errors"
)

// ObtenerConsecutivo
func ObtenerConsecutivo(id string, idEmpresa int) (models.Consecutivo, error) {
	var consecutivo models.Consecutivo

	sql := `select * from redeco.cat_consecutivos where id = ? and id_empresa = ?`

	if result := database.DB.Raw(sql, id, idEmpresa).Find(&consecutivo); result.Error != nil {
		return models.Consecutivo{}, result.Error
	}

	return consecutivo, nil
}

// ActualizarConsecutivo
func ActualizarConsecutivo(id string, idEmpresa, siguiente int) error {
	result := database.DB.Model(models.Consecutivo{}).Where("id = ? and id_empresa = ?", id, idEmpresa).Updates(map[string]interface{}{
		"consecutivo": siguiente,
	})

	if result.Error != nil {
		return result.Error
	}

	if result.RowsAffected == 0 {
		return errors.New("ningun registro fue afectado")
	}

	return nil
}

// AgregarConsecutivo
func AgregarConsecutivo(id string, idEmpresa, siguiente int, mask string) error {
	result := database.DB.Model(models.Consecutivo{}).Create(map[string]interface{}{
		"id":          id,
		"mask":        mask,
		"consecutivo": siguiente,
		"id_empresa":  idEmpresa,
	})

	if result.Error != nil {
		return result.Error
	}

	if result.RowsAffected == 0 {
		return errors.New("ningun registro fue afectado")
	}

	return nil
}
