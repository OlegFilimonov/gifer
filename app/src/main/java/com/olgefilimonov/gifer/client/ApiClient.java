package com.olgefilimonov.gifer.client;

import com.google.gson.GsonBuilder;
import com.olgefilimonov.gifer.singleton.Constant;
import java.util.concurrent.TimeUnit;
import lombok.val;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

/**
 * @author Oleg Filimonov
 */
public class ApiClient {
  private Retrofit.Builder adapterBuilder;

  public ApiClient() {
    createDefaultAdapter();
  }

  public void createDefaultAdapter() {
    val gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();

    // Add logging inteceptor
    val logging = new HttpLoggingInterceptor();
    logging.setLevel(Constant.DEBUG ? HttpLoggingInterceptor.Level.BASIC : HttpLoggingInterceptor.Level.NONE);

    val okClient =
        new OkHttpClient.Builder().connectTimeout(120, TimeUnit.SECONDS).readTimeout(120, TimeUnit.SECONDS).writeTimeout(120, TimeUnit.SECONDS).addInterceptor(logging).build();

    adapterBuilder = new Retrofit.Builder().baseUrl(Constant.BASE_URL).client(okClient).addConverterFactory(GsonCustomConverterFactory.create(gson));
  }

  public <S> S createService(Class<S> serviceClass) {
    return adapterBuilder.build().create(serviceClass);
  }
}
