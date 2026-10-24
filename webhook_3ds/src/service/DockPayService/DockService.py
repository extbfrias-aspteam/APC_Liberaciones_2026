from sqlmodel import Session, select
from src.db.session import get_session
from fastapi import Depends, HTTPException
from src.core.config import settings
from src.utils.logger import logger
from src.utils.valida_datos import validar_resultado

from src.db.models.DockAuthorization.db_models_dockAuthorization import (
    PlNotificacionesAuthorization,
    PlAhTransaccionesCuentas
) 

from src.schemas.DtoDockAuthorization.DtoDockAuthorizationRequest import GlobalAuthorizationRequest

from src.schemas.DtoTransacciones.DtoAhorro_transacciones import AhorroOperacionDTO
from src.core.transacciones.InsertarTransaccion import AhorroTransaccionesService

from src.repositories.DockAuthorizationRepository.DockAuthorizationRepository import (
    insert_notificaciones_repository,
    insert_ahtransacciones_cuentas,
    obtener_datos_dockone,
    obtener_id_ahcuenta,
     obtener_ultimo_saldo_actual,
     obtener_persona_id,
    existe_operation_id,
     pl_insert_ahtransacciones_cuentas, 
     pl_insert_transaccion_asp_dock
)
ahorro_service = AhorroTransaccionesService()

def insertar_operacion_service(session: Session, data: GlobalAuthorizationRequest, json: str) -> PlNotificacionesAuthorization:
# Aquí podrías agregar validaciones o reglas de negocio si es necesario
    return insert_notificaciones_repository(session, data, json)



def insertar_operacion_service_transacciones_cuentas(session: Session, data: GlobalAuthorizationRequest):
    op = AhorroOperacionDTO(
        cuenta_dock=data.payload.account_id,
        processing_type=data.payload.processing.type if data.payload.processing else None,
        monto=float(data.payload.values.source_value),
        descripcion=data.payload.establishment or "",
        autorizacion=data.payload.authorization_code or "",
        clave_rastreo=data.payload.transaction_uuid,
        banco = settings.BANCO,
        tipo_pago = settings.TIPO_PAGO 
    )

    transaccion_ahorro = ahorro_service.insertar_operacion(session, op)
    return transaccion_ahorro
    

    

def insertar_relacion_asp_dock(session: Session, data: GlobalAuthorizationRequest, ahtransacciones_cuentas_id:int) -> int:
    #Dependiendo Si la cuenta Concentradora

    dockone_id = data.payload.transaction_uuid

    evento = data.event_name


    #Insert en ahcuentasTransacciones y aqui se le pondria la id_cuenta_asp como cuenta_id
    return pl_insert_transaccion_asp_dock(session, ahtransacciones_cuentas_id, dockone_id, evento)



def existe_operacion(session: Session, data: GlobalAuthorizationRequest) -> bool:
    

    dockone_id = data.payload.transaction_uuid

    existe_operacion_core_asp = existe_operation_id(session,dockone_id)

    
    return existe_operacion_core_asp