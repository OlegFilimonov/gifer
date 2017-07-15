package com.olgefilimonov.gifer.usecase;

import android.support.annotation.Nullable;
import com.birbit.android.jobqueue.Params;
import com.olgefilimonov.gifer.model.RatedGif;
import com.olgefilimonov.gifer.mvp.UseCase;
import io.objectbox.Box;
import java.util.List;

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

    String gifId = requestValues.getGifId();

    RatedGif ratedGif;
    List<RatedGif> ratedGifList = gifsBox.find("gifId", gifId);
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

  public static final class RequestValues implements UseCase.RequestValues {
    private String gifId;

    public RequestValues(String gifId) {
      this.gifId = gifId;
    }

    public String getGifId() {
      return gifId;
    }

    public void setGifId(String gifId) {
      this.gifId = gifId;
    }
  }

  public static final class ResponseValues implements UseCase.ResponseValue {
    private String gifId;
    private int newRating;

    public ResponseValues(String gifId, int newRating) {
      this.gifId = gifId;
      this.newRating = newRating;
    }

    public String getGifId() {
      return gifId;
    }

    public void setGifId(String gifId) {
      this.gifId = gifId;
    }

    public int getNewRating() {
      return newRating;
    }

    public void setNewRating(int newRating) {
      this.newRating = newRating;
    }
  }
}
