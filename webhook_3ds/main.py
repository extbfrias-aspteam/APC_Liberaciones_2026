import asyncio
import uvicorn
import psycopg2
from src.core.config import settings
from src.app import app

#Constantes
MAX_RETRIES = 5
RETRY_DELAY = 5  # segundos


async def test_db_connection():
     for attempt in range(1, MAX_RETRIES + 1):
        try:
            conn = psycopg2.connect(settings.DATABASE_URL)
            print("✅ Conexión a la base de datos exitosa.")
            conn.close()
            return
        except Exception as e:
            print(f"❌ Intento {attempt}: Error al conectar a la base de datos: {e}")
            if attempt < MAX_RETRIES:
                print(f"⏳ Reintentando en {RETRY_DELAY} segundos...")
                await asyncio.sleep(RETRY_DELAY)
            else:
                print("❌ No se pudo conectar después de varios intentos. Abortando.")
                raise  # Esto detendrá el contenedor

async def main():

    config = uvicorn.Config(
        app,
        host=settings.HOST,
        port=settings.PORT,
        log_level="debug",
        ssl_keyfile="key.pem",
        ssl_certfile="cert.pem"
    )
    server = uvicorn.Server(config)
    await server.serve()

if __name__ == "__main__":
    asyncio.run(main())