package com.olgefilimonov.gifer.presenter;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.TagConstraint;
import com.olgefilimonov.gifer.model.Gif;
import com.olgefilimonov.gifer.model.RatedGif;
import com.olgefilimonov.gifer.mvp.UseCase;
import com.olgefilimonov.gifer.mvp.contract.SearchContract;
import com.olgefilimonov.gifer.singleton.Constant;
import com.olgefilimonov.gifer.singleton.GiferApplication;
import com.olgefilimonov.gifer.usecase.CheckGifRatingJob;
import com.olgefilimonov.gifer.usecase.LoadGifsJob;
import com.olgefilimonov.gifer.usecase.RateGifJob;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.inject.Inject;

/**
 * @author Oleg Filimonov
 */

public class SearchPresenter implements SearchContract.Presenter {
  /**
   * A tag that is unique to this presenter instance. Can be used to cancel all jobs of the presenter at once
   */
  private final String presenterTag = UUID.randomUUID().toString();
  private final String loadGifsTag = "load_gifs";
  @Inject JobManager jobManager;
  @Inject BoxStore boxStore;
  private SearchContract.View view;
  private long lastCallbackTimestamp;

  public SearchPresenter(SearchContract.View view) {
    this.view = view;
    GiferApplication.getInstance().getComponent().inject(this);
  }

  @Override public void loadGifs(String query, final int skip, int limit) {
    view.showProgress();

    if (query.trim().equals("")) {
      view.hideProgress();
      view.showSearchResults(new ArrayList<Gif>());
      lastCallbackTimestamp = System.currentTimeMillis();
      return;
    }

    // Cancel all previous jobs
    jobManager.cancelJobsInBackground(null, TagConstraint.ALL, loadGifsTag);

    UseCase.UseCaseCallback<LoadGifsJob.ResponseValue> useCaseCallback = new UseCase.UseCaseCallback<LoadGifsJob.ResponseValue>() {
      @Override public void onSuccess(LoadGifsJob.ResponseValue response) {
        view.hideProgress();

        if (response.getAddedTimestamp() < lastCallbackTimestamp) {
          return;
        }

        lastCallbackTimestamp = response.getAddedTimestamp();
        // Reset search results if we're on the first page
        if (skip == 0) view.clearSearchResults();
        List<Gif> gifs = response.getGifs();
        view.showSearchResults(gifs);
      }

      @Override public void onError() {
        view.hideProgress();
        view.showError();
      }
    };

    LoadGifsJob.RequestValues requestValues = new LoadGifsJob.RequestValues(query, skip, limit);
    Params params = new Params(Constant.DEFAULT_PRIORITY).addTags(presenterTag, loadGifsTag);
    Box<RatedGif> gifsBox = boxStore.boxFor(RatedGif.class);

    LoadGifsJob job = new LoadGifsJob(requestValues, Constant.GIPHER_API_KEY, gifsBox, useCaseCallback, params);

    jobManager.addJobInBackground(job);
  }

  @Override public void updateGifRating(String gifId) {
    CheckGifRatingJob.RequestValues requestValues = new CheckGifRatingJob.RequestValues(gifId);
    CheckGifRatingJob job = new CheckGifRatingJob(requestValues, boxStore.boxFor(RatedGif.class), new UseCase.UseCaseCallback<CheckGifRatingJob.ResponseValues>() {
      @Override public void onSuccess(CheckGifRatingJob.ResponseValues response) {
        view.showGifRating(response.getGifId(), response.getNewRating());
      }

      @Override public void onError() {
        view.showError();
      }
    }, new Params(Constant.DEFAULT_PRIORITY).addTags(presenterTag));
    jobManager.addJobInBackground(job);
  }

  @Override public void rateGif(String gifId, int rating) {
    RateGifJob.RequestValues requestValues = new RateGifJob.RequestValues(gifId, rating);
    UseCase.UseCaseCallback<RateGifJob.ResponseValues> useCaseCallback = new UseCase.UseCaseCallback<RateGifJob.ResponseValues>() {
      @Override public void onSuccess(RateGifJob.ResponseValues response) {
        view.showGifRating(response.getGifId(), response.getNewRating());
      }

      @Override public void onError() {
        view.showError();
      }
    };
    RateGifJob job = new RateGifJob(requestValues, boxStore.boxFor(RatedGif.class), useCaseCallback, new Params(Constant.DEFAULT_PRIORITY).addTags(presenterTag));
    jobManager.addJobInBackground(job);
  }
}
