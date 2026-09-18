FROM nginxinc/nginx-unprivileged:1.30.4-alpine-slim


LABEL org.opencontainers.image.title="html-empresarial" \
      org.opencontainers.image.version="nginx-1.30.4"


COPY --chown=101:101 nginx.conf /etc/nginx/conf.d/default.conf
COPY --chown=101:101 security_headers.conf /etc/nginx/snippets/security_headers.conf
COPY --chown=101:101 proxy_params.conf /etc/nginx/snippets/proxy_params.conf

COPY --chown=101:101 404.html /usr/share/nginx/html/__errors/404.html

COPY --chown=101:101 dist/bussiness_backoffice/bussiness_backoffice/ /usr/share/nginx/html/bussiness_backoffice/
COPY --chown=101:101 dist/business_banking/ /usr/share/nginx/html/business_banking/


USER 101:101
EXPOSE 8080
STOPSIGNAL SIGQUIT

HEALTHCHECK NONE
CMD ["nginx", "-g", "daemon off;"]