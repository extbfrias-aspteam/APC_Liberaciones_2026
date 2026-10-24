# Webhook 3DS con NGINX y HTTPS

Este proyecto configura una aplicación webhook_3ds de Python detrás de un proxy NGINX con HTTPS.

## Estructura del proyecto

```
webhook_3ds/
├── Dockerfile           # Definición del contenedor Python
├── main.py              # Punto de entrada de la aplicación
├── docker-compose.yml   # Configuración de Docker Compose
├── nginx/               # Configuración de NGINX
│   ├── nginx.conf       # Configuración general de NGINX
│   ├── conf.d/          # Configuraciones específicas de sitios
│   │   └── webhook_3ds.conf # Configuración del proxy reverso
│   └── ssl/             # Certificados SSL
│       ├── webhook_3ds.crt  # Certificado público
│       └── webhook_3ds.key  # Clave privada
└── src/                # Código fuente de la aplicación
```

## Preparación

1. **Generar certificados SSL autofirmados:**

   En Windows con PowerShell (requiere OpenSSL):

   ```powershell
   .\generate_ssl_openssl.ps1
   ```

   En Linux:

   ```bash
   chmod +x generate_ssl.sh
   ./generate_ssl.sh
   ```

2. **Construir y ejecutar los contenedores:**
   ```bash
   docker-compose up -d
   ```

## Detalles de la configuración

- **webhook_3ds**: Aplicación Python ejecutándose en el puerto 8000 internamente
- **nginx**: Proxy reverso que escucha en los puertos 80 (HTTP) y 443 (HTTPS)
  - Redirecciona todo el tráfico HTTP a HTTPS
  - Enruta el tráfico HTTPS desde `/dock-3ds-webhook` a la aplicación

## Acceso

La aplicación estará disponible en:

```
https://wh3dsbeta.aspintegraopciones.mx/dock-3ds-webhook
```

## Notas

- El certificado es autofirmado, por lo que los navegadores mostrarán advertencias de seguridad.
- Para entornos de producción, se recomienda usar certificados emitidos por una autoridad de certificación confiable.
- El contenedor de la aplicación no expone directamente ningún puerto al host, todo el tráfico pasa por NGINX.
