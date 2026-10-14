package repositories

import (
	"condusef-api/app/database"
	"condusef-api/app/models"
	"fmt"
)

// ObtenerProductos
func ObtenerProductos(schema string, idEmpresa int) ([]models.Producto, error) {
	var productos []models.Producto

	//reune
	if schema == "reune" {
		sql := fmt.Sprintf(`select id, producto, subproducto from %s.cat_productos where id_institucion = ? order by id`, schema)
		fmt.Print(sql, idEmpresa)
		if result := database.DB.Raw(sql, idEmpresa).Find(&productos); result.Error != nil {
			return nil, result.Error
		}
	} else {
		sql := fmt.Sprintf(`select id, producto from %s.cat_productos where id_institucion = ? order by id`, schema)
		fmt.Print(sql, idEmpresa)
		if result := database.DB.Raw(sql, idEmpresa).Find(&productos); result.Error != nil {
			return nil, result.Error
		}
	}
	return productos, nil
}

// ObtenerCausas
func ObtenerCausas(schema, idProducto string, tipo string) ([]models.Causa, error) {
	var causas []models.Causa

	sql := fmt.Sprintf(`select id, descripcion from %s.cat_causas where id_producto = ?`, schema)
	if schema == "reune" {
		switch tipo {
		case "2":
			sql += ` AND reclamacion = 1`
		case "3":
			sql += ` AND aclaracion = 1`
		default:
			sql += ` AND consulta = 1`
		}
	}

	if result := database.DB.Raw(sql, idProducto).Find(&causas); result.Error != nil {
		return nil, result.Error
	}

	return causas, nil
}

// ObtenerEntidadesFed
func ObtenerEntidadesFed() ([]models.EntidadFed, error) {
	var entidadesFed []models.EntidadFed

	sql := `select * from redeco.cat_entidades_fed`

	if result := database.DB.Raw(sql).Find(&entidadesFed); result.Error != nil {
		return nil, result.Error
	}

	return entidadesFed, nil
}

// ObtenerMunicipios
func ObtenerMunicipios(idEntidadFed int, cp int) ([]models.Municipio, error) {
	var municipios []models.Municipio
	if cp > 0 {
		sql := `select id_entidad_fed, id_municipio, municipio 
			from redeco.cat_sepomex where id_entidad_fed = ? and cp = ? 
			group by id_entidad_fed, id_municipio, municipio
			order by municipio ASC`

		if result := database.DB.Raw(sql, idEntidadFed, cp).Find(&municipios); result.Error != nil {
			return nil, result.Error
		}
	} else {
		sql := `select id_entidad_fed, id_municipio, municipio 
			from redeco.cat_sepomex where id_entidad_fed = ?
			group by id_entidad_fed, id_municipio, municipio
			order by municipio ASC`

		if result := database.DB.Raw(sql, idEntidadFed).Find(&municipios); result.Error != nil {
			return nil, result.Error
		}
	}

	return municipios, nil
}

// ObtenerColonias
func ObtenerColonias(cp int) ([]models.Colonia, error) {
	var colonias []models.Colonia

	sql := `select id_entidad_fed, id_municipio, municipio, id_colonia, colonia, id_localidad, localidad
			from redeco.cat_sepomex where cp = ?`

	if result := database.DB.Raw(sql, cp).Find(&colonias); result.Error != nil {
		return nil, result.Error
	}

	return colonias, nil
}

// ObtenerCodigosPostales
func ObtenerCodigosPostales(idEntidadFed int) ([]models.CodigoPostal, error) {
	var codigos []models.CodigoPostal

	sql := `select id_entidad_fed, cp from redeco.cat_sepomex where id_entidad_fed = ?`

	if result := database.DB.Raw(sql, idEntidadFed).Find(&codigos); result.Error != nil {
		return nil, result.Error
	}

	return codigos, nil
}

// ObtenerMediosRecepcion
func ObtenerMediosRecepcion(schema string) ([]models.MedioRecepcion, error) {
	var municipios []models.MedioRecepcion

	sql := fmt.Sprintf(`select * from %s.cat_medios_recepcion order by id`, schema)

	if result := database.DB.Raw(sql).Find(&municipios); result.Error != nil {
		return nil, result.Error
	}

	return municipios, nil
}

// ObtenerNivelesAtencion
func ObtenerNivelesAtencion(schema string) ([]models.NivelAtencion, error) {
	var municipios []models.NivelAtencion

	sql := fmt.Sprintf(`select * from %s.cat_niveles_atencion order by id`, schema)

	if result := database.DB.Raw(sql).Find(&municipios); result.Error != nil {
		return nil, result.Error
	}

	return municipios, nil
}
