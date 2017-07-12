package com.olgefilimonov.gifer.client;

import com.olgefilimonov.gifer.model.GiphyResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static com.olgefilimonov.gifer.singleton.Constant.API_V1;

/**
 * @author Oleg Filimonov
 */
public interface DefaultApi {
  @GET(API_V1 + "gifs/search") Call<GiphyResponse> searchGifs(
      @Query("api_key") String apiKey,
      @Query("q") String query,
      @Query("limit") Integer limit,
      @Query("offset") Integer offset
  );
}