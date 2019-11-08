package com.example.findroommate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MyAdapter1 extends RecyclerView.Adapter<MyAdapter1.MyViewHolder1> {


    private Context mContext;
    private ArrayList<Post> mposts;

    public MyAdapter1(Context context, ArrayList<Post> posts) {
        mContext = context;
        mposts = posts;
    }

    @NonNull
    @Override
    public MyAdapter1.MyViewHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyAdapter1.MyViewHolder1(LayoutInflater.from(mContext).inflate(R.layout.cardview_homefragment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter1.MyViewHolder1 holder, final int position) {
        holder.textViewTitle.setText(mposts.get(position).getTitle());
        holder.textViewDescription.setText(mposts.get(position).getDescription());
        //holder.textViewPostedDate.setText(mposts.get(position).getPostDate());
        String timeAgo = TimeAgo.getTimeAgo(Long.valueOf(mposts.get(position).getPostDate()));
        holder.textViewPostedDate.setText(timeAgo);
        if (!String.valueOf(mposts.get(position).getPrice()).equals("0")) {
            holder.textViewRent.setVisibility(View.VISIBLE);
            holder.textViewRent.setText(String.valueOf(mposts.get(position).getPrice()));
        } else {
            holder.textViewRent.setVisibility(View.GONE);
        }
        if (!mposts.get(position).getImageUri().equals("")) {
            Glide.with(mContext).load(mposts.get(position).getImageUri()).into(holder.imageViewImages);
        } else {
            Glide.with(mContext).load(R.drawable.noimage).into(holder.imageViewImages);
        }

        //holder.textViewTitle.setText(posts.get(position).getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailActivity1.class);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("postId", String.valueOf(mposts.get(position).getPostId()));
                editor.putString("postUser", mposts.get(position).getPostUser());
                editor.apply();
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mposts.size();
    }

    class MyViewHolder1 extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewDescription, textViewRent, textViewPostedDate;
        ImageView imageViewImages;

        private MyViewHolder1(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textView_title);
            textViewDescription = itemView.findViewById(R.id.textView_description);
            textViewRent = itemView.findViewById(R.id.textView_rent);
            textViewPostedDate = itemView.findViewById(R.id.textView_postedDate);
            imageViewImages = itemView.findViewById(R.id.images_search);
        }
    }
}