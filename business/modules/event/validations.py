from flask import jsonify


def validate_event_data(data):
    required_fields = [
        "name",
        "date",
        "location",
        "capacity",
        "description",
        "organizer_id",
    ]
    missing_fields = [field for field in required_fields if field not in data]

    if missing_fields:
        return jsonify({"error": f"Missing fields: {', '.join(missing_fields)}"}), 400

    if not isinstance(data.get("capacity"), int) or data["capacity"] <= 0:
        return jsonify({"error": "Capacity must be a positive integer"}), 400

    return None
