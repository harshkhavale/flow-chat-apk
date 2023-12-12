package com.hashcode.Flow.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.hashcode.Flow.databinding.ActivityStartBinding;

public class StartActivity extends AppCompatActivity {
    ActivityStartBinding binding;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        Thread thread = new Thread(){
            @Override
            public void run() {
                try{
                    sleep(3000);
                    if(auth.getCurrentUser() != null){
                        Intent intent = new Intent(StartActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Intent intent = new Intent(StartActivity.this,PhoneNumberActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }catch (Exception e)
                {
                    Toast.makeText(StartActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        };
        thread.start();





    }
}