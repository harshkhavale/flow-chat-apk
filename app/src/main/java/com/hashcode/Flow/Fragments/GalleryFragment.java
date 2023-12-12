package com.hashcode.Flow.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.hashcode.Flow.Activities.ChatActivity;
import com.hashcode.Flow.Fragments.AudioFragment;
import com.hashcode.Flow.Fragments.ContactsFragment;
import com.hashcode.Flow.Fragments.DocxFragment;
import com.hashcode.Flow.Fragments.PhotoFragment;
import com.hashcode.Flow.Fragments.VideoFragment;
import com.hashcode.Flow.R;


public class GalleryFragment extends BottomSheetDialogFragment {
    SharedPreferences preferences;
    SharedPreferences.Editor prefsEditor;
    LinearLayout exploreBox,itemsBox;
    public GalleryFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fragment photoFrag = new PhotoFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.frame_container, photoFrag).commit();
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
//        prefsEditor = preferences.edit();
//        prefsEditor.clear();

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
//                setupFullHeight(bottomSheetDialog); //to open bottomSheet in full height
            }
        });
        return dialog;
    }


    private void setupFullHeight(BottomSheetDialog bottomSheetDialog) {
        FrameLayout bottomSheet = (FrameLayout) bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();

        int windowHeight = getWindowHeight();
        if (layoutParams != null) {
            layoutParams.height = windowHeight;
        }
        bottomSheet.setLayoutParams(layoutParams);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private int getWindowHeight() {
        // Calculate window height for fullscreen use
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        ImageView sendForeword = view.findViewById(R.id.sendForwordBtn);

        sendForeword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!preferences.getAll().isEmpty()) {
                    Intent intent = new Intent(getContext(), ChatActivity.class);
                    startActivity(intent);
                }

            }
        });
        view.findViewById(R.id.exploreContainer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(intent, 100);
                prefsEditor = preferences.edit();
                prefsEditor.clear();
                prefsEditor.commit();
            }
        });


        view.findViewById(R.id.images).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment photoFrag = new PhotoFragment();
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_container, photoFrag).commit();
                prefsEditor = preferences.edit();
                prefsEditor.clear();
                prefsEditor.commit();
            }
        });
        view.findViewById(R.id.videos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment videoFrag = new VideoFragment();
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_container, videoFrag).commit();
                prefsEditor = preferences.edit();
                prefsEditor.clear();
                prefsEditor.commit();
            }
        });
        view.findViewById(R.id.contacts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment contactFrag = new ContactsFragment();
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_container, contactFrag).commit();
                prefsEditor = preferences.edit();
                prefsEditor.clear();
                prefsEditor.commit();
            }
        });
        view.findViewById(R.id.audios).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment audioFrag = new AudioFragment();
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_container, audioFrag).commit();
                prefsEditor = preferences.edit();
                prefsEditor.clear();
                prefsEditor.commit();
            }
        });
        view.findViewById(R.id.documents).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment docxFrag = new DocxFragment();
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_container, docxFrag).commit();
                prefsEditor = preferences.edit();
                prefsEditor.clear();
                prefsEditor.commit();

            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (data != null) {
                if (data.getData() != null) {
                    Uri selectedContent = data.getData();
                    Toast.makeText(getContext(), selectedContent.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

//    public void uploadContent(String contentType,Intent data){
//        if(data !=null){
//            if(data.getData()!=null){
//
//                Uri selectedContent = data.getData();
//                Calendar calendar = Calendar.getInstance();
//                StorageReference reference = storage.getReference().child("chats").child(calendar.getTimeInMillis()+"");
//                reference.putFile(selectedContent).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                        if(task.isSuccessful()){
//                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                @Override
//                                public void onSuccess(Uri uri) {
//                                    String filePath = uri.toString();
//                                    Date date = new Date();
//                                    String msgText = binding.messageBox.getText().toString();
//                                    Message message = new Message(msgText,senderUid,date.getTime());
//                                    message.setMessage(contentType);
//                                    message.setImageUrl(filePath);
//                                    String randomKey = database.getReference().push().getKey();
//                                    message.setMessageId(randomKey);
//                                    HashMap<String,Object> lastMsgObj = new HashMap<>();
//                                    lastMsgObj.put("lastMessage",message.getMessage());
//                                    lastMsgObj.put("msgTime",date.getTime());
//                                    database.getReference().child("chats").child(SenderRoom).updateChildren(lastMsgObj);
//                                    database.getReference().child("chats").child(RecieverRoom).updateChildren(lastMsgObj);
//
//                                    database.getReference().child("chats").child(SenderRoom)
//                                            .child("messages")
//                                            .child(randomKey)
//                                            .setValue(message)
//                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                @Override
//                                                public void onSuccess(Void unused) {
//
//                                                    popupWindow.dismiss();
//                                                    dialog.dismiss();
//                                                    database.getReference().child("chats")
//                                                            .child(RecieverRoom)
//                                                            .child("messages")
//                                                            .child(randomKey)
//                                                            .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                                @Override
//                                                                public void onSuccess(Void unused) {
//
//                                                                }
//                                                            });
//                                                }
//                                            });
//
//                                }
//                            });
//
//                        }
//                    }
//                });
//            }
//        }
//
//    }

}