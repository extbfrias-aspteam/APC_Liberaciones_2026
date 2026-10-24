from sqlmodel import Session, select
from src.db.session import get_session
from fastapi import Depends, HTTPException
from src.core.config import settings
from src.utils.logger import logger
from src.utils.valida_datos import validar_resultado
from src.core.config import Settings


from src.core.transacciones.InsertarTransaccion import AhorroTransaccionesService
from src.schemas.DtoTransacciones.DtoAhorro_transacciones import AhorroOperacionDTO


#Modelo de la base de datos.
from src.db.models.DockP2pGlobal.db_models_dockP2pGlobal import (
    PlGlobalP2p
) 
from src.db.models.DockAuthorization.db_models_dockAuthorization import PlAhTransaccionesCuentas

from src.schemas.DtoDockP2pGlobal.DtoDockP2pGlobalRequest import P2pGlobalRequest

#Funciones de la base de datos.

from src.repositories.DockP2pGlobalRepository.DockP2pGlobalRepository import (
    #Dependencias del repository
    insert_global_p2p_repository,
    existe_operation_id,
    insertar_transaccion_asp_dock,

)
# Instancia
ahorro_service = AhorroTransaccionesService()
settings = Settings()


def insertar_operacion_service(session: Session, data: P2pGlobalRequest, json: str) -> PlGlobalP2p:
# Aquí podrías agregar validaciones o reglas de negocio si es necesario
    return insert_global_p2p_repository(session, data,json)


def insertar_operacion_ahtransacciones_cuentas(session: Session,data: P2pGlobalRequest):
    
    #Validacion para obtener la Cuenta de Dock dependiendo el tipo de operacion.
    debtor_account_id = data.payload.debtor_account_id
    creditor_account_id = data.payload.creditor_account_id
    operation_id = data.payload.operation_instance_id
    cuenta_concentradora= settings.CUENTA_CONCENTRADORA_ASP_DOCK_ID


    cuenta_dock = (
        debtor_account_id
        if cuenta_concentradora == creditor_account_id
        else creditor_account_id
    )

    raw = data.dict()

    existe_operacion_core_asp = existe_operation_id(session,operation_id)

    if(existe_operacion_core_asp):
        logger.info("operation_id encontrado en la BD: No Hace Insert en AhtransaccionesCuentas.")
        return

    #Teniendo la cuenta de dock ya podemos  mapear el dto
    op = AhorroOperacionDTO(
        cuenta_dock=cuenta_dock,
        operation_type=data.event_type,  
        monto=float(data.payload.operation_instance_amount),  
        descripcion=data.payload.description or "",
        autorizacion=data.payload.external_id or "",         
        clave_rastreo=data.payload.operation_instance_id,
        banco = settings.BANCO,
        tipo_pago = settings.TIPO_PAGO     
    )

    #Id de la operacion
    id_transaccion_ahorro_ahcuentas = ahorro_service.insertar_operacion(session, op)

    #Guardarla en la tabla de TransaccionesDock
    insertar_transaccion_asp_dock(
        session=session,
        ahtransacciones_cuentas_id=id_transaccion_ahorro_ahcuentas,
        dockone_id=operation_id,
        evento="global_dockpay_p2p",  
    )


