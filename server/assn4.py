from flask import Flask, request, jsonify
from flask_socketio import SocketIO, emit, join_room, send, leave_room

app = Flask(__name__)
app.config['SECRET_KEY'] = 'iems5722'
socketio = SocketIO(app)


@app.route("/api/a4/broadcast_room", methods=["POST"])
def broadcast_room():
    chatroom_id = request.form.get("chatroom_id")
    message = request.form.get("message")
    if not all([chatroom_id, message]):
        return jsonify(message='<error message>', status='ERROR')

    broadcast_data = {'chatroom_id': chatroom_id, 'message': message}
    socketio.emit('message', broadcast_data, broadcast=True, room=chatroom_id, include_self=False)
    return jsonify(status='OK')


@socketio.on('connect')
def connect_handler():
    print('Client connected')


@socketio.on('disconnect')
def disconnect_handler():
    print('Client disconnected')


@socketio.on('join')
def on_join(username, chatroom_id):
    # username = data['username']
    # chatroom_id = data['chatroom_id']
    join_room(chatroom_id)
    print(username, chatroom_id)
    emit('join', {'data': username + ' has entered the room ' + chatroom_id}, room=chatroom_id, broadcast=True, include_self=False)


@socketio.on('leave')
def on_leave(username, chatroom_id):
    leave_room(chatroom_id)
    print(username, chatroom_id)
    emit('leave', {'data': username + ' has leaved the room ' + chatroom_id}, room=chatroom_id, broadcast=True, include_self=False)


if __name__ == '__main__':
    socketio.run(app, host='0.0.0.0', port=8001)
