package com.hashcode.Flow.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hashcode.Flow.Models.User;
import com.hashcode.Flow.databinding.ActivitySetUserProfileBinding;

import java.util.Objects;

public class SetUserProfileActivity extends AppCompatActivity {
    ActivitySetUserProfileBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseStorage storage;
    Uri selectedImg;
    StorageReference reference;
   ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressDialog = new ProgressDialog(SetUserProfileActivity.this);
        progressDialog.setMessage("creating user");
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage =  FirebaseStorage.getInstance();

        binding.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,45);
            }
        });
        binding.setProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.profileName.getText().toString();
                if(name.isEmpty()){
                    binding.profileName.setError("Enter a Name");
                }
                else{
                    progressDialog.show();
                    if(selectedImg!=null){
                        reference = storage.getReference().child("Profiles").child(Objects.requireNonNull(auth.getUid()));
                        reference.putFile(selectedImg).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()){
                                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String img = uri.toString();
                                            String phone = Objects.requireNonNull(auth.getCurrentUser()).getPhoneNumber();
                                            String name = binding.profileName.getText().toString();
                                            String uid = auth.getUid();
                                            User user = new User(uid,name,img,phone);
                                            database.getReference().child("Users").child(uid).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Intent intent = new Intent(SetUserProfileActivity.this,MainActivity.class);
                                                    progressDialog.dismiss();
                                                    startActivity(intent);
                                                }
                                            });

                                        }
                                    });
                                }
                                else{
                                    Toast.makeText(SetUserProfileActivity.this, "error", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });


                    }
                    else{
                        String img = "No Image";
                        String phone = Objects.requireNonNull(auth.getCurrentUser()).getPhoneNumber();
                        String userName = binding.profileName.getText().toString();
                        String uid = auth.getUid();
                        User user = new User(uid,userName,img,phone);
                        database.getReference().child("Users").child(uid).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Intent intent = new Intent(SetUserProfileActivity.this,MainActivity.class);
                                progressDialog.dismiss();
                                startActivity(intent);
                            }
                        });
                    }


                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==45){

            if(data != null){
                if(data.getData() != null)
                {
                    binding.userImage.setImageURI(data.getData());
                    selectedImg = data.getData();
                }

            }
        }


    }
}