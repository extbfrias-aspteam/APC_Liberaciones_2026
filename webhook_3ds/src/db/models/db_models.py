from sqlmodel import SQLModel, Field
from datetime import datetime, timezone
from typing import Optional

class EnvVar(SQLModel, table=True):
    __tablename__ = "env_vars"
    key: str = Field(primary_key=True)
    value: str


# Modelo para plasticos.pladministracion_tarjetas
class PlAdministracionTarjetas(SQLModel, table=True):
    __tablename__ = "pladministracion_tarjetas"
    __table_args__ = {"schema": "plasticos"}
    
    id_tarjeta_dock: int = Field(primary_key=True)
    id_solicitante: int = Field(nullable=False)

# Modelo para directorio_telefonico.telefono
class Telefono(SQLModel, table=True):
    __tablename__ = "directorio_telefonico"
    __table_args__ = {"schema": "public"}
    
    id_directorio_telefonico: Optional[int] = Field(
        default=None,
        primary_key=True
    )
    id_cat_telefono: int = Field(nullable=False)
    id_solicitante: int = Field(nullable=False)
    # Campo para el número de teléfono:
    telefono: str = Field(default=None, nullable=True)


class LogSMS(SQLModel, table=True):
    __tablename__ = "log_sms"
    __table_args__ = {"schema": "public"}
    
    id: int = Field(default=None, primary_key=True)
    card_id: str
    phone: str
    otp: str
    sms_response: str
    success: bool
    sent_at: datetime = Field(default_factory= lambda: datetime.now(timezone.utc))


class NcPosicionGlobalAH(SQLModel, table=True):
    __tablename__  = "ncposicionglobalah"
    __table_args__ = {"schema": "nucleocentral"}

    tarjeta_principal: str = Field(..., alias="tarjeta_principal", primary_key=True)
    idpersona:           int = Field(..., alias="idpersona")