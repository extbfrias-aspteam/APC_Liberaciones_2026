package services

import (
	"condusef-api/app/models"
	"condusef-api/app/repositories"

	"strconv"

	"time"
)

// GetConsecutivo
func GetConsecutivo(id string, idEmpresa int) (string, error) {
	item, err := repositories.ObtenerConsecutivo(id, idEmpresa)
	if err != nil {
		return "", err
	}

	if item == (models.Consecutivo{}) {

		actual := 1
		mask := strconv.Itoa(actual)
		year := time.Now().Year()
		if id == "reune" {
			mask = "DC-" + strconv.Itoa(year) + "-"
		} else {
			mask = "DD-" + strconv.Itoa(year) + "-"
		}

		err = repositories.AgregarConsecutivo(id, idEmpresa, actual+1, mask)
		if err != nil {
			return "", err
		}

		if id == "reune" {
			mask = "DC-" + strconv.Itoa(year) + "-" + strconv.Itoa(actual)
		} else {
			mask = "DD-" + strconv.Itoa(year) + "-" + strconv.Itoa(actual)
		}

		return mask, nil
	}

	actual := item.Consecutivo
	siguiente := actual + 1
	mask := strconv.Itoa(actual)
	if item.Mask != "" {
		mask = item.Mask + strconv.Itoa(actual)
	}

	err = repositories.ActualizarConsecutivo(id, idEmpresa, siguiente)
	if err != nil {
		return "", err
	}

	return mask, nil
}
