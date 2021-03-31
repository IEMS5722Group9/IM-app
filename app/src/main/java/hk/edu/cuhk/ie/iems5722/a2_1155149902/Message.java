package hk.edu.cuhk.ie.iems5722.a2_1155149902;

public class Message {
    private String id;
    private String chatroom_id;
    private String user_id;
    private String name;
    private String message;
    private String message_time;

    public Message(String id, String chatroom_id, String user_id, String name, String message, String message_time) {
        this.id = id;
        this.chatroom_id = chatroom_id;
        this.user_id = user_id;
        this.name = name;
        this.message = message;
        this.message_time = message_time;
    }


    public String getMessage(){
        return message;
    }

    public String getTime(){
        return message_time;
    }

    public String getUserId(){
        return user_id;
    }

    public String getName(){
        return name;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public void setTime(String message_time){
        this.message_time = message_time;
    }

    public void setName(String name){
        this.name = name;
    }

}
