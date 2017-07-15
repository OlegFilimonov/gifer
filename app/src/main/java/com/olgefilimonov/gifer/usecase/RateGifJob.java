package com.olgefilimonov.gifer.usecase;

import android.support.annotation.Nullable;
import com.olgefilimonov.gifer.model.Gif;
import com.olgefilimonov.gifer.model.RatedGif;
import com.olgefilimonov.gifer.mvp.UseCase;
import io.objectbox.Box;
import java.util.List;

/**
 * @author Oleg Filimonov
 */

public class RateGifJob extends UseCase<RateGifJob.RequestValues, RateGifJob.ResponseValues> {
  private final Box<RatedGif> gifsBox;

  public RateGifJob(RequestValues requestValues, String tag, Box<RatedGif> gifsBox, UseCaseCallback<ResponseValues> useCaseCallback) {
    super(requestValues, tag, useCaseCallback);
    this.gifsBox = gifsBox;
  }

  @Override protected void executeUseCase(RequestValues requestValues) throws Throwable {
    Gif gif = requestValues.getGif();
    gif.setScore(gif.getScore() + 1);

    String gifId = gif.getGifId();

    RatedGif ratedGif;
    List<RatedGif> ratedGifList = gifsBox.find("gifId", gifId);
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

    ratedGif.setScore(gif.getScore());
    gifsBox.put(ratedGif);

    onSuccess(new ResponseValues(ratedGif.getGifId(), ratedGif.getScore()));
  }

  @Override protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
    onError();
  }

  public static final class RequestValues implements UseCase.RequestValues {
    private Gif gif;
    private int rating;

    public RequestValues(Gif gif, int rating) {
      this.gif = gif;
      this.rating = rating;
    }

    public Gif getGif() {
      return gif;
    }

    public void setGif(Gif gif) {
      this.gif = gif;
    }

    public int getRating() {
      return rating;
    }

    public void setRating(int rating) {
      this.rating = rating;
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
