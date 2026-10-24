from typing import Optional
from sqlmodel import Session
import logging

from src.core.transacciones.TipoTransaccion import TransactionTypeResolver, TransactionDirection
from src.schemas.DtoTransacciones.DtoAhorro_transacciones import AhorroOperacionDTO
from src.db.models.DockAuthorization.db_models_dockAuthorization import PlAhTransaccionesCuentas 

from src.utils.valida_datos import validar_resultado

from src.repositories.DockAuthorizationRepository.DockAuthorizationRepository import (
    obtener_datos_dockone,
    obtener_persona_id,
    obtener_id_ahcuenta,
    obtener_ultimo_saldo_actual,
    pl_insert_ahtransacciones_cuentas
)

logger = logging.getLogger(__name__)


class AhorroTransaccionesService:

    def __init__(self, resolver: TransactionTypeResolver | None = None):
        self._resolver = resolver or TransactionTypeResolver()

    def insertar_operacion(
        self,
        session: Session,
        op: AhorroOperacionDTO,
        *,
        prefer_operation_type: bool = True,
    ) -> Optional[PlAhTransaccionesCuentas]:
        """
        Caso de uso: registrar operación en ahorro.ahtransacciones_cuentas

        Recibe un AhorroOperacionDTO genérico que puede venir de cualquier service.
        """

        # Determinar dirección RET / DEP usando lo que traiga el DTO
        direction = self._resolver.resolve(
            processing_type=op.processing_type,
            operation_type=op.operation_type,
            prefer_operation_type=prefer_operation_type,
        )

        if direction is None:
            logger.info(
                "No se pudo determinar tipo de transacción. "
                f"processing_type={op.processing_type}, operation_type={op.operation_type}"
            )
            return None

        clave_trans = direction.value  # "RET" o "DEP"

        # Todo lo demás se basa en la cuenta_dock, que ya viene resuelta en el DTO
        cuenta_interna = op.cuenta_dock
        logger.info(f"CUENTA ID DOCKONE: {cuenta_interna}")

        cuenta_asp = obtener_datos_dockone(cuenta_interna, session)
        validar_resultado(cuenta_asp, "No se encontro una Cuenta en asp con ese ID DOCKONE")

        persona_id = obtener_persona_id(cuenta_asp, session)

        id_cuenta_asp = obtener_id_ahcuenta(cuenta_asp, session)
        validar_resultado(id_cuenta_asp, "No se encuentra ningun ID en AhCuentas.")

        ultimo_saldo = obtener_ultimo_saldo_actual(id_cuenta_asp, session)
        logger.info(f"Último saldo obtenido: {ultimo_saldo}")

        return pl_insert_ahtransacciones_cuentas(
            session,
            op,
            id_cuenta_asp,
            persona_id,
            clave_trans,
        )
