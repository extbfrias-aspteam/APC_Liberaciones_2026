# tests/test_dock_webhook.py
import pytest
from fastapi import status
from sqlmodel import Session, select

from src.schemas.dock_schemas import EventModel, Payload, Properties
from src.db.models.db_models import PlAdministracionTarjetas

# Payload de ejemplo (simulando el evento encriptado)
ENCRYPTED_PAYLOAD = "FlHuSqYGX3Eru1uje9RKDpOy8keW8GPiKbIWFBPy6d3l0OSm+8+Upzqo4ARSXxPy/liIwh3evNnyMVmBwzM4S4aACqFX11ZoZqsp77SpvZEmznXF3JqSaJ6Bny+gnaQGoDPw1Wnhy8et"

# Mock de la función de desencriptación
def mock_decrypt_dock_payload(encrypted_data: str) -> str:
    """Simula la desencriptación del payload."""
    return EventModel(
        event_name="global_3ds_otp",
        event_type="3ds_otp",
        payload=Payload(
            card_id="12345",
            transaction_id="67890",
            otp_value="123456",
            card_number="**** **** **** 1234",
            merchant_name="Test Merchant",
            merchant_country="MX",
            merchant_url="https://example.com",
            purchase_currency_acronym="MXN",
            purchase_currency=484,
            purchase_amount=100.0,
            purchase_date="2023-10-01T00:00:00Z",
        ),
        properties=Properties(
            event_date="2023-10-01T00:00:00Z",
            trace_id="abc123",
        ),
    ).json()

# Prueba del endpoint
def test_dock_webhook(client, session: Session):
    # 1. Preparar datos de prueba en la base de datos
    tarjeta = PlAdministracionTarjetas(
        id_tarjeta_dock="12345",
        id_solicitante=1,
    )
    session.add(tarjeta)
    session.commit()

    # 2. Mockear la función de desencriptación
    from src.utils.utils import decrypt_dock_payload
    decrypt_dock_payload = mock_decrypt_dock_payload

    # 3. Hacer la solicitud al endpoint
    response = client.post(
        "/dock-webhook",
        content=ENCRYPTED_PAYLOAD,
        headers={"Content-Type": "text/plain"},
    )

    # 4. Verificar la respuesta
    assert response.status_code == status.HTTP_200_OK
    assert response.json() == {
        "status": "success",
        "message": "Webhook procesado",
    }

    # 5. Verificar que se procesó correctamente
    tarjeta = session.exec(
        select(PlAdministracionTarjetas)
        .where(PlAdministracionTarjetas.id_tarjeta_dock == "12345")
    ).first()
    assert tarjeta is not None