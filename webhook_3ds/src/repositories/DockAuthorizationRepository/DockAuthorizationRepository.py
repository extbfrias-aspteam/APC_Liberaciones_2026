from sqlmodel import Session
from typing import Optional
from src.db.models.DockAuthorization.db_models_dockAuthorization  import (
    PlNotificacionesAuthorization,
    PlAhTransaccionesCuentas
) 

from src.schemas.DtoDockAuthorization.DtoDockAuthorizationRequest import GlobalAuthorizationRequest
from src.schemas.DtoTransacciones.DtoAhorro_transacciones import AhorroOperacionDTO
from src.utils.logger import logger
from sqlalchemy import text

from datetime import datetime
from zoneinfo import ZoneInfo

#Funcion para insert

def insert_notificaciones_repository(session: Session, data: GlobalAuthorizationRequest, json: str) -> PlNotificacionesAuthorization:
   
    # Corta referencias anidadas para facilidad
    payload = data.payload
    values = payload.values if payload else None
    indicators = payload.transaction_indicators if payload else None
    original_data = payload.original_transaction_data if payload else None
    processing = payload.processing if payload else None
    card_entry = payload.card_entry if payload else None
    spending = payload.spending_control_response_data if payload else None
    properties = data.properties


    nueva_operacion = PlNotificacionesAuthorization(
        event_name=data.event_name,
        event_type=data.event_type,
        
        # Nivel 1 General
        terminal_code=payload.terminal_code if payload else None,
        reference_number=payload.reference_number if payload else None,
        card_status=payload.card_status if payload else None,
        transaction_type_indicator=payload.transaction_type_indicator if payload else None,
        nsu=payload.nsu if payload else None,
        pos_postal_code=payload.pos_postal_code if payload else None,
        acquirer_country_code=payload.acquirer_country_code if payload else None,
        establishment=payload.establishment if payload else None,
        establishment_code=payload.establishment_code if payload else None,
        transaction_origin=payload.transaction_origin if payload else None,
        transmission_date_time_gmt=payload.transmission_date_time_gmt if payload else None,
        merchant_category_code=payload.merchant_category_code if payload else None,
        brand_response_code=payload.brand_response_code if payload else None,
        authorizer_response_detail=payload.authorizer_response_detail if payload else None,
        transaction_uuid=payload.transaction_uuid if payload else None,
        installments=payload.installments if payload else None,
        installments_type=payload.installments_type if payload else None,
        card_id=payload.card_id if payload else None,
        account_id=payload.account_id if payload else None,
        external_account_id=payload.external_account_id if payload else None,
        endpoint=payload.endpoint if payload else None,
        mti=payload.mti if payload else None,
        authorization_code=payload.authorization_code if payload else None,

        # Nivel 2 - values
        billing_value=values.billing_value if values else None,
        settlement_value=values.settlement_value if values else None,
        source_value=values.source_value if values else None,
        billing_currency_code=values.billing_currency_code if values else None,
        source_currency_code=values.source_currency_code if values else None,
        settlement_currency_code=values.settlement_currency_code if values else None,

        # Nivel 2 - transaction_indicators
        card_present=indicators.card_present if indicators else None,
        cardholder_present=indicators.cardholder_present if indicators else None,
        password_present=indicators.password_present if indicators else None,
        cvv1_present=indicators.cvv1_present if indicators else None,
        cvv2_present=indicators.cvv2_present if indicators else None,
        cvv3_present=indicators.cvv3_present if indicators else None,
        token_present=indicators.token_present if indicators else None,
        is_3ds_present=indicators.is_3ds_present if indicators else None,
        is_3ds_valid=indicators.is_3ds_valid if indicators else None,
        recurring=indicators.recurring if indicators else None,
        allows_partial_approval=indicators.allows_partial_approval if indicators else None,
        pin_validated_offline=indicators.pin_validated_offline if indicators else None,
        partially_reversed=indicators.partially_reversed if indicators else None,
        preauthorization=indicators.preauthorization if indicators else None,
        is_crossborder=indicators.is_crossborder if indicators else None,
        is_dcc=indicators.is_dcc if indicators else None,
        only_supports_purchase=indicators.only_supports_purchase if indicators else None,
        is_international=indicators.is_international if indicators else None,
        is_funds_transfer=indicators.is_funds_transfer if indicators else None,
        is_automated_fuel_dispenser=indicators.is_automated_fuel_dispenser if indicators else None,
        is_ecommerce=indicators.is_ecommerce if indicators else None,

        # Nivel 2 - original_transaction_data
        original_transaction_uuid=original_data.original_transaction_uuid if original_data else None,
        original_nsu=original_data.original_nsu if original_data else None,
        original_mti=original_data.original_mti if original_data else None,
        original_date_time=original_data.original_date_time if original_data else None,
        original_transmission_date_time_gmt=original_data.original_transmission_date_time_gmt if original_data else None,
        original_institution_code=original_data.original_institution_code if original_data else None,
        original_institution_name=original_data.original_institution_name if original_data else None,
        original_accounting_date=original_data.original_accounting_date if original_data else None,
        original_transaction_identifier=original_data.original_transaction_identifier if original_data else None,

        # Nivel 2 - processing
        processing_type=processing.type if processing else None,
        origin_account_type=processing.origin_account_type if processing else None,
        destiny_account_type=processing.destiny_account_type if processing else None,
        processing_code=processing.code if processing else None,

        # Nivel 2 - card_entry
        card_entry_code=card_entry.card_entry_code if card_entry else None,
        card_entry_pin=card_entry.card_entry_pin if card_entry else None,
        card_entry_mode=card_entry.card_entry_mode if card_entry else None,

        # Nivel 2 - spending_control_response_data
        spending_trace_id=spending.spending_trace_id if spending else None,
        spending_category=spending.spending_category if spending else None,
        spending_group=spending.spending_group if spending else None,
        spending_limit=spending.spending_limit if spending else None,
        spending_limit_min=spending.spending_limit_min if spending else None,
        spending_limit_max=spending.spending_limit_max if spending else None,
        spending_request_value=spending.spending_request_value if spending else None,
        spending_summarized_value=spending.spending_summarized_value if spending else None,
        spending_accumulated_value=spending.spending_accumulated_value if spending else None,

        # Nivel 1 - properties
        trace_id=properties.trace_id,
        event_date=properties.event_date,
        json=json,
    )

    session.add(nueva_operacion)
    session.commit()
    session.refresh(nueva_operacion)
    return nueva_operacion

def insert_ahtransacciones_cuentas(sesion: Session, data: GlobalAuthorizationRequest, cuenta_asp_id: str, ultimo_saldo:float, persona_id:str, clave_trans:str) -> PlAhTransaccionesCuentas:
    source_value = float(data.payload.values.source_value)
    # Calcula el saldo actualizado
    saldoAcutalizado = ultimo_saldo - source_value
    transaccion_ahcuentas_transacciones_cuentas = PlAhTransaccionesCuentas(
        cuenta_id=cuenta_asp_id,
        tipo_transaccion_id=2,
        fecha=datetime.now(ZoneInfo("America/Mexico_City")),
        monto=data.payload.values.source_value,
        descripcion=data.payload.establishment or "",
        num_autorizacion=data.payload.authorization_code or "",
        estatus_id=1,
        forma_pago_id=101,
        usuario_creacion=10009,
        fecha_creacion=datetime.now(ZoneInfo("America/Mexico_City")),
        usuario_modificacion=None,
        fecha_modificacion=None,
        conciliado=None,
        tipo_clave=clave_trans,
        saldo_anterior=ultimo_saldo,
        saldo_actual=saldoAcutalizado,
        sucursal_id=1,
        canal_id=1,
        usuario_id=10009,
        cliente_id=persona_id,
        host="127.0.0.1",
        clave_rastreo=data.payload.transaction_uuid,
        banco_id=659,
        cuenta_referencia="659803000000244333",
        importacion_id=None,
        poliza=None,
        poliza_provision=None,
        status_notificacion=0,
        id_spei=None,
        fecha_notificacion=None,
        status_saldo=1
    )

    sesion.add(transaccion_ahcuentas_transacciones_cuentas)
    sesion.commit()
    sesion.refresh(transaccion_ahcuentas_transacciones_cuentas)

    return transaccion_ahcuentas_transacciones_cuentas

def pl_insert_ahtransacciones_cuentas(
    sesion: Session,
    op: AhorroOperacionDTO,
    cuenta_asp_id: str,
    persona_id: str,
    clave_trans: str,
):
    source_value = float(op.monto)

    if clave_trans == "RET":
        movimiento = 0
    else:
        movimiento = 1

    result = sesion.execute(text("""
        SELECT * FROM ahorro.insertar_transaccion_saldo_global(
            :cuenta_id, :monto, :descripcion, :autorizacion, :tipo_trx,
            :id_usuario, :id_sucursal, :id_canal, :id_forma_pago,
            :id_persona, :id_banco, :id_estatus, :conciliado, :ip_host,
            :clave_rastreo, :cuenta_referencia, :id_spei, :movimiento
        )
    """), {
        "cuenta_id": int(cuenta_asp_id),
        "monto": source_value,
        "descripcion": op.descripcion or "",
        "autorizacion": op.autorizacion or "",
        "tipo_trx": clave_trans,
        "id_usuario": 10009,
        "id_sucursal": 1,
        "id_canal": 1,
        "id_forma_pago": op.tipo_pago,
        "id_persona": persona_id,
        "id_banco": op.banco,
        "id_estatus": 1,
        "conciliado": "",
        "ip_host": "172.17.8.167",
        "clave_rastreo": op.clave_rastreo,
        "cuenta_referencia": "",
        "id_spei": None,
        "movimiento": movimiento
    }).fetchone()

    

    sesion.commit()
    print(result)

    #Validaciones del Pl
    if not result:
        logger.error("La función insertar_transaccion_saldo_global no regresó resultado")
        return None
    
    # Acceso por índice (porque hiciste SELECT * ...):
    id_result = result[1]
   
    # Si quieres devolver solo el ID de la transacción:
    return id_result



#Funcion para traer el id de la tabla pldatos_dockone
def obtener_datos_dockone(cuenta_interna: str,session: Session,):

     # Query SQL cruda
    query = text("""
        SELECT cuenta_asp
        FROM plasticos.pldatos_dockone
        WHERE tipo_id = :tipo_id
          AND estatus = :estatus
          AND id_dockone = :id_dockone
        LIMIT 1
    """)

    # Ejecutar el query
    result = session.execute(query, {
        "tipo_id": 2,
        "estatus": "ACTIVO",
        "id_dockone": cuenta_interna
    }).fetchone()

    # Validar resultado
    if result:
        id_cuenta = result[0]
        logger.info(f"cuenta ASP encontrado: {id_cuenta}")
        return id_cuenta
    else:
        logger.warning("No se encontró ninguna cuenta ASP con esos criterios.")
        return None
    
    #Funcion para traer el id de la tabla pldatos_dockone
def obtener_persona_id(cuenta_asp: str,session: Session):

     # Query SQL cruda
    query = text("""
        SELECT persona_id FROM ahorro.ahcuentas 
        WHERE cuenta = :cuenta
        LIMIT 1
    """)

    # Ejecutar el query
    result = session.execute(query, {
        "cuenta": cuenta_asp
    }).fetchone()

    # Validar resultado
    if result:
        persona_id = result[0]
        logger.info(f"persona id encontrado: {persona_id}")
        return persona_id
    else:
        logger.warning("No se encontró ninguna cuenta ASP con esos criterios.")
        return None




#Funcion para traer el id de la cuenta asp en ahcuentas 
def obtener_id_ahcuenta(cuenta_asp: str,session: Session,):
  #Obtener el Cuenta ASP
    logger.info(f"Parámetros de la CuentaASP: {cuenta_asp}")

     # Query SQL cruda
    query = text("""
        SELECT id
        FROM ahorro.ahcuentas
        WHERE cuenta = :cuenta
        LIMIT 1
    """)

    # Ejecutar el query
    result = session.execute(query, {
        "cuenta": cuenta_asp
    }).fetchone()

    # Validar resultado
    if result:
        cuenta_asp_ahcuenta = result[0]
        logger.info(f"Ahcuenta encontrado: {cuenta_asp_ahcuenta}")
        return cuenta_asp_ahcuenta
    else:
        logger.warning("No se encontró ninguna Ahcuenta con esos criterios.")
        return None
    
def obtener_ultimo_saldo_actual(cuenta_id: str,session: Session) -> Optional[float]:
  #Obtener el Cuenta ASP
  # Log de entrada
    logger.info(f"Parametros de la CuentaId para consultar su ultimo saldo de la cuent id: {cuenta_id}")
    # Consulta SQL para obtener el último saldo_actual
    query = text("""
        SELECT saldo_actual
        FROM ahorro.ahtransacciones_cuentas
        WHERE cuenta_id = :cuenta_id
        AND saldo_actual IS NOT NULL
        ORDER BY fecha DESC
        LIMIT 1
    """)
    # Ejecutar el query con parámetros
    result = session.execute(query, {"cuenta_id": cuenta_id}).fetchone()
    # Validar si el resultado existe
    if result:
        saldo_actual = result[0]
        logger.info(f"Saldo Actual encontrado: {saldo_actual}")
        return saldo_actual
    else:
        logger.warning("No se encontró ningún saldo_actual para la cuenta proporcionada.")
        return 0
    

def pl_insert_transaccion_asp_dock(sesion: Session, ahtransacciones_cuentas_id:int, dockone_id:str, evento:str):
    
  
    sesion.execute(
        text("""
            INSERT INTO plasticos.transaccion_asp_dock 
            (ahtransacciones_cuentas_id, dockone_id, usuario_creacion, fecha_creacion, evento)
            VALUES (:ahtransacciones_cuentas_id, :dockone_id, 9, current_timestamp, :evento)
        """),
        {
            "ahtransacciones_cuentas_id": ahtransacciones_cuentas_id,
            "dockone_id": dockone_id,
            "evento": evento,
        }
    )

    

    sesion.commit()

    return True


def existe_operation_id(session: Session,operation_id: str) -> bool:
    """
    Retorna True si existe el operation_id en plasticos.transaccion_asp_dock,
    en caso contrario False.
    """

    logger.info(f"Validando existencia de operation_id: {operation_id}")

    query = text("""
        SELECT dockone_id
        FROM plasticos.transaccion_asp_dock
        WHERE dockone_id = :operation_id
        LIMIT 1
    """)

    result = session.execute(query, {"operation_id": operation_id}).fetchone()

    if result:
        logger.info("operation_id encontrado en la BD")
        return True
    else:
        logger.warning("operation_id NO se encontró en la BD")
        return False