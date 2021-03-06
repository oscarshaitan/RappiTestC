package com.allegorit.testrappi;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.util.List;

public class YoutubeVideoAdapter extends RecyclerView.Adapter<YoutubeVideoAdapter.VideoInfoHolder>{

    private Activity activity;
    private List<String> videoKeys;
    private final String KEY ="KEY";

    public YoutubeVideoAdapter(Activity activity, List<String> videoKeys) {
        this.activity = activity;
        this.videoKeys = videoKeys;
    }

    @NonNull
    @Override
    public YoutubeVideoAdapter.VideoInfoHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.youtube_player, viewGroup, false);
        return new VideoInfoHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final VideoInfoHolder videoInfoHolder, final int i) {

         videoInfoHolder.youTubeThumbnailView.initialize(KEY, new YouTubeThumbnailView.OnInitializedListener() {
             @Override
             public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, final YouTubeThumbnailLoader youTubeThumbnailLoader) {

                 youTubeThumbnailLoader.setVideo(videoKeys.get(i));
                 youTubeThumbnailLoader.setOnThumbnailLoadedListener(new YouTubeThumbnailLoader.OnThumbnailLoadedListener(){
                     @Override
                     public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {}

                     @Override
                     public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                         youTubeThumbnailView.setVisibility(View.VISIBLE);
                         videoInfoHolder.relativeLayoutOverYouTubeThumbnailView.setVisibility(View.VISIBLE);
                         youTubeThumbnailLoader.release();

                     }
                 });
             }

             @Override
             public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
                 //write something for failure
             }
         });

    }

    @Override
    public int getItemCount() {
        return videoKeys.size();
    }

    public class VideoInfoHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private RelativeLayout relativeLayoutOverYouTubeThumbnailView;
        YouTubeThumbnailView youTubeThumbnailView;
        private ImageView playButton;

        public VideoInfoHolder(View itemView) {
            super(itemView);
            playButton=(ImageView)itemView.findViewById(R.id.btnYoutube_player);
            playButton.setOnClickListener(this);
            relativeLayoutOverYouTubeThumbnailView = (RelativeLayout) itemView.findViewById(R.id.relativeLayout_over_youtube_thumbnail);
            youTubeThumbnailView = (YouTubeThumbnailView) itemView.findViewById(R.id.youtube_thumbnail);
        }

        @Override
        public void onClick(View v) {

            Intent intent = YouTubeStandalonePlayer.createVideoIntent(activity, KEY, videoKeys.get(getLayoutPosition()));
            activity.startActivity(intent);
        }
    }
}
