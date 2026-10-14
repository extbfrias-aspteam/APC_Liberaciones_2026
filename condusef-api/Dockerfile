# Imagen base para la compilación del proyecto
FROM golang:1.19 as builder

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar el código fuente a la imagen base
COPY . .

# Descargar las dependencias
RUN go mod download

# Compilar la aplicación
RUN CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build -a -installsuffix cgo -o condusef-api .

# Creación de la imagen final
FROM gcr.io/google.com/cloudsdktool/google-cloud-cli:alpine
RUN apk --no-cache add ca-certificates
WORKDIR /root/
COPY --from=builder /app/condusef-api .
RUN mkdir -p ./public/html
RUN mkdir -p ./temp
COPY public/html ./public/html

# Archivos opcionales de la imagen final

# Si se coloca un archivo de credenciales para el google storage, este debe ser 
# colocado en la raiz del proyecto
# COPY ./application_default_credentials.json ./application_default_credentials.json

# Si se utiliza un archivo .env para las variables de ambiente, este debe ser
# colocado en la raiz del proyecto basado en el archivo .env.template
#COPY ./.env ./.env

# Ejecutar la aplicación
CMD ["./condusef-api"]