package com.hashcode.Flow.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.hashcode.Flow.R;
import com.hashcode.Flow.databinding.ActivityStoryBinding;

public class StoryActivity extends AppCompatActivity {
ActivityStoryBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}