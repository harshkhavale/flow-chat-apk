package com.hashcode.Flow.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hashcode.Flow.Models.MediaModel;
import com.hashcode.Flow.R;

import java.util.ArrayList;

public class SummeryAdapter extends RecyclerView.Adapter<SummeryAdapter.ViewHolder> {
    Activity activity;
    ArrayList<MediaModel> mediaList = new ArrayList<>();

    public SummeryAdapter(Activity activity, ArrayList<MediaModel> mediaList) {
        this.activity = activity;
        this.mediaList = mediaList;
        notifyDataSetChanged();
    }

    public SummeryAdapter() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.summery_layout, parent, false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MediaModel media = mediaList.get(position);
        holder.name.setText(media.getDisplayName());
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
TextView name;
        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            name = itemView.findViewById(R.id.displayName);
        }
    }
}
