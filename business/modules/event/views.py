from flask import request, jsonify
from modules.event.services import EventService
from modules.event.validations import validate_event_data
from utils.response_service import (
    error_response,
    success_response,
    system_error_response,
)
from modules.inscription.services import InscriptionService


class EventView:
    inscription_service = InscriptionService()

    def __init__(self):
        self.event_service = EventService()

    def create_event(self):
        try:
            data = request.get_json()

            validation_error = validate_event_data(data)
            if validation_error:
                return jsonify(error_response("Campos Faltantes", data=None)), 400
            filters = {
                "site": data["site"],
                "date": data["date"],
            }
            conflict_event = self.event_service.get_events(filters)
            if conflict_event:
                return (
                    jsonify(
                        error_response(
                            "Ya existe un evento en esta fecha y lugar", None
                        )
                    ),
                    400,
                )
            event = self.event_service.create_event(data)
            return (
                jsonify(success_response(event.to_dict(), "Evento creado con exito")),
                201,
            )
        except Exception as e:
            return (
                jsonify(
                    system_error_response(
                        "Error del sistema al intentar crear un evento", str(e)
                    )
                ),
                500,
            )

    def get_events(self):
        try:
            data = request.get_json()
            filters = data.get("filters", {})
            events = self.event_service.get_events(filters)
            if events:
                return (
                    jsonify(
                        success_response(events, "Eventos encontrados correctamente")
                    ),
                    200,
                )
            return jsonify(error_response("No se encontraron eventos", None)), 404
        except Exception as e:
            return (
                jsonify(
                    system_error_response(
                        "Error del sistema al intentar listar los eventos", str(e)
                    )
                ),
                500,
            )

    def get_user_events(self, user_id):
        try:
            filters = {"user_id": user_id}
            inscriptions = self.inscription_service.get_inscriptions(filters)
            data = request.get_json()
            data = data.get("filters", {})
            events = []
            for inscription in inscriptions:
                event = self.event_service.get_event_by_id(inscription["event_id"])
                event["inscription_id"] = inscription["_id"]
                if data:
                    if "participated" in data:
                        if inscription["participated"] is True:
                            if "date" in data:
                                if data["date"] in event["date"]:
                                    events.append(event)
                            else:
                                events.append(event)
                    else:
                        if "date" in data:
                            if data["date"] in event["date"]:
                                events.append(event)
                        else:
                            events.append(event)
                else:
                    events.append(event)
            if events:
                return (
                    jsonify(
                        success_response(events, "Eventos encontrados correctamente")
                    ),
                    200,
                )
            return jsonify(success_response([], "No se encontraron eventos")), 200
        except Exception as e:
            return (
                jsonify(
                    system_error_response(
                        "Error del sistema al intentar listar los eventos", str(e)
                    )
                ),
                500,
            )

    def get_event(self, event_id):
        try:
            event = self.event_service.get_event_by_id(event_id)
            if event:
                # Convierte '_id' de ObjectId a str
                event["_id"] = str(event["_id"])
                return (
                    jsonify(success_response(event, "Evento encontrado correctamente")),
                    200,
                )
            return jsonify(error_response("Evento no encontrado", None)), 404
        except Exception as e:
            return (
                jsonify(
                    system_error_response(
                        "Error del sistema al intentar obtener un evento", str(e)
                    )
                ),
                500,
            )

    def get_event_users(self, event_id):
        try:
            users = self.event_service.get_event_users(event_id)
            if users:
                return (
                    jsonify(
                        success_response(users, "usuarios encontrados correctamente")
                    ),
                    200,
                )
            return (
                jsonify(
                    success_response([], "No se encontraron usuarios para este evento")
                ),
                200,
            )
        except Exception as e:
            return (
                jsonify(
                    system_error_response(
                        "Error del sistema al intentar obtener los usuarios de un evento",
                        str(e),
                    )
                ),
                500,
            )

    def update_event(self, event_id):
        try:
            data = request.get_json()
            validation_error = validate_event_data(data)
            if validation_error:
                return jsonify(error_response("Campos Faltantes", data=None)), 400
            self.event_service.update_event(event_id, data)
            return (
                jsonify(success_response(data, "Evento actualizado con exito")),
                201,
            )
        except Exception as e:
            return (
                jsonify(
                    system_error_response(
                        "Error del sistema al intentar actualizar un evento", str(e)
                    )
                ),
                500,
            )

    def change_status(self, event_id):
        try:
            self.event_service.change_status(event_id)
            return (
                jsonify(success_response([], "Evento actualizado con exito")),
                201,
            )
        except Exception as e:
            return (
                jsonify(
                    system_error_response(
                        "Error del sistema al intentar actualizar un evento", str(e)
                    )
                ),
                500,
            )

    def delete_event(self, event_id):
        try:
            self.event_service.delete_event(event_id)
            return (
                jsonify(success_response(None, "Evento eliminado con exito")),
                201,
            )
        except Exception as e:
            return (
                jsonify(
                    system_error_response(
                        "Error del sistema al intentar eliminar un evento", str(e)
                    )
                ),
                500,
            )
