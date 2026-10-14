package controllers

import (
	"condusef-api/app/models"
	"condusef-api/app/repositories"
	"condusef-api/app/services"
	"condusef-api/app/utils"

	"net/http"
	"os"
	"path"
	"time"

	"github.com/labstack/echo/v4"
	"github.com/rs/xid"
)

// SubirEvidencia
func SubirEvidencia(c echo.Context) error {
	folio := c.Param("folio")
	if folio == "" {
		return c.JSON(http.StatusBadRequest, "Es necesario indicar el número de folio como parte de la petición")
	}

	file, err := c.FormFile("file")
	if err != nil {
		return c.JSON(http.StatusBadRequest, err.Error())
	}

	src, err := file.Open()
	if err != nil {
		return c.JSON(http.StatusInternalServerError, err.Error())
	}
	defer src.Close()

	idDoc := xid.New().String()
	object := path.Join(folio, idDoc)

	err = services.G_STORAGE.Upload(src, object)
	if err != nil {
		utils.ErrorMessage("G_STORAGE.Upload", err.Error())
		return c.JSON(http.StatusInternalServerError, err.Error())
	}

	evidencia := models.Evidencia{
		Id:            idDoc,
		NumFolio:      folio,
		NombreDoc:     file.Filename,
		FechaCreacion: time.Now().UTC(),
	}
	err = repositories.CrearEvidencia(&evidencia)
	if err != nil {
		utils.ErrorMessage("repositories.CrearEvidencia", err.Error())
		return c.JSON(http.StatusInternalServerError, err.Error())
	}

	return c.JSON(http.StatusOK, "")
}

// DescargarEvidencia
func DescargarEvidencia(c echo.Context) error {
	folio := c.Param("folio")
	idDoc := c.QueryParam("id")
	if idDoc == "" {
		return c.JSON(http.StatusBadRequest, "El identificador del documento es incorrecto")
	}

	object := path.Join(folio, idDoc)
	tempFilename := xid.New().String()
	destFilename := path.Join("temp", tempFilename)

	err := services.G_STORAGE.Download(object, destFilename)
	if err != nil {
		return c.JSON(http.StatusInternalServerError, err.Error())
	}

	reader, err := os.Open(destFilename)
	if err != nil {
		return c.JSON(http.StatusInternalServerError, err.Error())
	}

	defer func() {
		reader.Close()
		err = os.Remove(destFilename)
		if err != nil {
			utils.ErrorMessage("Removing file error", err.Error())
		}
	}()

	return c.Stream(http.StatusOK, "application/pdf", reader)
}

// ObtenerEvidenciasPorFolio
func ObtenerEvidenciasPorFolio(c echo.Context) error {
	folio := c.Param("folio")
	if folio == "" {
		return c.JSON(http.StatusBadRequest, "")
	}

	evidencias, err := repositories.ObtenerEvidenciasPorFolio(folio)
	if err != nil {
		return c.JSON(http.StatusInternalServerError, err.Error())
	}

	return c.JSON(http.StatusOK, evidencias)
}
