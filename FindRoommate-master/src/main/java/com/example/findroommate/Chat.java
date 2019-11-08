package com.example.findroommate;

public class Chat {
    private String message;
    private String author;
    private String id;
    public String type;
    private String sender_id;
    private String recepient_id;
    private String timestamp, userImage, timeDuration;
    public boolean isSame = false;
    private String otherUserName, otherUserImage;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    public Chat() {
    }

    public Chat(String message, String author, String id, String timestamp, String type, String sender_id, String recepient_id, String userImage, String otherUserImage, String otherUserName) {
        this.message = message;
        this.author = author;
        this.id = id;
        this.type = type;
        this.sender_id = sender_id;
        this.recepient_id = recepient_id;
        this.timestamp = timestamp;
        this.userImage = userImage;
        this.otherUserImage = otherUserName;
        this.otherUserImage = otherUserImage;

    }

    public String getTimeDuration() {
        return timeDuration;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getSender_id() {
        return sender_id;
    }

    public String getRecepient_id() {
        return recepient_id;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String thumbnail) {
        this.userImage = thumbnail;
    }


    public String getOtherUserImage() {
        return otherUserImage;
    }

    public void setOtherUserImage(String thumbnail) {
        this.otherUserImage = thumbnail;
    }

    public String getOtherUserName() {
        return otherUserName;
    }

    public void setOtherUserName(String thumbnail) {
        this.otherUserName = thumbnail;
    }
}
