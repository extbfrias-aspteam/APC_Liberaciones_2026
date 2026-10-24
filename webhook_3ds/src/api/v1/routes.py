from fastapi import APIRouter
from .Controllers.DockPayController.DockController import dockPay_router
from .Controllers.DockP2pGlobalController.DockP2pGlobalController import dockP2pGlobal_router
from .Controllers.dock_webhook import dock_webhook_router

router = APIRouter()


router.include_router(dockPay_router, prefix="/operaciones", tags=["Operaciones"])

router.include_router(dockP2pGlobal_router, prefix="/movimientos", tags=["movimientos"])

router.include_router(dock_webhook_router, prefix="/dock_service", tags=["Operaciones"])

