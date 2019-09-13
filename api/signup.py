from flask_restful import Resource, reqparse
from model.User import User
from model.database import db, gen_id
from tools.request_message import request_message
from App import app


class SignUp(Resource):
    def post(self):
        try:
            parser = reqparse.RequestParser()
            parser.add_argument('email',    type=str)
            parser.add_argument('name',     type=str)
            parser.add_argument('password', type=str)
            args = parser.parse_args()

            public_id = gen_id()
            email = args['email']
            name = args['name']
            password = args['password']
            app.logger.debug('[sign-up] email: %s', email)
            app.logger.debug('[sign-up] name: %s', name)
            user = User(public_id=public_id, email=email, name=name, password=password)
            if(user.is_duplicate()):
                return request_message('fail', 'duplicate id')
            else:
                db.session.add(user)
                db.session.commit()
                return request_message('success', 'sing up')
        except Exception as e:
            return request_message('error', str(e))
