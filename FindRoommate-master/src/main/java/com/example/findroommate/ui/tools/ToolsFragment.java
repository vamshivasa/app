package com.example.findroommate.ui.tools;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findroommate.Chat;
import com.example.findroommate.ChatHistory;
import com.example.findroommate.ChatHistoryAdapter;
import com.example.findroommate.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ToolsFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference databaseReferenceChatHistory;
    private String username, chatNode;
    private ArrayList<ChatHistory> chatHistories;
    private ChatHistoryAdapter chatHistoryAdapter;
    private long totalChats;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        username = preferences.getString("UserName", null);
        chatHistories = new ArrayList<>();
        databaseReferenceChatHistory = FirebaseDatabase.getInstance().getReference()
                .child("ChatHistory");
        chatHistories.clear();
        databaseReferenceChatHistory.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey() != null) {
                        chatNode = snapshot.getKey();
                        if (chatNode.contains(username)) {
                            ChatHistory chatHistory = snapshot.getValue(ChatHistory.class);
                            chatHistories.add(chatHistory);

                        }
                    }

                }
                chatHistoryAdapter = new ChatHistoryAdapter(getContext(), chatHistories);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                chatHistoryAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(chatHistoryAdapter);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        View root = inflater.inflate(R.layout.fragment_tools, container, false);
        recyclerView = root.findViewById(R.id.recycler_View_chat_list);
        return root;
    }

}