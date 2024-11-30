from flask import jsonify


def validate_event_data(data):
    required_fields = [
        "name",
        "site",
        "description",
        "date",
        "time",
        "categories",
        "image",
        "organizer_id",
    ]
    missing_fields = [field for field in required_fields if field not in data]

    if missing_fields:
        return True

    return False
