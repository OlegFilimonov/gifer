package com.olgefilimonov.gifer.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
import com.olgefilimonov.gifer.model.RatedGif;
import com.olgefilimonov.gifer.singleton.GiferApplication;
import io.objectbox.Box;
import java.util.List;

public class GifDetailActivity extends AppCompatActivity {
  public static final String URL_EXTRA = "URL";
  public static final String GIF_ID_EXTRA = "gifId";
  @BindView(R.id.exoplayer) SimpleExoPlayerView exoPlayerView;
  @BindView(R.id.gif_like) ImageView like;
  @BindView(R.id.gif_dislike) ImageView dislike;
  @BindView(R.id.gif_score) TextView score;

  private String url;
  private String gifId;
  private Box<RatedGif> gifsBox;
  private RatedGif ratedGif;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_gif_detail);
    ButterKnife.bind(this);

    gifsBox = GiferApplication.getInstance().getBoxStore().boxFor(RatedGif.class);

    // Get url from intent extras
    if (!getIntent().hasExtra(URL_EXTRA) || !getIntent().hasExtra(GIF_ID_EXTRA)) {
      // A bit of foolproofing
      throw new RuntimeException("You should pass a " + URL_EXTRA + " and " + GIF_ID_EXTRA + " extras to the activity for it to work properly");
    }

    url = getIntent().getStringExtra(URL_EXTRA);
    gifId = getIntent().getStringExtra(GIF_ID_EXTRA);

    // Create a default TrackSelector
    Handler mainHandler = new Handler();
    // Measures bandwidth during playback. Can be null if not required.
    DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
    TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
    TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
    // Create the player
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

    // Check the rating
    List<RatedGif> ratedGifList = gifsBox.find("gifId", gifId);
    if (ratedGifList.size() == 0) {
      // No rating found
      ratedGif = new RatedGif();
      ratedGif.setGifId(gifId);
    } else if (ratedGifList.size() == 1) {
      // Rating found
      ratedGif = ratedGifList.get(0);
    } else {
      throw new RuntimeException("Database error. gifId must be unique");
    }
    updateScore();
  }

  @OnClick(R.id.gif_like) void onGifLike() {
    ratedGif.setScore(ratedGif.getScore() + 1);
    updateScore();
  }

  @OnClick(R.id.gif_dislike) void onGifDislike() {
    ratedGif.setScore(ratedGif.getScore() - 1);
    updateScore();
  }

  private void updateScore() {
    int score = ratedGif.getScore();
    this.score.setText(String.valueOf(score));
    gifsBox.put(ratedGif);
  }

  @Override protected void onResume() {
    super.onResume();
  }
}
