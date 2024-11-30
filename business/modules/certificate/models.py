import json


class Certificate:
    def __init__(
        self,
        user_name,
        event_name,
        site,
        date,
        time,
        description,
        entity,
        image,
        restrictions,
    ):
        self.user_name: str = user_name
        self.event_name: str = event_name
        self.site: str = site
        self.description: str = description
        self.date = date
        self.time = time
        self.entity: str = entity
        self.image: str = image
        self.restrictions: list[str] = restrictions

    def to_dict(self):
        return {
            "user_name": self.user_name,
            "event_name": self.event_name,
            "site": self.site,
            "description": self.description,
            "date": self.date,
            "time": self.time,
            "categories": [str(categorie) for categorie in self.categories],
            "entity": self.entity,
            "image": self.image,
            "restrictions": [str(restriction) for restriction in self.restrictions],
        }
