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
import unittest
import json
import coverage


def init_db():
    treasure_list = Treasure.objects().all()
    if len(treasure_list) == 0:
        load_treasure()


def create_app():
    app = Flask(__name__)
    app.config.from_object(config)
    db.init_app(app)
    init_db()
    app.register_blueprint(user.route.user_bp, url_prefix="/user")
    app.register_blueprint(game.route.game_bp, url_prefix="/game")
    app.register_blueprint(container.route.container_bp, url_prefix="/container")
    app.register_blueprint(market.route.market_bp, url_prefix="/market")
    app.register_blueprint(admin.route.admin_bp, url_prefix="/admin")
    CORS(app, supports_credentials=True)
    return app


class TestApp(unittest.TestCase):
    def setUp(self) -> None:
        app = create_app()
        app.config['TESTING'] = True
        self.app = app.test_client()

    def test_a_register(self):
        response = self.app.post('/user/register', data=json.dumps({'username': 'test6', 'password': 'test6'}))
        status = json.loads(response.data)['status']
        self.assertEqual(status, 1)

    def test_b_login(self):
        response = self.app.post('/user/login', data=json.dumps({'username': 'admin', 'password': 'admin'}))
        status = json.loads(response.data)['status']
        self.assertEqual(status, 1)

    def test_c_user_info(self):
        response = self.app.get('/user/info')
        status = json.loads(response.data)['status']
        self.assertEqual(status, 1)

    def test_d_set_nickname(self):
        response = self.app.post('/user/set_nickname', data=json.dumps({'nickname': 'testName'}))
        status = json.loads(response.data)['status']
        self.assertEqual(status, 1)

    def test_e_seek(self):
        response = self.app.get('/game/seek')
        data = json.loads(response.data)
        status = data['status']
        self.assertEqual(status, 1)

    def test_f_work(self):
        response = self.app.get('/game/work')
        status = json.loads(response.data)['status']
        self.assertEqual(status, 1)

    def test_g_list_container(self):
        response = self.app.get('/container/list')
        data = json.loads(response.data)
        status = data['status']
        self.targetId = data['items'][0]['id']
        self.assertEqual(status, 1)

    def test_h_wear(self):
        response = self.app.post('/container/wear', data=json.dumps({'id': self.targetId}))
        status = json.loads(response.data)['status']
        self.assertEqual(status, 1)

    def test_i_equipment(self):
        response = self.app.get('/user/equipment')
        status = json.loads(response.data)['status']
        self.assertEqual(status, 1)

    def test_j_takeoff(self):
        response = self.app.post('/container/takeoff', data=json.dumps({'id': self.targetId}))
        status = json.loads(response.data)['status']
        self.assertEqual(status, 1)

    def test_k_sell(self):
        response = self.app.post('/container/sell', data=json.dumps({'id': self.targetId, 'price': 10}))
        status = json.loads(response.data)['status']
        self.assertEqual(status, 1)

    def test_l_list_market(self):
        response = self.app.get('/market/list')
        status = json.loads(response.data)['status']
        self.assertEqual(status, 1)

    def test_m_abort_sell(self):
        response = self.app.post('/container/abortsell', data=json.dumps({'id': self.targetId}))
        status = json.loads(response.data)['status']
        self.assertEqual(status, 1)


if __name__ == "__main__":
    unittest.main(verbosity=2)
