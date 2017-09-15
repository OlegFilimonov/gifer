package com.olgefilimonov.gifer.job;

import android.support.annotation.Nullable;
import com.birbit.android.jobqueue.Params;
import com.olgefilimonov.gifer.entity.RatedGif;
import io.objectbox.Box;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

/**
 * @author Oleg Filimonov
 */

public class CheckGifRatingJob extends UseCase<CheckGifRatingJob.RequestValues, CheckGifRatingJob.ResponseValues> {
  private final Box<RatedGif> gifsBox;

  public CheckGifRatingJob(RequestValues requestValues, Box<RatedGif> gifsBox, UseCaseCallback<ResponseValues> useCaseCallback, Params params) {
    super(requestValues, useCaseCallback, params);
    this.gifsBox = gifsBox;
  }

  @Override protected void executeUseCase(RequestValues requestValues) throws Throwable {

    val gifId = requestValues.getGifId();

    RatedGif ratedGif;
    val ratedGifList = gifsBox.find("gifId", gifId);
    if (ratedGifList.size() == 0) {
      // No rating found
      ratedGif = new RatedGif();
      ratedGif.setScore(0);
      ratedGif.setGifId(gifId);
    } else if (ratedGifList.size() == 1) {
      // Rating found
      ratedGif = ratedGifList.get(0);
    } else {
      throw new RuntimeException("Database error. gifId must be unique");
    }

    onSuccess(new ResponseValues(ratedGif.getGifId(), ratedGif.getScore()));
  }

  @Override protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
    onError();
  }

  @Getter @Setter @AllArgsConstructor public static final class RequestValues implements UseCase.RequestValues {
    private String gifId;
  }

  @Getter @Setter @AllArgsConstructor public static final class ResponseValues implements UseCase.ResponseValue {
    private String gifId;
    private int newRating;
  }
}
