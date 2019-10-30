from flask_restful import Resource, reqparse
from flask import Response, stream_with_context
from tools.request_message import request_message
from api.auth import Auth
from model.music import Music
from App import logger
import os
from model.database import update


class Download(Resource):
    def post(self):
        try:
            parser = reqparse.RequestParser()
            parser.add_argument('token', type=str)
            parser.add_argument('music_id', type=str)
            args = parser.parse_args()

            (token, err) = Auth.decord_token(args['token'])
            if token is None:
                return err, 200

            _file = Music.query.filter_by(music_id=args['music_id']).first()
            logger().debug('[download] file: %s', _file.music_id)
            logger().debug('[download] file: %s', _file.name)
            if _file is not None:
                if(os.path.exists(_file.path())):
                    buffer = open(_file.path(), 'rb')
                    _file.downloads += 1
                    update()
                    def streaming():
                        for line in buffer:
                            yield line
                    return Response(stream_with_context(streaming()))
                return request_message('error', 'Can\'t find file')
            else:
                return request_message('fail', 'Can\'t find music')
        except Exception as e:
            logger().error(str(e))
            return request_message('error', str(e))

# response = make_response(streaming)
# response.headers['Content-Type'] = "application/octet-stream"
# content_type='application/octet-stream',
