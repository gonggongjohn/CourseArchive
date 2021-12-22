from flask import Flask
from flask_cors import CORS
import config
from ext import db
import user.route
import game.route
import container.route
import market.route
import admin.route
from models import Treasure
from utils import load_treasure
from flask_socketio import SocketIO

app = Flask(__name__)


def init_db():
    treasure_list = Treasure.objects().all()
    if len(treasure_list) == 0:
        load_treasure()


if __name__ == "__main__":
    debug = False
    app.config.from_object(config)
    db.init_app(app)
    init_db()
    app.register_blueprint(user.route.user_bp, url_prefix="/user")
    app.register_blueprint(game.route.game_bp, url_prefix="/game")
    app.register_blueprint(container.route.container_bp, url_prefix="/container")
    app.register_blueprint(market.route.market_bp, url_prefix="/market")
    app.register_blueprint(admin.route.admin_bp, url_prefix="/admin")
    CORS(app, supports_credentials=True)
    socket_io = SocketIO(app, cors_allowed_origin='*')
    app.run(host='0.0.0.0', port=5000, debug=True, threaded=True)
    socket_io.run(app, host='0.0.0.0', port=8000)


    @socket_io.on('connect', namespace='/chat')
    def test_connect():
        socket_io.emit('my_response', {'data': 'Connected'})


    @socket_io.on('disconnect', namespace='/chat')
    def test_disconnect():
        print('Client disconnected')

    @socket_io.on('message')
    def send_message():
        print("Sending message!")
        socket_io.emit("response", {'world_time': 0})


    @app.after_request
    def after_request(response):
        header = response.headers
        header['Access-Control-Allow-Origin'] = '*'
        header['Access-Control-Allow-Headers'] = 'x-requested-with, content-type'
        return response
