package com.example.findroommate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.findroommate.ui.AddPost.AddPostFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import dmax.dialog.SpotsDialog;

public class EditPost extends AppCompatActivity {
    private DatabaseReference databaseReference, databaseReference1;
    private TextInputEditText textInputEditText_price,
            textInputEditTextTitle, textInputEditTextDescription,
            textInputEditTextPhoneNumber;
    private ImageView imageButton;
    private AlertDialog progressDialog;
    private ArrayList<Uri> image_uris;
    private Button getLocationButton, buttondelete, buttonDone, buttonHide;
    private DatePickerDialog datePickerDialog;
    private EditText editText_visitTime;
    private String username, imageUri, postDate,
            postTitle, postDescription, postLocation,
            PostVisitDateTime, postUser, postPhoneNumber;
    private long mPostId, mFilterId;
    private Post post;
    private List<ImagesPost> list;
    private Filters filters;
    private StorageReference UserimagesRef;
    private int year, month, date, hour, mintue;
    private int year_x, month_x, date_x, hour_x, mintue_x;
    TextView editText;
    private ArrayList<Integer> mUserFiletrs = new ArrayList<>();


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        }
        progressDialog = new SpotsDialog(EditPost.this, R.style.Custom);
        filters = new Filters();
        image_uris = new ArrayList<>();
        list = new ArrayList<>();
        UserimagesRef = FirebaseStorage.getInstance().getReference().child("PostImages");
        textInputEditTextTitle = findViewById(R.id.editText_title);
        textInputEditTextDescription = findViewById(R.id.editText_description);
        textInputEditTextPhoneNumber = findViewById(R.id.editText_phoneNumber);
        imageButton = findViewById(R.id.imageButton);
        getLocationButton = findViewById(R.id.button_add_location);
        // editText_visitTime = findViewById(R.id.EditTExt_visitTime);
        editText = findViewById(R.id.button_addVisitTime);
        textInputEditText_price = findViewById(R.id.editText_price);
        buttondelete = findViewById(R.id.button_deletePost);
        buttonHide = findViewById(R.id.button_HidePost);
        final Intent intent = getIntent();
        mPostId = Long.parseLong(intent.getStringExtra("postId"));
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Post").child(String.valueOf(mPostId));
        databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Filters");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        username = preferences.getString("UserName", null);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(getApplicationContext(), EditPicture.class);
                intent.putExtra("postId", mPostId);
                intent.putExtra("username", username);
                startActivityForResult(intent1, 89);
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("title").exists()) {
                    textInputEditTextTitle.setText(dataSnapshot.child("title").getValue().toString());
                }
                if (dataSnapshot.child("description").exists()) {
                    textInputEditTextDescription.setText(dataSnapshot.child("description").getValue().toString());

                }
                if (dataSnapshot.child("location").exists()) {
                    //   textViewId.setText("Post Id: "+dataSnapshot.child("postId").getValue().toString());
                    if (!dataSnapshot.child("location").getValue().toString().equals("")) {
                        getLocationButton.setVisibility(View.VISIBLE);
                        getLocationButton.setText(dataSnapshot.child("location").getValue().toString());

                    } else {
                        getLocationButton.setHint("Add Location");
                    }
                }
                if (dataSnapshot.child("price").exists()) {
                    if (!dataSnapshot.child("price").getValue().toString().isEmpty()) {
                        textInputEditText_price.setVisibility(View.VISIBLE);
                        textInputEditText_price.setText(dataSnapshot.child("price").getValue().toString());
                    }
                }
                if (dataSnapshot.child("phoneNumber").exists()) {
                    textInputEditTextPhoneNumber.setText(dataSnapshot.child("phoneNumber").getValue().toString());

                }
                if (dataSnapshot.child("visitDateTime").exists()) {
                    if (!dataSnapshot.child("visitDateTime").getValue().equals(""))
                        editText.setText(dataSnapshot.child("visitDateTime").getValue().toString());
                    else
                        editText.setHint("Pick Visit Date Time");
                }
                if(dataSnapshot.child("imageUri").getValue()!=null) {
                   Glide.with(getApplicationContext()).load(dataSnapshot.child("imageUri").getValue()).into(imageButton);
                }else
                {
                    Glide.with(getApplicationContext()).load(R.drawable.noimage).into(imageButton);
                }
                if (dataSnapshot.child("filterId").exists()) {
                    if (Long.parseLong(dataSnapshot.child("filterId").getValue().toString()) != 0) {
                        mFilterId = Long.parseLong(dataSnapshot.child("filterId").getValue().toString());
                    } else {
                        mFilterId = 0;
                    }
                }
                if (dataSnapshot.child("shown").exists()) {
                    if (dataSnapshot.child("shown").getValue().equals(false)) {
                        buttonHide.setText("UNHIDE POST");
                    } else {
                        buttonHide.setText("HIDE POST");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //get date and time for visit
        findViewById(R.id.button_addVisitTime).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                final TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        hour_x = i;
                        mintue_x = i1;
                        editText.setText(date_x + "/" + month_x + "/" + year_x + " " + hour_x + ":" + mintue_x);
                    }

                };
                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        year_x = year;
                        month_x = month;
                        date_x = dayOfMonth;
                        Calendar c = Calendar.getInstance();
                        hour = c.get(Calendar.HOUR);
                        mintue = c.get(Calendar.MINUTE);
                        TimePickerDialog timePickerDialog = new TimePickerDialog(EditPost.this, R.style.CustomTimePickerDialogTheme, onTimeSetListener, hour, mintue, true);
                        timePickerDialog.show();
                    }
                };


                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                date = calendar.get(Calendar.DAY_OF_MONTH);
                //DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), AddPostFragment.this, year, month, date);
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditPost.this, R.style.CustomDatePickerDialogTheme, onDateSetListener, year, month, date);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();

            }
        });

        final String[] mListItems = getResources().getStringArray(R.array.filters);
        final boolean[] mCheckedItems = new boolean[mListItems.length];
        findViewById(R.id.button_filters).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(EditPost.this);
                builder.setTitle("Choose Filters");
                builder.setMultiChoiceItems(mListItems, mCheckedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if (!isChecked) {
                            mUserFiletrs.add(position);

                        }
                        if (isChecked) {
                            if (!mUserFiletrs.contains(position)) {
                                mUserFiletrs.add(position);

                            } else {
                                mUserFiletrs.remove(position);
                            }
                        }
                    }
                });

                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Toast.makeText(getApplicationContext(), String.valueOf(mUserFiletrs), Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("DISMISS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setNeutralButton("CLEAR ALL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        for (int i = 0; i < mCheckedItems.length; i++) {
                            mCheckedItems[i] = false;
                            mUserFiletrs.clear();
                        }
                    }
                });
                AlertDialog mDialog = builder.create();
                mDialog.show();
            }
        });
        buttonHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                        .child("Post").child(String.valueOf(mPostId));
                Intent i = new Intent(getApplicationContext(), HidePostActivity.class);
                if (buttonHide.getText().toString().equalsIgnoreCase("HIDE POST")) {

                    ref.child("shown").setValue(false);
                    i.putExtra("postKey", " Hidden!!!");
                    buttonHide.setText("UNHIDE POST");

                } else if (buttonHide.getText().toString().equalsIgnoreCase("UNHIDE POST")) {
                    ref.child("shown").setValue(true);

                    i.putExtra("postKey", " Posted!!!");
                    buttonHide.setText("HIDE POST");


                }
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
        buttondelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Query deleteQuery = ref.child("Post").child(String.valueOf(mPostId));

                deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot deleteSnapshot : dataSnapshot.getChildren()) {
                            deleteSnapshot.getRef().removeValue();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Log.e(TAG, "onCancelled", databaseError.toException());
                    }
                });
                if (mFilterId != 0) {
                    final Query query1 = ref.child("Filters").child(String.valueOf(mFilterId));
                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                snapshot.getRef().removeValue();
                                Intent intent1 = new Intent(getApplicationContext(), DeleteActivity.class);
                                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent1);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuq, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.mybutton) {

            postTitle = textInputEditTextTitle.getText().toString();
            postDescription = textInputEditTextDescription.getText().toString();
            postLocation = getLocationButton.getText().toString();
            PostVisitDateTime = editText.getText().toString();
            postPhoneNumber = textInputEditTextPhoneNumber.getText().toString();
            if (postTitle.isEmpty() || postDescription.isEmpty()) {
                if (postTitle.equals("")) {
                    textInputEditTextTitle.setError("Post Title Cannot be empty");
                } else {
                    textInputEditTextTitle.setError(null);
                }
                if (postDescription.equals("")) {
                    textInputEditTextDescription.setError("Post description cannot be empty");
                } else {
                    textInputEditTextDescription.setError(null);
                }


            } else {

                progressDialog.setMessage("please wait....");
                progressDialog.show();
                DatabaseReference updateData = FirebaseDatabase.getInstance()
                        .getReference("Post")
                        .child(String.valueOf(mPostId));
                updateData.child("title").setValue(postTitle);
                updateData.child("postTime").setValue(String.valueOf(System.currentTimeMillis()));
                if (!("").equals(Objects.requireNonNull(textInputEditText_price.getText()).toString())) {
                    updateData.child("price").setValue(Integer.parseInt(String.valueOf(textInputEditText_price.getText())));
                } else {
                    updateData.child("price").setValue(0);
                }
                updateData.child("location").setValue(postLocation);
                updateData.child("phoneNumber").setValue(postPhoneNumber);

                if (!mUserFiletrs.isEmpty()) {
                    if (mFilterId != 0)
                        updateData.child("filterId").setValue(mFilterId);
                } else {
                    updateData.child("filterId").setValue(0);
                }

            }
            if (!mUserFiletrs.isEmpty()) {
                for (int i = 0; i < mUserFiletrs.size(); i++) {
                    if (mUserFiletrs.get(i) == 0) {
                        databaseReference1.child(String.valueOf(mFilterId)).child("furnished").setValue(true);
                    }
                    if (mUserFiletrs.get(i) == 1) {
                        databaseReference1.child(String.valueOf(mFilterId)).child("noSmoking").setValue(true);
                    }
                    if (mUserFiletrs.get(i) == 2) {
                        databaseReference1.child(String.valueOf(mFilterId)).child("noDrinking").setValue(true);
                    }
                    if (mUserFiletrs.get(i) == 3) {
                        databaseReference1.child(String.valueOf(mFilterId)).child("noPets").setValue(true);
                    }
                    if (mUserFiletrs.get(i) == 4) {
                        databaseReference1.child(String.valueOf(mFilterId)).child("male").setValue(true);
                    }
                    if (mUserFiletrs.get(i) == 5) {
                        databaseReference1.child(String.valueOf(mFilterId)).child("female").setValue(true);
                    }
                }
                databaseReference1.child(String.valueOf(mFilterId)).child("filterId").setValue(mPostId);
                databaseReference1.child(String.valueOf(mFilterId)).child("postId").setValue(mPostId);
            }
            progressDialog.dismiss();
            uploadingPictures();
            // databaseReference.child(String.valueOf(mPostId)).setValue(post);
            textInputEditTextTitle.setText("");
            textInputEditTextDescription.setText("");
            editText.setHint("Pick Visit Time");
            textInputEditText_price.setText("");
            getLocationButton.setText("");
            getLocationButton.setHint("Add Location");
            textInputEditTextPhoneNumber.setText("");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                imageButton.setImageDrawable(getApplicationContext().getDrawable(R.drawable.photoa));
            }
            Intent intent = new Intent(getApplicationContext(), EditPostDone.class);
            startActivity(intent);
            finish();

        }
        return super.

                onOptionsItemSelected(item);

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12345) {
            if (resultCode == RESULT_OK) {
                getLocationButton.setText(data.getStringExtra("key"));
            }
        }
        if (requestCode == 89) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                image_uris = data.getParcelableArrayListExtra("list");
                assert image_uris != null;
                if (image_uris.size() > 0) {
                    if (image_uris.get(0) != null) {
                        Glide.with(this).load(image_uris.get(0)).into(imageButton);
                    }
                } else {
                    Glide.with(this).load(R.drawable.noimage).into(imageButton);
                }


            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void uploadingPictures() {


        if (!username.isEmpty()) {
            final DatabaseReference databaseReferenceImages = FirebaseDatabase.getInstance().getReference()
                    .child("postImages").
                            child(username)
                    .child(String.valueOf(mPostId + 1));
            for (int i = 0; i < image_uris.size(); i++) {
                final StorageReference ImageRef = UserimagesRef
                        .child(username)
                        .child(String.valueOf(mPostId + 1))
                        .child(Objects.requireNonNull(image_uris.get(i).getLastPathSegment()));
                UploadTask uploadTask = ImageRef.putFile(image_uris.get(i));

                // Register observers to listen for when the download is done or if it fails

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imageUri = String.valueOf(uri);
                                storeLink(imageUri);
                                databaseReferenceImages.setValue(list).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        DatabaseReference databaseReferenceUpdate = FirebaseDatabase.getInstance().getReference()
                                                .child("Post").child(String.valueOf(mPostId + 1));
                                        databaseReferenceUpdate.child("imageUri").setValue(imageUri.toString());
                                        progressDialog.dismiss();
                                    }
                                });

                                Toast.makeText(getApplicationContext(), "post added successfully", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });

            }


        }

    }


    private void storeLink(String imageUri) {
        ImagesPost imagesPost = new ImagesPost();
        imagesPost.setImages(imageUri);
        list.add(imagesPost);
        // Toast.makeText(getContext(), "post added successfully", Toast.LENGTH_LONG).show();

    }


}

