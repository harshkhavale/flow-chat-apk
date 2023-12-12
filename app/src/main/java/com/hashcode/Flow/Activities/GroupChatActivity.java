package com.hashcode.Flow.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.PopupWindow;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.hashcode.Flow.Adapters.GroupMessageAdapter;
import com.hashcode.Flow.Adapters.MessageAdapter;
import com.hashcode.Flow.Models.Message;
import com.hashcode.Flow.databinding.ActivityGroupChatBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class GroupChatActivity extends AppCompatActivity {
    ActivityGroupChatBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    GroupMessageAdapter groupMessageAdapter;
    ArrayList<Message> msgList;
    String SenderRoom, RecieverRoom;
    FirebaseStorage storage;
    String senderUid;
    String recieverUid;
    PopupWindow popupWindow;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setContentView(binding.getRoot());

        msgList = new ArrayList<>();
        groupMessageAdapter = new GroupMessageAdapter(this,msgList);
        binding.chatRecyclerView.setAdapter(groupMessageAdapter);
        binding.chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        senderUid = auth.getUid();

        binding.sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();
                String msgText = binding.messageBox.getText().toString();
                Message message = new Message(msgText,senderUid,date.getTime());
                binding.messageBox.setText("");
                database.getReference().child("public").push().setValue(msgText);





            }
        });
        database.getReference().child("public")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        msgList.clear();
                        for(DataSnapshot snapshot1:snapshot.getChildren())
                        {
                            Message message = snapshot1.getValue(Message.class);
                            message.setMessageId(snapshot1.getKey());
                            msgList.add(message);
                        }
                        groupMessageAdapter.notifyDataSetChanged();                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}