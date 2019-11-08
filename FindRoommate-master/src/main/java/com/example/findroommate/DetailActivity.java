package com.example.findroommate;

import android.animation.ArgbEvaluator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class DetailActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private Adapter adapter;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference, databaseReference1, databaseReferenceChat, databaseReferenceFilter;
    private Integer[] colors = null;
    private TextView textViewTitle, textViewDescription, textViewLocation, textViewPrice;
    private List<ImagesPost> imagesPostList;
    private Button buttonContactUser, buttonCallUser;
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    String phone, postUser, username, postId;
    Long mFilterId;
    private ArrayList<String> fList;

    private MyAdapterFilter myAdapter;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        }
        fList = new ArrayList<String>();
        imagesPostList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView_filter);
        textViewTitle = findViewById(R.id.detail_title);
        textViewDescription = findViewById(R.id.detail_description);
        textViewLocation = findViewById(R.id.detail_address);
        textViewPrice = findViewById(R.id.detail_price);
        //textViewId = findViewById(R.id.detail_postId);
        buttonContactUser = findViewById(R.id.detail_contactUser);
        buttonCallUser = findViewById(R.id.detail_callUser);
        //CallSwitch = findViewById(R.id.simpleSwitch);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        username = preferences.getString("UserName", null);
        postId = preferences.getString("postId", null);
        postUser = preferences.getString("postUser", null);


        if (postUser != null && postId != null) {
            databaseReference1 = FirebaseDatabase.getInstance().getReference().
                    child("postImages").child(Objects.requireNonNull(postUser)).
                    child(String.valueOf(postId));
            databaseReference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ImagesPost imagesPost = snapshot.getValue(ImagesPost.class);
                        imagesPostList.add(imagesPost);
                    }
                    adapter = new Adapter(imagesPostList, getApplicationContext());
                    viewPager.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        if (postId != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference().
                    child("Post").child(Objects.requireNonNull(postId));
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("postUser").exists()) {
                        postUser = dataSnapshot.child("postUser").getValue().toString();
                    }
                    if (dataSnapshot.child("title").exists()) {
                        textViewTitle.setText(dataSnapshot.child("title").getValue().toString());
                    }
                    if (dataSnapshot.child("description").exists()) {
                        textViewDescription.setText(dataSnapshot.child("description").getValue().toString());
                    }
                    if (dataSnapshot.child("location").exists()) {
                        if (!dataSnapshot.child("location").getValue().toString().equals("Add Location")) {
                            textViewLocation.setVisibility(View.VISIBLE);
                            textViewLocation.setText(dataSnapshot.child("location").getValue().toString());
                        } else {
                            textViewLocation.setVisibility(View.GONE);
                        }
                    }
                    if (dataSnapshot.child("price").exists()) {
                        if (!dataSnapshot.child("price").getValue().toString().isEmpty()) {
                            textViewPrice.setVisibility(View.VISIBLE);
                            textViewPrice.setText(dataSnapshot.child("price").getValue().toString());
                        } else {
                            textViewPrice.setVisibility(View.GONE);
                        }
                    }
                    if (dataSnapshot.child("phoneNumber").exists()) {
                        if (dataSnapshot.child("phoneNumber").getValue() != null) {
                            if (!postUser.equals(username)) {
                                phone = String.valueOf(dataSnapshot.child("phoneNumber").getValue());
                                if (phone.length() != 0) {
                                    setBUttonVisiBility();

                                } else {
                                    buttonCallUser.setVisibility(View.GONE);
                                }
                            }else
                            {
                                buttonCallUser.setVisibility(View.GONE);
                            }
                        }
                    }
                    if (dataSnapshot.child("filterId").exists()) {
                        mFilterId = Long.parseLong(String.valueOf(dataSnapshot.child("filterId").getValue()));
                    }
                    Query queryFilter = FirebaseDatabase.getInstance().getReference().child("Filters").orderByChild("filterId").equalTo(mFilterId);
                    queryFilter.
                            addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        if (Objects.equals(snapshot.child("furnished").getValue(), true)) {
                                            fList.add("Furnished");
                                        }
                                        if (Objects.equals(snapshot.child("noSmoking").getValue(), true)) {
                                            fList.add("No Smoking");
                                        }
                                        if (Objects.equals(snapshot.child("noDrinking").getValue(), true)) {
                                            fList.add("No Drinking");
                                        }
                                        if (Objects.equals(snapshot.child("noPets").getValue(), true)) {
                                            fList.add("No Pets");
                                        }
                                        if (Objects.equals(snapshot.child("male").getValue(), true)) {
                                            fList.add("For Male");
                                        }
                                        if (Objects.equals(snapshot.child("female").getValue(), true)) {
                                            fList.add("For Female");
                                        }
                                    }
                                    if (fList.size() > 0) {
                                        myAdapter = new MyAdapterFilter(fList, getApplicationContext());
                                        recyclerView.setVisibility(View.VISIBLE);
                                        recyclerView.setAdapter(myAdapter);
                                    } else {
                                        recyclerView.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        //imagesPosts = new ArrayList<ImagesPost>();
        Integer[] colors_temp = {getResources().getColor(R.color.color1),
                getResources().getColor(R.color.color2)};
        colors = colors_temp;

        viewPager = findViewById(R.id.viewPager);
        //viewPager.setAdapter(adapter);
        viewPager.setPadding(130, 0, 130, 0);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position < (adapter.getCount() - 1) && position < (colors.length - 1)) {
                    viewPager.setBackgroundColor(
                            (Integer) argbEvaluator.evaluate(
                                    positionOffset,
                                    colors[position],
                                    colors[position + 1]
                            )
                    );
                } else {
                    viewPager.setBackgroundColor(colors[colors.length - 1]);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        buttonCallUser.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (phone != null) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                    startActivity(intent);

                }
            }
        });
        buttonContactUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String chat_id = "";
                int compare = username.compareTo(postUser);

                if (compare < 0) {
                    chat_id = username + postUser;
                } else {
                    chat_id = postUser + username;
                }

                databaseReferenceChat = FirebaseDatabase.getInstance().getReference().child("ChatHistory").child(chat_id);
                String unread_message_value = "0";

                long createTime = System.currentTimeMillis() / 1000;

                ChatHistory chat_history = new ChatHistory();
                chat_history.setId(chat_id);
                chat_history.setName("");
                chat_history.setType("");
                chat_history.setImage("");
                chat_history.setOccupant_id(username + "," + postUser);
                chat_history.setLast_message("");
                chat_history.setLast_message_timeStamp(String.valueOf(createTime));
                chat_history.setLast_message_user_id(username);
                chat_history.setCreateTime(String.valueOf(createTime));

                HashMap<String, String> receiverUnreadMessage = new HashMap<>();
                receiverUnreadMessage.put("userid", postUser);

                receiverUnreadMessage.put("unread_message_count", unread_message_value);
                System.out.println("value unread in chat activity " + "Unread" + " , " + unread_message_value);

                HashMap<String, String> senderUnreadMessage = new HashMap<>();
                senderUnreadMessage.put("userid", username);
                senderUnreadMessage.put("unread_message_count", "0");
                ArrayList<HashMap<String, String>> unreadmessageList = new ArrayList<>();
                unreadmessageList.add(senderUnreadMessage);
                unreadmessageList.add(receiverUnreadMessage);
                chat_history.setUnread_message(unreadmessageList);

                HashMap<String, String> senderUserData = new HashMap<>();
                senderUserData.put("userid", username);

                //new user logged in
                senderUserData.put("user_name", username);
                //senderUserData.put("user_image", mRideModel.getPic());

                HashMap<String, String> receivrUserData = new HashMap<>();
                receivrUserData.put("userid", postUser);


                //new other user data
                receivrUserData.put("user_name", postUser);
                //receivrUserData.put("user_image", mReceiverModel.getPic());

                ArrayList<HashMap<String, String>> userDataList = new ArrayList<>();
                userDataList.add(senderUserData);
                userDataList.add(receivrUserData);
                chat_history.setUser_data(userDataList);

                databaseReferenceChat.setValue(chat_history);
                setResult(Activity.RESULT_OK);
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("chatHistory", chat_history);
                intent.putExtra("chat_id", chat_id);
                intent.putExtra("user_id", username);
                intent.putExtra("name_header", "Chats");
                startActivity(intent);

            }
        });

    }

    private void setBUttonVisiBility() {
        buttonCallUser.setClickable(true);
        buttonCallUser.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.rounded_button));
    }

}
