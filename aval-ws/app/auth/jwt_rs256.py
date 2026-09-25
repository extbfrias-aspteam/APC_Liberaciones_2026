from functools import wraps
from flask import request, jsonify
from jose import jwt, JWTError, ExpiredSignatureError

with open("keys/public.pem", "r") as f:
    PUBLIC_KEY = f.read()

ALGORITHM = "RS256"

def jwt_required_rs256(fn):
    @wraps(fn)
    def wrapper(*args, **kwargs):
        auth_header = request.headers.get("Authorization", None)
        if not auth_header or not auth_header.startswith("Bearer "):
            return jsonify({"msg": "Token no proporcionado"}), 401

        token = auth_header.split(" ")[1]

        try:
            payload = jwt.decode(token, PUBLIC_KEY, algorithms=[ALGORITHM])

            if payload.get("type") != "access":
                return jsonify({"msg": "Solo se permiten tokens de acceso"}), 401

            request.jwt_payload = payload

        except ExpiredSignatureError:
            return jsonify({"msg": "Token expirado"}), 401
        except JWTError as e:
            return jsonify({"msg": f"Token inválido: {str(e)}"}), 401

        return fn(*args, **kwargs)
    return wrapper
