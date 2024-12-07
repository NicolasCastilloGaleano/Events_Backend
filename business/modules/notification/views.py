from datetime import datetime
from flask import request, jsonify
from modules.notification.services import NotificationService
from utils.response_service import (
    error_response,
    success_response,
    system_error_response,
)
from modules.event.services import EventService
from modules.inscription.services import InscriptionService


class NotificationView:
    event_service = EventService()
    inscription_service = InscriptionService()

    def __init__(self):
        self.notification_service = NotificationService()

    def create_notification(self, event_id):
        try:
            data = request.get_json()
            event = self.event_service.get_event_by_id(event_id)
            if not event:
                return jsonify(
                    error_response("No se encontro el evento relacionado", None)
                )
            notification = self.notification_service.create_notification(event_id, data)
            return (
                jsonify(
                    success_response(
                        notification.to_dict(), "Notificacion creado con exito"
                    )
                ),
                201,
            )
        except Exception as e:
            return (
                jsonify(
                    system_error_response(
                        "Error del sistema al intentar crear un notificacion", str(e)
                    )
                ),
                500,
            )

    def get_notifications(self):
        try:
            data = request.get_json()
            filters = data.get("filters", {})
            notifications = self.notification_service.get_notifications(filters)
            if notifications:
                return (
                    jsonify(
                        success_response(
                            notifications, "Notificaciones encontrados correctamente"
                        )
                    ),
                    200,
                )
            return (
                jsonify(success_response([], "No se encontraron notificaciones")),
                200,
            )
        except Exception as e:
            return (
                jsonify(
                    system_error_response(
                        "Error del sistema al intentar listar las notificaciones",
                        str(e),
                    )
                ),
                500,
            )

    def get_user_notification(self, user_id):
        try:
            filters = {"user_id": user_id}
            inscriptions = self.inscription_service.get_inscriptions(filters)
            notifications = []
            for inscription in inscriptions:
                event_notifications = self.notification_service.get_event_notifications(
                    inscription["event_id"]
                )
                event = self.event_service.get_event_by_id(inscription["event_id"])
                for notification in event_notifications:
                    notification["event_name"] = event["name"]
                    notification["event_image"] = event["image"]
                notifications.extend(event_notifications)
            if notifications:
                notifications = sorted(
                    notifications,
                    key=lambda x: datetime.strptime(x["date"], "%d/%m/%Y"),
                )
                return (
                    jsonify(
                        success_response(
                            notifications, "Notificaciones encontrados correctamente"
                        )
                    ),
                    200,
                )
            return (
                jsonify(success_response([], "No se encontraron notificaciones")),
                200,
            )
        except Exception as e:
            return (
                jsonify(
                    system_error_response(
                        "Error del sistema al intentar listar las notificaciones",
                        str(e),
                    )
                ),
                500,
            )

    def get_event_notification(self, event_id):
        try:
            notifications = self.notification_service.get_event_notifications(event_id)
            if notifications:
                notifications = sorted(
                    notifications,
                    key=lambda x: datetime.strptime(x["date"], "%d/%m/%Y"),
                )
                return (
                    jsonify(
                        success_response(
                            notifications, "Notificaciones encontrados correctamente"
                        )
                    ),
                    200,
                )
            return (
                jsonify(success_response([], "No se encontraron notificaciones")),
                200,
            )
        except Exception as e:
            return (
                jsonify(
                    system_error_response(
                        "Error del sistema al intentar listar las notificaciones",
                        str(e),
                    )
                ),
                500,
            )

    def get_notification(self, notification_id):
        try:
            notification = self.notification_service.get_notification_by_id(
                notification_id
            )
            if notification:
                # Convierte '_id' de ObjectId a str
                notification["_id"] = str(notification["_id"])
                return (
                    jsonify(
                        success_response(
                            notification, "Notificacion encontrado correctamente"
                        )
                    ),
                    200,
                )
            return jsonify(error_response("Notificacion no encontrado", None)), 404
        except Exception as e:
            return (
                jsonify(
                    system_error_response(
                        "Error del sistema al intentar obtener una notificacion", str(e)
                    )
                ),
                500,
            )

    def update_notification(self, notification_id):
        try:
            data = request.get_json()
            self.notification_service.update_notification(notification_id, data)
            return (
                jsonify(success_response(data, "Notificacion actualizado con exito")),
                201,
            )
        except Exception as e:
            return (
                jsonify(
                    system_error_response(
                        "Error del sistema al intentar actualizar una notificacion",
                        str(e),
                    )
                ),
                500,
            )

    def delete_notification(self, notification_id):
        try:
            self.notification_service.delete_notification(notification_id)
            return (
                jsonify(success_response(None, "Notificacion eliminado con exito")),
                201,
            )
        except Exception as e:
            return (
                jsonify(
                    system_error_response(
                        "Error del sistema al intentar eliminar una notificacion",
                        str(e),
                    )
                ),
                500,
            )
