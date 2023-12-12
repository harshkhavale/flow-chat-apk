package com.hashcode.Flow.Fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.hashcode.Flow.Adapters.DocxAdapter;
import com.hashcode.Flow.Models.MediaModel;
import com.hashcode.Flow.R;

import java.util.ArrayList;


public class DocxFragment extends Fragment {

    private static final int MY_READ_PERMISSION_CODE = 101;
    RecyclerView recyclerView;
    ArrayList<MediaModel> documents = new ArrayList<MediaModel>();
    DocxAdapter docxAdapter;
    SharedPreferences preferences;

    public DocxFragment() {
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
        View view = inflater.inflate(R.layout.fragment_docx, container, false);
        recyclerView = view.findViewById(R.id.docxRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        docxAdapter = new DocxAdapter(documents, getActivity());
        recyclerView.setAdapter(docxAdapter);
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS}, MY_READ_PERMISSION_CODE);
        } else {
            Log.e("DB", "PERMISSION GRANTED");
            getAllDocuments(requireContext());
        }
        return view;
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



    @RequiresApi(api = Build.VERSION_CODES.R)
    public void getAllDocuments(Context context) {
        ContentResolver contentResolver = context.getContentResolver();

        // Get all documents from MediaStore
        String[] projection = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.MIME_TYPE
        };

        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + " = " + MediaStore.Files.FileColumns.MEDIA_TYPE_DOCUMENT;

        Cursor cursor = contentResolver.query(MediaStore.Files.getContentUri("external"), projection, selection, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID);
            int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME);
            int typeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE);
            int pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.RELATIVE_PATH);


            do {
                // Get document details
                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                String type = cursor.getString(typeColumn);
                float size = cursor.getFloat(sizeColumn);
                String path = cursor.getString(pathColumn);

                // Differentiate between PDF, Word, PowerPoint, and Excel files
                String extension = MimeTypeMap.getFileExtensionFromUrl(name);
                String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                String finalSize = checkSize(size);
                MediaModel document = new MediaModel();
                document.setDisplayName(name);
                document.setPath(path);
                document.setSize(finalSize);
                if (mimeType != null) {
                    if (mimeType.equals("application/pdf")) {
                        document.setType("PDF");
                    } else if (mimeType.equals("application/msword") || mimeType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                        document.setType("MSWORD");
                    } else if (mimeType.equals("application/vnd.ms-powerpoint") || mimeType.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation")) {
                        document.setType("MSPP");
                    } else if (mimeType.equals("application/vnd.ms-excel") || mimeType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
                        document.setType("MSEXCEL");
                    } else {
                        document.setType("OTHER");
                    }
                } else {
                    document.setType("OTHER");
                }
            documents.add(document);
            } while (cursor.moveToNext());

            cursor.close();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_READ_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "permission granted", Toast.LENGTH_SHORT).show();
                getAllDocuments(requireContext());

            } else {
                Toast.makeText(getContext(), "permission de-granted", Toast.LENGTH_SHORT).show();

            }
        }
    }

}