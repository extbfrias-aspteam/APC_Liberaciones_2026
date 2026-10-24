from typing import Optional
from enum import Enum

class TransactionDirection(str,Enum):
    RETIRO = "RET"
    DEPOSITO = "DEP"


class TransactionTypeResolver:
    """
    Resolver de dirección de la transacción (RET / DEP) a partir
    de distintos campos que puedan venir en el request.
    """

    _CARGOS = {
        "PURCHASE", "WITHDRAWAL", "DEBIT_ADJUSTMENT",
        "PURCHASE_WITH_CASH_BACK", "VISA_ONLY_ACCOUNT_FUNDING",
        "CASH_DISBURSEMENT", "SCRIP_ISSUE", "PAYMENT_TRANSACTION",
    }

    _ABONOS = {
        "REFUND", "DEPOSIT", "CREDIT_ADJUSTMENT",
        "CHECK_DEPOSIT_GUARANTEE", "CHECK_DEPOSIT",
        "ACCOUNT_TRANSFER",
    }


 #   Resolver usando el processing.type de DockOne.
    def _from_processing_type(self, processing_type: Optional[str]) -> Optional[TransactionDirection]:

        """
         Resolver usando el processing.type de DockOne.
         processing_type: Arreglo de String

         Retorna: Tipo de TransactionDirection (Deposito o retiro)
            
        """

        if not processing_type:
            return None

        if processing_type in self._CARGOS:
            return TransactionDirection.RETIRO
        if processing_type in self._ABONOS:
            return TransactionDirection.DEPOSITO
        return None

    #  Resolver usando operationType (lo que llega del global)
    def _from_operation_type(self, operation_type: Optional[str]) -> Optional[TransactionDirection]:
        
        """
         Resolver uResolver usando operationType dependiendo del Service
         operationType: Arreglo de String

         Retorna: Tipo de TransactionDirection (Deposito o retiro)
            
        """
        
        if not operation_type:
            return None

        # EJEMPLOS: ajusta estos mapeos a lo que realmente te manda Global
        
        op_upper = operation_type.upper()
        return (
            TransactionDirection.RETIRO    if "OUT" in op_upper else
            TransactionDirection.DEPOSITO  if "IN" in op_upper else
            None
        )

        return None

    #  Resolver con prioridad configurable
    def resolve(
        self,
        *,
        processing_type: Optional[str] = None,
        operation_type: Optional[str] = None,
        prefer_operation_type: bool = True,
    ) -> Optional[TransactionDirection]:
        """
        Si prefer_operation_type=True:
            - primero intenta con operation_type
            - si no se puede, cae a processing_type
        Si prefer_operation_type=False:
            - primero intenta con processing_type
            - si no se puede, cae a operation_type
        """

        if prefer_operation_type:
            direction = self._from_operation_type(operation_type)
            if direction is not None:
                return direction
            return self._from_processing_type(processing_type)

        # preferimos processing_type
        direction = self._from_processing_type(processing_type)
        if direction is not None:
            return direction
        return self._from_operation_type(operation_type)

