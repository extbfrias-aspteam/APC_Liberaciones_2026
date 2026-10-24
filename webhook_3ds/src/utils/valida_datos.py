from fastapi import HTTPException

# Valida el resultado no Venga None(Nulo)
def validar_resultado(valor, mensaje_error: str, status_code: int = 404):
    if valor is None:
        raise HTTPException(status_code=status_code, detail=mensaje_error)
    return valor