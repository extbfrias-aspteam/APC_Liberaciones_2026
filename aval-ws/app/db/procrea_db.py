from sqlalchemy import text
from app.db.database import db

def get_solicitante(app, numero):
    engine = db.get_engine(app, bind='procrea')
    query = text("""
        SELECT 
            s.numero,
            s.nombre,
            s.curp,
            s.domicilio,
            s.telefono,
            s.celular,
            s.rfc1,
            s.rfc2,
            s.rfc3,
            s.t_persona,
            s.originario,
            s.credencial_ife,
            s.domicilio,
            n.descripcion AS nacionalidad,
            e.nombre AS edocivil,
            o.descripcion AS ocupacion
        FROM solicitante s
        LEFT JOIN nacionalidades n ON n.clave = s.cve_nacionalidad
        LEFT JOIN edocivil e       ON e.clave = s.edo_civil
        LEFT JOIN ocupaciones o    ON o.clave = s.cve_ocupacion
        WHERE s.numero = :numero;
    """)
    with engine.connect() as conn:
        result = conn.execute(query, {"numero": numero}).mappings().first()
        return dict(result) if result else None
