from flask import jsonify


def validate_event_data(data):
    required_fields = [
        "_id",
        "name",
        "site",
        "description",
        "date",
        "categories",
        "image",
        "organizer_id",
    ]
    missing_fields = [field for field in required_fields if field not in data]

    if missing_fields:
        return jsonify({"error": f"Missing fields: {', '.join(missing_fields)}"}), 400

    return None
