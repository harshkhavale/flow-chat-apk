package com.hashcode.Flow.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hashcode.Flow.Adapters.StoriesAdapter;
import com.hashcode.Flow.Adapters.UserAdapter;
import com.hashcode.Flow.Models.Story;
import com.hashcode.Flow.Models.User;
import com.hashcode.Flow.Models.UserStories;
import com.hashcode.Flow.R;
import com.hashcode.Flow.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase database;
    ActivityMainBinding binding;
    ArrayList<User> users;
    FirebaseMessaging firebaseMessaging;
    UserAdapter userAdapter;
    ProgressDialog progressDialog;
    ArrayList<Story> Stories;
    ArrayList<UserStories> userStories;
    StoriesAdapter storiesAdapter;

    User user;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                auth.signOut();
                database.getReference().child("Presence").child(auth.getUid()).setValue("UNACTIVE");
                Intent intent = new Intent(MainActivity.this, PhoneNumberActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.newgroup:
                Intent intent2 = new Intent(MainActivity.this, GroupChatActivity.class);
                startActivity(intent2);
                finish();
                break;
            case R.id.settings:
                Toast.makeText(this, "settings cliked", Toast.LENGTH_SHORT).show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);


        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String token) {
                HashMap<String,Object> map = new HashMap<>();
                map.put("token", token);
                database.getReference().child("Users").child(auth.getUid()).updateChildren(map);
            }
        }); users = new ArrayList<>();
        userStories = new ArrayList<>();
        userAdapter = new UserAdapter(this, users);
        storiesAdapter = new StoriesAdapter(this, userStories);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        binding.recyclerView.setAdapter(userAdapter);


        database.getReference().child("Users").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                Glide.with(MainActivity.this).load(user.getProfilePic()).placeholder(R.drawable.user).into(binding.profileImg);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot ds1 : snapshot.getChildren()) {
                    User user = ds1.getValue(User.class);
                    if (!user.getuId().equals(FirebaseAuth.getInstance().getUid())) {
                        users.add(user);
                    }

                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        database.getReference().child("Presence").child(auth.getUid()).setValue("ACTIVE");
    }


    @Override
    protected void onPause() {
        super.onPause();
        try{
            database.getReference().child("Presence").child(auth.getUid()).setValue("UNACTIVE");

        }catch(Exception e){

        }


    }

}