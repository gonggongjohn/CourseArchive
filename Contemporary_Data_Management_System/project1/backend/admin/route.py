from flask import Blueprint, session
from flask import request, send_file
from models import User, Treasure
import json
from bson.objectid import ObjectId

admin_bp = Blueprint(name="admin", import_name="__name__", static_folder="static", template_folder="template")


@admin_bp.route('/add_treasure', methods=['POST'])
def add_treasure():
    req_str = request.get_data(as_text=True)
    try:
        status = 0
        if session.get('user') is not None:
            user_id_str = session.get('user')
            user_id = ObjectId(user_id_str)
            user_obj = User.objects(id=user_id).first()
            if user_obj.permission == 2:
                req_dict = json.loads(req_str)
                treasure_name = req_dict['name']
                treasure_type = req_dict['type']
                treasure_gain = req_dict['gain']
                treasure_obj = Treasure(name=treasure_name, type=treasure_type, gain=treasure_gain)
                treasure_obj.save()
                status = 1
            else:
                status = 2  # No permission
        else:
            status = 100
        return json.dumps({'status': status})
    except Exception as e:
        print(e)
        print("Error when phasing request json string!")
        return json.dumps({'status': 999})