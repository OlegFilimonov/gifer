package com.olgefilimonov.gifer.data.reporitory;

import com.olgefilimonov.gifer.data.model.GiphyResponse;
import io.reactivex.Observable;
import java.util.List;

/**
 * @author Oleg Filimonov
 */

public interface GifRepository {
  Observable<GiphyResponse> searchGifs(String query, int limit, int skip);

  Observable<Integer> getGifRating(String gifId);

  Observable<Integer> rateGif(String gifId, int rating);

  Observable<List<Integer>> getGifRatings(GiphyResponse giphyResponse);
}
