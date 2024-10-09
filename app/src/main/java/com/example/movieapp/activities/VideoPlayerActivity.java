package com.example.movieapp.activities;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.movieapp.R;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;

public class VideoPlayerActivity extends AppCompatActivity {

    private ExoPlayer player;
    private StyledPlayerView playerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.activity_video_player);
        playerView = findViewById(R.id.player_view);

        String urlFromIntent = getIntent().getStringExtra("VIDEO_URL");
        String videoUrl = extractVideoUrl(urlFromIntent);

        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        // Thiết lập thuộc tính âm thanh
        player.setAudioAttributes(new AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                .build(), true);

        if (videoUrl != null) {
//            video định dang m3u8
            DefaultHttpDataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory();
            HlsMediaSource hlsMediaSource = new HlsMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(Uri.parse(videoUrl)));


            player.setMediaSource(hlsMediaSource);
            player.prepare();
            player.play();


            player.addListener(new com.google.android.exoplayer2.Player.Listener() {
                @Override
                public void onPlaybackStateChanged(int playbackState) {
                    if (playbackState == com.google.android.exoplayer2.Player.STATE_ENDED) {
                        finish();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Video URL is null", Toast.LENGTH_SHORT).show();
        }
    }


    private String extractVideoUrl(String url) {
        if (url != null && url.startsWith("https://player.phimapi.com/player/?url=")) {
            // Tách phần sau "url=" ra để lấy URL video
            return url.substring(url.indexOf("url=") + 4); // +4 để bỏ qua "url="
        }
        return null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        player.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
