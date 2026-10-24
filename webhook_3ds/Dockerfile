FROM python:3.10-slim

# 1) Definir variables de entorno ANTES de instalar Poetry
ENV POETRY_VERSION=1.5.1
ENV POETRY_HOME="/opt/poetry"
ENV POETRY_NO_INTERACTION=1
ENV POETRY_VIRTUALENVS_CREATE=false
ENV PYTHONUNBUFFERED=1
ENV PATH="$POETRY_HOME/bin:$PATH"

# 2) Instalar herramientas necesarias
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    build-essential \
    libpq-dev \
    && rm -rf /var/lib/apt/lists/*


# 3) Instalar Poetry dentro del contenedor
RUN curl -sSL https://install.python-poetry.org | python3 -

# 4) Definir el directorio de trabajo dentro del contenedor
WORKDIR /app

# 5) Copiar archivos que definen dependencias
COPY pyproject.toml poetry.lock ./

# 6) Instalar dependencias con Poetry
RUN poetry install --only main --no-root

# 7) Copiar el resto del código
COPY . .

# 7.1) Copiar certificados SSL
COPY cert.pem key.pem /app/

# 8) Exponer puerto y comando final
EXPOSE 9000
CMD ["python", "main.py"]
