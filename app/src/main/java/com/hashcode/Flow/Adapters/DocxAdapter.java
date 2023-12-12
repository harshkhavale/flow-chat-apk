package com.hashcode.Flow.Adapters;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hashcode.Flow.Models.MediaModel;
import com.hashcode.Flow.R;

import java.util.ArrayList;

public class DocxAdapter extends RecyclerView.Adapter<DocxAdapter.ViewHolder> {
    ArrayList<MediaModel> documents;
    Activity activity;
    SharedPreferences preferences;
    SharedPreferences.Editor prefsEditor;

    public DocxAdapter(ArrayList<MediaModel> documents, Activity activity) {
        this.documents = documents;
        this.activity = activity;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.docx_layout, parent, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Gson gson = new Gson();
        holder.setIsRecyclable(true);

        MediaModel document = documents.get(position);
        holder.docName.setText(document.getType());
        holder.docSize.setText((document.getSize()));
        if(document.getType().equals("PDF")){
            Glide.with(holder.itemView).load(R.drawable.pdf_logo).into(holder.docImage);
        }
        String key = "document" + position;
        holder.checkBox.setChecked(document.getChecked());
        String json = gson.toJson(document);
        holder.checkBox.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (holder.checkBox.isChecked()) {
                    prefsEditor = preferences.edit();
                    document.setChecked(false);
                    holder.checkBox.setChecked(false);
                    prefsEditor.remove(key);
                    prefsEditor.commit();
                    holder.checkBox.setVisibility(View.GONE);
                }
                return true;
            }
        });
        prefsEditor = preferences.edit();
        if (document.getChecked()) {
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
                    document.setChecked(false);
                    prefsEditor.remove(key.toString());
                    holder.checkBox.setVisibility(View.GONE);
                } else {
                    holder.checkBox.setChecked(true);
                    document.setChecked(true);

                    prefsEditor.putString(key.toString(), json);
                    holder.checkBox.setVisibility(View.VISIBLE);
                }

                prefsEditor.commit();

            }
        });
    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView docName, docSize;
        CheckBox checkBox;
        ImageView docImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            docName = itemView.findViewById(R.id.docName);
            docSize = itemView.findViewById(R.id.docSize);
            checkBox = itemView.findViewById(R.id.selection);
            docImage = itemView.findViewById(R.id.docImage);
        }
    }
}
