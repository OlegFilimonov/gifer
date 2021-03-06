package com.olgefilimonov.gifer.data.api;

import com.olgefilimonov.gifer.data.model.GiphyResponse;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static com.olgefilimonov.gifer.app.AppConfig.API_V1;

/**
 * @author Oleg Filimonov
 */
public interface RestApi {
  @GET(API_V1 + "gifs/search")
  Observable<GiphyResponse> searchGifs(@Query("api_key") String apiKey, @Query("q") String query,
      @Query("limit") Integer limit, @Query("offset") Integer offset);
}