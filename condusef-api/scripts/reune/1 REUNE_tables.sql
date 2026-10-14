-- Table: reune.cat_productos

-- DROP TABLE IF EXISTS reune.cat_productos;

CREATE TABLE IF NOT EXISTS reune.cat_productos
(
    id character varying COLLATE pg_catalog."default" NOT NULL,
    tipo_operacion character varying COLLATE pg_catalog."default" NOT NULL,
    producto character varying COLLATE pg_catalog."default" NOT NULL,
    subproducto character varying COLLATE pg_catalog."default" NOT NULL,
    id_institucion integer DEFAULT 1,
    CONSTRAINT cat_productos_pk PRIMARY KEY (id)
)
-- Table: reune.cat_causas

-- DROP TABLE IF EXISTS reune.cat_causas;

CREATE TABLE IF NOT EXISTS reune.cat_causas
(
    id integer NOT NULL,
    id_producto character varying COLLATE pg_catalog."default" NOT NULL,
    descripcion character varying COLLATE pg_catalog."default" NOT NULL,
    consulta smallint NOT NULL DEFAULT 0,
    reclamacion smallint NOT NULL DEFAULT 0,
    aclaracion smallint NOT NULL DEFAULT 0,
    id_institucion integer DEFAULT 1,
    CONSTRAINT cat_causas_pk PRIMARY KEY (id, id_producto),
    CONSTRAINT cat_causas_fk FOREIGN KEY (id_producto)
        REFERENCES reune.cat_productos (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

-- Table: reune.cat_medios_recepcion

-- DROP TABLE IF EXISTS reune.cat_medios_recepcion;

CREATE TABLE IF NOT EXISTS reune.cat_medios_recepcion
(
    id integer NOT NULL,
    nombre character varying COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT cat_medios_recepcion_pk PRIMARY KEY (id)
)

-- Table: reune.cat_niveles_atencion

-- DROP TABLE IF EXISTS reune.cat_niveles_atencion;

CREATE TABLE IF NOT EXISTS reune.cat_niveles_atencion
(
    id integer NOT NULL,
    nombre character varying COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT cat_niveles_atencion_pk PRIMARY KEY (id)
)

-- Table: reune.periodos

-- DROP TABLE IF EXISTS reune.periodos;

CREATE TABLE IF NOT EXISTS reune.periodos
(
    mes integer NOT NULL,
    anio integer NOT NULL,
    fecha_inicial date NOT NULL,
    fecha_final date NOT NULL,
    activo boolean NOT NULL DEFAULT false,
    fecha_cierre timestamp with time zone,
    id_empresa integer NOT NULL,
    CONSTRAINT periodos_pk PRIMARY KEY (mes, anio, id_empresa)
)

-- Table: reune.aclaraciones

-- DROP TABLE IF EXISTS reune.aclaraciones;

CREATE TABLE IF NOT EXISTS reune.aclaraciones
(
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    periodo integer NOT NULL,
    aclaracion_numero integer NOT NULL,
    folio character varying(255) COLLATE pg_catalog."default" NOT NULL,
    estatus integer NOT NULL,
    aclaracion_fecha_reclamacion date NOT NULL,
    aclaracion_fecha_atencion date,
    aclaracion_medio_recepcion_canal integer NOT NULL,
    id_producto character varying(255) COLLATE pg_catalog."default" NOT NULL,
    id_causa integer NOT NULL,
    aclaracion_fecha_resolucion date,
    aclaracion_fecha_notifi_usuario date,
    id_aclaracion_entidad_federativa integer NOT NULL,
    aclaracion_codigo_postal numeric(10,0),
    id_aclaracion_municipio_alcaldia integer NOT NULL,
    id_aclaracion_localidad integer,
    id_aclaracion_colonia integer,
    aclaracion_monetario character varying(255) COLLATE pg_catalog."default" NOT NULL,
    aclaracion_monto_reclamado numeric,
    aclaracion_pori character varying(255) COLLATE pg_catalog."default" NOT NULL,
    aclaracion_tipo_persona integer NOT NULL,
    aclaracion_sexo character varying(255) COLLATE pg_catalog."default",
    aclaracion_edad integer,
    id_nivel_atencion integer,
    aclaracion_folio_condusef character varying(255) COLLATE pg_catalog."default",
    aclaracion_reversa integer,
    aclaracion_operacion_extranjero character varying(255) COLLATE pg_catalog."default" NOT NULL,
    sucursal integer NOT NULL,
    reportado boolean NOT NULL DEFAULT false,
    id_aclaracion_nivel_atencion integer,
    id_empresa integer,
    CONSTRAINT aclaraciones_pk PRIMARY KEY (id),
    CONSTRAINT aclaraciones_empresa_fk FOREIGN KEY (id_empresa)
        REFERENCES redeco.cat_empresas (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID,
    CONSTRAINT aclaraciones_fk FOREIGN KEY (id_producto)
        REFERENCES reune.cat_productos (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

-- Table: reune.consultas

-- DROP TABLE IF EXISTS reune.consultas;

CREATE TABLE IF NOT EXISTS reune.consultas
(
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    periodo integer NOT NULL,
    num_consultas integer NOT NULL,
    folio character varying(255) COLLATE pg_catalog."default" NOT NULL,
    estatus integer NOT NULL,
    consultas_fec_aten date,
    id_estados integer NOT NULL,
    consultas_fec_recepcion date NOT NULL,
    id_medios integer NOT NULL,
    id_producto character varying COLLATE pg_catalog."default" NOT NULL,
    id_causa integer NOT NULL,
    consultas_cp numeric(10,0),
    id_consultas_mpio integer NOT NULL,
    id_consultas_loc integer,
    id_consultas_col integer,
    id_consultascatnivelaten integer,
    consultas_pori character varying(255) COLLATE pg_catalog."default" NOT NULL,
    sucursal integer NOT NULL,
    reportado boolean NOT NULL DEFAULT false,
    id_empresa integer,
    CONSTRAINT consultas_pk PRIMARY KEY (id),
    CONSTRAINT consultas_fk FOREIGN KEY (id_causa, id_producto)
        REFERENCES reune.cat_causas (id, id_producto) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

-- Table: reune.reclamaciones

-- DROP TABLE IF EXISTS reune.reclamaciones;

CREATE TABLE IF NOT EXISTS reune.reclamaciones
(
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    periodo integer NOT NULL,
    rec_numero integer NOT NULL,
    folio character varying(255) COLLATE pg_catalog."default" NOT NULL,
    estatus integer NOT NULL,
    rec_fecha_reclamacion date NOT NULL,
    rec_fecha_atencion date,
    rec_medio_recepcion_canal integer NOT NULL,
    id_producto character varying COLLATE pg_catalog."default" NOT NULL,
    id_causa integer NOT NULL,
    rec_fecha_resolucion date,
    rec_fecha_notifi_usuario date,
    id_rec_entidad_federativa integer NOT NULL,
    rec_codigo_postal numeric(10,0),
    id_rec_municipio_alcaldia integer NOT NULL,
    id_rec_localidad integer,
    id_rec_colonia integer,
    rec_monetario character varying(255) COLLATE pg_catalog."default" NOT NULL,
    rec_monto_reclamado numeric,
    rec_importe_abonado numeric,
    rec_fecha_abono_importe date,
    rec_pori character varying(255) COLLATE pg_catalog."default" NOT NULL,
    rec_tipo_persona integer NOT NULL,
    rec_sexo character varying(255) COLLATE pg_catalog."default",
    rec_edad integer,
    rec_sentido_resolucion integer,
    id_nivel_atencion integer,
    rec_folio_condusef character varying(255) COLLATE pg_catalog."default",
    rec_reversa integer,
    sucursal integer NOT NULL,
    reportado boolean NOT NULL DEFAULT false,
    id_empresa integer,
    CONSTRAINT reclamaciones_pk PRIMARY KEY (id),
    CONSTRAINT reclamaciones_fk FOREIGN KEY (id_causa, id_producto)
        REFERENCES reune.cat_causas (id, id_producto) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
