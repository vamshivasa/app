package com.example.findroommate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ForgetPasswordActivity extends AppCompatActivity {
    FirebaseAuth auth;
    DatabaseReference dbref;
    TextInputEditText textInputEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        auth = FirebaseAuth.getInstance();
        textInputEditText = findViewById(R.id.textInputLayoutphoneNumber);
        dbref = FirebaseDatabase.getInstance().getReference().child("User");


    }

    public void forgetPassword(View view) {
        if (textInputEditText.getText().toString() != null) {
            String phone = textInputEditText.getText().toString();
            Query UserNameQuery = FirebaseDatabase.getInstance().getReference().child("User").
                    orderByChild("phoneNumber").equalTo(Long.parseLong(phone));

            UserNameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() > 0) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            textInputEditText.setError(null);
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ForgetPasswordActivity.this);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("FirstName", snapshot.child("firstName").getValue().toString());
                            editor.putString("LastName", snapshot.child("lastName").getValue().toString());
                            editor.putString("UserName", snapshot.child("userName").getValue().toString());
                            editor.putString("Phone", snapshot.child("phoneNumber").getValue().toString());
                            editor.putString("Email", snapshot.child("emailId").getValue().toString());
                            editor.apply();

                            Intent intent = new Intent(getApplicationContext(), VerficationActivity.class);
                            intent.putExtra("key", "forgotPassword");
                            intent.putExtra("phoneNumber", "+1" + textInputEditText.getText().toString());
                            startActivity(intent);
                        }

                    } else {
                        textInputEditText.setError("your phone number is not registered");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
