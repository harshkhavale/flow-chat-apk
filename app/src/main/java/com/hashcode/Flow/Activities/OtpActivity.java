package com.hashcode.Flow.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hashcode.Flow.databinding.ActivityOtpBinding;

import java.util.concurrent.TimeUnit;

import in.aabhasjindal.otptextview.OTPListener;

public class OtpActivity extends AppCompatActivity {
    FirebaseAuth auth;
    String verificationId;
    ProgressDialog progressDialog;

    ActivityOtpBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(OtpActivity.this);
        progressDialog.setMessage("sending otp");
        progressDialog.show();

        String number =  getIntent().getStringExtra("number");
        binding.numberLbl.setText("Verify "+ number);
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth).setPhoneNumber(number).setTimeout(60L, TimeUnit.SECONDS).setActivity(OtpActivity.this).setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

            }

            @Override
            public void onCodeSent(@NonNull String verifyId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verifyId, forceResendingToken);
                progressDialog.dismiss();
                verificationId = verifyId;

            }
        }).build();
        PhoneAuthProvider.verifyPhoneNumber(options);


        binding.otpView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {

            }

            @Override
            public void onOTPComplete(String otp) {

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,otp);
            auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        final int[] a = {0};
                        FirebaseDatabase.getInstance()
                                .getReference()
                                .child("Users").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        for(DataSnapshot data: snapshot.getChildren()){
                                            if (data.child("phoneNumber").getValue(String.class).equals(number)) {
                                                //do ur stuff
                                                Toast.makeText(OtpActivity.this, "wellcome back", Toast.LENGTH_SHORT).show();
                                                Intent intent2 = new Intent(OtpActivity.this,MainActivity.class);
                                                startActivity(intent2);
                                                a[0] = 1;
                                                finish();
                                            }

                                        }

                                    }



                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }


                                });
                        if(a[0]==0){
                            //do something if not exists
                            Toast.makeText(OtpActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(OtpActivity.this,SetUserProfileActivity.class);
                            startActivity(intent);
                            finish();

                        }



                    }
                    else{
                        Toast.makeText(OtpActivity.this, "Failed", Toast.LENGTH_SHORT).show();

                    }
                }
            });
            }
        });


    }
}