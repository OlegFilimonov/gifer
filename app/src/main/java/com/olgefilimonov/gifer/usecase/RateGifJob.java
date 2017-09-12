package com.olgefilimonov.gifer.usecase;

import android.support.annotation.Nullable;
import com.birbit.android.jobqueue.Params;
import com.olgefilimonov.gifer.model.RatedGif;
import com.olgefilimonov.gifer.mvp.UseCase;
import io.objectbox.Box;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

/**
 * @author Oleg Filimonov
 */

public class RateGifJob extends UseCase<RateGifJob.RequestValues, RateGifJob.ResponseValues> {
  private final Box<RatedGif> gifsBox;

  public RateGifJob(RequestValues requestValues, Box<RatedGif> gifsBox, UseCaseCallback<ResponseValues> useCaseCallback, Params params) {
    super(requestValues, useCaseCallback, params);
    this.gifsBox = gifsBox;
  }

  @Override protected void executeUseCase(RequestValues requestValues) throws Throwable {

    val gifId = requestValues.getGifId();
    val ratedGifList = gifsBox.find("gifId", gifId);

    RatedGif ratedGif;
    if (ratedGifList.size() == 0) {
      // No rating found
      ratedGif = new RatedGif();
      ratedGif.setGifId(gifId);
    } else if (ratedGifList.size() == 1) {
      // Rating found
      ratedGif = ratedGifList.get(0);
    } else {
      throw new RuntimeException("Database error. gifId must be unique");
    }

    ratedGif.setScore(ratedGif.getScore() + requestValues.getRating());
    gifsBox.put(ratedGif);

    onSuccess(new ResponseValues(ratedGif.getGifId(), ratedGif.getScore()));
  }

  @Override protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
    onError();
  }

  @Getter @Setter @AllArgsConstructor public static final class RequestValues implements UseCase.RequestValues {
    private String gifId;
    private int rating;
  }

  @Getter @Setter @AllArgsConstructor public static final class ResponseValues implements UseCase.ResponseValue {
    private String gifId;
    private int newRating;
  }
}
