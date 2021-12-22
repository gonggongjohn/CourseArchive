from flask import Blueprint, session
from flask import request, send_file
from models import User, Container, Treasure
import json
import os
import io
import numpy as np
from bson.objectid import ObjectId

container_bp = Blueprint(name="container", import_name="__name__", static_folder="static", template_folder="template")


@container_bp.route("/list", methods=['GET'])
def list_container():
    status = 0
    item_list = []
    if session.get('user') is not None:
        user_id_str = session.get('user')
        user_id = ObjectId(user_id_str)
        item_list_raw = Container.objects(owner=user_id).all()
        for item in item_list_raw:
            item_info_dict = {'id': str(item.id), 'treasure_id': str(item.treasure_id), 'treasure_name': item.treasure_name,
                              'treasure_type': item.treasure_type, 'treasure_gain': item.treasure_gain, 'status': item.status}
            if item.status == 3:
                if item.price is not None:
                    item_info_dict['price'] = item.price
                else:
                    item_info_dict['price'] = 0
            item_list.append(item_info_dict)
        status = 1
    else:
        status = 100
    return json.dumps({'status': status, 'items': item_list})


@container_bp.route("/wear", methods=['POST'])
def wear_equipment():
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
            if record_obj.treasure_type == 1:
                if user_obj.tool_equipped >= 4:
                    status = 2  # Cannot wear more equipment
                else:
                    record_obj.status = 1
                    user_obj.tool_equipped += 1
                    user_obj.power += record_obj.treasure_gain
                    record_obj.save()
                    user_obj.save()
                    status = 1
            elif record_obj.treasure_type == 2:
                if user_obj.accessory_equipped >= 4:
                    status = 2  # Cannot wear more equipment
                else:
                    record_obj.status = 1
                    user_obj.accessory_equipped += 1
                    user_obj.luck += record_obj.treasure_gain
                    record_obj.save()
                    user_obj.save()
                    status = 1
        else:
            status = 100
        return json.dumps({'status': status})
    except Exception as e:
        print(e)
        print("Error when phasing request json string!")
        return json.dumps({'status': 999})


@container_bp.route("/takeoff", methods=['POST'])
def takeoff_equipment():
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
            if record_obj.status == 1:
                record_obj.status = 2
                record_obj.save()
                if record_obj.treasure_type == 1:
                    user_obj.tool_equipped -= 1
                    user_obj.power -= record_obj.treasure_gain
                elif record_obj.treasure_type == 2:
                    user_obj.accessory_equipped -= 1
                    user_obj.luck -= record_obj.treasure_gain
                user_obj.save()
                status = 1
            else:
                status = 2  # Not wearing this
        else:
            status = 100
        return json.dumps({'status': status})
    except Exception as e:
        print(e)
        print("Error when phasing request json string!")
        return json.dumps({'status': 999})


@container_bp.route("/sell", methods=['POST'])
def sell():
    req_str = request.get_data(as_text=True)
    try:
        status = 0
        if session.get('user') is not None:
            req_dict = json.loads(req_str)
            id_str = req_dict['id']
            price = req_dict['price']
            record_id = ObjectId(id_str)
            record_obj = Container.objects(id=record_id).first()
            if record_obj.status == 2:
                record_obj.status = 3
                record_obj.price = price
                record_obj.save()
                status = 1
            else:
                status = 2  # Current status cannot be sold
        else:
            status = 100
        return json.dumps({'status': status})
    except Exception as e:
        print(e)
        print("Error when phasing request json string!")
        return json.dumps({'status': 999})


@container_bp.route("/abortsell", methods=['POST'])
def abort_sell():
    req_str = request.get_data(as_text=True)
    try:
        status = 0
        if session.get('user') is not None:
            req_dict = json.loads(req_str)
            id_str = req_dict['id']
            record_id = ObjectId(id_str)
            record_obj = Container.objects(id=record_id).first()
            if record_obj.status == 3:
                record_obj.status = 2
                record_obj.price = 0
                record_obj.save()
                status = 1
            else:
                status = 2  # Current status is not selling state
        else:
            status = 100
        return json.dumps({'status': status})
    except Exception as e:
        print(e)
        print("Error when phasing request json string!")
        return json.dumps({'status': 999})
