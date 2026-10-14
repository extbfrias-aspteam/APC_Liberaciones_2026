-- redeco.cat_empresas definition

-- Drop table

-- DROP TABLE redeco.cat_empresas;

CREATE TABLE redeco.cat_empresas (
	id int4 NOT NULL,
	nombre varchar NOT NULL,
	sector varchar NOT NULL,
	denominacion varchar NOT NULL,
	CONSTRAINT cat_empresas_pk PRIMARY KEY (id)
);


-- redeco.cat_entidades_fed definition

-- Drop table

-- DROP TABLE redeco.cat_entidades_fed;

CREATE TABLE redeco.cat_entidades_fed (
	id int4 NOT NULL,
	nombre varchar NOT NULL,
	CONSTRAINT cat_entidades_fed_pk PRIMARY KEY (id)
);


-- redeco.cat_medios_recepcion definition

-- Drop table

-- DROP TABLE redeco.cat_medios_recepcion;

CREATE TABLE redeco.cat_medios_recepcion (
	id int4 NOT NULL,
	nombre varchar NOT NULL,
	CONSTRAINT cat_medios_recepcion_pk PRIMARY KEY (id)
);


-- redeco.cat_niveles_atencion definition

-- Drop table

-- DROP TABLE redeco.cat_niveles_atencion;

CREATE TABLE redeco.cat_niveles_atencion (
	id int4 NOT NULL,
	nombre varchar NOT NULL,
	CONSTRAINT cat_niveles_atencion_pk PRIMARY KEY (id)
);


-- redeco.cat_productos definition

-- Drop table

-- DROP TABLE redeco.cat_productos;

CREATE TABLE redeco.cat_productos (
	id varchar NOT NULL,
	producto varchar NOT NULL,
	institucion varchar NULL,
	id_institucion int4 NULL,
	CONSTRAINT cat_productos_pk PRIMARY KEY (id)
);


-- redeco.cat_sepomex definition

-- Drop table

-- DROP TABLE redeco.cat_sepomex;

CREATE TABLE redeco.cat_sepomex (
	id_entidad_fed int4 NOT NULL,
	id_municipio int4 NOT NULL,
	municipio varchar NOT NULL,
	id_localidad int4 NULL,
	localidad varchar NULL,
	id_colonia int4 NOT NULL,
	colonia varchar NOT NULL,
	cp int4 NOT NULL,
	CONSTRAINT cat_sepomex_pk PRIMARY KEY (id_entidad_fed, id_municipio, id_colonia, cp)
);


-- redeco.evidencias definition

-- Drop table

-- DROP TABLE redeco.evidencias;

CREATE TABLE redeco.evidencias (
	id varchar NOT NULL,
	num_folio varchar NOT NULL,
	nombre_doc varchar NOT NULL,
	fecha_creacion timestamptz NOT NULL,
	CONSTRAINT evidencias_pk PRIMARY KEY (id)
);


-- redeco.quejas definition

-- Drop table

-- DROP TABLE redeco.quejas;

CREATE TABLE redeco.quejas (
	nombre_cliente varchar NOT NULL,
	correo_cliente varchar NULL,
	telefono_cliente varchar NULL,
	sucursal int4 NOT NULL,
	despacho_cobranza varchar NULL,
	num_credito varchar NOT NULL,
	num_mes int4 NOT NULL,
	num_quejas int4 NOT NULL,
	num_folio varchar NOT NULL,
	fecha_recepcion date NOT NULL,
	id_medio_recepcion int4 NOT NULL,
	id_nivel_atencion int4 NOT NULL,
	id_producto varchar NOT NULL,
	id_causa varchar NOT NULL,
	pori varchar NOT NULL,
	estatus int4 NOT NULL,
	id_entidad_fed int4 NOT NULL,
	id_municipio int4 NOT NULL,
	id_localidad int4 NULL,
	id_colonia int4 NOT NULL,
	cod_postal int4 NOT NULL,
	id_tipo_persona int4 NOT NULL,
	persona_sexo varchar NULL,
	persona_edad int4 NULL,
	fecha_resolucion date NULL,
	fecha_notificacion date NULL,
	id_respuesta_res int4 NULL,
	num_penalizacion int4 NULL,
	id_penalizacion int4 NULL,
	reportado bool DEFAULT false NOT NULL,
	id_empresa int4 NOT NULL,
	CONSTRAINT quejas_pk PRIMARY KEY (num_folio)
);


-- redeco.cat_causas definition

-- Drop table

-- DROP TABLE redeco.cat_causas;

CREATE TABLE redeco.cat_causas (
	id varchar NOT NULL,
	id_producto varchar NOT NULL,
	descripcion varchar NOT NULL,
	institucion varchar NULL,
	CONSTRAINT cat_causas_pk PRIMARY KEY (id, id_producto),
	CONSTRAINT cat_causas_producto_fk FOREIGN KEY (id_producto) REFERENCES redeco.cat_productos(id)
);


-- redeco.cat_consecutivos definition

-- Drop table

-- DROP TABLE redeco.cat_consecutivos;

CREATE TABLE redeco.cat_consecutivos (
	id varchar NOT NULL,
	mask varchar NULL,
	consecutivo int4 NOT NULL,
	id_empresa int4 NOT NULL,
	CONSTRAINT cat_consecutivos_unique UNIQUE (id, id_empresa),
	CONSTRAINT cat_consecutivos_cat_empresas_fk FOREIGN KEY (id_empresa) REFERENCES redeco.cat_empresas(id)
);


-- redeco.periodos definition

-- Drop table

-- DROP TABLE redeco.periodos;

CREATE TABLE redeco.periodos (
	mes int4 NOT NULL,
	anio int4 NOT NULL,
	fecha_inicial date NOT NULL,
	fecha_final date NOT NULL,
	activo bool DEFAULT false NOT NULL,
	fecha_cierre timestamptz NULL,
	id_empresa int4 NOT NULL,
	CONSTRAINT periodos_pk PRIMARY KEY (mes, anio, id_empresa),
	CONSTRAINT periodos_cat_empresas_fk FOREIGN KEY (id_empresa) REFERENCES redeco.cat_empresas(id)
);