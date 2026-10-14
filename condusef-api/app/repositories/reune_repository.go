package repositories

import (
	"condusef-api/app/database"
	"condusef-api/app/models"
)

// ObtenerPaginadoREUNE
func ObtenerPaginadoREUNE(trimestre, offset, limit int, filter string, id_empresa int) ([]models.Registro, int64, error) {
	var registros []models.Registro
	var total int64

	sql := `SELECT r.*, p.producto, p.subproducto FROM (SELECT id, folio, id_producto, id_causa, 1 AS tipo, reportado, periodo, estatus FROM reune.consultas WHERE id_empresa = ? AND periodo = ?
			UNION 
			SELECT id, folio, id_producto, id_causa, 2 AS tipo, reportado, periodo, estatus FROM reune.reclamaciones WHERE id_empresa = ? AND periodo = ?
				UNION  
			SELECT id, folio, id_producto, id_causa, 3 AS tipo, reportado, periodo, estatus FROM reune.aclaraciones WHERE id_empresa = ? AND periodo = ?
			ORDER BY folio DESC 
			) r
			INNER JOIN reune.cat_productos p ON p.id = r.id_producto
			INNER JOIN reune.cat_causas c ON c.id = r.id_causa and c.id_producto = r.id_producto 
			LIMIT ? OFFSET ?`

	if result := database.DB.Raw(sql, id_empresa, trimestre, id_empresa, trimestre, id_empresa, trimestre, limit, (offset-1)*limit).Find(&registros); result.Error != nil {
		return nil, 0, result.Error
	}

	sql2 := `SELECT SUM(cnt) AS total FROM (SELECT COUNT(*) AS cnt  FROM reune.consultas WHERE id_empresa = ? AND periodo = ?
			UNION ALL
			SELECT COUNT(*) AS cnt  FROM reune.reclamaciones WHERE id_empresa = ? AND periodo = ?
				UNION  ALL
			SELECT COUNT(*) AS cnt  FROM reune.aclaraciones WHERE id_empresa = ? AND periodo = ?
			) AS registros`

	result := database.DB.Raw(sql2, id_empresa, trimestre, id_empresa, trimestre, id_empresa, trimestre).Scan(&total)
	if result.Error != nil {
		return nil, 0, result.Error
	}

	return registros, total, nil
}

// ObtenerRegistrosREUNESinReportar
func ObtenerRegistrosREUNESinReportar(trimestre int, id_empresa int) ([]models.Registro, error) {
	var registros []models.Registro

	sql := `SELECT r.*, p.producto, p.subproducto FROM (SELECT id, folio, id_producto, id_causa, 1 AS tipo, reportado, periodo FROM reune.consultas WHERE id_empresa = ? AND reportado = false AND periodo = ?
			UNION 
			SELECT id, folio, id_producto, id_causa, 2 AS tipo, reportado, periodo FROM reune.reclamaciones WHERE id_empresa = ? AND reportado = false AND periodo = ?
				UNION  
			SELECT id, folio, id_producto, id_causa, 3 AS tipo, reportado, periodo FROM reune.aclaraciones WHERE id_empresa = ? AND reportado = false AND periodo = ?
			ORDER BY folio DESC
			) r
			INNER JOIN reune.cat_productos p ON p.id = r.id_producto
			INNER JOIN reune.cat_causas c ON c.id = r.id_causa and c.id_producto = r.id_producto 
			LIMIT 20`

	if result := database.DB.Raw(sql, id_empresa, trimestre, id_empresa, trimestre, id_empresa, trimestre).Find(&registros); result.Error != nil {
		return nil, result.Error
	}

	return registros, nil
}
