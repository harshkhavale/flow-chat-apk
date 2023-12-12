package com.hashcode.Flow.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.hashcode.Flow.Models.Message;
import com.hashcode.Flow.R;
import com.hashcode.Flow.databinding.RecievemsgBinding;
import com.hashcode.Flow.databinding.SendmsgBinding;

import java.util.ArrayList;

public class GroupMessageAdapter extends RecyclerView.Adapter{
    Context context;
    ArrayList<Message> msgList;
    final int SENT = 1;
    final int RECIEVE = 2;

    int[] reactions = new int[]{
            R.drawable.like,
            R.drawable.love,
            R.drawable.laughing,
            R.drawable.wow,
            R.drawable.sad,
            R.drawable.angry
    };

    @Override
    public int getItemViewType(int position) {
        Message msg = msgList.get(position);
        if(FirebaseAuth.getInstance().getUid().equals(msg.getSenderId()))
        {
            return SENT;
        }
        else{
            return RECIEVE;
        }
    }

    public GroupMessageAdapter(Context context, ArrayList<Message> msgList) {
        this.context = context;

        this.msgList = msgList;
    }

    public GroupMessageAdapter() {
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == SENT){
            View view = LayoutInflater.from(context).inflate(R.layout.sendmsg,parent,false);
            return new SendViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.recievemsg,parent,false);
            return new RecieveViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    Message msg = msgList.get(position);
        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();
        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {
            if(holder.getClass()==SendViewHolder.class){
                SendViewHolder viewHolder = (SendViewHolder)holder;
                viewHolder.binding.feelings.setImageResource(reactions[pos]);

                viewHolder.binding.feelings.setVisibility(View.VISIBLE);
            }
            else{
                RecieveViewHolder viewHolder = (RecieveViewHolder) holder;
                viewHolder.binding.feeling.setImageResource(reactions[pos]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);


            }
            msg.setFeelings(pos);
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("public")
                    .setValue(msg);


            return true; // true is closing popup, false is requesting a new selection
        });
    if(holder.getClass() == SendViewHolder.class)
    {
        SendViewHolder viewHolder = (SendViewHolder)holder;
        viewHolder.binding.sendedMsg.setText(msg.getMessage());
        if(msg.getMessage().equals("photo")){
            viewHolder.binding.image.setVisibility(View.VISIBLE);
            viewHolder.binding.sendedMsg.setVisibility(View.GONE);
            Glide.with(context).load(msg.getImageUrl()).placeholder(R.drawable.image).into(viewHolder.binding.image);

        }
        if(msg.getFeelings()>=0){
            viewHolder.binding.feelings.setImageResource(reactions[(int)msg.getFeelings()]);
            viewHolder.binding.feelings.setVisibility(View.VISIBLE);

        }
        else{
            viewHolder.binding.feelings.setVisibility(View.GONE);

        }
        viewHolder.binding.sendedMsg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popup.onTouch(v,event);
                return false;
            }
        });

        viewHolder.binding.image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popup.onTouch(v,event);
                return false;
            }
        });
    }
    else{
        RecieveViewHolder viewHolder = (RecieveViewHolder) holder;
        viewHolder.binding.recievedMsg.setText(msg.getMessage());
        if(msg.getMessage().equals("photo")){
            viewHolder.binding.image.setVisibility(View.VISIBLE);
            viewHolder.binding.recievedMsg.setVisibility(View.GONE);
            Glide.with(context).load(msg.getImageUrl()).placeholder(R.drawable.image).into(viewHolder.binding.image);

        }
        if(msg.getFeelings()>=0){
            viewHolder.binding.feeling.setImageResource(reactions[(int)msg.getFeelings()]);
            viewHolder.binding.feeling.setVisibility(View.VISIBLE);

        }
        else{
            viewHolder.binding.feeling.setVisibility(View.GONE);

        }
        viewHolder.binding.recievedMsg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popup.onTouch(v,event);
                return false;
            }
        });
        viewHolder.binding.image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popup.onTouch(v,event);
                return false;
            }
        });
    }
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    public class RecieveViewHolder extends RecyclerView.ViewHolder{
        RecievemsgBinding binding;
        public RecieveViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RecievemsgBinding.bind(itemView);

        }
    }
    public class SendViewHolder extends RecyclerView.ViewHolder{
        SendmsgBinding binding;
        public SendViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SendmsgBinding.bind(itemView);
        }
    }
}
