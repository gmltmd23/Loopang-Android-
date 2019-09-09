from flask_restful import Resource, reqparse
from model.User import User
from api.auth import Auth
from tools.request_message import request_message


class Login(Resource):
    def post(self):
        try:
            parser = reqparse.RequestParser()
            parser.add_argument('email',    type=str)
            parser.add_argument('password', type=str)
            args = parser.parse_args()

            email = args['email']
            password = args['password']

            db_user = User.query.filter_by(email=email).first()
            if db_user is None:
                return request_message('fail', 'unregistered id or wrong password')
                #return {'status': 'fail', 'message': 'unregistered id or wrong password'}, 200
            access = Auth.encord_access_token(db_user.public_id).decode('UTF-8')
            if db_user.check_password(password):
                return {
                    'status': 'success',
                    'message': 'login',
                    'refreshToken': Auth.encord_refresh_token(db_user.public_id).decode('UTF-8'),
                    'accessToken': access
                }, 200
            else:
                return request_message('fail', 'unregistered id or wrong password')

        except Exception as e:
            return request_message('error', str(e))
