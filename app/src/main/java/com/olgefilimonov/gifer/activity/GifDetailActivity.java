package com.olgefilimonov.gifer.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
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
import com.olgefilimonov.gifer.contract.GifDetailContract;
import com.olgefilimonov.gifer.presenter.GifDetailPresenter;

/**
 * @author Oleg Filimonov
 */
public class GifDetailActivity extends AppCompatActivity implements GifDetailContract.View {

  public static final String URL_EXTRA = "URL";
  public static final String GIF_ID_EXTRA = "gifId";

  @BindView(R.id.exoplayer) SimpleExoPlayerView exoPlayerView;
  @BindView(R.id.gif_like) ImageView like;
  @BindView(R.id.gif_dislike) ImageView dislike;
  @BindView(R.id.gif_score) TextView score;

  private String url;
  private String gifId;

  private GifDetailContract.Presenter presenter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_gif_detail);
    ButterKnife.bind(this);

    // Presenter
    new GifDetailPresenter(this);
    // Setup
    setupExtras();
    setupPlayer();

    presenter.updateGifRating(gifId);
  }

  private void setupPlayer() {
    // Measures bandwidth during playback. Can be null if not required
    DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
    TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
    TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
    // Create the player
    final SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

    // Bind the player to the view
    exoPlayerView.setPlayer(player);

    // Produces DataSource instances through which media data is loaded
    DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "yourApplicationName"), bandwidthMeter);
    // Produces Extractor instances for parsing the media data
    ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
    // This is the MediaSource representing the media to be played
    MediaSource videoSource = new ExtractorMediaSource(Uri.parse(url), dataSourceFactory, extractorsFactory, null, null);
    // Loops the video indefinitely
    LoopingMediaSource loopingSource = new LoopingMediaSource(videoSource);
    // Prepare the player with the source
    player.prepare(loopingSource);
  }

  private void setupExtras() {
    if (!getIntent().hasExtra(URL_EXTRA) || !getIntent().hasExtra(GIF_ID_EXTRA)) {
      // A bit of foolproofing
      throw new RuntimeException("You should pass a " + URL_EXTRA + " and " + GIF_ID_EXTRA + " extras to the activity for it to work properly");
    }

    url = getIntent().getStringExtra(URL_EXTRA);
    gifId = getIntent().getStringExtra(GIF_ID_EXTRA);
  }

  @Override public void onBackPressed() {
    Intent intent = new Intent();
    intent.putExtras(getIntent().getExtras());
    setResult(RESULT_OK, intent);
    finish();
  }

  @OnClick(R.id.gif_like) void onGifLike() {
    presenter.rateGif(gifId, +1);
  }

  @OnClick(R.id.gif_dislike) void onGifDislike() {
    presenter.rateGif(gifId, -1);
  }

  @Override public void setPresenter(GifDetailContract.Presenter presenter) {
    this.presenter = presenter;
  }

  @Override public void showGifRating(int newRating) {
    score.setText(String.valueOf(newRating));
  }

  @Override public void showError() {
    Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
  }
}
