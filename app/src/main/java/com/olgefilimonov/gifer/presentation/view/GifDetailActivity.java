package com.olgefilimonov.gifer.presentation.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.OnClick;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.olgefilimonov.gifer.R;
import com.olgefilimonov.gifer.presentation.contract.GifDetailContract;
import com.olgefilimonov.gifer.presentation.presenter.GifDetailPresenter;
import icepick.State;
import lombok.val;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

/**
 * @author Oleg Filimonov
 */

public class GifDetailActivity extends BaseActivity<GifDetailContract.Presenter>
    implements GifDetailContract.View {

  public static final String GIF_DETAIL_VIDEO_URL = "GIF_DETAIL_VIDEO_URL";
  public static final String GIF_DETAIL_GIF_ID = "GIF_DETAIL_GIF_ID";

  @BindView(R.id.exoplayer) SimpleExoPlayerView exoPlayerView;
  @BindView(R.id.gif_score) TextView scoreView;
  @State long playerPosition;
  private String videoUrl;
  private String gifId;

  public static void start(Context context, String videoUrl, String gifId) {
    val intent = new Intent(context, GifDetailActivity.class);
    intent.putExtra(GIF_DETAIL_VIDEO_URL, videoUrl);
    intent.putExtra(GIF_DETAIL_GIF_ID, gifId);
    context.startActivity(intent);
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_gif_detail);
    setPresenter(new GifDetailPresenter(this));

    setupData();
    setupExoPlayer();

    presenter.updateGifRating(gifId);
  }

  private void setupData() {
    Intent intent = getIntent();
    videoUrl = intent.getStringExtra(GIF_DETAIL_VIDEO_URL);
    gifId = intent.getStringExtra(GIF_DETAIL_GIF_ID);
  }

  private void setupExoPlayer() {
    final SimpleExoPlayer player =
        ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());

    exoPlayerView.setPlayer(player);

    val dataSourceFactory =
        new DefaultDataSourceFactory(this, Util.getUserAgent(this, getString(R.string.app_name)),
            null);
    val extractorsFactory = new DefaultExtractorsFactory();
    val videoSource =
        new ExtractorMediaSource(Uri.parse(videoUrl), dataSourceFactory, extractorsFactory, null,
            null);
    val loopingSource = new LoopingMediaSource(videoSource);

    player.setPlayWhenReady(true);
    player.prepare(loopingSource);
    player.seekTo(playerPosition);

    if (getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE) {
      getWindow().getDecorView()
          .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
              | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
              | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
              | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
              | View.SYSTEM_UI_FLAG_FULLSCREEN
              | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    playerPosition = exoPlayerView.getPlayer().getCurrentPosition();
    super.onSaveInstanceState(outState);
  }

  @OnClick(R.id.gif_like)
  void onGifLike() {
    presenter.rateGif(gifId, +1);
  }

  @OnClick(R.id.gif_dislike)
  void onGifDislike() {
    presenter.rateGif(gifId, -1);
  }

  @Override
  public void showGifRating(int newRating) {
    scoreView.setText(String.valueOf(newRating));
  }

  @Override
  public void showError() {
    Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
  }
}
