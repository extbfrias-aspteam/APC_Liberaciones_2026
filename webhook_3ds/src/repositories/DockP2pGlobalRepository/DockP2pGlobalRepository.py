from sqlmodel import Session
from typing import Optional
from src.db.models.DockP2pGlobal.db_models_dockP2pGlobal import (
    PlGlobalP2p
) 

from src.db.models.DockAuthorization.db_models_dockAuthorization import PlAhTransaccionesCuentas
from src.schemas.DtoDockP2pGlobal.DtoDockP2pGlobalRequest import P2pGlobalRequest
from src.utils.logger import logger
from sqlalchemy import text

from datetime import datetime
from zoneinfo import ZoneInfo


#Funcion para Insert
def insert_global_p2p_repository(session: Session, data: P2pGlobalRequest,json_data:str) -> PlGlobalP2p:
    #Mapeo del Objeto Aninado
    nueva_operacion = PlGlobalP2p(
        event_name = data.event_name,
        event_type = data.event_type,
        operation_instance_id=data.payload.operation_instance_id,
        product_id=data.payload.product_id,
        operation_instance_amount=data.payload.operation_instance_amount,
        operation_instance_original_amount=data.payload.operation_instance_original_amount,
        operation_step_instance_amount=data.payload.operation_step_instance_amount,
        operation_instance_asset=data.payload.operation_instance_asset,
        operation_step_instance_event_status=data.payload.operation_step_instance_event_status,
        operation_step_instance_event_reason=data.payload.operation_step_instance_event_reason,
        description=data.payload.description,
        external_id=data.payload.external_id,
        debtor_product_id=data.payload.debtor_product_id,
        debtor_person_id=data.payload.debtor_person_id,
        debtor_account_id=data.payload.debtor_account_id,
        debtor_name=data.payload.debtor_name,
        debtor_document_type=data.payload.debtor_document_type,
        debtor_document=data.payload.debtor_document,
        debtor_asset=data.payload.debtor_asset,
        debtor_category_code=data.payload.debtor_category_code,
        creditor_product_id=data.payload.creditor_product_id,
        creditor_person_id=data.payload.creditor_person_id,
        creditor_account_id=data.payload.creditor_account_id,
        creditor_name=data.payload.creditor_name,
        creditor_document_type=data.payload.creditor_document_type,
        creditor_document=data.payload.creditor_document,
        creditor_category_code=data.payload.creditor_category_code,
        trace_id=data.properties.trace_id,
        event_date=data.properties.event_date,
        json= json_data
    )

    session.add(nueva_operacion)
    session.commit()
    session.refresh(nueva_operacion)
    return nueva_operacion

    #Insert de AhTransacciones Cuentas
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


def insertar_transaccion_asp_dock(
    session: Session,
    ahtransacciones_cuentas_id: int,
    dockone_id: str,
    evento: str,
    usuario_creacion: int = 9,
) -> int:
    """
    Inserta un registro en plasticos.transaccion_asp_dock y retorna el id generado.

    :param session: Sesión de base de datos.
    :param ahtransacciones_cuentas_id: ID de ahorro.ahtransacciones_cuentas.
    :param dockone_id: operation_instance_id que viene de DockOne.
    :param evento: Nombre del evento (ej. 'global_dockpay_p2p').
    :param usuario_creacion: ID de usuario que realiza el registro.
    :return: id (PK) del registro insertado en plasticos.transaccion_asp_dock.
    """

    logger.info(
        f"Insertando en plasticos.transaccion_asp_dock: "
        f"ahtransacciones_cuentas_id={ahtransacciones_cuentas_id}, "
        f"dockone_id={dockone_id}, evento={evento}"
    )

    query = text("""
        INSERT INTO plasticos.transaccion_asp_dock (
            ahtransacciones_cuentas_id,
            dockone_id,
            usuario_creacion,
            fecha_creacion,
            evento
        )
        VALUES (
            :ahtransacciones_cuentas_id,
            :dockone_id,
            :usuario_creacion,
            NOW(),
            :evento
        )
        RETURNING id
    """)

    result = session.execute(query, {
        "ahtransacciones_cuentas_id": ahtransacciones_cuentas_id,
        "dockone_id": dockone_id,
        "usuario_creacion": usuario_creacion,
        "evento": evento,
    }).scalar_one()

    session.commit()

    logger.info(f"Registro creado en plasticos.transaccion_asp_dock con id={result}")
    return result

