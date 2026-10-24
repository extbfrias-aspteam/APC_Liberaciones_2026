import json
import logging
import requests
from requests.auth import HTTPBasicAuth

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

class GeneralConfiguration:
    def __init__(self, usuario, password, ws_envio_sms, ws_envio_sms_content_type):
        self.usuario = usuario
        self.password = password
        self.ws_envio_sms = ws_envio_sms
        self.ws_envio_sms_content_type = ws_envio_sms_content_type

    def get_usuario(self):
        return self.usuario

    def get_pass(self):
        return self.password

    def get_ws_envio_sms(self):
        return self.ws_envio_sms

    def get_ws_envio_sms_content_type(self):
        return self.ws_envio_sms_content_type

class SMS:
    def __init__(
        self,
        solicitante_id: str,
        celular: str,
        mensaje: str,
        usuario_id: int     = 9,
        transaccion_id: int = 24,
    ):
        self.solicitanteId   = solicitante_id
        self.celular         = celular
        self.usuarioId       = usuario_id
        self.mensaje         = mensaje
        self.transaccionId   = transaccion_id

    def to_dict(self):
        return self.__dict__

class EnviaNotificacionReq:
    def __init__(self, sms, tipo):
        self.sms = sms.__dict__
        self.tipo = tipo

class NotificacionSMSService:
    def __init__(self, config):
        self.config = config

    def enviar_sms(self, sms: SMS):
        try:
            payload = sms.to_dict()
            logger.info(f"Enviando SMS: {json.dumps(payload)}")
            response = requests.post(
                self.config.get_ws_envio_sms(),
                json=payload,
                auth=HTTPBasicAuth(
                    self.config.get_usuario(),
                    self.config.get_pass()
                ),
                headers={"Content-Type": self.config.get_ws_envio_sms_content_type()},

            )
            if response.status_code == 200:
                logger.info(response.text)
                return True
            else:
                logger.error(f"Error {response.status_code}: {response.text}")
                return False
        except Exception as e:
            logger.error("Error al enviar el SMS: %s", e)
            return False

config = GeneralConfiguration("ASP", "a5p2017$", "http://172.17.7.183:8080/CEROEnvioSMS/rest/enviarSms", "application/json")
servicio_sms = NotificacionSMSService(config)

if __name__ == "__main__":
    config = GeneralConfiguration("ASP", "a5p2017$", "http://172.17.7.183:8080/CEROEnvioSMS/rest/enviaSMS", "application/json")
    servicio_sms = NotificacionSMSService(config)

    sms = SMS("123456789", "Este es un mensaje de prueba")
    servicio_sms.enviar_sms(sms)
