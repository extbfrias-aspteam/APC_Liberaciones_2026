from sqlmodel import SQLModel, Field
from sqlalchemy import Column, Integer, String
from typing import Optional
from datetime import datetime

#Conexion a Base de Datos de Cero hacia el schema plasticos.plwh_global_p2p_pay

# Modelo para plasticos.plwh_global_p2p_pay
class PlGlobalP2p(SQLModel, table=True):
    __tablename__ = "plwh_global_p2p_pay"
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

    #json
    json: str | None = Field(default=None)




