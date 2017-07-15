package com.olgefilimonov.gifer.controller;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
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

public class GifDetailController extends BaseController implements GifDetailContract.View {

  @BindView(R.id.exoplayer) SimpleExoPlayerView exoPlayerView;
  @BindView(R.id.gif_score) TextView score;

  private String url;
  private String gifId;

  private GifDetailContract.Presenter presenter;

  public GifDetailController() {
  }

  public GifDetailController(String url, String gifId) {
    this.url = url;
    this.gifId = gifId;
  }

  @Override protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
    return inflater.inflate(R.layout.activity_gif_detail, container, false);
  }

  @Override protected void onViewBound(@NonNull View view) {
    super.onViewBound(view);

    // Presenter
    new GifDetailPresenter(this);

    // Setup
    setupPlayer();

    presenter.updateGifRating(gifId);
  }

  private void setupPlayer() {
    // Measures bandwidth during playback. Can be null if not required
    DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
    TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
    TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
    // Create the player
    final SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector);

    // Bind the player to the view
    exoPlayerView.setPlayer(player);

    // Produces DataSource instances through which media data is loaded
    DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getActivity(), Util.getUserAgent(getActivity(), "yourApplicationName"), bandwidthMeter);
    // Produces Extractor instances for parsing the media data
    ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
    // This is the MediaSource representing the media to be played
    MediaSource videoSource = new ExtractorMediaSource(Uri.parse(url), dataSourceFactory, extractorsFactory, null, null);
    // Loops the video indefinitely
    LoopingMediaSource loopingSource = new LoopingMediaSource(videoSource);
    // Prepare the player with the source
    player.prepare(loopingSource);
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
    Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
  }
}
