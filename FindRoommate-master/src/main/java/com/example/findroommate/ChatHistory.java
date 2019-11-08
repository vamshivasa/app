package com.example.findroommate;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatHistory implements Parcelable {
    String id,name,type,image,occupant_id,last_message,last_message_user_id,last_message_timeStamp,createTime,player_id,is_online;
    ArrayList<HashMap<String,String>> unread_message;
    ArrayList<HashMap<String,String>> user_data;


    public String getPlayer_id() {
        return player_id;
    }

    public void setPlayer_id(String player_id) {
        this.player_id = player_id;
    }


    public void setOnline(String online){
        is_online=online;
    }

    public String getOnline(){
        return is_online;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getOccupant_id() {
        return occupant_id;
    }

    public void setOccupant_id(String occupant_id) {
        this.occupant_id = occupant_id;
    }

    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }

    public String getLast_message_user_id() {
        return last_message_user_id;
    }

    public void setLast_message_user_id(String last_message_user_id) {
        this.last_message_user_id = last_message_user_id;
    }

    public String getLast_message_timeStamp() {
        return last_message_timeStamp;
    }

    public void setLast_message_timeStamp(String last_message_timeStamp) {
        this.last_message_timeStamp = last_message_timeStamp;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public ArrayList<HashMap<String, String>> getUnread_message() {
        return unread_message;
    }

    public void setUnread_message(ArrayList<HashMap<String, String>> unread_message) {
        this.unread_message = unread_message;
    }

    public ArrayList<HashMap<String, String>> getUser_data() {
        return user_data;
    }

    public void setUser_data(ArrayList<HashMap<String, String>> user_data) {
        this.user_data = user_data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(image);
        dest.writeString(occupant_id);
        dest.writeString(last_message);
        dest.writeString(last_message_user_id);
        dest.writeString(last_message_timeStamp);
        dest.writeString(createTime);
        dest.writeList(user_data);
        dest.writeList(unread_message);

    }
    public static Creator<ChatHistory> CREATOR = new Creator<ChatHistory>() {
        @Override
        public ChatHistory createFromParcel(Parcel source) {
            ChatHistory chat_history_new_detail = new ChatHistory();
            chat_history_new_detail.setId(source.readString());
            chat_history_new_detail.setName(source.readString());
            chat_history_new_detail.setType(source.readString());
            chat_history_new_detail.setImage(source.readString());
            chat_history_new_detail.setOccupant_id(source.readString());
            chat_history_new_detail.setLast_message(source.readString());
            chat_history_new_detail.setLast_message_user_id(source.readString());
            chat_history_new_detail.setLast_message_timeStamp(source.readString());
            chat_history_new_detail.setCreateTime(source.readString());
            chat_history_new_detail.setUser_data(source.readArrayList(ChatHistory.class.getClassLoader()));
            chat_history_new_detail.setUnread_message(source.readArrayList(ChatHistory.class.getClassLoader()));
            return chat_history_new_detail;
        }

        @Override
        public ChatHistory[] newArray(int size) {
            return new ChatHistory[0];
        }
    };
}