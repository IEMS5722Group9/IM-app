# Instant Messaging Application

IEMS5722 Group9

## Introduction

- Basic features:  register, login, logout, private chat, add friend via user ID and user avatar
- Advanced feature: add friend via QR code 
- Home page: Chat, Friends and Me
- Server APIs: 

baseURL: http://18.219.150.95/api/a3

GET /get_chatrooms

POST /add_chatroom

POST /register_user

POST /get_user

GET /get_friends 

POST /search_friend 

POST /add_friend

GET /get_avatar

POST /post_avatar

## Environment

- Target SDK: 30
- Tested on Android 11, Sumsung
- Import yzing-lib 
- Import Base64Coder

## Project Structure

### **client**

  \- app #main application

​	  - src/main

​		  - /java/hk/edu/cuhk/ie/iems5722/a2_1155149902

​			  - **activity**

- AddFriendsActivity	#add friend via user id and via scan QR code
- ChatActivity		#chat page
- LoginActivity		#log in
- MainActivity		#includes 3 fragments
- SignupActivity		#register

​			  - **adapter**

- RoomAdapter
- MessageAdapter
- FriendAdapter

​			  - **fragment**

- ChatFragment
- FriendsFragment
- MeFragment

​			  - **model**

- RoomModel
- MessageModel
- FriendModel

​			  - **util**	# handle Image, MD5, Http, ...

​		  - res

- drawable	#icon, background...
- layout
- menu          #toolbar
- naviagation	# bottom navigation
- values
-  ...

  \- base64coder	# handling avatars

  \- yzing-lib 	# handling QR code

## server

- assn3.py		# APIs
- assn4.py		# socket

## Features

**Register & Login** 

- MD5: encode the password

- Error handling: wrong password, 6-15 characters length, same username, user do not exist

**Chat**

- chat list: chatrooms ordered by time, show newest message of each room

- private chat

- group chat

- view history messages

**Friends**

- friends list: click and start a private chat

- add friend via user id

- add friend via QR code
     - scan QR code
     - choose picture from gallery

**Me**

- upload user avatar (compress the image)

- generate QR code

- save to the gallery

- log out

## Contributing

- YIN Zijing 1155149902

- ZHENG Qingdan 1155154714
