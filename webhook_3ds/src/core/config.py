from pydantic_settings import BaseSettings
from pydantic import PrivateAttr, Field
from dotenv import load_dotenv
load_dotenv(".env")

class Settings(BaseSettings):
    DB_USER: str
    DB_PASSWORD: str
    DB_HOST: str
    DB_PORT: str
    DB_NAME: str

    # --- Nucleocentral ---
    NC_DB_USER:     str
    NC_DB_PASSWORD: str
    NC_DB_HOST:     str
    NC_DB_PORT:     str
    NC_DB_NAME:     str


    AES_KEY: str 

    HOST: str = "0.0.0.0"  # Default para evitar errores si no está en .env
    PORT: int = 8080

    CUENTA_CONCENTRADORA_ASP_DOCK_ID: str
    BANCO: int
    TIPO_PAGO: int

    _database_url: str = PrivateAttr()  # <- Atributo privado, no se valida ni busca en .env

    def __init__(self, **values):
        super().__init__(**values)
        self._database_url = f"postgresql://{self.DB_USER}:{self.DB_PASSWORD}@{self.DB_HOST}:{self.DB_PORT}/{self.DB_NAME}"

        self._nc_database_url = f"postgresql://{self.NC_DB_USER}:{self.NC_DB_PASSWORD}@{self.NC_DB_HOST}:{self.NC_DB_PORT}/{self.NC_DB_NAME}"


    @property
    def DATABASE_URL(self) -> str:
        return self._database_url
    
    @property
    def NC_DATABASE_URL(self) -> str:
        return self._nc_database_url

    class Config:
        env_file = ".env"
        


# ✅ Esto es lo que necesitas para poder importar `settings`
settings = Settings()