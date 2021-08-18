package com.bicrowny.exoplayervs;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bicrowny.exoplayervs.databinding.ActivityMainBinding;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

public class MainActivity extends AppCompatActivity {

    private SimpleExoPlayer mp4Player;
    private boolean mp4PlayWhenReady = true;
    private int mp4CurrentWindow = 0;
    private long mp4PlaybackPosition = 0;

    private SimpleExoPlayer dashPlayer;
    private boolean dashPlayWhenReady = true;
    private int dashCurrentWindow = 0;
    private long dashPlaybackPosition = 0;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    public void onStart() {
        super.onStart();
        initializePlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSystemUi();
        if (mp4Player == null) initializePlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
    }

    private void initializePlayer() {
        if (mp4Player == null) mp4Player = new SimpleExoPlayer.Builder(this).build();
        binding.mp4PlayerPv.setPlayer(mp4Player);

        Uri mp4Uri = Uri.parse(getString(R.string.media_url_mp4));
        MediaSource mp4MediaSource = mp4BuildMediaSource(mp4Uri);

        mp4Player.setPlayWhenReady(mp4PlayWhenReady);
        mp4Player.seekTo(mp4CurrentWindow, mp4PlaybackPosition);
        mp4Player.prepare(mp4MediaSource, false, false);

        if (dashPlayer == null) {
            DefaultTrackSelector trackSelector = new DefaultTrackSelector(this);
            trackSelector.setParameters(trackSelector.buildUponParameters().setMaxVideoSizeSd());
            dashPlayer = new SimpleExoPlayer.Builder(this).setTrackSelector(trackSelector).build();
        }

        binding.dashPlayerPv.setPlayer(dashPlayer);
        Uri dashUri = Uri.parse(getString(R.string.media_url_dash));
        MediaSource dashMediaSource = dashBuildMediaSource(dashUri);

        dashPlayer.setPlayWhenReady(dashPlayWhenReady);
        dashPlayer.seekTo(dashCurrentWindow, dashPlaybackPosition);
        dashPlayer.prepare(dashMediaSource, false, false);
    }

    private MediaSource mp4BuildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, "exoplayer-codelab");
        return new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
    }

    private MediaSource dashBuildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, "exoplayer-codelab");
        DashMediaSource.Factory mediaSourceFactory = new DashMediaSource.Factory(dataSourceFactory);
        return mediaSourceFactory.createMediaSource(uri);
    }

    private void releasePlayer() {
        if (mp4Player != null) {
            mp4PlaybackPosition = mp4Player.getCurrentPosition();
            mp4CurrentWindow = mp4Player.getCurrentWindowIndex();
            mp4PlayWhenReady = mp4Player.getPlayWhenReady();
            mp4Player.release();
            mp4Player = null;
        }

        if (dashPlayer != null) {
            dashPlaybackPosition = dashPlayer.getCurrentPosition();
            dashCurrentWindow = dashPlayer.getCurrentWindowIndex();
            dashPlayWhenReady = dashPlayer.getPlayWhenReady();
            dashPlayer.release();
            dashPlayer = null;
        }
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        binding.mp4PlayerPv.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
}