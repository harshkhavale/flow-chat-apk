package com.hashcode.Flow.Adapters;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hashcode.Flow.Models.MediaModel;
import com.hashcode.Flow.R;

import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    ArrayList<MediaModel> videos;
    Activity activity;

    SharedPreferences preferences;
    SharedPreferences.Editor prefsEditor;

    public VideoAdapter(ArrayList<MediaModel> videos, Activity activity) {
        this.videos = videos;
        this.activity = activity;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.video_layout, parent, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setIsRecyclable(true);
        Gson gson = new Gson();
        MediaModel video = videos.get(position);
        String key = "video" + position;
        Glide.with(activity).load(video.getPath()).into(holder.video);
        holder.checkBox.setChecked(video.getChecked());
        String json = gson.toJson(video);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showPopupWindow(view,video.getPath(),video.getDisplayName(),video.getSize());
                return true;
            }
        });
        holder.checkBox.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (holder.checkBox.isChecked()) {
                    prefsEditor = preferences.edit();
                    video.setChecked(false);
                    holder.checkBox.setChecked(false);
                    prefsEditor.remove(key);
                    prefsEditor.commit();
                    holder.checkBox.setVisibility(View.GONE);
                }
                return true;
            }
        });
        prefsEditor = preferences.edit();
        if (video.getChecked()) {
            prefsEditor.putString(key, json);
            holder.checkBox.setVisibility(View.VISIBLE);

        } else {
            prefsEditor.remove(key);
            holder.checkBox.setVisibility(View.GONE);

        }
        prefsEditor.commit();
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                holder.setIsRecyclable(false);
                prefsEditor = preferences.edit();
                if (holder.checkBox.isChecked()) {
                    holder.checkBox.setChecked(false);
                    video.setChecked(false);
                    prefsEditor.remove(key.toString());
                    holder.checkBox.setVisibility(View.GONE);
                } else {
                    holder.checkBox.setChecked(true);
                    video.setChecked(true);
                    prefsEditor.putString(key.toString(), json);
                    holder.checkBox.setVisibility(View.VISIBLE);
                }

                prefsEditor.commit();

            }
        });

    }


    @Override
    public int getItemCount() {
        return videos.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView video;
        CheckBox checkBox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            video = itemView.findViewById(R.id.video);
            checkBox = itemView.findViewById(R.id.videoSelection);

        }
    }

    public void showPopupWindow(final View view,String path,String name,String size) {

        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.media_view, null);

        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        TextView mediaName =  popupView.findViewById(R.id.mediaName);
        TextView mediaSize =  popupView.findViewById(R.id.mediaSize);
        ImageView imageView = popupView.findViewById(R.id.ImageView);
        VideoView videoView = popupView.findViewById(R.id.VideoView);
        LinearLayout audioView = popupView.findViewById(R.id.AudioView);
        videoView.setVisibility(View.VISIBLE);
        audioView.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);
        videoView.setVideoPath(path);
        videoView.start();
        videoView.canPause();
        videoView.animate().alpha(1);
        mediaName.setText(name);
        mediaSize.setText(size);

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true ;

        //Create a window with our parameters
        PopupWindow popupWindow = new PopupWindow(popupView, width, height,true);
        popupWindow.setOutsideTouchable(false);

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        popupWindow.setFocusable(true);
        //Initialize the elements of our window, install the handler
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                popupWindow.dismiss();
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
