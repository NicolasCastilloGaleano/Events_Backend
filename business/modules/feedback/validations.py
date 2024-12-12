from flask import jsonify


def validate_feedback_data(data):
    required_fields = [
        "user_id",
        "event_id",
        "stars",
    ]
    missing_fields = [field for field in required_fields if field not in data]

    if missing_fields:
        return True

    return False