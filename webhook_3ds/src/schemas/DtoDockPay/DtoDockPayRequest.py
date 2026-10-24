from pydantic import BaseModel,Field
from typing import Optional


# Estos modelos se estaran dando de Alta en SchemasGeneral/dock_schema_modelos.py

#Clase del Payload (Atributos del payload) 
class DtoPayload_dock_pay(BaseModel):
        operation_instance_id: Optional[str] = Field(default=None)
        product_id: Optional[str] = Field(default=None)
        operation_instance_amount: Optional[int] = Field(default=None)
        operation_instance_original_amount: Optional[int] = Field(default=None)
        operation_step_instance_amount: Optional[int] = Field(default=None)
        operation_instance_asset: Optional[str] = Field(default=None)
        operation_step_instance_event_status: Optional[str] = Field(default=None)
        operation_step_instance_event_reason: Optional[str] = Field(default=None)
        description: Optional[str] = Field(default=None)
        external_id: Optional[str] = Field(default=None)
        debtor_product_id: Optional[str] = Field(default=None)
        debtor_person_id: Optional[str] = Field(default=None)
        debtor_account_id: Optional[str] = Field(default=None)
        debtor_name: Optional[str] = Field(default=None)
        debtor_document_type: Optional[str] = Field(default=None)
        debtor_document: Optional[str] = Field(default=None)
        debtor_asset: Optional[str] = Field(default=None)
        debtor_category_code: Optional[str] = Field(default=None)
        creditor_product_id: Optional[str] = Field(default=None)
        creditor_person_id: Optional[str] = Field(default=None)
        creditor_account_id: Optional[str] = Field(default=None)
        creditor_name: Optional[str] = Field(default=None)
        creditor_document_type: Optional[str] = Field(default=None)
        creditor_document: Optional[str] = Field(default=None)
        creditor_category_code: Optional[str] = Field(default=None)

class Properties(BaseModel):
    event_date: str = Field(..., description="Creación de evento")
    trace_id: str = Field(..., description="Identificador único de evento")



class EventModel(BaseModel):
    event_name: str = Field(..., description="Representa número de evento.")
    event_type: str = Field(..., description="Representa el tipo de evento.")
    payload: DtoPayload_dock_pay = Field(..., description="Campos de datos de eventos")
    properties: Properties = Field(...)




class EncryptedPayload(BaseModel):
    encrypted_message: str





