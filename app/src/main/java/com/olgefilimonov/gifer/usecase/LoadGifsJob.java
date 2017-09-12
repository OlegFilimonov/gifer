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
      GiphyResponse body = response.body();
      List<Datum> data = body != null ? body.getData() : null;
      if (data != null) {
        for (Datum datum : data) {
          String previewUrl = datum.getImages().getDownsizedStill().getUrl();
          // Sometimes original MP4 is unavailable. If so, don't add the gif
          if (datum.getImages().getOriginalMp4() == null) continue;
          String videoUrl = datum.getImages().getOriginalMp4().getMp4();
          Gif gif = new Gif(datum.getId(), videoUrl, previewUrl);
          gifs.add(gif);
        }
      }

      // Check user ratings
      for (int i = 0; i < gifs.size(); i++) {
        Gif gif = gifs.get(i);
        List<RatedGif> ratedGifList = gifsBox.find("gifId", gif.getGifId());
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

  public static final class RequestValues implements UseCase.RequestValues {
    private String query;
    private int skip;
    private int limit;

    public RequestValues(String query, int page, int limit) {
      this.query = query;
      this.skip = page;
      this.limit = limit;
    }

    public String getQuery() {
      return query;
    }

    public void setQuery(String query) {
      this.query = query;
    }

    public int getSkip() {
      return skip;
    }

    public void setSkip(int skip) {
      this.skip = skip;
    }

    public int getLimit() {
      return limit;
    }

    public void setLimit(int limit) {
      this.limit = limit;
    }
  }

  public static final class ResponseValue implements UseCase.ResponseValue {

    private final List<Gif> gifs;
    private long addedTimestamp;

    public ResponseValue(List<Gif> gifs, long addedTimestamp) {
      this.gifs = gifs;
      this.addedTimestamp = addedTimestamp;
    }

    public long getAddedTimestamp() {
      return addedTimestamp;
    }

    public List<Gif> getGifs() {
      return gifs;
    }
  }
}
