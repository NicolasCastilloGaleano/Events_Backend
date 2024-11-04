def success_response(data, message):
    return {"data": data, "message": message, "error": None}


def error_response(error_message, data):
    return {"data": data, "message": error_message, "error": None}


def system_error_response(error_message, error):
    return {"data": None, "message": error_message, "error": error}
