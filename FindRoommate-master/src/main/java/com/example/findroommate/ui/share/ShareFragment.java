package com.example.findroommate.ui.share;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.findroommate.CircleTransform;
import com.example.findroommate.R;
import com.example.findroommate.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import dmax.dialog.SpotsDialog;

public class ShareFragment extends Fragment {

    ImageButton imageView_profile;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private StorageReference UserimagesRef;
    private TextView textViewDOB, textView_dob_result;
    private DatePickerDialog datePickerDialog;
    private TextInputEditText textInputEditTextFirstName, textInputEditTextLastName, textInputEditTextUserName, textInputEditTextPhoneNumber, textInputEditTextEmail;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private User user;
    private Uri image_uri;
    private Spinner spinnerGender;
    private String emailId, FirstName, LastName, PhoneNumber, UserNameOld, dob, password, Gender, profile_image_uri;
    private ArrayAdapter<CharSequence> adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_share, container, false);
        spinnerGender = root.findViewById(R.id.spinner_profile_Gender);
        imageView_profile = root.findViewById(R.id.profile_image);
        textViewDOB = root.findViewById(R.id.textView_profile_dob);
        textInputEditTextFirstName = root.findViewById(R.id.profile_FirstName);
        textInputEditTextLastName = root.findViewById(R.id.profile_LastName);
        textInputEditTextUserName = root.findViewById(R.id.profile_UserName);
        textInputEditTextEmail = root.findViewById(R.id.profile_Email);
        textInputEditTextPhoneNumber = root.findViewById(R.id.profile_PhoneNumber);
        textView_dob_result = root.findViewById(R.id.text_profile_DOBresult);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = new User();
        myRef = mFirebaseDatabase.getReference().child("User");
        adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.gender, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerGender.setAdapter(adapter);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        UserimagesRef = storageReference.child("ProfileImage");
        getDataFromDatabase();

        root.findViewById(R.id.text_profile_DOBresult).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                // if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                textView_dob_result.setText(day + "/" + (month + 1) + "/" + year);

                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.setTitle("Select Date");
                datePickerDialog.show();
            }
        });
        imageView_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Change Profile Picture");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo")) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (getContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                                        getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                                    String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                                    requestPermissions(permission, 1000);
                                } else {
                                    openCamera();
                                }
                            } else {
                                openCamera();
                            }
                        } else if (options[item].equals("Choose from Gallery")) {

                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);

                            startActivityForResult(intent, 2);

                        } else if (options[item].equals("Cancel")) {

                            dialog.dismiss();
                        }
                    }

                    private void openCamera() {
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.TITLE, "New Picture");
                        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
                        image_uri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                        //Camera intent
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
                        startActivityForResult(takePictureIntent, 1);
                    }
                });
                builder.show();
            }
        });
        root.findViewById(R.id.changeProfileDone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkNullElements();
                DatabaseReference updateData = FirebaseDatabase.getInstance()
                        .getReference("User")
                        .child(UserNameOld);
                updateData.child("firstName").setValue(textInputEditTextFirstName.getText().toString());
                updateData.child("lastName").setValue(textInputEditTextLastName.getText().toString());
                if (textInputEditTextEmail.getText() != null) {
                    updateData.child("emailId").setValue(textInputEditTextEmail.getText().toString());

                }
                if (textView_dob_result.getText() != null) {
                    updateData.child("dob").setValue(textView_dob_result.getText().toString());

                }
                Gender = spinnerGender.getSelectedItem().toString();
                updateData.child("gender").setValue(Gender);
                Toast.makeText(getContext(), "profile updated", Toast.LENGTH_LONG).show();
            }
        });
        return root;
    }

    private void checkNullElements() {
        if (textInputEditTextFirstName.getText().toString() == null) {
            textInputEditTextFirstName.setError("please enter first name");
        } else {
            textInputEditTextFirstName.setError(null);
        }
        if (textInputEditTextLastName.getText().toString() == null) {
            textInputEditTextLastName.setError("please enter last name");
        } else {
            textInputEditTextLastName.setError(null);
        }

    }




    private void getDataFromDatabase() {
        final SpotsDialog progressDialog = new SpotsDialog(getContext());
        progressDialog.setMessage("processing...");
        progressDialog.show();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        final String username = preferences.getString("UserName", null);
        StorageReference ImageRef = UserimagesRef.child(preferences.getString("UserName", null));
        ImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                profile_image_uri = uri.toString();
                Picasso.get()
                        .load(profile_image_uri)
                        .transform(new CircleTransform())
                        .into(imageView_profile);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                profile_image_uri = null;
            }
        });
        myRef.orderByChild("userName").equalTo(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                progressDialog.dismiss();
                //User user = new User();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    UserNameOld = data.child("userName").getValue().toString();
                    if (data.child("emailId").getValue().toString() != null) {
                        emailId = data.child("emailId").getValue().toString();
                    } else {
                        emailId = "";
                    }
                    FirstName = data.child("firstName").getValue().toString();
                    LastName = data.child("lastName").getValue().toString();
                    PhoneNumber = data.child("phoneNumber").getValue().toString();
                    password = data.child("password").getValue().toString();
                    dob = data.child("dob").getValue().toString();
                    Gender = data.child("gender").getValue().toString();
                    textInputEditTextEmail.setText(emailId);
                    textInputEditTextFirstName.setText(FirstName);
                    textInputEditTextLastName.setText(LastName);
                    textInputEditTextPhoneNumber.setText(PhoneNumber);
                    textInputEditTextUserName.setText(username);
                    textView_dob_result.setText(dob);

                    if (Gender != null) {
                        int spinnerPosition = adapter.getPosition(Gender);
                        spinnerGender.setSelection(spinnerPosition);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();

            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Result code is RESULT_OK only if the user selects an Image
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            switch (requestCode) {
                case 1:
                    Glide.with(this).load(image_uri).into(imageView_profile);

                    break;
                case 2:
                    //data.getData returns the content URI for the selected Image
                    image_uri = data.getData();
                    Glide.with(this).load(image_uri).into(imageView_profile);
                    break;
            }
            if (image_uri != null) {
                UploadImage();
            }
        }
    }

    private void UploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading Photo");
        progressDialog.show();

        StorageReference ImageRef = UserimagesRef.child(UserNameOld);
        UploadTask uploadTask = ImageRef.putFile(image_uri);

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
                progressDialog.dismiss();
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                Toast.makeText(getContext(), "Profile Update Successfully", Toast.LENGTH_LONG).show();
            }
        });

    }

}


