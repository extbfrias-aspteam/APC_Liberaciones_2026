package repositories

import (
	"condusef-api/app/database"
	"condusef-api/app/models"
	"fmt"
	"strings"
	"time"

	"errors"
)

func ObtenerPeriodos(schema string, idEmpresa int) ([]models.Periodo, error) {
	var periodos []models.Periodo

	sql := fmt.Sprintf(`SELECT mes, 
							   anio, 
							   TO_CHAR(fecha_inicial, 'dd/mm/yyyy') as fecha_inicial, 
							   TO_CHAR(fecha_final, 'dd/mm/yyyy') as fecha_final, 
							   activo,
							   timezone('America/Mexico_City', fecha_cierre) as fecha_cierre,
							   id_empresa
						FROM %s.periodos WHERE id_empresa = ? order by anio, mes`, schema)

	if result := database.DB.Raw(sql, idEmpresa).Scan(&periodos); result.Error != nil {
		return nil, result.Error
	}

	return periodos, nil
}

// ObtenerPeriodoActivo
func ObtenerPeriodoActivo(schema string, idEmpresa int) (models.Periodo, error) {
	var periodo models.Periodo

	sql := fmt.Sprintf(`SELECT mes, 
							   anio, 
							   TO_CHAR(fecha_inicial, 'dd/mm/yyyy') as fecha_inicial, 
							   TO_CHAR(fecha_final, 'dd/mm/yyyy') as fecha_final, 
							   activo,
							   timezone('America/Mexico_City', fecha_cierre) as fecha_cierre,
							   id_empresa
	          FROM %s.periodos WHERE activo = true and id_empresa = ?`, schema)

	if result := database.DB.Raw(sql, idEmpresa).Scan(&periodo); result.Error != nil {
		return models.Periodo{}, result.Error
	}

	return periodo, nil
}

// CrearPeriodo
func CrearPeriodo(schema string, periodo *models.Periodo) error {
	tablename := fmt.Sprintf(`%s.periodos`, schema)

	if result := database.DB.Table(tablename).Create(&periodo); result.Error != nil {
		if strings.Contains(result.Error.Error(), "duplicate key value") {
			return fmt.Errorf("no fue posible crear el periodo %d/%d por que ya existe", periodo.Mes, periodo.Anio)
		}

		return result.Error
	}

	return nil
}

// CerrarPeriodo
func CerrarPeriodo(schema string, idEmpresa, mes, anio int) error {
	tablename := fmt.Sprintf(`%s.periodos`, schema)

	result := database.DB.Model(models.Periodo{}).
		Where("mes = ? AND anio = ? AND id_empresa = ?", mes, anio, idEmpresa).
		Table(tablename).
		Updates(map[string]interface{}{"activo": false, "fecha_cierre": time.Now().UTC()})

	if result.Error != nil {
		return result.Error
	}

	if result.RowsAffected == 0 {
		return errors.New("no rows affected")
	}

	return nil
}

// AbrirPeriodo
func AbrirPeriodo(schema string, idEmpresa, mes, anio int) error {
	tablename := fmt.Sprintf(`%s.periodos`, schema)

	result := database.DB.Model(models.Periodo{}).
		Where("mes = ? AND anio = ? AND id_empresa = ?", mes, anio, idEmpresa).
		Table(tablename).
		Updates(map[string]interface{}{"activo": true})

	if result.Error != nil {
		return result.Error
	}

	if result.RowsAffected == 0 {
		return errors.New("no rows affected")
	}

	return nil
}
