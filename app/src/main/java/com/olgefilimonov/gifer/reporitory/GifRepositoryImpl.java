package com.olgefilimonov.gifer.reporitory;

import com.olgefilimonov.gifer.EntityConverter;
import com.olgefilimonov.gifer.api.RestApi;
import com.olgefilimonov.gifer.entity.GifEntity;
import com.olgefilimonov.gifer.entity.RatedGifEntity;
import com.olgefilimonov.gifer.singleton.AppConfig;
import io.objectbox.Box;
import io.reactivex.Observable;
import java.util.List;
import javax.inject.Inject;
import lombok.val;

/**
 * @author Oleg Filimonov
 */

public class GifRepositoryImpl implements GifRepository {
  @Inject Box<RatedGifEntity> gifsBox;
  @Inject RestApi restApi;

  @Inject public GifRepositoryImpl(Box<RatedGifEntity> gifsBox, RestApi restApi) {
    this.gifsBox = gifsBox;
    this.restApi = restApi;
  }

  @Override public Observable<List<GifEntity>> searchGifs(String query, int limit, int skip) {
    return restApi.searchGifs(AppConfig.GIPHER_API_KEY, query, limit, skip).map(EntityConverter::convertGifList).map(this::getUserRatings);
  }

  @Override public Observable<Integer> getGifRating(String gifId) {
    return Observable.just(getUserRating(gifId));
  }

  @Override public Observable<Integer> rateGif(String gifId, int rating) {

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

  private List<GifEntity> getUserRatings(List<GifEntity> list) {
    list.forEach(this::getUserRating);
    return list;
  }

  private GifEntity getUserRating(GifEntity gifEntity) {
    String gifId = gifEntity.getGifId();
    int score = getUserRating(gifId);
    gifEntity.setScore(score);
    return gifEntity;
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
