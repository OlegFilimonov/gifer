package com.olgefilimonov.gifer.presenter;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.Params;
import com.olgefilimonov.gifer.contract.GifDetailContract;
import com.olgefilimonov.gifer.model.RatedGif;
import com.olgefilimonov.gifer.mvp.UseCase;
import com.olgefilimonov.gifer.singleton.Constant;
import com.olgefilimonov.gifer.singleton.GiferApplication;
import com.olgefilimonov.gifer.usecase.CheckGifRatingJob;
import com.olgefilimonov.gifer.usecase.RateGifJob;
import io.objectbox.BoxStore;
import java.util.UUID;
import javax.inject.Inject;

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
    GiferApplication.getInstance().getComponent().inject(this);
    view.setPresenter(this);
  }

  @Override public void updateGifRating(String gifId) {
    CheckGifRatingJob.RequestValues requestValues = new CheckGifRatingJob.RequestValues(gifId);
    CheckGifRatingJob job = new CheckGifRatingJob(requestValues, boxStore.boxFor(RatedGif.class), new UseCase.UseCaseCallback<CheckGifRatingJob.ResponseValues>() {
      @Override public void onSuccess(CheckGifRatingJob.ResponseValues response) {
        view.showGifRating(response.getNewRating());
      }

      @Override public void onError() {
        view.showError();
      }
    }, new Params(Constant.DEFAULT_PRIORITY).addTags(tag));
    jobManager.addJobInBackground(job);
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
    RateGifJob job = new RateGifJob(requestValues, boxStore.boxFor(RatedGif.class), useCaseCallback, new Params(Constant.DEFAULT_PRIORITY).addTags(tag));
    jobManager.addJobInBackground(job);
  }
}
