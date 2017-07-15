package com.olgefilimonov.gifer.presenter;

import com.birbit.android.jobqueue.JobManager;
import com.olgefilimonov.gifer.contract.GifDetailContract;
import com.olgefilimonov.gifer.model.RatedGif;
import com.olgefilimonov.gifer.mvp.UseCase;
import com.olgefilimonov.gifer.singleton.GiferApplication;
import com.olgefilimonov.gifer.usecase.CheckGifRatingJob;
import com.olgefilimonov.gifer.usecase.RateGifJob;
import io.objectbox.BoxStore;
import java.util.UUID;

/**
 * @author Oleg Filimonov
 */

public class GifDetailPresenter implements GifDetailContract.Presenter {
  private final GifDetailContract.View view;
  private JobManager jobManager;
  private String tag = UUID.randomUUID().toString();
  private BoxStore boxStore;

  public GifDetailPresenter(GifDetailContract.View view) {
    this.view = view;
    this.jobManager = GiferApplication.getInstance().getJobManager();
    this.boxStore = GiferApplication.getInstance().getBoxStore();

    view.setPresenter(this);
  }

  @Override public void updateGifRating(String gifId) {
    CheckGifRatingJob.RequestValues requestValues = new CheckGifRatingJob.RequestValues(gifId);
    jobManager.addJobInBackground(new CheckGifRatingJob(requestValues, tag, boxStore.boxFor(RatedGif.class), new UseCase.UseCaseCallback<CheckGifRatingJob.ResponseValues>() {
      @Override public void onSuccess(CheckGifRatingJob.ResponseValues response) {
        view.showGifRating(response.getNewRating());
      }

      @Override public void onError() {
        view.showError();
      }
    }));
  }

  @Override public void rateGif(String gifId, int rating) {
    RateGifJob.RequestValues requestValues = new RateGifJob.RequestValues(gifId, rating);
    UseCase.UseCaseCallback<RateGifJob.ResponseValues> useCaseCallback = new UseCase.UseCaseCallback<RateGifJob.ResponseValues>() {
      @Override public void onSuccess(RateGifJob.ResponseValues response) {
        view.showGifRating(response.getNewRating());
      }

      @Override public void onError() {
        view.showError();
      }
    };
    RateGifJob job = new RateGifJob(requestValues, tag, boxStore.boxFor(RatedGif.class), useCaseCallback);
    jobManager.addJobInBackground(job);
  }
}
