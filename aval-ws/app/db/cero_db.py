from sqlalchemy import text
from app.db.database import db
from datetime import date

def get_catalogo_parentesco(app):
    engine = db.get_engine(app, bind='cero')
    query = text("""
        SELECT 
            id,
            descripcion 
        FROM nucleocentral.ncrelaciones
    """)
    with engine.connect() as conn:
        result = conn.execute(query).mappings().all()
        return [dict(row) for row in result] if result else []

def get_aval_by_id_solicitud(app, id_solicitud: int):
    engine = db.get_engine(app, bind='cero')

    query = text("""
        SELECT 
            id_solicitud,
            id_solicitante,
            nombre_aval,
            ncrelaciones_id
        FROM solicitud.soavales
        WHERE id_solicitud = :id_solicitud
    """)

    with engine.connect() as conn:
        result = conn.execute(query, {"id_solicitud": id_solicitud}).mappings().first()
        return dict(result) if result else None

def insertar_aval(app, data: dict) -> bool:
    engine = db.get_engine(app, bind='cero')

    query = text("""
        INSERT INTO solicitud.soavales (
            id_solicitud, id_solicitante, nombre_aval,
            creado_por, fecha_creacion, modificado_por,
            fecha_modificacion, ncrelaciones_id
        ) VALUES (
            :id_solicitud, :id_solicitante, :nombre_aval,
            :creado_por, :fecha_creacion, :modificado_por,
            :fecha_modificacion, :ncrelaciones_id
        )
    """)

    try:
        with engine.begin() as conn:  # Maneja automáticamente commit/rollback
            conn.execute(query, {
                "id_solicitud": data.get("id_solicitud"),
                "id_solicitante": data.get("id_solicitante"),
                "nombre_aval": data.get("nombre_aval"),
                "creado_por": data.get("creado_por"),
                "fecha_creacion": date.today(),
                "modificado_por": data.get("modificado_por"),
                "fecha_modificacion": date.today(),
                "ncrelaciones_id": data.get("ncrelaciones_id"),
            })
        return True
    except Exception as e:
        raise Exception(f"Error al insertar aval: {e}")

def actualizar_aval(app, id_solicitud: int, data: dict):
    engine = db.get_engine(app, bind='cero')
    query = text("""
        UPDATE solicitud.soavales
        SET 
            id_solicitante = :id_solicitante,
            nombre_aval = :nombre_aval,
            modificado_por = :modificado_por,
            fecha_modificacion = CURRENT_DATE,
            ncrelaciones_id = :ncrelaciones_id
        WHERE id_solicitud = :id_solicitud
    """)
    with engine.begin() as conn:
        conn.execute(query, **data)

def eliminar_aval_por_id(app, id_solicitud: int) -> bool:
    engine = db.get_engine(app, bind='cero')
    query = text("""
        DELETE FROM solicitud.soavales
        WHERE id_solicitud = :id_solicitud
    """)
    with engine.begin() as conn:
        result = conn.execute(query, {"id_solicitud": id_solicitud})
        return result.rowcount > 0 

