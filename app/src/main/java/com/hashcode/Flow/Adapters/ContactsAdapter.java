package com.hashcode.Flow.Adapters;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.hashcode.Flow.Models.MediaModel;
import com.hashcode.Flow.R;

import java.util.ArrayList;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    ArrayList<MediaModel> contacts;
    Activity activity;
    SharedPreferences preferences;
    SharedPreferences.Editor prefsEditor;

    public ContactsAdapter(ArrayList<MediaModel> contacts, Activity activity) {
        this.contacts = contacts;
        this.activity = activity;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_layout, parent, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setIsRecyclable(true);
        Gson gson = new Gson();

        MediaModel contact = contacts.get(position);
        holder.contactName.setText(contact.getDisplayName());
        holder.contactNumber.setText(contact.getNumber());
        String key = "contact" + position;
        holder.checkBox.setChecked(contact.getChecked());
        String json = gson.toJson(contact);

        holder.checkBox.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (holder.checkBox.isChecked()) {
                    prefsEditor = preferences.edit();
                    contact.setChecked(false);
                    holder.checkBox.setChecked(false);
                    prefsEditor.remove(key);
                    prefsEditor.commit();
                    holder.checkBox.setVisibility(View.GONE);
                }
                return true;
            }
        });
        prefsEditor = preferences.edit();
        if (contact.getChecked()) {
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
                    contact.setChecked(false);
                    prefsEditor.remove(key.toString());
                    holder.checkBox.setVisibility(View.GONE);
                } else {
                    holder.checkBox.setChecked(true);
                    contact.setChecked(true);
                    prefsEditor.putString(key.toString(), json);
                    holder.checkBox.setVisibility(View.VISIBLE);
                }

                prefsEditor.commit();

            }
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView contactName, contactNumber;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.contactName);
            contactNumber = itemView.findViewById(R.id.contactNumber);
            checkBox = itemView.findViewById(R.id.selection);
        }
    }
}
