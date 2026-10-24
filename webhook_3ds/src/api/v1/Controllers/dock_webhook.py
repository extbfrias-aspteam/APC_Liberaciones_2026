from fastapi import APIRouter, Body, HTTPException, BackgroundTasks
from src.schemas.dock_schemas import EventModel
from src.utils.utils import decrypt_dock_payload
from src.utils.logger import logger
from src.service.Service3ds.dock_service import process_webhook_event
import json

dock_webhook_router = APIRouter()


@dock_webhook_router.post("/dock-3ds-webhook")
async def handle_dock_3ds_webhook(background_tasks: BackgroundTasks,
                              encrypted_payload: str = Body(..., media_type="text/plain"), 
                              ):

    try:
        # Desencriptar payload
        decrypt_data = decrypt_dock_payload(encrypted_payload)

        #Validar estructura de datos
        event = EventModel.model_validate(json.loads(decrypt_data))
        logger.info("Webhook recibido y validado")
        background_tasks.add_task(process_webhook_event, event) 

        return {"status": "success", "message":"Webhook procesado"}
    

    except Exception as e:
        logger.error(f"Error inesperado: {str(e)}")
        raise HTTPException(500, detail="Error interno en el servidor")    


