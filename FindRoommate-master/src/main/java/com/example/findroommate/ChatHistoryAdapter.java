package com.example.findroommate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ChatHistoryAdapter extends RecyclerView.Adapter<ChatHistoryAdapter.ViewHolder> {
    Context mContext;
    ArrayList<ChatHistory> chatHistories;
    private String username, otherUser;

    public ChatHistoryAdapter(Context context, ArrayList<ChatHistory> chatHistories) {
        mContext = context;
        this.chatHistories = chatHistories;
    }

    @NonNull

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        username = preferences.getString("UserName", null);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list, parent, false);
        return new ViewHolder(view);
    }


    public void onBindViewHolder(@NonNull final ChatHistoryAdapter.ViewHolder holder, final int position) {
        holder.linearLayout.setVisibility(View.VISIBLE);
        holder.linearLayout1.setVisibility(View.VISIBLE);
        holder.imageViewNext.setVisibility(View.VISIBLE);
        holder.textViewTime.setVisibility(View.VISIBLE);
        holder.textViewName.setVisibility(View.VISIBLE);
        String[] ids = chatHistories.get(position).occupant_id.split(",");
        if (ids[0].equals(username)) {
            otherUser = ids[1];
        } else if (ids[1].equals(username)) {
            otherUser = ids[0];
        }
        if (!chatHistories.get(position).last_message.equals("")) {
            holder.textViewLastMessage.setVisibility(View.VISIBLE);
            holder.textViewName.setText(otherUser);
            holder.textViewMessageUnread.setVisibility(View.GONE);
            holder.textViewTime.setText(convertDate(chatHistories.get(position).last_message_timeStamp,"hh:mm a"));
            holder.textViewLastMessage.setText(chatHistories.get(position).last_message);
        } else if (!chatHistories.get(position).unread_message.equals("")) {
            holder.textViewMessageUnread.setVisibility(View.VISIBLE);
            holder.textViewName.setText(otherUser);
            holder.textViewLastMessage.setVisibility(View.GONE);
            holder.textViewTime.setText(convertDate(chatHistories.get(position).last_message_timeStamp,"hh:mm a"));
            holder.textViewMessageUnread.setText(String.valueOf(chatHistories.get(position).unread_message.size()) + " new messages");
        } else {
            holder.linearLayout.setVisibility(View.GONE);
            holder.linearLayout1.setVisibility(View.GONE);
            holder.imageViewNext.setVisibility(View.GONE);
            holder.textViewMessageUnread.setVisibility(View.GONE);
            holder.textViewName.setVisibility(View.GONE);
            holder.textViewTime.setVisibility(View.GONE);
            holder.textViewLastMessage.setVisibility(View.GONE);
            holder.textViewMessageUnread.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(mContext, ChatActivity.class);
                String otherUserId=holder.textViewName.getText().toString();
                // intent1.putExtra("chat_users",chatHistories.get(position).occupant_id);
                //intent1.putExtra("chat_id",chatHistories.get(position).id);
                intent1.putExtra("user_id", username);
                ChatHistory chat_history = new ChatHistory();
                HashMap<String, String> senderUserData = new HashMap<>();
                senderUserData.put("userid", username);

                //new user logged in
                senderUserData.put("user_name", username);
                //senderUserData.put("user_image", mRideModel.getPic());

                HashMap<String, String> receivrUserData = new HashMap<>();
                receivrUserData.put("userid", otherUserId);


                //new other user data
                receivrUserData.put("user_name", otherUserId);
                //receivrUserData.put("user_image", mReceiverModel.getPic());

                ArrayList<HashMap<String, String>> userDataList = new ArrayList<>();
                userDataList.add(senderUserData);
                userDataList.add(receivrUserData);
                chat_history.setUser_data(userDataList);

                chat_history.setId(chatHistories.get(position).id);
                chat_history.setName("");
                chat_history.setType("");
                chat_history.setImage("");
                chat_history.setOccupant_id(username + "," + otherUserId);
                chat_history.setLast_message("");
                chat_history.setLast_message_timeStamp(String.valueOf(chatHistories.get(position).last_message_timeStamp));
                chat_history.setLast_message_user_id(username);
                intent1.putExtra("chatHistory", chat_history);
                chat_history.setCreateTime(String.valueOf(chatHistories.get(position).createTime));

                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(intent1);
            }
        });
    }


    @Override
    public int getItemCount() {
        return chatHistories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewLastMessage, textViewMessageUnread,textViewTime;
        LinearLayout linearLayout, linearLayout1;
        ImageView imageViewNext;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.linea_layout_chat_main);
            linearLayout1 = itemView.findViewById(R.id.layoutChat1);
            imageViewNext = itemView.findViewById(R.id.imagevIew_next);
            textViewName = itemView.findViewById(R.id.text_username);
            textViewLastMessage = itemView.findViewById(R.id.text_lastmessage);
            textViewTime=itemView.findViewById(R.id.textView_timeLastMessage);
            textViewMessageUnread = itemView.findViewById(R.id.text_lastmessageUnread);

        }
    }

    public static String convertDate(String dateInMilliseconds, String dateFormat) {
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }
}

