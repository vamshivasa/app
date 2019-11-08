package com.example.findroommate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import static java.sql.Types.TIMESTAMP;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> implements Filterable {
    private Context mContext;
    private ArrayList<Post> mposts;
    private ArrayList<Post> mpostsFull;
    private ArrayList<Uri> mUri;


    public MyAdapter(Context context, ArrayList<Post> posts) {
        mContext = context;
        mposts = posts;
        mpostsFull = new ArrayList<Post>(mposts);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.cardview_homefragment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.textViewTitle.setText(mposts.get(position).getTitle());
        holder.textViewDescription.setText(mposts.get(position).getDescription());
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailActivity.class);
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

    @Override
    public Filter getFilter() {
        return postFilter;
    }

    private Filter postFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Post> filteredPosts = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredPosts.addAll(mpostsFull);
            } else {
                String filteredPattern = constraint.toString().toLowerCase().trim();
                for (Post post : mpostsFull) {
                    if (post.getTitle().toLowerCase().contains(filteredPattern) || post.getDescription().toLowerCase().contains(filteredPattern)) {
                        filteredPosts.add(post);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredPosts;
            return filterResults;
        }

        protected FilterResults filtersAll(String filterName) {

            FilterResults filterResults = new FilterResults();
            // filterResults.values = filteredPosts;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mposts.clear();
            mposts.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewDescription, textViewRent, textViewPostedDate;
        ImageView imageViewImages;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textView_title);
            textViewDescription = itemView.findViewById(R.id.textView_description);
            textViewRent = itemView.findViewById(R.id.textView_rent);
            textViewPostedDate = itemView.findViewById(R.id.textView_postedDate);
            imageViewImages = itemView.findViewById(R.id.images_search);
        }
    }


}
