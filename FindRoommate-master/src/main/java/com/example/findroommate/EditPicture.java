package com.example.findroommate;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dmax.dialog.SpotsDialog;

public class EditPicture extends AppCompatActivity {

    ImageView imageButton2, imageButton3, imageButton4, imageButton5, imageButton6, imageButton7,
            imageButton8, imageButton9;
    ImageView imageButton1;
    public static final int IMAGE_PICK_CODE = 1000;
    public static final int IMAGE_PICK_CODE1 = 10001;
    public static final int IMAGE_PICK_CODE2 = 10002;
    public static final int IMAGE_PICK_CODE3 = 10003;
    public static final int IMAGE_PICK_CODE4 = 10004;
    public static final int IMAGE_PICK_CODE5 = 10005;
    public static final int IMAGE_PICK_CODE6 = 10006;
    public static final int IMAGE_PICK_CODE7 = 10007;
    public static final int IMAGE_PICK_CODE8 = 10008;
    //  public static final int IMAGE_PICK_CODE9 = 1009;
    String stringPostId,stringUsername;
    Uri imageUri,imageUri1;
    Button images_doneButton;
    DatabaseReference databaseReference1;
    private List<ImagesPost> imagesPostList;
    ArrayList<Uri> imagesFromURL = new ArrayList<Uri>();
    private SpotsDialog progressDailog;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        final String[] mListItems = getResources().getStringArray(R.array.pictureLoad);
        init();
        Intent intent=getIntent();
        imagesPostList=new ArrayList<>();
        progressDailog=new SpotsDialog(getApplicationContext(),R.style.Custom);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        stringUsername = preferences.getString("UserName", null);
        stringPostId = preferences.getString("postId", null);
        if (stringUsername != null && stringPostId != null) {
            //progressDailog.show();
            databaseReference1 = FirebaseDatabase.getInstance().getReference().
                    child("postImages").child(Objects.requireNonNull(stringUsername)).
                    child(String.valueOf(stringPostId));
            databaseReference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        ImagesPost imagesPost = snapshot.getValue(ImagesPost.class);
                        imagesPostList.add(imagesPost);
                    }
                    ImageView[] ButtonIds ={imageButton1,imageButton2,imageButton3,imageButton4,imageButton5,imageButton6,imageButton7,imageButton8,imageButton9};
                    for (int listImages = 0; listImages < imagesPostList.size(); listImages++) {
                        for (int i = 0; i < imagesPostList.size(); i++) {
                            if (imagesPostList.get(listImages) != null) {
                                Glide.with(getApplicationContext()).load(listImages).into(ButtonIds[i]);
                            }

                            //      progressDailog.dismiss();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE_PICK_CODE);
            }
        });
        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE_PICK_CODE1);
            }
        });
        imageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE_PICK_CODE2);
            }
        });
        imageButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE_PICK_CODE3);
            }
        });
        imageButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE_PICK_CODE4);
            }
        });
        imageButton6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE_PICK_CODE5);
            }
        });
        imageButton7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE_PICK_CODE6);
            }
        });
        imageButton8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE_PICK_CODE7);
            }
        });
        imageButton9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE_PICK_CODE8);
            }
        });

        images_doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();

                resultIntent.putExtra("image1", String.valueOf(imageUri1));
                resultIntent.putParcelableArrayListExtra("list",imagesFromURL);

                // TODO Add extras or a data URI to this intent as appropriate
                setResult(RESULT_OK,resultIntent);
                finish();
            }
        });
    }

    private void init() {
        imageButton1 = findViewById(R.id.imageView1);
        imageButton2 = findViewById(R.id.imageView2);
        imageButton3 = findViewById(R.id.imageView3);
        imageButton4 = findViewById(R.id.imageView4);
        imageButton5 = findViewById(R.id.imageView5);
        imageButton6 = findViewById(R.id.imageView6);
        imageButton7 = findViewById(R.id.imageView7);
        imageButton8 = findViewById(R.id.imageView8);
        imageButton9 = findViewById(R.id.imageView9);
        //imageButton10 = findViewById(R.id.imageView10);
        images_doneButton = findViewById(R.id.buttonImagesDone);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            assert data != null;
            imageUri = data.getData();
            if (requestCode == IMAGE_PICK_CODE) {
                Glide.with(this).load(imageUri).into(imageButton1);
                imageUri1=imageUri;
                imagesFromURL.add(imageUri);
            } else if (requestCode == IMAGE_PICK_CODE1) {
                Glide.with(this).load(imageUri).into(imageButton2);
                imagesFromURL.add(imageUri);
            } else if (requestCode == IMAGE_PICK_CODE2) {
                Glide.with(this).load(imageUri).into(imageButton3);
                imagesFromURL.add(imageUri);
            } else if (requestCode == IMAGE_PICK_CODE3) {
                Glide.with(this).load(imageUri).into(imageButton4);
                imagesFromURL.add(imageUri);
            } else if (requestCode == IMAGE_PICK_CODE4) {
                Glide.with(this).load(imageUri).into(imageButton5);
                imagesFromURL.add(imageUri);
            } else if (requestCode == IMAGE_PICK_CODE5) {
                Glide.with(this).load(imageUri).into(imageButton6);
                imagesFromURL.add(imageUri);
            } else if (requestCode == IMAGE_PICK_CODE6) {
                Glide.with(this).load(imageUri).into(imageButton7);
                imagesFromURL.add(imageUri);
            } else if (requestCode == IMAGE_PICK_CODE7) {
                Glide.with(this).load(imageUri).into(imageButton8);
                imagesFromURL.add(imageUri);
            } else if (requestCode == IMAGE_PICK_CODE8) {
                Glide.with(this).load(imageUri).into(imageButton9);
                imagesFromURL.add(imageUri);
            }
        }
    }
}
