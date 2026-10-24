from typing import Any, Optional,Dict
from pydantic import BaseModel


class AhorroOperacionDTO(BaseModel):
    """
    Contexto mínimo que necesita el módulo de ahorro para registrar la transacción.
    Este DTO lo construyen los distintos services (authorization, global, etc.).
    """

    # ID de cuenta en DockOne / sistema externo
    cuenta_dock: str

    # Info para determinar RET/DEP
    operation_type: Optional[str] = None  # viene de Global o donde tengas
    processing_type: Optional[str] = None  # viene de DockOne (payload.processing.type)

   # Datos necesarios para insertar en ahorro
    monto: float                    # antes data.payload.values.source_value
    descripcion: str                # antes data.payload.establishment
    autorizacion: str               # antes data.payload.authorization_code
    clave_rastreo: str              # antes data.payload.transaction_uuid
    banco:int                       # Banco que se va a asignar, puede ser 659 o  1528
    tipo_pago:int                   # Tipo de Operaccion puede ser 101 o 100