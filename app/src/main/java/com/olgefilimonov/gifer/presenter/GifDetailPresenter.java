package com.olgefilimonov.gifer.presenter;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.Params;
import com.olgefilimonov.gifer.contract.GifDetailContract;
import com.olgefilimonov.gifer.entity.RatedGif;
import com.olgefilimonov.gifer.job.CheckGifRatingJob;
import com.olgefilimonov.gifer.job.RateGifJob;
import com.olgefilimonov.gifer.job.UseCase;
import com.olgefilimonov.gifer.singleton.App;
import com.olgefilimonov.gifer.singleton.AppConfig;
import io.objectbox.BoxStore;
import java.util.UUID;
import javax.inject.Inject;
import lombok.val;

/**
 * @author Oleg Filimonov
 */

public class GifDetailPresenter implements GifDetailContract.Presenter {
  private final GifDetailContract.View view;
  @Inject JobManager jobManager;
  @Inject BoxStore boxStore;
  private String tag = UUID.randomUUID().toString();

  public GifDetailPresenter(GifDetailContract.View view) {
    this.view = view;
    App.getInstance().getComponent().inject(this);
  }

  @Override public void updateGifRating(String gifId) {
    val requestValues = new CheckGifRatingJob.RequestValues(gifId);
    val job = new CheckGifRatingJob(requestValues, boxStore.boxFor(RatedGif.class), new UseCase.UseCaseCallback<CheckGifRatingJob.ResponseValues>() {
      @Override public void onSuccess(CheckGifRatingJob.ResponseValues response) {
        view.showGifRating(response.getNewRating());
      }

      @Override public void onError() {
        view.showError();
      }
    }, new Params(AppConfig.DEFAULT_PRIORITY).addTags(tag));
    jobManager.addJobInBackground(job);
  }

  @Override public void rateGif(String gifId, int rating) {
    val requestValues = new RateGifJob.RequestValues(gifId, rating);
    val useCaseCallback = new UseCase.UseCaseCallback<RateGifJob.ResponseValues>() {
      @Override public void onSuccess(RateGifJob.ResponseValues response) {
        view.showGifRating(response.getNewRating());
      }

      @Override public void onError() {
        view.showError();
      }
    };
    val job = new RateGifJob(requestValues, boxStore.boxFor(RatedGif.class), useCaseCallback, new Params(AppConfig.DEFAULT_PRIORITY).addTags(tag));
    jobManager.addJobInBackground(job);
  }
}
