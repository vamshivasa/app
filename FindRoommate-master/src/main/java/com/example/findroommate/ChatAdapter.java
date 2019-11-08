package com.example.findroommate;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    List<Chat> mChatList;
    String mUserId;
    Context mContext;
    CustomClickListener mClickListener;

    public ChatAdapter(List<Chat> models, String userId, Context context, CustomClickListener clickListener) {
        mChatList = models;
        mUserId = userId;
        mContext = context;
        mClickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        if (position == 0) {
            holder.mLayoutChatStart.setVisibility(View.GONE);
            holder.mLayoutChatEnd.setVisibility(View.GONE);
            holder.mTextSupport.setVisibility(View.VISIBLE);
        } else {
            holder.mTextSupport.setVisibility(View.GONE);
            holder.mTextName.setText(mChatList.get(position).getAuthor());
            //Glide.with(mContext).load(mChatList.get(position).getUserImage()).placeholder(R.drawable.user).centerCrop().into(holder.mImageUser);
            holder.mTextMessage.setText(mChatList.get(position).getMessage());

            holder.mTextStart.setText(mChatList.get(position).getAuthor());
            //Glide.with(mContext).load(mChatList.get(position).getUserImage()).placeholder(R.drawable.user).centerCrop().into(holder.mImageUserStart);
            holder.mTextMessageStart.setText(mChatList.get(position).getMessage());

            if (mChatList.get(position).getSender_id().equals(mUserId)) {
                holder.mTextTimeSender.setText(convertDate(mChatList.get(position).getTimestamp(),"hh:mm a"));
                holder.mLayoutChatStart.setVisibility(View.GONE);
                holder.mLayoutChatEnd.setVisibility(View.VISIBLE);
            } else {
                holder.mtextTime.setText(convertDate(mChatList.get(position).getTimestamp(),"hh:mm a"));
                holder.mLayoutChatEnd.setVisibility(View.GONE);
                holder.mLayoutChatStart.setVisibility(View.VISIBLE);
            }


        }
    }


    @Override
    public int getItemCount() {
        return mChatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextName, mTextStart, mTextSupport;
        ImageView mImageUser, mImageUserStart;
        RelativeLayout mLayoutChatEnd, mLayoutChatStart;
        TextView mTextMessage, mTextMessageStart;
        TextView mtextTime,mTextTimeSender;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mtextTime=itemView.findViewById(R.id.text_time_chat_start);
            mTextName = itemView.findViewById(R.id.text_name_chat);
            mImageUser = itemView.findViewById(R.id.image_person);
            mTextMessage = itemView.findViewById(R.id.text_message_chat);
            mTextStart = itemView.findViewById(R.id.text_name_chat_start);
            mImageUserStart = itemView.findViewById(R.id.image_person_start);
            mTextMessageStart = itemView.findViewById(R.id.text_message_chat_start);
            mLayoutChatEnd = itemView.findViewById(R.id.layout_chat_end);
            mLayoutChatStart = itemView.findViewById(R.id.layout_chat_start);
            mTextSupport = itemView.findViewById(R.id.text_classy_support);
            mTextTimeSender=itemView.findViewById(R.id.text_time_chat);
        }
    }
    public static String convertDate(String dateInMilliseconds,String dateFormat) {
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }
}
