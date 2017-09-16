package com.olgefilimonov.gifer.presenter;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.Params;
import com.olgefilimonov.gifer.contract.GifDetailContract;
import com.olgefilimonov.gifer.job.CheckGifRatingJob;
import com.olgefilimonov.gifer.job.DefaultObserver;
import com.olgefilimonov.gifer.job.RateGifJob;
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
    val requestValues = new CheckGifRatingJob.Request(gifId);
    val observer = new DefaultObserver<CheckGifRatingJob.Response>() {
      @Override public void onNext(CheckGifRatingJob.Response response) {
        view.showGifRating(response.getNewRating());
      }

      @Override public void onError(Throwable exception) {
        view.showError();
      }
    };
    val params = new Params(AppConfig.DEFAULT_PRIORITY).addTags(tag);

    val job = new CheckGifRatingJob(requestValues, observer, params);

    jobManager.addJobInBackground(job);
  }

  @Override public void rateGif(String gifId, int rating) {
    val requestValues = new RateGifJob.Request(gifId, rating);
    val observer = new DefaultObserver<RateGifJob.Response>() {
      @Override public void onNext(RateGifJob.Response response) {
        view.showGifRating(response.getNewRating());
      }

      @Override public void onError(Throwable exception) {
        view.showError();
      }
    };
    val params = new Params(AppConfig.DEFAULT_PRIORITY).addTags(tag);

    val job = new RateGifJob(requestValues, observer, params);

    jobManager.addJobInBackground(job);
  }
}
