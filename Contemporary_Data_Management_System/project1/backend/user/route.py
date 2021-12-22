from flask import Blueprint, session
from flask import request, send_file
from models import User, Container
import json
import os
import io
from bson.objectid import ObjectId

user_bp = Blueprint(name="user", import_name="__name__", static_folder="static", template_folder="template")


@user_bp.route("/login", methods=['POST'])
def login():
    req_str = request.get_data(as_text=True)
    try:
        status = 0
        req_dict = json.loads(req_str)
        username = req_dict['username']
        password = req_dict['password']
        user_item = User.objects(username=username).first()
        if user_item is not None:
            if password == user_item.password:
                status = 1  # Login succeeded
                session['user'] = str(user_item.id)
            else:
                status = 2  # Wrong password
        else:
            status = 3  # Username doesn't exists
        return json.dumps({'status': status})
    except Exception as e:
        print(e)
        print("Error when phasing request json string!")
        return json.dumps({'status': 999})


@user_bp.route("/register", methods=['POST'])
def register():
    req_str = request.get_data(as_text=True)
    try:
        status = 0
        req_dict = json.loads(req_str)
        username = req_dict['username']
        password = req_dict['password']
        if username == "" and password == "":
            status = 3  # Empty info is forbidden
        else:
            user_item = User.objects(username=username).first()
            if user_item is not None:
                status = 2  # Username has already been occupied
            else:
                user = User(username=username, password=password)
                user.save()
                status = 1  # Register succeeded
        return json.dumps({'status': status})
    except Exception as e:
        print(e)
        print("Error when phasing request json string!")
        return json.dumps({'status': 999})


@user_bp.route("/info", methods=['GET'])
def get_info():
    status = 0
    info_dict = {}
    if session.get('user') is not None:
        user_id_str = session.get('user')
        user_id = ObjectId(user_id_str)
        user_obj = User.objects(id=user_id).first()
        if user_obj.nickname is not None:
            info_dict['nickname'] = user_obj.nickname
        else:
            info_dict['nickname'] = ""
        if user_obj.regtime is not None:
            info_dict['regtime'] = user_obj.regtime.timestamp()
        if user_obj.permission is not None:
            info_dict['permission'] = user_obj.permission
        else:
            info_dict['permission'] = 1
        if user_obj.coin is not None:
            info_dict['coin'] = user_obj.coin
        else:
            info_dict['coin'] = 0
        if user_obj.power is not None:
            info_dict['power'] = user_obj.power
        else:
            info_dict['power'] = 0
        if user_obj.luck is not None:
            info_dict['luck'] = user_obj.luck
        status = 1
    else:
        status = 100
    return json.dumps({'status': status, 'info': info_dict})


@user_bp.route("/set_avatar", methods=['POST'])
def set_avatar():
    status = 0
    if session.get('user') is not None:
        user_id_str = session.get('user')
        avatar_img = request.files.get("avatar")
        save_dir = 'data/' + session.get('user') + '/'
        if not os.path.exists(save_dir):
            os.makedirs(save_dir)
        save_path = os.path.join(save_dir, avatar_img.filename)
        avatar_img.save(save_path)
        avatar_f = open(save_path, 'rb')
        user_id = ObjectId(user_id_str)
        user_obj = User.objects(id=user_id).first()
        if user_obj.avatar.read() is not None:
            user_obj.avatar.replace(avatar_f)
        else:
            user_obj.avatar.put(avatar_f)
        user_obj.save()
        status = 1
    else:
        status = 100
    return json.dumps({'status': status})


@user_bp.route("/get_avatar", methods=['GET'])
def get_avatar():
    default_avatar_path = 'data/avatar_default.png'
    if session.get('user') is not None:
        user_id_str = session.get('user')
        user_id = ObjectId(user_id_str)
        user_obj = User.objects(id=user_id).first()
        buffer_tmp = user_obj.avatar.read()
        if buffer_tmp is not None:
            avatar_buffer = buffer_tmp
        else:
            avatar_f = open(default_avatar_path, 'rb')
            avatar_buffer = avatar_f.read()
    else:
        avatar_f = open(default_avatar_path, 'rb')
        avatar_buffer = avatar_f.read()
    return send_file(io.BytesIO(avatar_buffer), attachment_filename='avatar.jpg')


@user_bp.route("/set_nickname", methods=['POST'])
def set_nickname():
    req_str = request.get_data(as_text=True)
    try:
        status = 0
        if session.get('user') is not None:
            req_dict = json.loads(req_str)
            nickname = req_dict['nickname']
            user_id_str = session.get('user')
            user_id = ObjectId(user_id_str)
            user_obj = User.objects(id=user_id).first()
            user_obj.nickname = nickname
            user_obj.save()
            status = 1
        else:
            status = 100
        return json.dumps({'status': status})
    except Exception as e:
        print(e)
        print("Error when phasing request json string!")
        return json.dumps({'status': 999})


@user_bp.route("/equipment", methods=['GET'])
def get_equipment():
    status = 0
    equipment_list = []
    if session.get('user') is not None:
        user_id_str = session.get('user')
        user_id = ObjectId(user_id_str)
        equipment_list_raw = Container.objects(owner=user_id, status=1).all()
        for equipment in equipment_list_raw:
            equipment_list.append({'id': str(equipment.treasure_id), 'name': equipment.treasure_name,
                                   'type': equipment.treasure_type, 'gain': equipment.treasure_gain})
        status = 1
    else:
        status = 100
    return json.dumps({'status': status, 'equipment': equipment_list})
