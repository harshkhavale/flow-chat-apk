package com.hashcode.Flow.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hashcode.Flow.Activities.MainActivity;
import com.hashcode.Flow.Models.Story;
import com.hashcode.Flow.Models.UserStories;
import com.hashcode.Flow.R;
import com.hashcode.Flow.databinding.StoriesViewBinding;

import java.util.ArrayList;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class StoriesAdapter extends RecyclerView.Adapter<StoriesAdapter.viewHolder> {
    Context context;
    ArrayList<UserStories> stories;
    StoriesViewBinding binding;

    public class viewHolder extends RecyclerView.ViewHolder {

        public viewHolder(@NonNull View itemView) {

            super(itemView);
            binding = StoriesViewBinding.bind(itemView);

        }
    }

    public StoriesAdapter(Context context, ArrayList<UserStories> stories) {
        this.context = context;
        this.stories = stories;
    }

    public StoriesAdapter() {
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.stories_view, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        UserStories userStories = stories.get(position);
        Story lastStory = userStories.getStories().get(userStories.getStories().size() - 1);
        Glide.with(context).load(lastStory.getImgUrl()).into(binding.storyImg);
        binding.circularStatusView.setPortionsCount(userStories.getStories().size());
        binding.circularStatusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<MyStory> myStories = new ArrayList<>();
                for (Story story : userStories.getStories()) {
                    myStories.add(new MyStory(story.getImgUrl()));
                }
                new StoryView.Builder(((MainActivity) context).getSupportFragmentManager())
                        .setStoriesList(myStories) // Required
                        .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                        .setTitleText(userStories.getName()) // Default is Hidden
                        .setSubtitleText("") // Default is Hidden
                        .setTitleLogoUrl(userStories.getProfileImg()) // Default is Hidden
                        .setStoryClickListeners(new StoryClickListeners() {
                            @Override
                            public void onDescriptionClickListener(int position) {
                                //your action
                            }

                            @Override
                            public void onTitleIconClickListener(int position) {
                                //your action
                            }
                        }) // Optional Listeners
                        .build() // Must be called before calling show method
                        .show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }


}
