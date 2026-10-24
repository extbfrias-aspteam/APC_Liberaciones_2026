from pydantic import BaseModel, Field

class Payload(BaseModel):
    card_id: str = Field(..., description="El ID de la tarjeta se está autenticando en el flujo de 3DS.	")
    transaction_id: str = Field(..., description="ID de transacción único en el flujo de autenticación 3DS.	")
    otp_value: str = Field(..., description="Código de validación de transacción en el flujo de autenticación 3DS.	")
    card_number: str = Field(..., description="Número de tarjeta enmascarado.	")
    merchant_name: str = Field(..., description="Nombre del establecimiento.	")
    merchant_country: str = Field(..., description="País de establecimiento.	")
    merchant_url: str = Field(..., description="URL de establecimiento	")
    purchase_currency_acronym: str = Field(..., description="Siglas de la moneda.	")
    purchase_currency: int = Field(..., description="Moneda de transacción.	")
    purchase_amount: float = Field(..., description="Valor de transacción.	")
    purchase_date: str = Field(..., description="Fecha de la transacción	")

class Properties(BaseModel):
    event_date: str = Field(..., description="Creación de evento")
    trace_id: str = Field(..., description="Identificador único de evento")

class EventModel(BaseModel):
    event_name: str = Field(..., description="Representa número de evento.")
    event_type: str = Field(..., description="Representa el tipo de evento.")
    payload: Payload = Field(..., description="Campos de datos de eventos")
    properties: Properties = Field(...)

class EncryptedPayload(BaseModel):
    encrypted_message: str

