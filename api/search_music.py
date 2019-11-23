from flask_restful import Resource, reqparse
from sqlalchemy.orm import raiseload
from model.music import Music
from model.tag import Tag
from api.auth import Auth
from tools.request_message import request_message, make_data
from App import logger


class MusicSearch(Resource):
    def post(self):
        try:
            parser = reqparse.RequestParser()
            parser.add_argument('token',  type=str)
            parser.add_argument('target', type=str, action='append')
            parser.add_argument('flag',   type=int)
            args = parser.parse_args()
            (token, err) = Auth.decord_token(args['token'])
            if token is None: return err, 200
            user = token.get('sub')
            logger().debug('[search music] user_name: %s', user)
            logger().debug('[search music] parameter: %s', args['target'])
            logger().debug('[search music] flag: %s', args['flag'])

            flag = args['flag']
            result = []
            targets = []
            if isinstance(args['target'], list):
                logger().debug('[search music] is list : %s',type(args['target']))
                targets.extend(args['target'])
            else:
                logger().debug('[search music] not list: %s', type(args['target']))
                targets.append(args['target'])
            for target in targets:
                if flag == 1:   # music name
                    logger().debug('[search music] flag 1 in')
                    result.extend(make_data(Music.query.filter(Music.name.startswith(target)).all()))
                elif flag == 2:  # tag
                    logger().debug('[search music] flag 2 in')
                    tag = Tag.query.options(raiseload('*')).filter_by(name=target).first()
                    if tag is not None:
                        result.extend(make_data(tag.music_list()))
                        logger().debug('[search music] flag 2-2 in')
                elif flag == 3:  # user name
                    logger().debug('[search music] flag 3 in')
                    result.extend(make_data(Music.query.filter(Music.owner.startswith(target)).all()))
                else:
                    request_message('error', 'wrong flag {}'.format(flag))
            return {
                'status':  'success',
                'message': 'music-search',
                'results': result
            }, 200

        except Exception as e:
            logger().error(str(e))
            return request_message('error', str(e))
