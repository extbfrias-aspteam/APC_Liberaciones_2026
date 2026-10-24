from sqlmodel import SQLModel, create_engine, Session
from src.core.config import Settings

#  Crea instancia de Settings
settings = Settings()

# URL de conexión a la base de datos
DATABASE_URL = settings.DATABASE_URL

# Engine de nucleocentral
NC_DATABASE_URL = settings.NC_DATABASE_URL

# Motor de la base de datos
engine = create_engine(DATABASE_URL)

nc_engine = create_engine(NC_DATABASE_URL)

# Función para obtener una sesión de la base de datos
def get_session():
    with Session(engine) as session:
        yield session

def get_nc_session():
    with Session(nc_engine) as session:
        yield session

def create_db_and_tables():
    SQLModel.metadata.create_all(engine)
