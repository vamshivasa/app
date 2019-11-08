package com.example.findroommate.ui.AddPost;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.findroommate.Filters;
import com.example.findroommate.ImagesPost;
import com.example.findroommate.MapsActivity;
import com.example.findroommate.PictureUpload;
import com.example.findroommate.Post;
import com.example.findroommate.R;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import dmax.dialog.SpotsDialog;

import static android.app.Activity.RESULT_OK;


public class AddPostFragment extends Fragment {
    private TextInputEditText textInputEditText_price,
            textInputEditTextTitle, textInputEditTextDescription,
            textInputEditTextPhoneNumber;
    private ImageView imageButton;
    private Button getLocationButton;
    private String username, imageUri, postDate,
            postTitle, postDescription, postLocation,
            PostVisitDateTime, postUser, postPhoneNumber;
    private DatabaseReference databaseReference, databaseReference1;
    private long mPostId, mFilterId;
    private ArrayList<Integer> mUserFiletrs = new ArrayList<>();
    private Filters filters;
    private Uri uri1;
    private Post post;
    private StorageReference UserimagesRef;
    private ArrayList<Uri> image_uris;
    private List<ImagesPost> list;
    private HashMap<String, String> hashMap;
    private AlertDialog progressDialog;
    private int year, month, date, hour, mintue;
    private int year_x, month_x, date_x, hour_x, mintue_x;
    TextView editText;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        textInputEditTextTitle = root.findViewById(R.id.editText_title);
        textInputEditTextDescription = root.findViewById(R.id.editText_description);
        textInputEditTextPhoneNumber = root.findViewById(R.id.editText_phoneNumber);
        imageButton = root.findViewById(R.id.imageButton);
        getLocationButton = root.findViewById(R.id.button_add_location);
        //editText_visitTime = root.findViewById(R.id.EditTExt_visitTime);
        textInputEditText_price = root.findViewById(R.id.editText_price);
        editText = root.findViewById(R.id.button_addVisitTime);
        progressDialog = new SpotsDialog(getContext(), R.style.Custom);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        username = preferences.getString("UserName", null);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Post");
        databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Filters");
        image_uris = new ArrayList<Uri>();
        UserimagesRef = FirebaseStorage.getInstance().getReference().child("PostImages");
        filters = new Filters();
        hashMap = new HashMap<>();
        list = new ArrayList<>();
        post = new Post();
        //to get the post id from database
        DatabaseReference databaseReferenceLastNode = FirebaseDatabase.getInstance().getReference();
        Query lastQuery = databaseReferenceLastNode.child("Post").orderByKey().limitToLast(1);
        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    //if you call methods on dataSnapshot it gives you the required values
                    mPostId = Long.parseLong(Objects.requireNonNull(data.getKey()));// then it has the value "somekey4"
                    // String key = data.getKey(); // then it has the value "4:"
                    //as per your given snapshot of firebase database data
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });

        final int maxLength = 18;
        textInputEditTextTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() >= maxLength) {
                    // show toast / error here
                    String limitReached = "Maximum limit of characters reached!";
                    textInputEditTextTitle.setError(limitReached);
                } else {
                    // clear error
                    textInputEditTextTitle.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        textInputEditTextPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() >= 12) {
                    // show toast / error here
                    String limitReached = "Enter Valid Phone Number";
                    textInputEditTextPhoneNumber.setError(limitReached);
                } else {
                    // clear error
                    textInputEditTextPhoneNumber.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() < 10) {
                    textInputEditTextPhoneNumber.setError("Enter Valid Phone Number");
                } else {
                    textInputEditTextPhoneNumber.setError(null);
                }
            }
        });

        final String[] mListItems = getResources().getStringArray(R.array.filters);
        final boolean[] mCheckedItems = new boolean[mListItems.length];
        root.findViewById(R.id.button_filters).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                        Toast.makeText(getContext(), String.valueOf(mUserFiletrs), Toast.LENGTH_LONG).show();
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
        root.findViewById(R.id.button_add_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MapsActivity.class);
                startActivityForResult(intent, 12345);
            }
        });
        root.findViewById(R.id.imageButton).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 22) {
                    checkAndRequestForPermission();

                } else {
                    Intent intent = new Intent(getContext(), PictureUpload.class);
                    startActivityForResult(intent, 12);
                }
            }

            private void checkAndRequestForPermission() {
                if (ContextCompat.checkSelfPermission((Activity) getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        Toast.makeText(getContext(), "Please Grant this to access your gallary", Toast.LENGTH_SHORT).show();
                    } else {
                        ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);

                    }

                } else {
                    Intent intent = new Intent(getContext(), PictureUpload.class);
                    startActivityForResult(intent, 12);
                }
            }
        });

// to get the visit date
        root.findViewById(R.id.button_addVisitTime).setOnClickListener(new View.OnClickListener() {
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
                        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), R.style.CustomTimePickerDialogTheme, onTimeSetListener, hour, mintue, true);
                        timePickerDialog.show();
                    }
                };


                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                date = calendar.get(Calendar.DAY_OF_MONTH);
                //DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), AddPostFragment.this, year, month, date);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), R.style.CustomDatePickerDialogTheme, onDateSetListener, year, month, date);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();

            }
        });
        // to get current date
        String dateStr = "04/05/2010";

        @SuppressLint("SimpleDateFormat") SimpleDateFormat curFormater = new SimpleDateFormat("dd/MM/yyyy");
        Date dateObj = null;
        try {
            dateObj = curFormater.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        @SuppressLint("SimpleDateFormat") SimpleDateFormat postFormater = new SimpleDateFormat("MMMM dd, yyyy");
        postDate = postFormater.format(dateObj);

        // to add the post on database
        root.findViewById(R.id.button_addPost).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                String postUser = preferences.getString("UserName", null);
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
                    uploadingPictures();
                    progressDialog.setMessage("please wait....");
                    progressDialog.show();
                    post.setPostDate(String.valueOf(System.currentTimeMillis()));
                    post.setTitle(postTitle);
                    post.setPostUser(postUser);
                    post.setDescription(postDescription);
                    post.setLocation(postLocation);
                    post.setisShown(true);
                    if (!("").equals(Objects.requireNonNull(textInputEditText_price.getText()).toString())) {
                        post.setPrice(Integer.parseInt(String.valueOf(textInputEditText_price.getText())));
                    } else {
                        post.setPrice(0);
                    }
                    post.setPhoneNumber(postPhoneNumber);
                    post.setVisitDateTime(PostVisitDateTime);
                    post.setPostId(mPostId + 1);

                    post.setImageUri("");

                    if (!mUserFiletrs.isEmpty()) {
                        post.setFilterId(mPostId + 1);
                    } else {
                        post.setFilterId(0);
                    }

                    databaseReference.child(String.valueOf(mPostId + 1)).setValue(post);
                    textInputEditTextTitle.setText("");
                    textInputEditTextDescription.setText("");
                    editText.setHint("Pick Visit Time");
                    textInputEditText_price.setText("");
                    getLocationButton.setText("");
                    getLocationButton.setHint("Add Location");
                    textInputEditTextPhoneNumber.setText("");
                    textInputEditTextPhoneNumber.setError(null);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        imageButton.setImageDrawable(getContext().getDrawable(R.drawable.photoa));
                    }
                }
                if (!mUserFiletrs.isEmpty()) {
                    for (int i = 0; i < mUserFiletrs.size(); i++) {
                        if (mUserFiletrs.get(i) == 0) {
                            filters.setFurnished(true);
                        }
                        if (mUserFiletrs.get(i) == 1) {
                            filters.setNoSmoking(true);
                        }
                        if (mUserFiletrs.get(i) == 2) {
                            filters.setNoDrinking(true);
                        }
                        if (mUserFiletrs.get(i) == 3) {
                            filters.setNoPets(true);
                        }
                        if (mUserFiletrs.get(i) == 4) {
                            filters.setMale(true);
                        }
                        if (mUserFiletrs.get(i) == 5) {
                            filters.setFemale(true);
                        }
                    }
                    filters.setPostId(mPostId + 1);
                    filters.setFilterId(mPostId + 1);
                    databaseReference1.child(String.valueOf(mPostId + 1)).setValue(filters);
                    progressDialog.dismiss();
                }


            }
        });
        return root;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 12345) {
            if (resultCode == RESULT_OK) {
                getLocationButton.setText(data.getStringExtra("key"));
            }
        }
        if (requestCode == 12) {
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
                        Toast.makeText(getContext(), exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
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

                                Toast.makeText(getContext(), "post added successfully", Toast.LENGTH_LONG).show();
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

