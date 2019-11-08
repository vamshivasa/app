package com.example.findroommate;

class RequestNotificaton {

   // @SerializedName("to") //  "to" changed to token
    private String to;

    //@SerializedName("notification")
    private SendNotificationModel notification;


    //@SerializedName("data")
    private SendNotificationModel data;


    public void setData(SendNotificationModel data) {
        this.data = data;
    }

    public SendNotificationModel getData() {
        return data;
    }

    //@SerializedName("chat_id")
    private String chat_id;

    //@SerializedName("user_id")
    private String user_id;
    
    private String OtherUserId;

    public SendNotificationModel getSendNotificationModel() {
        return notification;
    }

    public void setSendNotificationModel(SendNotificationModel sendNotificationModel) {
        this.notification = sendNotificationModel;
    }

    public String getToken() {
        return to;
    }

    public void setToken(String token) {
        this.to = token;
    }

    public void setUserId(String userId) {
        this.user_id = userId;
    }

    public void setChatId(String chatId) {
        this.chat_id = chatId;
    }

    public void setOtherUserId(String otherUserId) {
        OtherUserId = otherUserId;
    }

    public String getUserId() {
        return user_id;
    }

    public String getChatId() {
        return chat_id;
    }

    public String getOtherUserId() {
        return OtherUserId;
    }
}