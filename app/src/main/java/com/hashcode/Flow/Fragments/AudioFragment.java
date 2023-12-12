package com.hashcode.Flow.Fragments;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hashcode.Flow.Adapters.AudioAdapter;
import com.hashcode.Flow.Models.MediaModel;
import com.hashcode.Flow.R;


import java.util.ArrayList;


public class AudioFragment extends Fragment {

    private static final int MY_READ_PERMISSION_CODE = 101;
    RecyclerView recyclerView;
    ArrayList<MediaModel> audios = new ArrayList<>();
    AudioAdapter audioAdapter;
    SharedPreferences preferences;

    public AudioFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_audio, container, false);
        recyclerView = view.findViewById(R.id.audioRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        audioAdapter = new AudioAdapter(audios, getActivity());
        recyclerView.setAdapter(audioAdapter);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS}, MY_READ_PERMISSION_CODE);
        } else {
            Log.e("DB", "PERMISSION GRANTED");
            loadAudios();
        }
        return view;
    }

    private void loadAudios() {
        Cursor audioCursor;
        Uri uri;
        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " !=0 AND title != ''";
        String sort = MediaStore.Audio.Media.DATE_ADDED + " ASC";
        String[] proj = {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME};
        audioCursor = getContext().getContentResolver().query(uri, null, null, null, sort);
        if (audioCursor.getCount() > 0) {
            while (audioCursor.moveToNext()) {

                int audioIndex = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
//                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = audioCursor.getString(audioCursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                long len = audioCursor.getLong(audioCursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                float size = audioCursor.getFloat(audioCursor.getColumnIndex(MediaStore.Audio.Media.SIZE));

                long minutes = (len / 1000) / 60;

                // formula for conversion for
                // milliseconds to seconds
                long seconds = (len / 1000) % 60;
                String dur = Long.toString(minutes) + ":" + Long.toString(seconds);
                String finalSize = checkSize(size);

                String path = audioCursor.getString((audioCursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                if (path != null) {
                    MediaModel audio = new MediaModel();
                    audio.setDisplayName(name);
                    audio.setLength(dur);
                    audio.setPath(path);
                    audio.setSize(finalSize);
                    audios.add(audio);

                }
            }


        }
        audioCursor.close();
    }
    public String checkSize(float size) {
        String finalSize = size+"";
        float tempSize = size;
        if (size >= 1024) {
            tempSize = tempSize / 1024;
            finalSize = String.format("%.02f", tempSize).concat(" KB");
            if (tempSize >= 1024) {
                tempSize = tempSize / 1024;
                finalSize = String.format("%.02f", tempSize).concat(" MB");
                if (tempSize >= 1024) {
                    tempSize = tempSize / 1024;
                    finalSize = String.format("%.02f", tempSize).concat(" GB");

                }
            }

        } else {
            finalSize = String.format("%.02f", size).concat(" KB");
        }
        return finalSize;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_READ_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "permission granted", Toast.LENGTH_SHORT).show();
                loadAudios();
            } else {
                Toast.makeText(getContext(), "permission de-granted", Toast.LENGTH_SHORT).show();

            }
        }
    }

}