from flask import Blueprint, session
from flask import request, send_file
from models import User, Container, Treasure
import json
import os
import io
import numpy as np
from bson.objectid import ObjectId

market_bp = Blueprint(name="market", import_name="__name__", static_folder="static", template_folder="template")


@market_bp.route("/list", methods=['GET'])
def list_market():
    status = 0
    item_list = []
    if session.get('user') is not None:
        item_list_raw = Container.objects(status=3).all()
        for item in item_list_raw:
            user_id_str = session.get('user')
            user_id = ObjectId(user_id_str)
            if item.owner == user_id:
                self_belonging = 1
            else:
                self_belonging = 0
            item_list.append({'id': str(item.id), 'treasure_id': str(item.treasure_id), 'treasure_name': item.treasure_name,
                              'treasure_type': item.treasure_type, 'seller': item.owner_name, 'self_belonging': self_belonging,
                              'price': item.price})
        status = 1
    else:
        status = 100
    return json.dumps({'status': status, 'items': item_list})


@market_bp.route("/purchase", methods=['POST'])
def purchase_item():
    req_str = request.get_data(as_text=True)
    try:
        status = 0
        if session.get('user') is not None:
            req_dict = json.loads(req_str)
            id_str = req_dict['id']
            record_id = ObjectId(id_str)
            record_obj = Container.objects(id=record_id).first()
            user_id_str = session.get('user')
            user_id = ObjectId(user_id_str)
            user_obj = User.objects(id=user_id).first()
            if record_obj.status == 3:
                if user_obj.coin >= record_obj.price:
                    seller_obj = User.objects(id=record_obj.owner).first()
                    seller_obj.container_usage -= 1
                    seller_obj.coin += record_obj.price
                    user_obj.coin -= record_obj.price
                    user_obj.container_usage += 1
                    record_obj.owner = user_id
                    record_obj.owner_name = user_obj.username
                    record_obj.price = 0
                    record_obj.status = 2
                    user_obj.save()
                    record_obj.save()
                    seller_obj.save()
                    if user_obj.container_usage > 20:
                        recycle_obj = Container.objects(owner=user_id).order_by('gain').first()
                        recycle_obj.delete()
                        user_obj.container_usage -= 1
                        user_obj.save()
                    status = 1
                else:
                    status = 2  # Not affordable
            else:
                status = 3  # Target item is not on sale
        else:
            status = 100
        return json.dumps({'status': status})
    except Exception as e:
        print(e)
        print("Error when phasing request json string!")
        return json.dumps({'status': 999})