package com.example.findroommate;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ChangePassword extends AppCompatActivity {
    private TextInputEditText textInputEditTextOld, textInputEditTextNew, textInputEditTextReEnter;
    private SharedPreferences preferences;
    private String mUsername, mOldPassword, mNewPassword, mreEnteredPassword;
    private DatabaseReference mDatabase;
// ...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        init();
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mUsername = preferences.getString("UserName", null);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("User");
        mDatabase.orderByChild("userName").equalTo(mUsername).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    mOldPassword = data.child("password").getValue().toString();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }

    private void init() {
        textInputEditTextNew = findViewById(R.id.changePassword_new);
        textInputEditTextOld = findViewById(R.id.changePassword_old);
        textInputEditTextReEnter = findViewById(R.id.changePassword_reEnter);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void changePassword(View view) {
        mNewPassword = textInputEditTextNew.getText().toString();
        mreEnteredPassword = textInputEditTextReEnter.getText().toString();
        if (!mOldPassword.equals(Objects.requireNonNull(textInputEditTextOld.getText()).toString())) {
            textInputEditTextOld.setError("Wrong Password");

        } else if (mNewPassword.length() < 8) {
            textInputEditTextNew.setError("Password must have atleast 8 chars");

        } else if (!mNewPassword.equals(mreEnteredPassword)) {
            textInputEditTextReEnter.setError("password mismatch");
        } else {
            DatabaseReference updateData = FirebaseDatabase.getInstance()
                    .getReference("User")
                    .child(mUsername);
            updateData.child("password").setValue(mreEnteredPassword);
            Toast.makeText(getApplicationContext(), "Password Changed Successfully", Toast.LENGTH_LONG).show();
            textInputEditTextReEnter.setText("");
            textInputEditTextNew.setText("");
            textInputEditTextOld.setText("");
        }

    }
}
