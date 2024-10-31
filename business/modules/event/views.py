from flask import request, jsonify
from modules.event.services import EventService
from modules.event.validations import validate_event_data


class EventView:
    def __init__(self):
        self.event_service = EventService()

    def create_event(self):
        data = request.get_json()

        validation_error = validate_event_data(data)
        if validation_error:
            return validation_error
        event = self.event_service.create_event(data)
        return (
            jsonify(
                {"message": "Event created successfully", "event": event.to_dict()}
            ),
            201,
        )

    def get_events(self):
        events = self.event_service.get_events()
        if events:
            return jsonify(events), 200
        return jsonify({"error": "Event not found"}), 404

    def get_event(self, event_id):
        event = self.event_service.get_event_by_id(event_id)
        if event:
            return jsonify(event), 200
        return jsonify({"error": "Event not found"}), 404

    def update_event(self, event_id):
        data = request.get_json()
        self.event_service.update_event(event_id, data)
        return jsonify({"message": "Event updated successfully"}), 200

    def delete_event(self, event_id):
        self.event_service.delete_event(event_id)
        return jsonify({"message": "Event deleted successfully"}), 200
