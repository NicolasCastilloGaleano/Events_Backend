from flask import request, jsonify
from modules.inscription.services import InscriptionService
from modules.inscription.validations import validate_inscription_data
from utils.response_service import (
    error_response,
    success_response,
    system_error_response,
)


class InscriptionView:
    def __init__(self):
        self.inscription_service = InscriptionService()

    def create_inscription(self):
        try:
            data = request.get_json()

            validation_error = validate_inscription_data(data)
            if validation_error:
                return jsonify(error_response("Campos Faltantes", data=None)), 400
            inscription = self.inscription_service.create_inscription(data)
            return (
                jsonify(
                    success_response(
                        inscription.to_dict(), "inscripcion creada con exito"
                    )
                ),
                201,
            )
        except Exception as e:
            return (
                jsonify(
                    system_error_response(
                        "Error del sistema al intentar crear un inscripcion", str(e)
                    )
                ),
                500,
            )

    def get_inscriptions(self):
        try:
            data = request.get_json()
            filters = data.get("filters", {})
            inscriptions = self.inscription_service.get_inscriptions(filters)
            if inscriptions:
                return (
                    jsonify(
                        success_response(
                            inscriptions, "inscripciones encontrados correctamente"
                        )
                    ),
                    200,
                )
            return jsonify(error_response("No se encontraron inscripciones", None)), 404
        except Exception as e:
            return (
                jsonify(
                    system_error_response(
                        "Error del sistema al intentar listar las inscripciones", str(e)
                    )
                ),
                500,
            )

    def get_inscription(self, inscription_id):
        try:
            inscription = self.inscription_service.get_inscription_by_id(inscription_id)
            if inscription:
                return (
                    jsonify(
                        success_response(
                            inscription, "inscripcion encontrada correctamente"
                        )
                    ),
                    200,
                )
            return jsonify(error_response("inscripcion no encontrada", None)), 404
        except Exception as e:
            return (
                jsonify(
                    system_error_response(
                        "Error del sistema al intentar obtener una inscripcion", str(e)
                    )
                ),
                500,
            )

    def update_inscription(self, inscription_id):
        try:
            data = request.get_json()
            validation_error = validate_inscription_data(data)
            if validation_error:
                return jsonify(error_response("Campos Faltantes", data=None)), 400
            self.inscription_service.update_inscription(inscription_id, data)
            return (
                jsonify(success_response(data, "inscripcion actualizada con exito")),
                201,
            )
        except Exception as e:
            return (
                jsonify(
                    system_error_response(
                        "Error del sistema al intentar actualizar una inscripcion",
                        str(e),
                    )
                ),
                500,
            )

    def delete_inscription(self, inscription_id):
        try:
            self.inscription_service.delete_inscription(inscription_id)
            return (
                jsonify(success_response(None, "inscripcion eliminada con exito")),
                201,
            )
        except Exception as e:
            return (
                jsonify(
                    system_error_response(
                        "Error del sistema al intentar eliminar una inscripcion", str(e)
                    )
                ),
                500,
            )
