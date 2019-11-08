package com.example.findroommate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HidePostActivity extends AppCompatActivity {
    String postAction;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hide_post);
        textView=findViewById(R.id.textView_showingAction);
        Intent intent=getIntent();
        postAction=intent.getStringExtra("postKey");
        if(postAction!=null){
            textView.append(postAction);
        }

        findViewById(R.id.goToHomeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
