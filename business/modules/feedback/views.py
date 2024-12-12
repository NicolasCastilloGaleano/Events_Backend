from flask import request, jsonify
from modules.feedback.services import FeedbackService
from modules.feedback.validations import validate_feedback_data
from utils.response_service import (
    error_response,
    success_response,
    system_error_response,
)


class FeedbackView:
    def __init__(self):
        self.feedback_service = FeedbackService()

    def create_feedback(self):
        try:
            data: dict = request.get_json()

            validation_error = validate_feedback_data(data)
            if validation_error:
                return jsonify(error_response("Campos Faltantes", data=None)), 400
            data_existente = data.copy()
            data_existente.pop("stars")
            data_existente.pop("message")
            feedback_existe = self.feedback_service.get_feedbacks(data_existente)
            if feedback_existe:
                return (
                    jsonify(error_response("feedback ya existente", None)),
                    400,
                )
            feedback = self.feedback_service.create_feedback(data)
            return (
                jsonify(
                    success_response(feedback.to_dict(), "feedback creado con exito")
                ),
                201,
            )
        except Exception as e:
            return (
                jsonify(
                    system_error_response(
                        "Error del sistema al intentar crear un feedback", str(e)
                    )
                ),
                500,
            )

    def get_feedbacks(self):
        try:
            data = request.get_json()
            filters = data.get("filters", {})
            feedbacks = self.feedback_service.get_feedbacks(filters)
            if feedbacks:
                return (
                    jsonify(
                        success_response(
                            feedbacks, "feedbacks encontrados correctamente"
                        )
                    ),
                    200,
                )
            return jsonify(success_response([], "No se encontraron feedbacks")), 200
        except Exception as e:
            return (
                jsonify(
                    system_error_response(
                        "Error del sistema al intentar listar las feedbacks", str(e)
                    )
                ),
                500,
            )

    def get_feedback(self, feedback_id):
        try:
            feedback = self.feedback_service.get_feedback_by_id(feedback_id)
            if feedback:
                return (
                    jsonify(
                        success_response(feedback, "feedback encontrado correctamente")
                    ),
                    200,
                )
            return jsonify(error_response("feedback no encontrado", None)), 404
        except Exception as e:
            return (
                jsonify(
                    system_error_response(
                        "Error del sistema al intentar obtener un feedback", str(e)
                    )
                ),
                500,
            )
