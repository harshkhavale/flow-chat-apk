package com.hashcode.Flow.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hashcode.Flow.Activities.ChatActivity;
import com.hashcode.Flow.Models.User;
import com.hashcode.Flow.R;
import com.hashcode.Flow.databinding.ConversationRowBinding;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    ArrayList<User> list;
    Context context;

    public UserAdapter(Context context, ArrayList<User> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.conversation_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = list.get(position);
        String senderUid = FirebaseAuth.getInstance().getUid();
        User self = list.get(position);
        String senderRoom = senderUid+user.getuId();
        FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {String lastMsg = snapshot.child("lastMessage").getValue(String.class);
                    long time = snapshot.child("msgTime").getValue(long.class);
                    holder.binding.lastMsg.setText(lastMsg);
                    holder.binding.time.setText(DateFormat.getDateInstance().format(new Date(time)));

                }
                else{
                    holder.binding.lastMsg.setText("start conversation");
                    holder.binding.time.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.binding.personName.setText(user.getName());
        Glide.with(context).load(user.getProfilePic()).placeholder(R.drawable.user).into(holder.binding.userDp);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("name",user.getName());
                intent.putExtra("profileImg",user.getProfilePic());
                intent.putExtra("uid",user.getuId());
                intent.putExtra("token",user.getToken());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ConversationRowBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ConversationRowBinding.bind(itemView);
        }
    }
}
