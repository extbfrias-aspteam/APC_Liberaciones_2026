#!/bin/bash

# Script para generar un certificado SSL autofirmado para el desarrollo

# Crear directorio si no existe
mkdir -p nginx/ssl

# Generar una clave privada
openssl genrsa -out nginx/ssl/webhook_3ds.key 2048

# Generar una solicitud de firma de certificado (CSR)
openssl req -new -key nginx/ssl/webhook_3ds.key -out nginx/ssl/webhook_3ds.csr -subj "/C=MX/ST=Ciudad de Mexico/L=CDMX/O=ASP Integra Opciones/OU=IT/CN=wh3dsbeta.aspintegraopciones.mx"

# Generar un certificado autofirmado válido por 1 año
openssl x509 -req -days 365 -in nginx/ssl/webhook_3ds.csr -signkey nginx/ssl/webhook_3ds.key -out nginx/ssl/webhook_3ds.crt

# Eliminar el archivo CSR (ya no es necesario)
rm nginx/ssl/webhook_3ds.csr

echo "Certificado SSL autofirmado generado con éxito."
