package com.olgefilimonov.gifer.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.olgefilimonov.gifer.R;

public class GifDetailActivity extends AppCompatActivity {
  public static final String URL_EXTRA = "URL";
  @BindView(R.id.exoplayer) SimpleExoPlayerView exoPlayerView;
  private String url;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_gif_detail);
    ButterKnife.bind(this);

    // Get url from intent extras
    if (!getIntent().hasExtra(URL_EXTRA)) {
      // A bit of foolproofing
      throw new RuntimeException("You should pass a " + URL_EXTRA + " extra to the activity for it to work properly");
    }
    url = getIntent().getStringExtra(URL_EXTRA);

    // 1. Create a default TrackSelector
    Handler mainHandler = new Handler();
    // Measures bandwidth during playback. Can be null if not required.
    DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
    TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
    TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
    // 2. Create the player
    SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

    // Bind the player to the view.
    exoPlayerView.setPlayer(player);

    // Produces DataSource instances through which media data is loaded.
    DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "yourApplicationName"), bandwidthMeter);
    // Produces Extractor instances for parsing the media data.
    ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
    // This is the MediaSource representing the media to be played.
    MediaSource videoSource = new ExtractorMediaSource(Uri.parse(url), dataSourceFactory, extractorsFactory, null, null);
    // Prepare the player with the source.
    player.prepare(videoSource);
  }

  @Override protected void onResume() {
    super.onResume();
  }
}
