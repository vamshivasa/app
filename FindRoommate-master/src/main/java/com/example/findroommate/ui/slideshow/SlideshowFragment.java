package com.example.findroommate.ui.slideshow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findroommate.MyAdapter;
import com.example.findroommate.MyAdapter1;
import com.example.findroommate.Post;
import com.example.findroommate.R;
import com.example.findroommate.ui.AddPost.AddPostFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SlideshowFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private ArrayList<Post> list;
    private MyAdapter1 myAdapter;
    private LinearLayout linearLayout;
    private Button button_addPost;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        recyclerView=root.findViewById(R.id.myPosts_recyclerView);
        linearLayout=root.findViewById(R.id.linearLayout_no_post);
        button_addPost=root.findViewById(R.id.button_post_add);
        list = new ArrayList<Post>();
        button_addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                AddPostFragment fragment = new AddPostFragment();
                fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                fragmentTransaction.commit();

            }
        });
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String username=preferences.getString("UserName",null);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Post");
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //getting data from database
        databaseReference.orderByChild("postUser").equalTo(username).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if(dataSnapshot1.getChildrenCount()>0) {
                        linearLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        Post p = dataSnapshot1.getValue(Post.class);
                        list.add(p);
                    }
                }
                myAdapter = new MyAdapter1(getContext(), list);
                recyclerView.setAdapter(myAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return root;
    }
}