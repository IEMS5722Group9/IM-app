import datetime
import json
import math
import mysql.connector
import requests
from flask import Flask, jsonify, request


class MyDatabase:
    conn = None
    cursor = None

    def __init__(self):
        self.connect()
        return

    def connect(self):
        self.conn = mysql.connector.connect(
            host="localhost",
            port=3306,  # default, can be omitted
            user="dbuser",  # dbuser
            password="password",  # password
            database="group9", # group9
        )
        self.cursor = self.conn.cursor(dictionary=True)
        return


class DateEncoder(json.JSONEncoder):
    def default(self, obj):
        if isinstance(obj, datetime.datetime):
            return obj.strftime('%Y-%m-%d %H:%M:%S')
        else:
            return json.JSONEncoder.default(self, obj)


app = Flask(__name__)
app.config['JSON_SORT_KEYS'] = False
app.json_encoder = DateEncoder


def getMessagesFromDB(chatroom_id, page):
    mydb = MyDatabase()
    query = "select messages.*, users.avatar from messages, users where messages.user_id=users.id and messages.chatroom_id=%s order by messages.message_time desc, messages.id desc limit %s, %s;"
    # query = "select * from messages where chatroom_id=%s order by message_time desc, id desc limit %s,%s"
    cur_first = 10 * (int(page) - 1)
    params = (chatroom_id, cur_first, 10)
    mydb.cursor.execute(query, params)
    messages = mydb.cursor.fetchall()
    return messages


# /api/a3/get_user
@app.route("/api/a3/get_user", methods=['POST'])
def get_user():
    username = request.form.get('username')
    mydb = MyDatabase()
    query = "select * from users where BINARY username=\"%s\"" % username
    mydb.cursor.execute(query)
    user = mydb.cursor.fetchall()
    if len(user) == 0:
        return jsonify(message='No data', status='ERROR')
    else:
        return jsonify(status='OK', data=user)


# /api/a3/register_user
@app.route("/api/a3/register_user", methods=['POST'])
def register_user():
    username = request.form.get('username')
    password = request.form.get("password")
    salt = request.form.get('salt')

    mydb = MyDatabase()
    query1 = "select * from users where username=\"%s\"" % username
    mydb.cursor.execute(query1)
    user = mydb.cursor.fetchall()
    if len(user) != 0:
        return jsonify(message='User already exists', status='ERROR')

    query2 = "INSERT INTO users (username, password, salt) VALUES (%s,%s,%s)"
    params = (username, password, salt)
    mydb.cursor.execute(query2, params)
    mydb.conn.commit()
    return jsonify(status='OK')


@app.route("/api/a3/get_chatrooms")
def get_chatrooms():
    mydb = MyDatabase()
    query = "SELECT chatrooms.id as room_id, chatrooms.name as room_name, chatrooms.type as room_type, messages.name as username, messages.message, messages.message_time FROM chatrooms, messages " \
            "WHERE messages.id=(SELECT max(id) FROM messages where chatroom_id=chatrooms.id) ORDER BY messages.message_time DESC;"
    # query = "SELECT * FROM chatrooms"
    mydb.cursor.execute(query)
    chatrooms = mydb.cursor.fetchall()
    return jsonify(status='OK', data=chatrooms)


# /api/a3/add_chatroom
@app.route("/api/a3/add_chatroom", methods=['POST'])
def add_chatroom():
    user_name = request.form.get('user_name')
    friend_name = request.form.get('friend_name')
    type = request.form.get('type')
    chatroom_name = user_name + '&' + friend_name
    reverse_name = friend_name + '&' + user_name

    # 如果chatroom已经存在就不插入
    mydb = MyDatabase()
    query1 = "INSERT INTO chatrooms(`name`,type) " \
             "SELECT %s,%s FROM dual WHERE NOT EXISTS" \
             "(SELECT *  FROM chatrooms WHERE `name` in(%s,%s));"
    params1 = (chatroom_name, type, chatroom_name, reverse_name)
    mydb.cursor.execute(query1, params1)
    mydb.conn.commit()

    query2 = "SELECT id FROM chatrooms WHERE name in(%s,%s)"
    params2 = (chatroom_name, reverse_name)
    mydb.cursor.execute(query2, params2)
    room = mydb.cursor.fetchall()
    return jsonify(status='OK', data=room)


# /api/a3/get_messages?chatroom_id=1&page=1
@app.route("/api/a3/get_messages")
def get_messages():
    chatroom_id = request.args.get('chatroom_id')
    page = request.args.get('page')
    if not all([chatroom_id, page]):
        return jsonify(message='<error message>', status='ERROR')

    mydb = MyDatabase()
    query1 = "select count(*) from messages where chatroom_id=%s"
    params1 = (chatroom_id,)
    mydb.cursor.execute(query1, params1)
    get_total = mydb.cursor.fetchone()
    total_messages = list(get_total.values())[0]
    total_pages = math.floor((total_messages - 1) / 10) + 1
    current_page = page
    if int(current_page) > int(total_pages):
        return jsonify(message='No data', status='ERROR')

    messages = getMessagesFromDB(chatroom_id, page)
    datalist = {"current_page": current_page, "messages": messages, "total_pages": total_pages}
    # print(datalist)
    return jsonify(status='OK', data=datalist)


# /send_message/chatroom_id=1&user_id=1234567891&name=Bob&message=hi
@app.route("/api/a3/send_message", methods=['POST'])
def send_message():
    chatroom_id = request.form.get("chatroom_id")
    user_id = request.form.get("user_id")
    name = request.form.get("name")
    message = request.form.get("message")
    if not all([chatroom_id, user_id, name, message]):
        return jsonify(message='<error message>', status='ERROR')

    mydb = MyDatabase()
    query = "INSERT INTO messages (chatroom_id, user_id, name, message) VALUES (%s,%s,%s,%s)"
    params = (chatroom_id, user_id, name, message)
    mydb.cursor.execute(query, params)
    mydb.conn.commit()

    data = {
        "chatroom_id": chatroom_id,
        "message": message
    }
    url = "http://18.219.150.95:8001/api/a4/broadcast_room"
    res = requests.post(url, data=data)
    # 云服务器要pip install requests
    return jsonify(status='OK')


# /api/a3/get_friends?user_id=1
@app.route('/api/a3/get_friends', methods=['GET'])
def get_friends():
    user_id = request.args.get('user_id')
    mydb = MyDatabase()
    # query = "SELECT friend_id, friend_name FROM friends WHERE user_id=%s;" % user_id
    query = "SELECT friend_id, friend_name, users.avatar FROM friends, users WHERE friends.user_id=%s and friends.friend_id=users.id;" % user_id
    mydb.cursor.execute(query)
    friend = mydb.cursor.fetchall()
    return jsonify(status='OK', data=friend)


# /api/a3/search_friend
@app.route("/api/a3/search_friend", methods=['POST'])
def search_friend():
    friend_id = request.form.get('friend_id')
    mydb = MyDatabase()
    query = "select * from users where id=%s" % friend_id
    mydb.cursor.execute(query)
    friend = mydb.cursor.fetchall()
    if len(friend) == 0:
        return jsonify(message='User does not exist', status='ERROR')

    return jsonify(status='OK', data=friend)


# /api/a3/add_friend
@app.route("/api/a3/add_friend", methods=['POST'])
def add_friend():
    user_id = request.form.get('user_id')
    friend_id = request.form.get('friend_id')
    friend_name = request.form.get('friend_name')

    mydb = MyDatabase()
    query1 = "SELECT * FROM friends where user_id=%s and friend_id=%s limit 1"
    params1 = (user_id, friend_id)
    mydb.cursor.execute(query1, params1)
    exist = mydb.cursor.fetchall()
    if len(exist) != 0:
        return jsonify(message='Alrealdy added', status='ERROR')

    query2 = "INSERT INTO friends (user_id, friend_id, friend_name) VALUES (%s,%s,%s)"
    params2 = (user_id, friend_id, friend_name)
    mydb.cursor.execute(query2, params2)
    mydb.conn.commit()

    return jsonify(status='OK')


# /api/a3/get_avatar?username=alice
@app.route('/api/a3/get_avatar')
def get_avatar():
    username = request.args.get('username')
    mydb = MyDatabase()
    query = "select avatar from users where username=\"%s\"" % username
    mydb.cursor.execute(query)
    avatar = mydb.cursor.fetchall()
    return jsonify(status='OK', data=avatar)

# /api/a3/post_avatar
@app.route("/api/a3/post_avatar", methods=['POST'])
def post_avatar():
    user_id = request.form.get('user_id')
    avatar = request.form.get('avatar')

    mydb = MyDatabase()
    query = "update users set avatar=%s where id=%s"
    params = (avatar, user_id)
    mydb.cursor.execute(query, params)
    mydb.conn.commit()

    return jsonify(status='OK')


if __name__ == '__main__':
    app.run(debug=False)
