from sqlalchemy import Column, Integer, String, SmallInteger, Date
from app.db.database import db

class SoAval(db.Model):
    __tablename__ = 'soavales'
    __table_args__ = {'schema': 'solicitud'}

    soavales_id = db.Column(db.Integer, primary_key=True)
    id_solicitud = db.Column(db.Integer)
    id_solicitante = db.Column(db.String(10), nullable=False)
    nombre_aval = db.Column(db.String(100), nullable=False)
    creado_por = db.Column(db.SmallInteger)
    fecha_creacion = db.Column(db.Date)
    modificado_por = db.Column(db.SmallInteger)
    fecha_modificacion = db.Column(db.Date)
    ncrelaciones_id = db.Column(db.SmallInteger)
