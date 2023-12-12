package com.hashcode.Flow.Adapters;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.hashcode.Flow.Models.MediaModel;
import com.hashcode.Flow.R;

import java.io.IOException;
import java.util.ArrayList;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.ViewHolder> {

    ArrayList<MediaModel> audios;
    Activity activity;
    SharedPreferences preferences;
    SharedPreferences.Editor prefsEditor;
    PopupWindow popupWindow;
    public AudioAdapter(ArrayList<MediaModel> audios, Activity activity) {
        this.audios = audios;
        this.activity = activity;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.audio_layout, parent, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());



        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setIsRecyclable(true);
        Gson gson = new Gson();
        MediaModel audio = audios.get(position);
        holder.audioName.setText(audio.getDisplayName());
        holder.audioLength.setText(audio.getLength());
        String key = "audio" + position;
        holder.checkBox.setChecked(audio.getChecked());
        String json = gson.toJson(audio);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                try {
                    showPopupWindow(view, audio.getPath(), audio.getDisplayName(), audio.getSize());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return true;
            }
        });
        holder.checkBox.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (holder.checkBox.isChecked()) {
                    prefsEditor = preferences.edit();
                    audio.setChecked(false);
                    holder.checkBox.setChecked(false);
                    prefsEditor.remove(key);
                    prefsEditor.commit();
                    holder.checkBox.setVisibility(View.GONE);
                }
                return true;
            }
        });
        prefsEditor = preferences.edit();
        if (audio.getChecked()) {
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

                String json = gson.toJson(audio);
                if (holder.checkBox.isChecked()) {
                    holder.checkBox.setChecked(false);
                    audio.setChecked(false);

                    prefsEditor.remove(key.toString());
                    holder.checkBox.setVisibility(View.GONE);
                } else {
                    holder.checkBox.setChecked(true);
                    audio.setChecked(true);

                    prefsEditor.putString(key.toString(), json);
                    holder.checkBox.setVisibility(View.VISIBLE);
                }

                prefsEditor.commit();

            }
        });


    }

    @Override
    public int getItemCount() {
        return audios.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView audioName, audioLength;
        CheckBox checkBox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            audioName = itemView.findViewById(R.id.audioName);
            audioLength = itemView.findViewById(R.id.audioLength);
            checkBox = itemView.findViewById(R.id.selection);

        }
    }

    public void showPopupWindow(final View view,String path,String name,String size) throws IOException {

        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.media_view, null);

        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        TextView mediaName = popupView.findViewById(R.id.mediaName);
        TextView mediaSize = popupView.findViewById(R.id.mediaSize);
        ImageView imageView = popupView.findViewById(R.id.ImageView);
        VideoView videoView = popupView.findViewById(R.id.VideoView);
        LinearLayout audioView = popupView.findViewById(R.id.AudioView);
        videoView.setVisibility(View.GONE);
        audioView.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.GONE);
        videoView.setVideoPath(path);
        Handler handler = new Handler();
        mediaName.setText(name);
        mediaSize.setText(size);
        MediaPlayer player = new MediaPlayer();
        SeekBar seekBar = popupView.findViewById(R.id.seekBar);

        try {
            player.setDataSource(path);
            player.prepare();
        } catch (Exception e) {
            System.out.println("Exception of type : " + e.toString());
            e.printStackTrace();
        }
        seekBar.setMax(player.getDuration());

        player.start();

        seekBar.setProgress(player.getCurrentPosition());
        int mediaPos = player.getCurrentPosition();
        int mediaMax = player.getDuration();

        seekBar.setMax(mediaMax/1000); // Set the Maximum range of the
        seekBar.setProgress(mediaPos);// set current progress to song's


        Handler mHandler = new Handler();
//Make sure you update Seekbar on UI thread
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if(player != null){
                    int mCurrentPosition = player.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                }
                mHandler.postDelayed(this, 1000);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                               @Override
                                               public void onProgressChanged(SeekBar seekBar, int progress, boolean user) {
                                                   if(player != null && user){
                                                       player.seekTo(progress * 1000);
                                                   }


                                               }

                                               @Override
                                               public void onStartTrackingTouch(SeekBar seekBar) {

                                               }

                                               @Override
                                               public void onStopTrackingTouch(SeekBar seekBar) {

                                               }
                                           });

            //Make Inactive Items Outside Of PopupWindow
            boolean focusable = true;

            //Create a window with our parameters
            popupWindow = new PopupWindow(popupView, width, height, true);
        popupWindow.setOutsideTouchable(false);

            //Set the location of the window on the screen
        popupWindow.showAtLocation(view,Gravity.CENTER,0,0);
        popupWindow.setFocusable(true);
            //Initialize the elements of our window, install the handler
       player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
           @Override
           public void onCompletion(MediaPlayer mediaPlayer) {
               popupWindow.dismiss();
           }
       });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                player.stop();
            }
        });
            //Handler for clicking on the inactive zone of the window
        popupView.setOnTouchListener(new View.OnTouchListener()

            {
                @Override
                public boolean onTouch (View v, MotionEvent event){
                if(player.isPlaying()){
                    player.stop();
                }
                //Close the window when clicked
                popupWindow.dismiss();
                return true;
            }
            });
        }

    }
