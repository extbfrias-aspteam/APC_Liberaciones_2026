from fastapi import APIRouter, Body, HTTPException,Request,Depends,Response,status
from pydantic import BaseModel, ValidationError
from src.utils.utils import decrypt_dock_payload
from src.utils.logger import logger
from sqlmodel import Session
from src.db.session import get_nc_session
from src.schemas.DtoDockAuthorization.DtoDockAuthorizationRequest import GlobalAuthorizationRequest
from src.service.DockPayService.DockService import (
    insertar_operacion_service,
    insertar_operacion_service_transacciones_cuentas,
    insertar_relacion_asp_dock,
    existe_operacion
)
import json

dockPay_router = APIRouter()

#Configurar el Tipo de Http y su Endpoint
@dockPay_router.post("/insertar-operacion")
async def insertar_operacion(
    encrypted_payload: str = Body(..., media_type="text/plain"),
    session: Session = Depends(get_nc_session)
):

    """
            Controlador Para la Conexion hacia DockOne Evento: p2pAuthorization
            Parámetros:
            encrypted_payload (str): Request Pero encriptado
            session (Session): Obtener una sesion en la base de datos y Cerrarla.
    """
    try:
       #Desencriptar payload
        logger.info(f"Parámetros encriptado: {encrypted_payload}")

        decrypted_data = decrypt_dock_payload(encrypted_payload)
        logger.info(f"Payload desencriptado: {decrypted_data}")

        #Convertir JSON string a diccionario
        data_dict_p2p = json.loads(decrypted_data)

        #Validar estructura de datos con Pydantic
        event = GlobalAuthorizationRequest.model_validate(data_dict_p2p)

        logger.info(f"Parámetros Decriptado y Validado: {event}")

        logger.info("Webhook recibido y validado")
        event_name = event.event_name
        
        if event_name == "global_authorization":

            # INSERT EN TABLA plwh_notificaciones_p2p_pay
            operacion_notificaciones = insertar_operacion_service(session, event, decrypted_data)

        existe_operacion_core = existe_operacion(session, event)

        if event.payload.authorizer_response_detail == "APPROVED" and not existe_operacion_core:
            operacion_ahtransaccionescuentas = insertar_operacion_service_transacciones_cuentas(session, event)
            id_trx = operacion_ahtransaccionescuentas

            if id_trx != -1:
               operacion_asp_dock = insertar_relacion_asp_dock(session, event, id_trx)

        

            # Respuesta general (igual en ambos casos, por eso no repetimos código)
            response_data = {
                "message": "Operaciones insertadas exitosamente.",
                "plwh_notificaciones_p2p_pay_id": "Pruebas Dock Autorization",
                "CuentaTransaccion": "Pruebas Dock Autorization",
            }

            logger.info("JSON response: %s", json.dumps(response_data, ensure_ascii=False))

            return Response(
                content=json.dumps(response_data),
                media_type="application/json",
                status_code=status.HTTP_200_OK
            )
    except Exception as e:
        logger.error(f"Codigo: 500: Error al Insertar Operaciones:{e}")
        raise HTTPException(status_code=500, detail=f"Error al insertar operaciónes: {e}")
