from bson import ObjectId


class Event:
    def __init__(
        self,
        name,
        site,
        date,
        categories,
        description,
        organizer_id,
        entity,
        image,
        restrictions,
        _id=None,
    ):
        self._id = ObjectId() if _id is None else _id
        self.name: str = name
        self.site: str = site
        self.description: str = description
        self.date = date
        self.categories: list[str] = categories
        self.entity: str = entity
        self.image: str = image
        self.restrictions: list[str] = restrictions
        self.organizer_id = ObjectId(organizer_id)

    def to_dict(self):
        return {
            "_id": str(self._id),
            "name": self.name,
            "site": self.site,
            "description": self.description,
            "date": self.date,
            "categories": [str(categorie) for categorie in self.categories],
            "entity": self.entity,
            "image": self.image,
            "restrictions": [str(restriction) for restriction in self.restrictions],
            "organizer_id": str(self.organizer_id),
        }
