from ext import db
from datetime import datetime


class User(db.Document):
    username = db.StringField(required=True)
    password = db.StringField(required=True)
    nickname = db.StringField()
    permission = db.IntField(default=1)  # 1 - Player; 2 - Admin
    avatar = db.FileField()
    coin = db.IntField(default=10)
    power = db.IntField(default=1)
    luck = db.IntField(default=1)
    regtime = db.DateTimeField(default=datetime.now())
    tool_equipped = db.IntField(default=0)
    accessory_equipped = db.IntField(default=0)
    container_usage = db.IntField(default=0)


class Treasure(db.Document):
    name = db.StringField(required=True)
    type = db.IntField(required=True)  # 1 - Tools; 2 - Accessories
    gain = db.IntField()
    meta = {
        'indexes': [
            'gain'
        ]
    }


class Container(db.Document):
    treasure_id = db.ObjectIdField(required=True)
    treasure_name = db.StringField()
    treasure_type = db.IntField()
    treasure_gain = db.IntField()
    owner = db.ObjectIdField(required=True)
    owner_name = db.StringField()
    status = db.IntField()  # 1 - Equipped; 2 - In inventory; 3 - On sale
    price = db.IntField()  # Only exists when status = 3
    meta = {
        'indexes': [
            'owner',
            'status'
        ]
    }

'''
class Market(db.Document):
    
    treasure_id = db.ObjectIdField(required=True)
    treasure_name = db.StringField()
    treasure_type = db.IntField()
    seller_id = db.ObjectIdField(required=True)
    seller_name = db.StringField()
'''