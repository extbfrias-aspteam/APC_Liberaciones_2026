from flask import Blueprint, jsonify, current_app, request
from app.db.procrea_db import get_solicitante
from app.db.cero_db import get_catalogo_parentesco, insertar_aval, get_aval_by_id_solicitud, actualizar_aval, eliminar_aval_por_id
from app.auth.jwt_rs256 import jwt_required_rs256 


aval_bp = Blueprint('aval', __name__, url_prefix='/aval')

@aval_bp.route('/solicitante/<string:numero>', methods=['GET'])
@jwt_required_rs256
def obtener_solicitante(numero):
    datos = get_solicitante(current_app, numero)
    if not datos:
        return jsonify({"msg": "Solicitante no encontrado"}), 404
    return jsonify(datos)

@aval_bp.route('/parentescoCatalogo', methods=['GET'])
@jwt_required_rs256
def obtener_catalogo_parentesco():
    datos = get_catalogo_parentesco(current_app)
    if not datos:
        return jsonify({"msg": "Catalogo no encontrado"}), 404
    return jsonify(datos)

@aval_bp.route('/insertar_o_actualizar_aval', methods=['POST'])
@jwt_required_rs256
def insertar_o_actualizar_aval():
    data = request.get_json()

    if not data:
        return jsonify({"msg": "JSON no proporcionado"}), 400

    try:
        id_solicitud = data.get("id_solicitud")
        if not id_solicitud:
            return jsonify({"msg": "id_solicitud es requerido"}), 400

        registro_existente = get_aval_by_id_solicitud(current_app, id_solicitud)

        if not registro_existente:
            insertar_aval(current_app, data)
            return jsonify({"msg": "Aval insertado correctamente"}), 201
        else:
            campos_a_comparar = ["id_solicitante", "nombre_aval", "ncrelaciones_id"]
            hay_cambios = any(
                str(data.get(campo)) != str(registro_existente.get(campo)) 
                for campo in campos_a_comparar
            )

            if not hay_cambios:
                return jsonify({"msg": "No hay cambios para actualizar"}), 200

            actualizar_aval(current_app, id_solicitud, data)
            return jsonify({"msg": "Aval actualizado correctamente"}), 200

    except Exception as e:
        current_app.logger.error(f"Error al insertar o actualizar aval: {e}")
        return jsonify({"msg": "Error en la operación"}), 500


@aval_bp.route('/obtener_aval/<int:id_solicitud>', methods=['GET'])
@jwt_required_rs256
def obtener_aval(id_solicitud):
    dato = get_aval_by_id_solicitud(current_app, id_solicitud)
    if not dato:
        return jsonify({"msg": "No se encontró registro para la solicitud"}), 404
    solicitante = get_solicitante(current_app, dato.get("id_solicitante"))
    if not solicitante:
        return jsonify({"msg": "No se encontró el solicitante"}), 404
    solicitante["ncrelaciones_id"] = dato.get("ncrelaciones_id")
    return jsonify(solicitante)

@aval_bp.route('/eliminar_aval/<int:id_solicitud>', methods=['DELETE'])
@jwt_required_rs256
def eliminar_aval(id_solicitud):
    try:
        eliminado = eliminar_aval_por_id(current_app, id_solicitud)
        if eliminado:
            return jsonify({"msg": "Aval eliminado correctamente"}), 200
        else:
            return jsonify({"msg": "No se encontró registro con ese id_solicitud"}), 404
    except Exception as e:
        current_app.logger.error(f"Error al eliminar aval: {e}")
        return jsonify({"msg": "Error al eliminar aval"}), 500





