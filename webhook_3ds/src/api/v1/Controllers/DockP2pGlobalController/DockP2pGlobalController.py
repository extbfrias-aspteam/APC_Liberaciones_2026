from fastapi import APIRouter,Body, HTTPException,Request,Depends,Response,status
from pydantic import BaseModel, ValidationError
from src.utils.utils import decrypt_dock_payload
from src.utils.logger import logger
from sqlmodel import Session
from src.db.session import get_nc_session
from src.schemas.DtoDockP2pGlobal.DtoDockP2pGlobalRequest import P2pGlobalRequest
from src.service.DockP2pGlobalService.DockP2pGlobalService import (
    insertar_operacion_service,
    insertar_operacion_ahtransacciones_cuentas
) 

import json

#Configurar el Tipo de Http y Su endpoint
dockP2pGlobal_router = APIRouter()


@dockP2pGlobal_router.post("/p2pGlobalInsertarOperacion")
async def  insertar_operacion_p2p_global(
    encrypted_payload: str = Body(..., media_type="text/plain"),
    session: Session = Depends(get_nc_session)
):

    """
            Controlador Para la Conexion hacia DockOne Evento: p2pGlobal
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
        event = P2pGlobalRequest.model_validate(data_dict_p2p)

        logger.info(f"Parámetros Decriptado y Validado: {event}")

        logger.info("Webhook recibido y validado")
        

        #INSERT EN LA TABLA DE plwh_notificaciones_p2p_pay
        operacion_notificaciones = insertar_operacion_service(session, event, decrypted_data)

        

        if event.payload.operation_step_instance_event_status == "APPROVED" and event.event_name == "p2p_out_status_updated":
            insertar_operacion_ahtransacciones_cuentas(session, event)

        
        response_data = {
            "message": "Operaciones insertadas exitosamente.",
            "plwh_global_p2p_pay": "Insertado Correctamente plwh_global_p2p_pay",
            "Operacion": "Satisfactoria",
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
