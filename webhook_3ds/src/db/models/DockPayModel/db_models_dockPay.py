from sqlmodel import SQLModel, Field
from sqlalchemy import Column, Integer, String
from typing import Optional
from datetime import datetime

#Conexion a Base de Datos de Cero hacia el schema plasticos.plwh_notificaciones_p2p_pay

# Modelo para plasticos.plwh_notificaciones_p2p_pay
class PlNotificacionesP2p(SQLModel, table=True):
    __tablename__ = "plwh_notificaciones_p2p_pay"
    __table_args__ = {"schema": "plasticos"}
    
    #Datos del Modelo
    id: int | None = Field(default=None, primary_key=True)
    
    #Parametros del Event
    event_name: str = Field(nullable=False)
    event_type: str = Field(nullable=False)

    #Parametros 
    trace_id: str = Field(nullable=False)
    event_date: str = Field(nullable=False)

    operation_instance_id: str | None = Field(default=None)
    product_id: str | None = Field(default=None)
    operation_instance_amount: int | None = Field(default=None)
    operation_instance_original_amount: int | None = Field(default=None)
    operation_step_instance_amount: int | None = Field(default=None)
    operation_instance_asset: str | None = Field(default=None)
    operation_step_instance_event_status: str | None = Field(default=None)
    operation_step_instance_event_reason: str | None = Field(default=None)
    description: str | None = Field(default=None)
    external_id: str | None = Field(default=None)
    debtor_product_id: str | None = Field(default=None)
    debtor_person_id: str | None = Field(default=None)
    debtor_account_id: str | None = Field(default=None)
    debtor_name: str | None = Field(default=None)
    debtor_document_type: str | None = Field(default=None)
    debtor_document: str | None = Field(default=None)
    debtor_asset: str | None = Field(default=None)
    debtor_category_code: str | None = Field(default=None)
    creditor_product_id: str | None = Field(default=None)
    creditor_person_id: str | None = Field(default=None)
    creditor_account_id: str | None = Field(default=None)
    creditor_name: str | None = Field(default=None)
    creditor_document_type: str | None = Field(default=None)
    creditor_document: str | None = Field(default=None)
    creditor_category_code: str | None = Field(default=None)


# Modelo para plasticos.plwh_notificaciones_p2p_pay

class PlAhTransaccionesCuentas(SQLModel, table=True):
    #TABLA Y SCHEMA
    __tablename__ = "ahtransacciones_cuentas"
    __table_args__ = {"schema": "ahorro"}

    #Datos del modelo
    id: Optional[int] = Field(default=None, primary_key=True)
    cuenta_id: int
    tipo_transaccion_id: int
    fecha: datetime
    monto: float
    descripcion: Optional[str] = None
    num_autorizacion: Optional[str] = None
    estatus_id: int
    forma_pago_id: int
    usuario_creacion: int
    fecha_creacion: datetime
    usuario_modificacion: Optional[int] = None
    fecha_modificacion: Optional[datetime] = None
    conciliado: Optional[bool] = None
    tipo_clave: Optional[str] = None
    saldo_anterior: Optional[float] = None
    saldo_actual: Optional[float] = None
    sucursal_id: int
    canal_id: int
    usuario_id: int
    cliente_id: int
    host: Optional[str] = None
    clave_rastreo: Optional[str] = None
    banco_id: Optional[int] = None
    cuenta_referencia: Optional[str] = None
    importacion_id: Optional[int] = None
    poliza: Optional[str] = None
    poliza_provision: Optional[str] = None
    status_notificacion: Optional[int] = None
    id_spei: Optional[int] = None
    fecha_notificacion: Optional[datetime] = None
    status_saldo: Optional[int] = None

