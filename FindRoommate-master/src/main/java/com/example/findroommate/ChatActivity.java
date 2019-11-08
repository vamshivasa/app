package com.example.findroommate;


import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.ResponseBody;

public class ChatActivity extends AppCompatActivity {
    TextView mTextHeader;
    ImageView mImageBack;
    DatabaseReference mFirebaseRefMessage, mFirebaseRefHistory;
    List<Chat> mChatList = new ArrayList<>();
    RecyclerView mChatRecycler;
    ChatAdapter mChatAdapter;
    String chat_id;
    ChatHistory mChatHistory;
    EditText mEditMessage;
    ImageView mImageSend;
    String user_id;
    String otherUserId;
    String mToken;
    TextView textViewOtherUserName;
    String userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mChatHistory = getIntent().getParcelableExtra("chatHistory");
        String[] ids = null;
        ids = mChatHistory.getOccupant_id().split(",");
        chat_id = mChatHistory.getId();
        user_id = getIntent().getStringExtra("user_id");
        if (ids[0].equals(user_id)) {
            otherUserId = ids[1];
        } else if (ids[1].equals(user_id)) {
            otherUserId = ids[0];
        }
        setTitle(otherUserId);
        setContentView(R.layout.activity_chat);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        }
        //textViewOtherUserName=findViewById(R.id.other_user_text_name);
        mChatRecycler = findViewById(R.id.chat_recyler);

        mEditMessage = findViewById(R.id.edit_chat);
        mImageSend = findViewById(R.id.image_send);


        //  mTextHeader.setText(getIntent().getStringExtra("name_header"));
        mFirebaseRefMessage = FirebaseDatabase.getInstance().getReference().child("Messages").child(chat_id);
        mFirebaseRefHistory = FirebaseDatabase.getInstance().getReference().child("ChatHistory").child(chat_id);
        setClicks();
        setAdapter();
        sendData();
    }

    void sendData() {
        mImageSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mEditMessage.getText().toString())) {
                    Toast.makeText(ChatActivity.this, "Type Message", Toast.LENGTH_SHORT).show();
                } else {
                    sendMessage(mEditMessage.getText().toString(), user_id, otherUserId);
                }
            }
        });
    }

    void setAdapter() {
        mChatAdapter = new ChatAdapter(mChatList, user_id, this, null);
        mChatRecycler.setLayoutManager(new LinearLayoutManager(this));
        mChatRecycler.setAdapter(mChatAdapter);
    }

    private void setClicks() {

    }


    @Override
    protected void onStart() {
        super.onStart();
        getData();
    }

    void getData() {
        mFirebaseRefMessage.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("MessageData", dataSnapshot.toString());
                mChatList.clear();
                Chat chatModel = new Chat();
                mChatList.add(0, chatModel);
                for (DataSnapshot msgSnapshot : dataSnapshot.getChildren()) {
                    try {
                        Chat chatMessageModel = msgSnapshot.getValue(Chat.class);
                        mChatList.add(chatMessageModel);
                        mChatRecycler.smoothScrollToPosition(mChatAdapter.getItemCount());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
//                chatMsgList.sort(chatMessageModel.getTime().);
                mChatAdapter.notifyDataSetChanged();
                mChatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onItemRangeInserted(int positionStart, int itemCount) {
                        mChatRecycler.smoothScrollToPosition(itemCount);
                        mChatRecycler.scrollTo(0, mChatAdapter.getItemCount());
                    }
                });
                if (!mChatList.isEmpty()) {
                    mChatRecycler.scrollTo(0, mChatAdapter.getItemCount());
                    mChatAdapter.notifyItemInserted(mChatAdapter.getItemCount());
                    mChatRecycler.smoothScrollToPosition(mChatAdapter.getItemCount());
//                    recyclerChatScreen.smoothScrollToPosition(chatMsgList.size() - 1);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("MessageData", databaseError.toString());
            }
        });
    }

    private void sendMessage(final String input, final String author, final String other_userId) {
        if (!input.equals("")) {

            DatabaseReference mFirebaseRefHistory_ = FirebaseDatabase.getInstance().getReference().child("ChatHistory").child(chat_id);
            mFirebaseRefHistory_.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        ChatHistory chat_historyObj = dataSnapshot.getValue(ChatHistory.class);

                        ArrayList<HashMap<String, String>> unreadmessageList_value = chat_historyObj.getUnread_message();

                        String unread_message_value = "0";
                        if (unreadmessageList_value.size() == 2) {
                            System.out.println("value aa ");
                            if (!unreadmessageList_value.get(0).get("userid").equals(user_id))//
                            {
                                unread_message_value = unreadmessageList_value.get(0).get("unread_message_count");
                                System.out.println("value a " + unreadmessageList_value.get(0).get("userid")
                                        + " , " + unreadmessageList_value.get(0).get("unread_message_count")
                                        + " , " + unread_message_value);
                            } else if (!unreadmessageList_value.get(1).get("userid").equals(user_id)) {
                                unread_message_value = unreadmessageList_value.get(1).get("unread_message_count");
                                System.out.println("value b " + unreadmessageList_value.get(1).get("userid")
                                        + " , " + unreadmessageList_value.get(1).get("unread_message_count")
                                        + " , " + unread_message_value);
                            }


                            //send msg now


                            long createTime = new Date().getTime();
                            ;
                            ChatHistory chat_history = new ChatHistory();
                            chat_history.setId(chat_id);
                            chat_history.setName("");
                            chat_history.setImage("");
                            chat_history.setOccupant_id(author + "," + other_userId);
                            chat_history.setLast_message(input);


                            chat_history.setLast_message_timeStamp(String.valueOf(createTime));

                            chat_history.setLast_message_user_id(author);
                            chat_history.setCreateTime(String.valueOf(createTime));

                            HashMap<String, String> receiverUnreadMessage = new HashMap<>();
                            receiverUnreadMessage.put("userid", other_userId);

                            int unreadMsg = Integer.parseInt(unread_message_value) + 1;
                            unread_message_value = String.valueOf(unreadMsg);
                            receiverUnreadMessage.put("unread_message_count", unread_message_value);
                            System.out.println("value unread in chat activity " + unreadMsg + " , " + unread_message_value);

                            HashMap<String, String> senderUnreadMessage = new HashMap<>();
                            senderUnreadMessage.put("userid", author);
                            senderUnreadMessage.put("unread_message_count", "0");
                            ArrayList<HashMap<String, String>> unreadmessageList = new ArrayList<>();
                            unreadmessageList.add(senderUnreadMessage);
                            unreadmessageList.add(receiverUnreadMessage);
                            chat_history.setUnread_message(unreadmessageList);


                            HashMap<String, String> senderUserData = new HashMap<>();
                            senderUserData.put("userid", author);


                            HashMap<String, String> receivrUserData = new HashMap<>();
                            receivrUserData.put("userid", other_userId);
                            String authorName = "", authorImage = "";
                            String OtherUserName = "", OtherUserImage = "";
                            for (int i = 0; i < mChatHistory.getUser_data().size(); i++) {
                                HashMap<String, String> userData = mChatHistory.getUser_data().get(i);
                                if ((userData.get("userid").equalsIgnoreCase(user_id))) {
                                    authorName = userData.get("user_name");
                                    authorImage = userData.get("user_image");
                                    senderUserData.put("user_name", userData.get("user_name"));
                                    senderUserData.put("user_image", userData.get("user_image"));
                                } else {
                                    OtherUserName = userData.get("user_name");
                                    OtherUserImage = userData.get("user_image");
                                    receivrUserData.put("user_name", userData.get("user_name"));
                                    receivrUserData.put("user_image", userData.get("user_image"));
                                }
                            }


                            //new other user data

                            // receivrUserData.put("player_id",player_id);

                            ArrayList<HashMap<String, String>> userDataList = new ArrayList<>();
                            userDataList.add(senderUserData);
                            userDataList.add(receivrUserData);
                            chat_history.setUser_data(userDataList);

                            mFirebaseRefHistory.setValue(chat_history);

                            Chat chat = new Chat(input, authorName, user_id, String.valueOf(createTime), "text", author, other_userId, authorImage, OtherUserImage, OtherUserName);
                            //         sendNotificationToPartner(user_id,other_userId,input,chat_id);

                            // Create a new, auto-generated child of that chat location, and save our chat data there
                            mFirebaseRefMessage.push().setValue(chat);
                            setResult(Activity.RESULT_OK);


                        }
                    }


                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {
                    System.out.println("datasnap shot online chat history onCancelled ");
                }
            });
            mEditMessage.setText("");
        }
    }

    private String createChatId(int first_id, int secound_id) {
        int max_id = Math.max(first_id, secound_id);
        int min_id = Math.min(first_id, secound_id);
        return max_id + "" + min_id;

    }


}
