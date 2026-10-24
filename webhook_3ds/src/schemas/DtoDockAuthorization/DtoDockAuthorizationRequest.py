from pydantic import BaseModel,Field
from typing import Optional


class TransactionValues(BaseModel):
    billing_value: Optional[str] = None
    settlement_value: Optional[str] = None
    source_value: Optional[str] = None
    billing_currency_code: Optional[int] = None
    source_currency_code: Optional[int] = None
    settlement_currency_code: Optional[int] = None


class OriginalTransactionData(BaseModel):
    original_transaction_uuid: Optional[str] = None
    original_nsu: Optional[str] = None
    original_mti: Optional[str] = None
    original_date_time: Optional[str] = None
    original_transmission_date_time_gmt: Optional[str] = None
    original_institution_code: Optional[str] = None
    original_institution_name: Optional[str] = None
    original_accounting_date: Optional[str] = None
    original_transaction_identifier: Optional[str] = None


class ProcessingInfo(BaseModel):
    type: Optional[str] = None
    origin_account_type: Optional[str] = None
    destiny_account_type: Optional[str] = None
    code: Optional[str] = None



class CardEntryInfo(BaseModel):
    card_entry_code: Optional[str] = None
    card_entry_pin: Optional[str] = None
    card_entry_mode: Optional[str] = None


class TransactionIndicators(BaseModel):
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

class SpendingControlData(BaseModel):
    spending_trace_id: Optional[str] = None
    spending_category: Optional[str] = None
    spending_group: Optional[str] = None
    spending_limit: Optional[str] = None
    spending_limit_min: Optional[int] = None
    spending_limit_max: Optional[int] = None
    spending_request_value: Optional[int] = None
    spending_summarized_value: Optional[int] = None
    spending_accumulated_value: Optional[int] = None


class GlobalAuthorizationPayload(BaseModel):
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

    values: Optional[TransactionValues] = None
    original_transaction_data: Optional[OriginalTransactionData] = None
    processing: Optional[ProcessingInfo] = None
    card_entry: Optional[CardEntryInfo] = None
    transaction_indicators: Optional[TransactionIndicators] = None
    spending_control_response_data: Optional[SpendingControlData] = None



class GlobalAuthorizationProperties(BaseModel):
    event_date: str
    trace_id: str


class GlobalAuthorizationRequest(BaseModel):
    event_name: str = Field(..., description="Nombre del evento.")
    event_type: str = Field(..., description="Tipo de evento.")
    payload: GlobalAuthorizationPayload = Field(..., description="Cuerpo principal del evento")
    properties: GlobalAuthorizationProperties = Field(..., description="Propiedades generales del evento")


class EncryptedGlobalAuthorizationRequest(BaseModel):
    encrypted_message: str