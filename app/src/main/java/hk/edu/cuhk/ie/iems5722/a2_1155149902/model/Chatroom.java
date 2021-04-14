package hk.edu.cuhk.ie.iems5722.a2_1155149902.model;

public class Chatroom {
    public String room_id;
    public String room_name;
    public String room_type;
    public Message newest_message=new Message();

    public Chatroom(String id, String name) {
        this.room_id = id;
        this.room_name = name;
    }

    public void setType(String type) {
        this.room_type = type;
    }

    public void setMessage(String name, String message, String time) {
        this.newest_message.setName(name);
        this.newest_message.setMessage(message);
        this.newest_message.setTime(time);
    }
}
