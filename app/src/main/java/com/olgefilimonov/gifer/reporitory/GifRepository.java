package com.olgefilimonov.gifer.reporitory;

import com.olgefilimonov.gifer.entity.GifEntity;
import io.reactivex.Observable;
import java.util.List;

/**
 * @author Oleg Filimonov
 */

public interface GifRepository {
  Observable<List<GifEntity>> searchGifs(String query, int limit, int skip);

  Observable<Integer> getGifRating(String gifId);

  Observable<Integer> rateGif(String gifId, int rating);
}
