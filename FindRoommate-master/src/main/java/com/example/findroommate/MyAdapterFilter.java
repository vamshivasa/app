package com.example.findroommate;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapterFilter extends RecyclerView.Adapter<MyAdapterFilter.MyViewHolder1> {


    private Context mContext;
    private ArrayList<String> mFilters;

    public MyAdapterFilter(ArrayList<String> filters, Context context) {
        mContext = context;
        mFilters = filters;
    }

    @NonNull
    @Override
    public MyAdapterFilter.MyViewHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyAdapterFilter.MyViewHolder1(LayoutInflater.from(mContext).inflate(R.layout.filter_recycler_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder1 holder, int position) {
       holder.textViewFilter.setText(mFilters.get(position));
    }

    @Override
    public int getItemCount() {
        return mFilters.size();
    }

    class MyViewHolder1 extends RecyclerView.ViewHolder {
        TextView textViewFilter;


        private MyViewHolder1(@NonNull View itemView) {
            super(itemView);
            textViewFilter = itemView.findViewById(R.id.text_view_filter);
        }
    }
}
