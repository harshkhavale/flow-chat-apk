package com.hashcode.Flow.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.pgreze.reactions.ReactionPopup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.hashcode.Flow.Models.Message;
import com.hashcode.Flow.R;
import com.hashcode.Flow.databinding.RecievemsgBinding;
import com.hashcode.Flow.databinding.SendmsgBinding;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class MessageAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<Message> msgList;

    final int SENT = 1;
    final int RECIEVE = 2;
    String senderRoom;
    View reactionView;
    String recieverRoom;

    int[] reactions = new int[]{
            R.drawable.angry,
            R.drawable.cryingemoji,
            R.drawable.love,
            R.drawable.laughing,
            R.drawable.likeemoji,
            R.drawable.loveeyesemoji,
            R.drawable.otheremoji,
            R.drawable.delete

    };

    @Override
    public int getItemViewType(int position) {
        Message msg = msgList.get(position);
        if (FirebaseAuth.getInstance().getUid().equals(msg.getSenderId())) {
            return SENT;
        } else {
            return RECIEVE;
        }
    }

    public MessageAdapter(Context context, ArrayList<Message> msgList, String senderRoom, String recieverRoom) {
        this.context = context;
        this.senderRoom = senderRoom;
        this.recieverRoom = recieverRoom;
        this.msgList = msgList;
    }

    public MessageAdapter() {
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        reactionView = LayoutInflater.from(context).inflate(R.layout.reaction_popup, parent, false);
        if (viewType == SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.sendmsg, parent, false);
            return new SendViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.recievemsg, parent, false);
            return new RecieveViewHolder(view);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        Message currentMsg = msgList.get(position);
        int pos = currentMsg.getFeelings();
        holder.setIsRecyclable(true);



        if (holder.getClass() == SendViewHolder.class) {
            SendViewHolder viewHolder = (SendViewHolder) holder;
            viewHolder.binding.sendedMsg.setText(currentMsg.getMessage());
            Date date = new Date(currentMsg.getTimeStamp());
            DateFormat formatter = new SimpleDateFormat("HH:mm a");

            formatter.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
            String dateFormatted = formatter.format(date);

            viewHolder.binding.timeZone.setText(dateFormatted);

            if (currentMsg.getMessage().equals("photo")) {
                viewHolder.setIsRecyclable(false);
                viewHolder.binding.forword.setVisibility(View.VISIBLE);
                viewHolder.binding.image.setVisibility(View.VISIBLE);
                viewHolder.binding.sendedMsg.setVisibility(View.GONE);
//                Picasso.get().load(currentMsg.getImageUrl()).into(viewHolder.binding.image);
                Glide.with(context)
                        .load(currentMsg.getImageUrl())
                        .error(R.drawable.noconnection)
                        .into(viewHolder.binding.image);

            }
            if (currentMsg.getMessage().equals("video")){
                viewHolder.binding.forword.setVisibility(View.VISIBLE);
                viewHolder.setIsRecyclable(false);
                viewHolder.binding.play.setVisibility(View.VISIBLE);
                viewHolder.binding.image.setVisibility(View.VISIBLE);
                viewHolder.binding.sendedMsg.setVisibility(View.GONE);
                ImageView image = viewHolder.binding.image;
                VideoView video = viewHolder.binding.video;
                video.setVisibility(View.GONE);
                Glide.with(context)
                        .load(currentMsg.getImageUrl())
                        .error(R.drawable.noconnection)
                        .into(viewHolder.binding.image);

            }
            if (currentMsg.getFeelings() >= 0) {
                viewHolder.binding.feelings.setImageResource(reactions[(int) currentMsg.getFeelings()]);
                viewHolder.binding.feelings.setVisibility(View.VISIBLE);

            } else {
                viewHolder.binding.feelings.setVisibility(View.GONE);

            }
            viewHolder.binding.sendedMsg.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showPopupWindow(view, currentMsg);
                    return false;
                }
            });
            viewHolder.binding.image.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showPopupWindow(view, currentMsg);
                    return false;
                }
            });
            viewHolder.binding.video.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showPopupWindow(view, currentMsg);
                    return false;
                }
            });


        } else {
            RecieveViewHolder viewHolder = (RecieveViewHolder) holder;
            viewHolder.binding.recievedMsg.setText(currentMsg.getMessage());
            Date date = new Date(currentMsg.getTimeStamp());
            DateFormat formatter = new SimpleDateFormat("HH:mm a");
            formatter.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
            String dateFormatted = formatter.format(date);
            viewHolder.binding.timeZone.setText(dateFormatted);
            if (currentMsg.getMessage().equals("photo")){
                viewHolder.binding.forword.setVisibility(View.VISIBLE);
                viewHolder.setIsRecyclable(false);
                viewHolder.binding.image.setVisibility(View.VISIBLE);
                viewHolder.binding.recievedMsg.setVisibility(View.GONE);
//                Picasso.get().load(currentMsg.getImageUrl()).into(viewHolder.binding.image);
                Glide.with(context)
                        .load(currentMsg.getImageUrl())
                        .error(R.drawable.noconnection)
                        .placeholder(R.drawable.photo)
                        .into(viewHolder.binding.image);

            }
            if (currentMsg.getMessage().equals("video")){
                viewHolder.binding.forword.setVisibility(View.VISIBLE);
                viewHolder.setIsRecyclable(false);
                viewHolder.binding.image.setVisibility(View.VISIBLE);
                viewHolder.binding.recievedMsg.setVisibility(View.GONE);
                ImageView image = viewHolder.binding.image;
                VideoView video = viewHolder.binding.video;
                video.setVisibility(View.GONE);
                Glide.with(context)
                        .load(currentMsg.getImageUrl())
                        .error(R.drawable.noconnection)
                        .placeholder(R.drawable.photo)
                        .into(viewHolder.binding.image);

            }

            if (currentMsg.getFeelings() >= 0) {
                viewHolder.binding.feeling.setImageResource(reactions[(int) currentMsg.getFeelings()]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);

            } else {
                viewHolder.binding.feeling.setVisibility(View.GONE);

            }
            viewHolder.binding.recievedMsg.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showPopupWindow(view, currentMsg);
                    return false;
                }
            });
            viewHolder.binding.image.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showPopupWindow(view, currentMsg);
                    return false;
                }
            });
            viewHolder.binding.video.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showPopupWindow(view, currentMsg);
                    return false;
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    public class RecieveViewHolder extends RecyclerView.ViewHolder {
        RecievemsgBinding binding;

        public RecieveViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RecievemsgBinding.bind(itemView);

        }
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    public class SendViewHolder extends RecyclerView.ViewHolder {
        SendmsgBinding binding;

        public SendViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SendmsgBinding.bind(itemView);
        }
    }

    public void showPopupWindow(final View view, Message msg) {


        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);

        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        final PopupWindow popupWindow = new PopupWindow(reactionView, width, height, focusable);
//        Animation popup = AnimationUtils.loadAnimation(reactionView.getContext(),R.anim.popup_animation);
//        reactionView.startAnimation(popup);
        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);


        //Initialize the elements of our window, install the handler



        //Animation hover = AnimationUtils.loadAnimation(this, R.anim.hover);

        ImageView angry = reactionView.findViewById(R.id.angry);
        ImageView cry = reactionView.findViewById(R.id.crying);
        ImageView love = reactionView.findViewById(R.id.love);
        ImageView laugh = reactionView.findViewById(R.id.laugh);
        ImageView ok = reactionView.findViewById(R.id.like);
        ImageView excite = reactionView.findViewById(R.id.loveeyes);
        ImageView others = reactionView.findViewById(R.id.others);
        ImageView delete = reactionView.findViewById(R.id.delete);
        clickHandler(angry,msg,0,popupWindow);
        clickHandler(cry,msg,1,popupWindow);
        clickHandler(love,msg,2,popupWindow);
        clickHandler(laugh,msg,3,popupWindow);
        clickHandler(ok,msg,4,popupWindow);
        clickHandler(excite,msg,5,popupWindow);
        clickHandler(others,msg,6,popupWindow);
        clickHandler(delete,msg,-1,popupWindow);






        //Handler for clicking on the inactive zone of the window

        reactionView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //Close the window when clicked
                popupWindow.dismiss();
                return true;
            }
        });
    }
    public void clickHandler(ImageView img,Message message, int rec ,PopupWindow pw){
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message.setFeelings(rec);
                FirebaseDatabase.getInstance()
                        .getReference()
                        .child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .child(message.getMessageId())
                        .setValue(message);
                FirebaseDatabase.getInstance()
                        .getReference()
                        .child("chats")
                        .child(recieverRoom)
                        .child("messages")
                        .child(message.getMessageId())
                        .setValue(message);
                pw.dismiss();

            }
        });
    }

}
