from sqlmodel import Session, select
from src.db.session import engine, nc_engine
from src.db.models.db_models import (
    Telefono,
    LogSMS,
    NcPosicionGlobalAH,     
)

from fastapi import Depends, HTTPException
from src.schemas.dock_schemas import EventModel
from src.service.Service3ds.sms_service import SMS, servicio_sms
from datetime import datetime, timezone
from src.utils.logger import logger


def process_webhook_event(event: EventModel):
    try:
        # 1. Validar tarjeta y obtener solicitante
        card_id = event.payload.card_id
        otp = event.payload.otp_value

        with Session(nc_engine) as session:
            # Consultar tarjeta en plásticos
            tarjeta = session.exec(
                select(NcPosicionGlobalAH)
                .where(NcPosicionGlobalAH.tarjeta_principal == card_id)
            ).first()

            if not tarjeta:
                logger.error(f"Tarjeta no encontrada: {card_id}")
                return
            
        with Session(engine) as session:
                # Consultar tarjeta en nucleocentral
            # 2. Obtener número telefónico
            telefonos = session.exec(
                select(Telefono)
                .where(
                    Telefono.id_cat_telefono == 7,
                    Telefono.id_solicitante == tarjeta.idpersona
                )
            ).all()
            
            if not telefonos:
                logger.error(f"No hay teléfonos registrados para solicitante {tarjeta.idpersona}")
                return

            principal_phone = telefonos[0].telefono # Tomamos el primer número

            # 3. Enviar SMS
            sms = SMS(
            solicitante_id = str(tarjeta.idpersona),  # el id persona que obtuviste
            celular         = principal_phone,
            mensaje        = f"Tu numero de validacion es el siguiente: {otp}",# si lo tienes en el evento
            )
            success = servicio_sms.enviar_sms(sms)

                
                # 4. Registrar en logs
            log_entry = LogSMS(
                    card_id=card_id,
                    phone=principal_phone,
                    otp=otp,
                    sms_response=str(success),
                    success=success,
                    sent_at=datetime.now(timezone.utc)
                )


            
        session.add(log_entry)
        session.commit()
        
        logger.info(f"SMS enviado correctamente a {principal_phone}")

    except HTTPException as he:
        logger.error(f"Error HTTP en procesamiento: {he.detail}")
    except Exception as e:
        logger.error(f"Error crítico en procesamiento: {str(e)}")