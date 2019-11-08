package com.example.findroommate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import dmax.dialog.SpotsDialog;

public class SignInActivity extends AppCompatActivity {
    Button button_signIn;

    TextInputEditText textInputEditTextUserName, textInputEditTextPassword;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private SpotsDialog progressDialog;
    private String mCustomToken;
    private TextView textViewForgetPassword,signUptextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        button_signIn = findViewById(R.id.button_signIn);
        textInputEditTextUserName = findViewById(R.id.textInputLayoutUserName);
        textInputEditTextPassword = findViewById(R.id.textInputLayoutPassword);
        textViewForgetPassword=findViewById(R.id.forgetPasswordButton);
        signUptextView=findViewById(R.id.textView_signUp);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myRef = mFirebaseDatabase.getReference().child("User");
        progressDialog = new SpotsDialog(SignInActivity.this, R.style.Custom);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final SharedPreferences.Editor editor = preferences.edit();
        textViewForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });
        signUptextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),SignUpActivity.class);
                startActivity(intent);
            }
        });
        button_signIn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                progressDialog.setTitle("please wait...");
                final String UserName = Objects.requireNonNull(textInputEditTextUserName.getText()).toString();
                final String Password = Objects.requireNonNull(textInputEditTextPassword.getText()).toString();
                progressDialog.show();
                myRef.orderByChild("userName").equalTo(UserName).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String password = null;
                        String emailId = null;
                        //User user = new User();
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            password = data.child("password").getValue().toString();
                            //emailId=data.child("emailId").getValue().toString();
                            // user.setPassword(dataSnapshot.child("password").getValue(User.class).getPassword());
                        }
                        if (Password.equals(password)) {
                            progressDialog.dismiss();
                            editor.putString("UserName", textInputEditTextUserName.getText().toString());
                            editor.putString("emailId", emailId);
                            editor.apply();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                            Toast.makeText(getApplicationContext(), "You are successfully Signed In", Toast.LENGTH_LONG).show();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), " Wrong Username or Password ", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });

            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
       /* FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);*/

    }

    private void updateUI(FirebaseUser currentUser) {
        if (user != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "sign in please", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

}
