# Repositorio ASP Integra Opciones

Repositorio creado y gestionado por QA.

## Commits

Para futuros commits, se recomienda y se sugiere el siguiente formato para el control y mejor comprension.
```
PROYECTO: Triggers 1.1.0 
FECHA: 13-06-2024
LÍDER/GERENTE: SECHAVEZ/correo institucional
RESPONSABLE: MPORRAS/correo institucional
DESCRIPCIÓN: Se realiza la carga del proyecto de Triggers. 
```
A continuacion se comparte el comando para facilitar el commit:
```
git commit -a -m "PROYECTO: dock-webhook 1.1.0
FECHA: 25-07-2025
LÍDER/GERENTE: VIRUVALCABA
RESPONSABLE: BRPALMA
DESCRIPCIÓN: Se realiza la actualziación del dockerfile."   
 
```
## Licencia
Los repositorios son propiedad de 
[Opciones Empresariales del Noreste, S.A. de C.V. S.F.P. (ASP Integra Opciones)](https://www.aspintegraopciones.com/)


# URL DEL DOMINIO
https://wh3dsbeta.aspintegraopciones.mx:9000/operaciones/insertar-operaciones

# Comandos para el servidor
Ver Puertos Disponibles
sudo ss -tuln

Ver Espacio en el servidor
df -h

# Comando para el Apartado de Https Certificados
Comando para instalar los Certificados para el https
openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -days 365 -nodes

# Eliminar Contenedor
docker rm dock-services-app

# Actualizar alguna Dependencia
1. poetry update
2. Instalar poety en el caso que queramos probar en local para actualizar, en el caso que queramos hacer afuer del contenedor pip install poetry


# PASOS PARA SUBIR A SERVIDOR EL DOCKER
Tag: DESA_DOCKSERVICE_DEV_ALEXIS_DOCKPAY
IMAGEN:dock-services
Nombre del Contenedor:dock-services-app
 1. Crear la imagen en el docker Desktop   docker build -t dock-services .
 2. Crear el tag con el servidor y push
     docker tag dock-services crdesarrollo.azurecr.io/dock-services:DESA_DOCKSERVICE_DEV_ALEXIS_DOCKPAY
     docker push crdesarrollo.azurecr.io/dock-services:DESA_DOCKSERVICE_DEV_ALEXIS_DOCKPAY
 
 3.Servidor hacer Pull
    docker pull crdesarrollo.azurecr.io/dock-services:DESA_DOCKSERVICE_DEV_ALEXIS_DOCKPAY
    #Correr el Aplicativo
    sudo docker run   --name dock-services-app --restart unless-stopped  -p 2053:2053 crdesarrollo.azurecr.io/dock-services:DESA_DOCKSERVICE_DEV_ALEXIS_DOCKPAY 
    




# INSTALACION LOCAL
1. Primero debemos tener en cuenta que ocupamos tener instalado Poetry para las instalaciones Locales, una vez que tengamos instalado podemos seguir con los siguientes Pasos.
2. Despues vamos a verificar que tengamos el poetry.lock o toml para las instalaciones, ahi vendran las dependencias que necesitamos, en el caso que ocupen actualizar seria otro procedimiento aparte.
3. Despues Correr poetry run python main.py


curl -v https://52.149.155.19:9000/operaciones/insertar-operacion
curl -v https://wh3dsbeta.aspintegraopciones.mx:9000/operaciones/insertar-operacion



