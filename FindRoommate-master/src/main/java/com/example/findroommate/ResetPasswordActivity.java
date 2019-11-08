package com.example.findroommate;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ResetPasswordActivity extends AppCompatActivity {
    TextInputEditText textInputEditText1, textInputEditText2;
    Button button;
    String pass1,pass2;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_password);
        textInputEditText1=findViewById(R.id.editText_enterNewPassword);
        textInputEditText2=findViewById(R.id.edittext_reEnterPassword);
        button=findViewById(R.id.button_signUpFinal);
        preferences = PreferenceManager.getDefaultSharedPreferences(ResetPasswordActivity.this);
        button.setText("Reset Password");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pass1 = textInputEditText1.getText().toString().trim();
                pass2 = textInputEditText2.getText().toString().trim();
                if (pass1.length() < 8) {
                    textInputEditText1.setError("password should consists atleast 8 chars");

                } else if (pass1.length() >= 8) {
                    textInputEditText1.setError(null);
                } else if (pass1.equals(pass2)) {

                    DatabaseReference updateChild= FirebaseDatabase.getInstance().getReference().child("User");
                            updateChild.child("password").setValue(pass2);

                }
            }
        });
    }
}
