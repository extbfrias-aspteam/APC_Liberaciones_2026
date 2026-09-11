hola mundo



![Logo](/uploads/bcc852c8ee1cb4ef0665396349957292/ASP_Logo.png)

# Origen

(ejemplo)
svn://172.17.7.109/CERO/VENTANILLA CAPA CONTROL/PRODUCCION/NEGOCIO/1.9.0/CreditoWS

#  ReportesCero.war

Descripción del contenido


## Numero de Requerimiento

RQM ###

## Servidor Contendor

**Pruebas**: 172.17.7.x

**Produccion**: 172.17.8.x

## Objetivos

Objetivos del proyecto


## Deployment

Copiar .war en la ruta

`/opt/wildfly-10.1.0.Final/standalone/deployments`

## Environment Variables

Para correr el proyecto en sus respectivos ambientes modificar:

spring.properties

`HOST.NAME`

`URL`


## Documentacion

[Documentacion](https://linktodocumentation)


## Bases de datos

(ejemplo)
pg-cero.integraopciones.mx cero
pg-cierre.integraopciones.mx	Bases de Datos de Cierre
pg-izel.integraopciones.mx	Izel
pg-izel-batch.integraopciones.mx	Izel_batch
pg-izelbpm.integraopciones.mx	izelBPM
pg-izelmigracion.integraopciones.mx	izelMigracion


## Ejecutar en ambiente de pruebas
Pasos

## Ejecutar Localmente

Clonar el proyecto

```bash
 git clone https://link-to-project
```

Abrir el proyecto en Eclipse

Ejecutar el servidor local WildFly

Ejecutar en el servidor


## Tecnologias

**Servidor:** Spring, Quartz


## Uso/Ejemplos

(ejemplos)
```java
private static void initialized() {

        try {
            Apps s = Apps.getInstance();
            synchronized (Apps.class) {
                if (apps == null) // si la referencia es null ...
                    apps = s; // ... agrega la clase singleton
            }
            
            cfDao = (CambioFechaSpeiDAO) s.getApplicationContext().getBean("CambioFechaSpeiDAO");
            
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
```


## Usado en...

(ejemplos)
Proyectos que hacen uso de la aplicación (aplicacion/servicio/proceso) :

- [CeroAhorroWS](https://gitlab.integraopciones.mx/repositorios_asp/servicios/ceroahorrows.war)
- Proyecto 2

## Dependende de...

(ejemplos)
Proyectos que necesita la aplicación  (aplicacion/servicio/proceso):

- [CeroAhorroWS](https://gitlab.integraopciones.mx/repositorios_asp/servicios/ceroahorrows.war)
- AdminSeg.war
- Proyecto 2
