package com.olgefilimonov.gifer.presenter;

import com.birbit.android.jobqueue.JobManager;
import com.olgefilimonov.gifer.client.DefaultApi;
import com.olgefilimonov.gifer.contract.SearchContract;
import com.olgefilimonov.gifer.model.Gif;
import com.olgefilimonov.gifer.model.RatedGif;
import com.olgefilimonov.gifer.mvp.UseCase;
import com.olgefilimonov.gifer.singleton.Constant;
import com.olgefilimonov.gifer.singleton.GiferApplication;
import com.olgefilimonov.gifer.usecase.CheckGifRatingJob;
import com.olgefilimonov.gifer.usecase.LoadGifsJob;
import com.olgefilimonov.gifer.usecase.RateGifJob;
import io.objectbox.BoxStore;
import java.util.List;
import java.util.UUID;

/**
 * @author Oleg Filimonov
 */

public class SearchPresenter implements SearchContract.Presenter {
  private JobManager jobManager;
  private DefaultApi api;
  private SearchContract.View view;
  private String tag = UUID.randomUUID().toString();
  private BoxStore boxStore;

  public SearchPresenter(SearchContract.View view) {
    this.view = view;
    this.jobManager = GiferApplication.getInstance().getJobManager();
    this.api = GiferApplication.getInstance().getApiClient().createService(DefaultApi.class);
    this.boxStore = GiferApplication.getInstance().getBoxStore();

    view.setPresenter(this);
  }

  @Override public void loadGifs(String query, int page, int limit) {
    view.showProgress();
    // Setup callback
    UseCase.UseCaseCallback<LoadGifsJob.ResponseValue> useCaseCallback = new UseCase.UseCaseCallback<LoadGifsJob.ResponseValue>() {
      @Override public void onSuccess(LoadGifsJob.ResponseValue response) {

        view.hideProgress();
        List<Gif> gifs = response.getGifs();
        view.showSearchResults(gifs);
      }

      @Override public void onError() {
        view.hideProgress();
        view.showError();
      }
    };
    // Setup the job
    LoadGifsJob.RequestValues requestValues = new LoadGifsJob.RequestValues(query, page, limit);
    LoadGifsJob job = new LoadGifsJob(requestValues, tag, Constant.GIPHER_API_KEY, boxStore.boxFor(RatedGif.class), useCaseCallback);
    // Execute the job
    jobManager.addJobInBackground(job);
  }

  @Override public void updateGifRating(String gifId) {
    CheckGifRatingJob.RequestValues requestValues = new CheckGifRatingJob.RequestValues(gifId);
    jobManager.addJobInBackground(new CheckGifRatingJob(requestValues, tag, boxStore.boxFor(RatedGif.class), new UseCase.UseCaseCallback<CheckGifRatingJob.ResponseValues>() {
      @Override public void onSuccess(CheckGifRatingJob.ResponseValues response) {
        view.updateGifRating(response.getGifId(), response.getNewRating());
      }

      @Override public void onError() {
        view.showError();
      }
    }));
  }

  @Override public void rateGif(Gif gif, int rating) {
    RateGifJob.RequestValues requestValues = new RateGifJob.RequestValues(gif, rating);
    UseCase.UseCaseCallback<RateGifJob.ResponseValues> useCaseCallback = new UseCase.UseCaseCallback<RateGifJob.ResponseValues>() {
      @Override public void onSuccess(RateGifJob.ResponseValues response) {
        view.updateGifRating(response.getGifId(), response.getNewRating());
      }

      @Override public void onError() {
        view.showError();
      }
    };
    RateGifJob job = new RateGifJob(requestValues, tag, boxStore.boxFor(RatedGif.class), useCaseCallback);
    jobManager.addJobInBackground(job);
  }
}
