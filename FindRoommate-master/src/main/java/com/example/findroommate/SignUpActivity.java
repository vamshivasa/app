package com.example.findroommate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    TextInputEditText editTextPhoneNumber, editTextFirstName, editTextLastName, editTextUserName, editTextEmailId;
    String sPhoneNumber, sFirstName, sLastName, sEmail, sUsername;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        }
        init();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public void verification(View view) {
        sPhoneNumber = editTextPhoneNumber.getText().toString();
        sFirstName = editTextFirstName.getText().toString();
        sLastName = editTextLastName.getText().toString();
        sUsername = editTextUserName.getText().toString();
        Query UserNameQuery = FirebaseDatabase.getInstance().getReference().child("User").
                orderByChild("userName").equalTo(sUsername);
        UserNameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    editTextUserName.setError("Username must be unique");
                } else {
                    editTextUserName.setError(null);
                    sEmail = editTextEmailId.getText().toString();
                    if (sFirstName.isEmpty() || sUsername.isEmpty() || sPhoneNumber.isEmpty() ) {
                        if (sFirstName.equals("")) {
                            editTextFirstName.setError("please enter first name");
                        } else {
                            editTextFirstName.setError(null);
                        }
                        if (sUsername.equals("")) {
                            editTextUserName.setError("please enter user name");
                        } else {
                            editTextUserName.setError(null);
                        }
                        if (sPhoneNumber.equals("") || sPhoneNumber.length() != 10) {
                            editTextPhoneNumber.setError("please enter a valid phone number");
                        } else {
                            editTextPhoneNumber.setError(null);
                        }
                        if (!isValidEmail(sEmail)) {
                            editTextEmailId.setError("please enter a valid email id");
                        } else {
                            editTextEmailId.setError(null);
                        }


                    } else {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SignUpActivity.this);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("FirstName", sFirstName);
                        editor.putString("LastName", sLastName);
                        editor.putString("UserName", sUsername);
                        editor.putString("Phone", sPhoneNumber);
                        editor.putString("Email", sEmail);
                        editor.apply();
                        Intent intent = new Intent(getApplicationContext(), VerficationActivity.class);
                        intent.putExtra("phoneNumber", "+1" + editTextPhoneNumber.getText().toString());
                        startActivity(intent);
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    public void init() {
        editTextPhoneNumber = findViewById(R.id.editText_phone);
        editTextEmailId = findViewById(R.id.editTExt_email);
        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextUserName = findViewById(R.id.editTextUserName);
    }
}
