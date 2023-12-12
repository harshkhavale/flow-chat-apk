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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.hashcode.Flow.Models.MediaModel;
import com.hashcode.Flow.Adapters.PhotoAdapter;
import com.hashcode.Flow.R;

import java.util.ArrayList;


public class PhotoFragment extends Fragment {

    private static final int MY_READ_PERMISSION_CODE = 101;
    RecyclerView recyclerView;
    ArrayList<MediaModel> images = new ArrayList<>();
    PhotoAdapter photoAdapter;
    SharedPreferences preferences;

    public PhotoFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadImages();
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        recyclerView = view.findViewById(R.id.photoRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        photoAdapter = new PhotoAdapter(images, getActivity());

        recyclerView.setAdapter(photoAdapter);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS}, MY_READ_PERMISSION_CODE);
        } else {
            Log.e("DB", "PERMISSION GRANTED");
            loadImages();
        }

        return view;
    }

    public void loadImages() {
        Cursor imageCursor;
        Uri uri;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        String[] proj = {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME};
        imageCursor = getContext().getContentResolver().query(uri, null, null, null, orderBy + " DESC");
        if (imageCursor.getCount() > 0) {
            while (imageCursor.moveToNext()) {

                int imageIndex = imageCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
//                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                float size = imageCursor.getFloat(imageCursor.getColumnIndex(MediaStore.Images.Media.SIZE));
                String path = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String finalSize = checkSize(size);

                if (path != null) {
                    MediaModel image = new MediaModel();
                    image.setDisplayName(name);;
                    image.setSize(finalSize);
                    image.setPath(path);
                    images.add(image);

                }
            }


        }
        imageCursor.close();

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
                loadImages();
            } else {
                Toast.makeText(getContext(), "permission de-granted", Toast.LENGTH_SHORT).show();

            }
        }
    }

}