from contextlib import asynccontextmanager

# Libreria para el Api Rest
from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse
from fastapi.openapi.utils import get_openapi
from fastapi.openapi.docs import get_swagger_ui_html


from src.utils.logger import logger
from src.api.v1.routes import router
#from fastapi.exceptions import RequestValidationError

DOCS_TITLE = "DOCK WEBHOOKS"


# Crear la aplicación FastAPI
app = FastAPI(
    title="DOCK Services",
    description="API para consumir servicios de DOCK",
    version="1.0.0",
    docs_url='/docs',
    redoc_url="/redoc",
    openapi_url="/openapi.json",
    reload=True
)


app.include_router(router)
