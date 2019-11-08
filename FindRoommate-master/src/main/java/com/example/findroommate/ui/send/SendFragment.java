package com.example.findroommate.ui.send;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.findroommate.ChangePassword;
import com.example.findroommate.CircleTransform;
import com.example.findroommate.MainActivity;
import com.example.findroommate.R;
import com.example.findroommate.SignUpActivity;
import com.example.findroommate.SplashActivity;
import com.squareup.picasso.Picasso;

public class SendFragment extends Fragment {
    private LinearLayout linearLayoutEditProfile, linearLayoutChangePassword, linearLayoutPrivacy;
    ImageView imageView;
    SharedPreferences preferences;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_send, container, false);
        // final TextView textView = root.findViewById(R.id.text_send);
        imageView = root.findViewById(R.id.profile_image);
        root.findViewById(R.id.linearLayout_editProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "coming soon", Toast.LENGTH_LONG).show();
            }
        });
        root.findViewById(R.id.linearLayout_changePassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ChangePassword.class);
                startActivity(i);
            }
        });
        Picasso.get()
                .load(R.drawable.dummyimage)
                .transform(new CircleTransform(true))
                .into(imageView);
        root.findViewById(R.id.linearLayout_signOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                final String username = preferences.getString("UserName", null);


                if (username != null) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    editor.apply();
                    Intent intent = new Intent(getContext(), SplashActivity.class);// New activity
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    getActivity().finish();
                    startActivity(intent);

                }
            }
        });

        return root;
    }
}