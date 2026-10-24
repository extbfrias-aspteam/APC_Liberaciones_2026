# Librerias 
from sqlmodel import SQLModel, Field
from sqlalchemy import Column, Integer, String
from typing import Optional
from datetime import datetime


#Conexion a Base de Datos de Cero hacia el schema plasticos.global_authorization_notification

class PlNotificacionesAuthorization(SQLModel,table=True):
    __tablename__ = "global_authorization_notification"
    __table_args__ = {"schema": "plasticos"}

    #Datos del Modelo
    id: int | None = Field(default=None, primary_key=True)

    # Nivel raíz
    event_name: str
    event_type: str

    # Payload
    terminal_code: Optional[str] = None
    reference_number: Optional[str] = None
    card_status: Optional[str] = None
    transaction_type_indicator: Optional[str] = None
    nsu: Optional[str] = None
    pos_postal_code: Optional[str] = None
    acquirer_country_code: Optional[str] = None
    establishment: Optional[str] = None
    establishment_code: Optional[str] = None
    transaction_origin: Optional[str] = None
    transmission_date_time_gmt: Optional[str] = None
    merchant_category_code: Optional[str] = None
    brand_response_code: Optional[str] = None
    authorizer_response_detail: Optional[str] = None
    transaction_uuid: Optional[str] = None
    installments: Optional[int] = None
    installments_type: Optional[str] = None
    card_id: Optional[str] = None
    account_id: Optional[str] = None
    external_account_id: Optional[str] = None
    endpoint: Optional[str] = None
    mti: Optional[str] = None
    authorization_code: Optional[str] = None

    # Payload.values
    billing_value: Optional[str] = None
    settlement_value: Optional[str] = None
    source_value: Optional[str] = None
    billing_currency_code: Optional[int] = None
    source_currency_code: Optional[int] = None
    settlement_currency_code: Optional[int] = None

    # Payload.original_transaction_data
    original_transaction_uuid: Optional[str] = None
    original_nsu: Optional[str] = None
    original_mti: Optional[str] = None
    original_date_time: Optional[str] = None
    original_transmission_date_time_gmt: Optional[str] = None
    original_institution_code: Optional[str] = None
    original_institution_name: Optional[str] = None
    original_accounting_date: Optional[str] = None
    original_transaction_identifier: Optional[str] = None

    # Payload.processing
    processing_type: Optional[str] = None
    origin_account_type: Optional[str] = None
    destiny_account_type: Optional[str] = None
    processing_code: Optional[str] = None

    # Payload.card_entry
    card_entry_code: Optional[str] = None
    card_entry_pin: Optional[str] = None
    card_entry_mode: Optional[str] = None

    # Payload.transaction_indicators
    card_present: Optional[bool] = None
    cardholder_present: Optional[bool] = None
    password_present: Optional[bool] = None
    cvv1_present: Optional[bool] = None
    cvv2_present: Optional[bool] = None
    cvv3_present: Optional[bool] = None
    token_present: Optional[bool] = None
    is_3ds_present: Optional[bool] = None
    is_3ds_valid: Optional[bool] = None
    recurring: Optional[bool] = None
    allows_partial_approval: Optional[bool] = None
    pin_validated_offline: Optional[bool] = None
    partially_reversed: Optional[bool] = None
    preauthorization: Optional[bool] = None
    is_crossborder: Optional[bool] = None
    is_dcc: Optional[bool] = None
    only_supports_purchase: Optional[bool] = None
    is_international: Optional[bool] = None
    is_funds_transfer: Optional[bool] = None
    is_automated_fuel_dispenser: Optional[bool] = None
    is_ecommerce: Optional[bool] = None

    # Payload.spending_control_response_data
    spending_trace_id: Optional[str] = None
    spending_category: Optional[str] = None
    spending_group: Optional[str] = None
    spending_limit: Optional[str] = None
    spending_limit_min: Optional[int] = None
    spending_limit_max: Optional[int] = None
    spending_request_value: Optional[int] = None
    spending_summarized_value: Optional[int] = None
    spending_accumulated_value: Optional[int] = None

    # properties
    event_date: str
    trace_id: str
    json: str



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

