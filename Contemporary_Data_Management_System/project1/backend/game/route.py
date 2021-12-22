from flask import Blueprint, session
from flask import request, send_file
from models import User, Container, Treasure
import json
import os
import io
import numpy as np
from bson.objectid import ObjectId

game_bp = Blueprint(name="game", import_name="__name__", static_folder="static", template_folder="template")


@game_bp.route("/seek", methods=['GET'])
def seek_treasure():
    status = 0
    treasure = {}
    if session.get('user') is not None:
        user_id_str = session.get('user')
        user_id = ObjectId(user_id_str)
        user_obj = User.objects(id=user_id).first()
        user_luck = user_obj.luck
        value_decide = int(np.random.normal(user_luck, 3))
        if value_decide < 1:
            value_decide = 1
        closest_value_obj = Treasure.objects(gain__lte=value_decide).order_by('-gain').first()
        available_objs = Treasure.objects(gain=closest_value_obj.gain).all()
        chosen_index = np.random.randint(0, len(available_objs))
        chosen_obj = available_objs[chosen_index]
        container_item = Container(treasure_id=chosen_obj.id, treasure_name=chosen_obj.name,
                                   treasure_type=chosen_obj.type, treasure_gain=chosen_obj.gain, owner=user_id,
                                   owner_name=user_obj.username, status=2)
        user_obj.container_usage += 1
        container_item.save()
        user_obj.save()
        if user_obj.container_usage > 20:
            recycle_obj = Container.objects(owner=user_id).order_by('gain').first()
            recycle_obj.delete()
            user_obj.container_usage -= 1
            user_obj.save()
        treasure['name'] = chosen_obj.name
        treasure['type'] = chosen_obj.type
        treasure['gain'] = chosen_obj.gain
        status = 1
    else:
        status = 100
    return json.dumps({'status': status, 'treasure': treasure})


@game_bp.route('/work', methods=['GET'])
def gain_money():
    status = 0
    coin = 0
    if session.get('user') is not None:
        user_id_str = session.get('user')
        user_id = ObjectId(user_id_str)
        user_obj = User.objects(id=user_id).first()
        user_power = user_obj.power
        gain_decide = int(np.random.normal(user_power, 5))
        if gain_decide < 1:
            gain_decide = 1
        coin = gain_decide
        user_obj.coin += coin
        user_obj.save()
        status = 1
    else:
        status = 100
    return json.dumps({'status': status, 'coin': coin})