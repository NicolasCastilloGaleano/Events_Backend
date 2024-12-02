from flask import jsonify, Response
from utils.response_service import (
    error_response,
    system_error_response,
)
from modules.certificate.services import CertificateService
from modules.inscription.services import InscriptionService


class CertificateView:
    inscription_service = InscriptionService()

    def __init__(self):
        self.certificate_service = CertificateService()

    def generate_certificate(self, inscription_id):
        try:
            inscription = self.inscription_service.get_inscription_by_id(inscription_id)
            if inscription:
                if inscription["participated"]:
                    certificate = self.certificate_service.generate_certificate(
                        inscription
                    )
                    response = Response(certificate, content_type="application/pdf")
                    response.headers["Content-Disposition"] = (
                        "inline; filename=certificado.pdf"
                    )
                    return response, 200
                else:
                    return (
                        jsonify(
                            error_response("El usuario no participo del evento", None)
                        ),
                        400,
                    )
            else:
                return (
                    jsonify(error_response("Error al encontrar la inscripcion", None)),
                    400,
                )
        except Exception as e:
            return (
                jsonify(
                    system_error_response(
                        "Error del sistema al intentar generar el certificado", str(e)
                    )
                ),
                500,
            )
