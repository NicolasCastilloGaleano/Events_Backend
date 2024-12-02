from datetime import date
from io import BytesIO
from reportlab.pdfgen import canvas
from reportlab.lib.pagesizes import letter, landscape
import requests
from flask import current_app, request
from modules.event.services import EventService


class CertificateService:
    event_service = EventService()
    meses = {
        "1": "enero",
        "2": "febrero",
        "3": "marzo",
        "4": "abril",
        "5": "mayo",
        "6": "junio",
        "7": "julio",
        "8": "agosto",
        "9": "septiembre",
        "10": "octubre",
        "11": "noviembre",
        "12": "diciembre",
    }

    def generate_certificate(self, inscription):
        event = self.event_service.get_event_by_id(inscription["event_id"])
        token = request.headers.get("Authorization")
        url = f"{current_app.config['SECURITY_URI']}/users/{inscription["user_id"]}/no_role"
        user = requests.get(
            url=url,
            headers={"Authorization": token},
            timeout=5,
        ).json()["data"]
        pdf = self.create_pdf(event, user)
        return pdf

    def create_pdf(self, event, user):
        # Crear un buffer en memoria para el PDF
        buffer = BytesIO()

        # Configuración del PDF en orientación horizontal
        c = canvas.Canvas(buffer, pagesize=landscape(letter))
        width, height = landscape(letter)  # Dimensiones del tamaño carta en horizontal

        # Cargar una imagen de fondo para el certificado
        background_image = "../business/assets/img/fondo_certificado.png"  # Imagen diseñada en horizontal
        c.drawImage(background_image, 0, 0, width, height)

        c.setFillColorRGB(237 / 255, 191 / 255, 109 / 255)

        c.setFont("Helvetica", 40)
        c.drawCentredString(
            width / 2, height - 248, f"{user["userProfile"]["name"][:24]}"
        )

        c.setFont("Helvetica", 16)
        c.drawCentredString(
            width / 2, height - 300, "Por su participación en el evento:"
        )

        c.setFont("Helvetica", 20)
        c.drawCentredString(width / 2, height - 330, f"{event["name"][:55]}")

        event_date = str(event["date"]).split("/")
        c.setFont("Helvetica", 16)
        c.drawCentredString(
            width / 2,
            height - 360,
            f"El dia {event_date[0]} de {self.meses[event_date[1]] } del {event_date[2]}",
        )

        today = date.today()
        c.setFont("Helvetica-Oblique", 12)
        c.drawCentredString(
            width / 2,
            height - 580,
            f"Otorgado el {today.day} de {self.meses[str(today.month)]} de {today.year}.",
        )

        # Finalizar y guardar el PDF
        c.save()

        # Configurar el buffer para leer desde el inicio
        buffer.seek(0)
        return buffer
