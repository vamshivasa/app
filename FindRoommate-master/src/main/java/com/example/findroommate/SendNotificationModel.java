package com.example.findroommate;

public class SendNotificationModel {
    private String message,title,noti_type,user_id;

    public SendNotificationModel(String message, String title,String noti_type,String user_id) {
        this.message = message;
        this.title = title;
        this.noti_type=noti_type;
        this.user_id=user_id;
    }

    public String getBody() {
        return message;
    }

    public void setBody(String body) {
        this.message = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNoti_type() {
        return noti_type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setNoti_type(String noti_type) {
        this.noti_type = noti_type;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
