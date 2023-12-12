package com.hashcode.Flow.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hashcode.Flow.Adapters.MessageAdapter;
import com.hashcode.Flow.Fragments.GalleryFragment;
import com.hashcode.Flow.Models.Message;
import com.hashcode.Flow.R;
import com.hashcode.Flow.databinding.ActivityChatBinding;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    ActivityChatBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    MessageAdapter messageAdapter;
    ArrayList<Message> msgList;
    String SenderRoom, RecieverRoom;
    FirebaseStorage storage;
    String senderUid;
    String recieverUid;
    PopupWindow popupWindow;
    ProgressDialog dialog;
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
    @Override
    protected void onResume() {
        super.onResume();
        database.getReference().child("Presence").child(auth.getUid()).setValue("ACTIVE");



    }
    @Override
    protected void onPause() {
        super.onPause();
        database.getReference().child("Presence").child(auth.getUid()).setValue("UNACTIVE");


    }

    @Override
    protected void onStop() {
        super.onStop();
        database.getReference().child("Presence").child(auth.getUid()).setValue("UNACTIVE");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.getReference().child("Presence").child(auth.getUid()).setValue("UNACTIVE");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("sending media");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String token = getIntent().getStringExtra("token");
        String name = getIntent().getStringExtra("name");
        String profileImg = getIntent().getStringExtra("profileImg");



        binding.userName.setText(name);
        Glide.with(ChatActivity.this).load(profileImg).placeholder(R.drawable.user).into(binding.profileImg);
        recieverUid = getIntent().getStringExtra("uid");
        senderUid = auth.getUid();
        msgList = new ArrayList<>();
        SenderRoom = senderUid + recieverUid;
        RecieverRoom = recieverUid + senderUid;
        messageAdapter = new MessageAdapter(this, msgList,SenderRoom,RecieverRoom);

        binding.chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.chatRecyclerView.setAdapter(messageAdapter);


        database.getReference().child("Presence").child(recieverUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String presence = snapshot.getValue(String.class);
                    if(!presence.isEmpty()){
                        if(presence.equals("UNACTIVE")){
                            binding.userState.setVisibility(View.GONE);
                        }else{
                            binding.userState.setText(presence);
                            binding.userState.setVisibility(View.VISIBLE);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        database.getReference().child("chats")
                .child(SenderRoom).child("messages")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                msgList.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren())
                {
                    Message message = snapshot1.getValue(Message.class);
                    msgList.add(message);
                }
                binding.chatRecyclerView.scrollToPosition(msgList.size()-1);
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GalleryFragment fragment = new GalleryFragment();
                fragment.show(getSupportFragmentManager(), "TAG");
            }
        });

        binding.sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();
                String msgText = binding.messageBox.getText().toString();
                Message message = new Message(msgText,senderUid,date.getTime());
                binding.messageBox.setText("");

                String randomKey = database.getReference().push().getKey();
                message.setMessageId(randomKey);
                HashMap<String,Object> lastMsgObj = new HashMap<>();
                lastMsgObj.put("lastMessage",message.getMessage());
                lastMsgObj.put("msgTime",date.getTime());
                database.getReference().child("chats").child(SenderRoom).updateChildren(lastMsgObj);
                database.getReference().child("chats").child(RecieverRoom).updateChildren(lastMsgObj);

                database.getReference().child("chats").child(SenderRoom)
                        .child("messages")
                        .child(randomKey)
                        .setValue(message)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        database.getReference().child("chats")
                                .child(RecieverRoom)
                                .child("messages")
                                .child(randomKey)
                                .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                sendNotifications(name,message.getMessage(),token);
                                binding.chatRecyclerView.scrollToPosition(msgList.size()-1);
                            }
                        });
                    }
                });
            }
        });
        final Handler handler = new Handler();
        binding.messageBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                database.getReference().child("Presence").child(senderUid).setValue("RESPONDING..");
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(userStopTyping,1000);
            }
            Runnable userStopTyping = new Runnable() {
                @Override
                public void run() {
                    database.getReference().child("Presence").child(senderUid).setValue("ACTIVE");

                }
            };
        });


    }
    void sendNotifications(String name,String message,String token){
        try{
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "http://fcm.googleapis.com/fcm/send";
            JSONObject data = new JSONObject();
            data.put("title",name);
            data.put("body",message);
            JSONObject notificationData = new JSONObject();
            notificationData.put("notification",data);
            notificationData.put("to",token);
            JsonObjectRequest request = new JsonObjectRequest(url, notificationData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(ChatActivity.this, "success", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();

                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String,String> map = new HashMap<>();
                    String key = "Key=AAAAnSbN23Y:APA91bEGsIX6IkXx4bOxZ0CadvkI6RoHrjCkSk3L0tAgAAuL9PEvZZgWU1Dnp0LtJ69hDioK38i6qeCPbPODuNxksOaWzLUcaHxdDG-NKYZBRL8r0wJkbciZtY5LWJGnXwN7NTvMo-Fb\t\n";
                    map.put("Authorization",key);
                    map.put("Content-Type","application/Json");
                    return map;
                }
            };
            queue.add(request);
        }
        catch (Exception e){

        }


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case 25:
                uploadContent("photo",data);
                break;
            case 30:
                uploadContent("video",data);
                break;
        };
    }
    public void uploadContent(String contentType,Intent data){
        if(data !=null){
            if(data.getData()!=null){
                dialog.show();
                Uri selectedContent = data.getData();
                Calendar calendar = Calendar.getInstance();
                StorageReference reference = storage.getReference().child("chats").child(calendar.getTimeInMillis()+"");
                reference.putFile(selectedContent).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String filePath = uri.toString();
                                    Date date = new Date();
                                    String msgText = binding.messageBox.getText().toString();
                                    Message message = new Message(msgText,senderUid,date.getTime());
                                    message.setMessage(contentType);
                                    message.setImageUrl(filePath);
                                    String randomKey = database.getReference().push().getKey();
                                    message.setMessageId(randomKey);
                                    HashMap<String,Object> lastMsgObj = new HashMap<>();
                                    lastMsgObj.put("lastMessage",message.getMessage());
                                    lastMsgObj.put("msgTime",date.getTime());
                                    database.getReference().child("chats").child(SenderRoom).updateChildren(lastMsgObj);
                                    database.getReference().child("chats").child(RecieverRoom).updateChildren(lastMsgObj);

                                    database.getReference().child("chats").child(SenderRoom)
                                            .child("messages")
                                            .child(randomKey)
                                            .setValue(message)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {

                                                    popupWindow.dismiss();
                                                    dialog.dismiss();
                                                    database.getReference().child("chats")
                                                            .child(RecieverRoom)
                                                            .child("messages")
                                                            .child(randomKey)
                                                            .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {

                                                                }
                                                            });
                                                }
                                            });

                                }
                            });

                        }
                    }
                });
            }
        }

    }

    public void showPopupWindow(final View view) {

        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.attach_options, null);

        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true ;

        //Create a window with our parameters
        popupWindow = new PopupWindow(popupView, width, height, focusable);
        //Set the location of the window on the screen
        Animation popup = AnimationUtils.loadAnimation(this,R.anim.attach_options_animation);
        popupView.startAnimation(popup);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        popupWindow.setFocusable(true);
        //Initialize the elements of our window, install the handler

        ImageView sendImg = popupView.findViewById(R.id.attachImage);
        ImageView sendVideo = popupView.findViewById(R.id.attachVideo);
        sendImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,25);
            }

        });
        sendVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,30);
            }

        });

        //Handler for clicking on the inactive zone of the window
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //Close the window when clicked
                popupWindow.dismiss();
                return true;
            }
        });
    }
}