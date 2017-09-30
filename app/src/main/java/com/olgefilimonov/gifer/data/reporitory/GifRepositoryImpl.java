package com.olgefilimonov.gifer.data.reporitory;

import com.olgefilimonov.gifer.app.AppConfig;
import com.olgefilimonov.gifer.data.api.RestApi;
import com.olgefilimonov.gifer.data.model.Datum;
import com.olgefilimonov.gifer.data.model.GiphyResponse;
import com.olgefilimonov.gifer.domain.entity.RatedGifEntity;
import io.objectbox.Box;
import io.reactivex.Observable;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import lombok.val;

/**
 * @author Oleg Filimonov
 */

public class GifRepositoryImpl implements GifRepository {
  private Box<RatedGifEntity> gifsBox;
  private RestApi restApi;

  @Inject
  public GifRepositoryImpl(Box<RatedGifEntity> gifsBox, RestApi restApi) {
    this.gifsBox = gifsBox;
    this.restApi = restApi;
  }

  @Override
  public Observable<GiphyResponse> searchGifs(String query, int limit, int skip) {
    return restApi.searchGifs(AppConfig.GIPHER_API_KEY, query, limit, skip);
  }

  @Override
  public Observable<Integer> getGifRating(String gifId) {
    return Observable.just(getUserRating(gifId));
  }

  @Override
  public Observable<Integer> rateGif(String gifId, int rating) {

    val ratedGifList = gifsBox.find("gifId", gifId);

    RatedGifEntity ratedGif = null;

    if (ratedGifList.size() == 0) {
      // No rating found
      ratedGif = new RatedGifEntity();
      ratedGif.setGifId(gifId);
    } else if (ratedGifList.size() == 1) {
      // Rating found
      ratedGif = ratedGifList.get(0);
    } else {
      throw new RuntimeException("Database has two entries of " + gifId);
    }

    val newScore = ratedGif.getScore() + rating;
    ratedGif.setScore(newScore);
    gifsBox.put(ratedGif);

    return Observable.just(newScore);
  }

  @Override
  public Observable<List<Integer>> getGifRatings(GiphyResponse giphyResponse) {
    final List<Datum> data = giphyResponse.getData();

    if (data == null) return Observable.just(new ArrayList<Integer>());

    val idList = new ArrayList<String>(data.size());
    for (Datum datum : data) {
      idList.add(datum.getId());
    }

    return Observable.just(getUserRatings(idList));
  }

  private List<Integer> getUserRatings(List<String> gifIdList) {
    val res = new ArrayList<Integer>(gifIdList.size());
    for (String id : gifIdList) {
      res.add(getUserRating(id));
    }
    return res;
  }

  private int getUserRating(String gifId) {
    val ratedGifList = gifsBox.find("gifId", gifId);
    if (ratedGifList.size() == 1) {
      // Rating found
      return ratedGifList.get(0).getScore();
    }
    return 0;
  }
}
