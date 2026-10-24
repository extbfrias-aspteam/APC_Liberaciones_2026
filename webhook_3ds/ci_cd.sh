#!/usr/bin/env bash
# build_and_save.sh - Construye la imagen Docker y la exporta a un archivo .tar

set -euo pipefail
IFS=$'\n\t'

# 1) Eliminar todos los .tar existentes en el directorio actual
echo "Eliminando archivos .tar existentes..."
find . -maxdepth 1 -type f -name "*.tar" -print -exec rm -f {} +

echo "Archivos .tar previos eliminados."

# 2) Construir la imagen Docker
IMAGE_NAME="dock_services-webhook_3ds"
TAG="latest"
FULL_IMAGE="${IMAGE_NAME}:${TAG}"

echo "Construyendo la imagen Docker '${FULL_IMAGE}'..."
docker build -t "${FULL_IMAGE}" .

echo "Imagen '${FULL_IMAGE}' construida exitosamente."

# 3) Guardar la imagen en un archivo .tar
OUTPUT_TAR="${IMAGE_NAME}.tar"

echo "Guardando la imagen en '${OUTPUT_TAR}'..."
docker save -o "${OUTPUT_TAR}" "${FULL_IMAGE}"

echo "Imagen guardada en '${OUTPUT_TAR}'."
