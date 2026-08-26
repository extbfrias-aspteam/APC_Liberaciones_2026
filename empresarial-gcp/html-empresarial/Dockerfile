FROM nginx:1.25-alpine

# Borrar configuración por defecto para evitar conflictos
RUN rm /etc/nginx/conf.d/default.conf

# Crear carpeta para snippets (esto evita que Nginx los cargue automáticamente)
RUN mkdir -p /etc/nginx/snippets

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY proxy_params.conf /etc/nginx/conf.d/proxy_params.conf
COPY auth_check.conf /etc/nginx/snippets/auth_check.conf


#COPY dist/onboarding /usr/share/nginx/html/onboarding
COPY dist/bussiness_backoffice/* /usr/share/nginx/html/bussiness_backoffice
COPY dist/business_banking/* /usr/share/nginx/html/business_banking

CMD ["nginx", "-g", "daemon off;"]
