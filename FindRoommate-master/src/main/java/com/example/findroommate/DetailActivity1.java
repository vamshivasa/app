package com.example.findroommate;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.findroommate.ui.slideshow.SlideshowFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DetailActivity1 extends AppCompatActivity {
    private ViewPager viewPager;
    private Adapter adapter;
    private SharedPreferences preferences;
    private DatabaseReference databaseReference, databaseReference1;
    private Integer[] colors = null;
    private TextView textViewTitle, textViewDescription, textViewLocation, textViewPrice, textViewId;
    private ArrayList<Objects> list;
    private Button buttonContactUser,buttonCallUser;
    private String mUsername, mpostId,mpostUser;
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    private ArrayList<String> fList;
    Long mFilterId;
    private List<ImagesPost> imagesPostList;
    private MyAdapterFilter myAdapter;
    private RecyclerView recyclerView;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        }
        fList=new ArrayList<>();
        textViewTitle = findViewById(R.id.detail_title);
        textViewDescription = findViewById(R.id.detail_description);
        textViewLocation = findViewById(R.id.detail_address);
        recyclerView = findViewById(R.id.recyclerView_filter);
        textViewPrice = findViewById(R.id.detail_price);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
       // textViewId = findViewById(R.id.detail_postId);
        buttonContactUser = findViewById(R.id.detail_contactUser);
        buttonCallUser = findViewById(R.id.detail_callUser);
        buttonCallUser.setVisibility(View.GONE);
        final Intent intent = getIntent();
        imagesPostList=new ArrayList<>();
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mUsername = preferences.getString("UserName", null);
        mpostId=preferences.getString("postId",null);
        mpostUser=preferences.getString("postUser",null);
        if (mpostUser.equals(mUsername)) {
            buttonContactUser.setText("Edit Post");
        }
        if (mUsername != null &&  mpostId!= null) {
            databaseReference1 = FirebaseDatabase.getInstance().getReference().
                    child("postImages").child(Objects.requireNonNull(mUsername)).
                    child(String.valueOf(mpostId));
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

        if (mpostId != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference().
                    child("Post").child(Objects.requireNonNull(mpostId));
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("postUser").exists()) {
                        mUsername = dataSnapshot.child("postUser").getValue().toString();
                    }
                    if (dataSnapshot.child("title").exists()) {
                        textViewTitle.setText(dataSnapshot.child("title").getValue().toString());
                    }
                    if (dataSnapshot.child("description").exists()) {
                        textViewDescription.setText(dataSnapshot.child("description").getValue().toString());
                    }
                    if (dataSnapshot.child("location").exists()) {
                        if (!dataSnapshot.child("location").getValue().toString().equals("")) {
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

                    if (dataSnapshot.child("filterId").exists()) {
                        mFilterId = Long.parseLong(String.valueOf(dataSnapshot.child("filterId").getValue()));
                    }
                    Query queryFilter=FirebaseDatabase.getInstance().getReference().child("Filters").orderByChild("filterId").equalTo(mFilterId);
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
        viewPager.setAdapter(adapter);
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
        buttonContactUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), EditPost.class);
                i.putExtra("postId", mpostId);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

    }

}

