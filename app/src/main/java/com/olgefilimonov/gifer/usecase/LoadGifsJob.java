package com.olgefilimonov.gifer.usecase;

import android.support.annotation.Nullable;
import com.birbit.android.jobqueue.Params;
import com.olgefilimonov.gifer.model.Datum;
import com.olgefilimonov.gifer.model.Gif;
import com.olgefilimonov.gifer.model.GiphyResponse;
import com.olgefilimonov.gifer.model.RatedGif;
import com.olgefilimonov.gifer.mvp.UseCase;
import io.objectbox.Box;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import retrofit2.Call;
import retrofit2.Response;

/**
 * @author Oleg Filimonov
 */

public class LoadGifsJob extends UseCase<LoadGifsJob.RequestValues, LoadGifsJob.ResponseValue> {
  private final Box<RatedGif> gifsBox;
  private String apiKey;
  private long addedTimestamp;

  public LoadGifsJob(RequestValues requestValues, String apiKey, Box<RatedGif> gifsBox, UseCaseCallback<ResponseValue> useCaseCallback, Params params) {
    super(requestValues, useCaseCallback, params);
    this.apiKey = apiKey;
    this.gifsBox = gifsBox;
    addedTimestamp = System.currentTimeMillis();
  }

  @Override protected void executeUseCase(RequestValues requestValues) throws Throwable {
    // Get list of gifs
    Call<GiphyResponse> call = defaultApi.searchGifs(apiKey, requestValues.getQuery(), requestValues.getLimit(), requestValues.getSkip());
    Response<GiphyResponse> response = call.execute();
    if (response.isSuccessful()) {

      List<Gif> gifs = new ArrayList<>();

      // Convert gifs to the local model
      val body = response.body();
      val data = body != null ? body.getData() : null;
      if (data != null) {
        for (Datum datum : data) {
          val previewUrl = datum.getImages().getDownsizedStill().getUrl();
          // Sometimes original MP4 is unavailable. If so, don't add the gif
          if (datum.getImages().getOriginalMp4() == null) continue;
          val videoUrl = datum.getImages().getOriginalMp4().getMp4();
          val gif = new Gif(datum.getId(), videoUrl, previewUrl);
          gifs.add(gif);
        }
      }

      // Check user ratings
      for (int i = 0; i < gifs.size(); i++) {
        val gif = gifs.get(i);
        val ratedGifList = gifsBox.find("gifId", gif.getGifId());
        if (ratedGifList.size() == 0) {
          // No rating found -- don't do anything
        } else if (ratedGifList.size() == 1) {
          // Rating found
          gif.setScore(ratedGifList.get(0).getScore());
        } else {
          throw new RuntimeException("Database error. gifId must be unique");
        }
      }

      onSuccess(new ResponseValue(gifs, addedTimestamp));
    } else {
      onError();
    }
  }

  @Override protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
    // Do nothing
  }

  @Getter @Setter @AllArgsConstructor public static final class RequestValues implements UseCase.RequestValues {
    private String query;
    private int skip;
    private int limit;
  }

  @Getter @Setter @AllArgsConstructor public static final class ResponseValue implements UseCase.ResponseValue {
    private final List<Gif> gifs;
    private long addedTimestamp;
  }
}
