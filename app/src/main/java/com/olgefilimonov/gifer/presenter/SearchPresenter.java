package com.olgefilimonov.gifer.presenter;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.TagConstraint;
import com.olgefilimonov.gifer.contract.SearchContract;
import com.olgefilimonov.gifer.entity.GifEntity;
import com.olgefilimonov.gifer.job.CheckGifRatingJob;
import com.olgefilimonov.gifer.job.DefaultObserver;
import com.olgefilimonov.gifer.job.LoadGifsJob;
import com.olgefilimonov.gifer.job.RateGifJob;
import com.olgefilimonov.gifer.singleton.App;
import com.olgefilimonov.gifer.singleton.AppConfig;
import io.reactivex.annotations.NonNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.inject.Inject;
import lombok.val;

/**
 * @author Oleg Filimonov
 */

public class SearchPresenter implements SearchContract.Presenter {
  /**
   * A tag that is unique to this presenter instance. Can be used to cancel all jobs of the presenter at once
   */
  private final String presenterTag = UUID.randomUUID().toString();
  @Inject JobManager jobManager;
  private SearchContract.View view;
  private long lastCallbackTimestamp;

  public SearchPresenter(SearchContract.View view) {
    this.view = view;
    App.getInstance().getComponent().inject(this);
  }

  @Override public void loadGifs(String query, final int skip, int limit) {
    view.showProgress();

    if (query.trim().equals("")) {
      view.hideProgress();
      view.showSearchResults(new ArrayList<GifEntity>());
      lastCallbackTimestamp = System.currentTimeMillis();
      return;
    }

    // Cancel all previous jobs
    String loadGifsTag = "load_gifs";

    jobManager.cancelJobsInBackground(null, TagConstraint.ALL, loadGifsTag);

    val observer = new DefaultObserver<LoadGifsJob.Response>() {
      @Override public void onError(@NonNull Throwable e) {
        view.hideProgress();
        view.showError();
      }

      @Override public void onNext(LoadGifsJob.Response response) {
        view.hideProgress();

        if (response.getAddedTimestamp() < lastCallbackTimestamp) {
          return;
        }

        lastCallbackTimestamp = response.getAddedTimestamp();
        // Reset search results if we're on the first page
        if (skip == 0) view.clearSearchResults();
        List<GifEntity> gifEntities = response.getGifEntities();
        view.showSearchResults(gifEntities);
      }
    };
    val requestValues = new LoadGifsJob.Request(query, skip, limit);
    val params = new Params(AppConfig.DEFAULT_PRIORITY).addTags(presenterTag, loadGifsTag);

    val job = new LoadGifsJob(requestValues, observer, params);

    jobManager.addJobInBackground(job);
  }

  @Override public void updateGifRating(String gifId) {

    val observer = new DefaultObserver<CheckGifRatingJob.Response>() {
      @Override public void onNext(CheckGifRatingJob.Response response) {
        view.showGifRating(response.getGifId(), response.getNewRating());
      }

      @Override public void onError(Throwable exception) {
        view.showError();
      }
    };
    val requestValues = new CheckGifRatingJob.Request(gifId);
    val params = new Params(AppConfig.DEFAULT_PRIORITY).addTags(presenterTag);

    val job = new CheckGifRatingJob(requestValues, observer, params);

    jobManager.addJobInBackground(job);
  }

  @Override public void rateGif(String gifId, int rating) {

    val observer = new DefaultObserver<RateGifJob.Response>() {
      @Override public void onNext(RateGifJob.Response response) {
        view.showGifRating(response.getGifId(), response.getNewRating());
      }

      @Override public void onError(Throwable exception) {
        view.showError();
      }
    };
    val requestValues = new RateGifJob.Request(gifId, rating);
    val params = new Params(AppConfig.DEFAULT_PRIORITY).addTags(presenterTag);

    val job = new RateGifJob(requestValues, observer, params);

    jobManager.addJobInBackground(job);
  }
}
