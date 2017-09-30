package com.olgefilimonov.gifer.data.api;

import com.google.gson.GsonBuilder;
import com.olgefilimonov.gifer.app.AppConfig;
import java.util.concurrent.TimeUnit;
import lombok.val;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

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
    val logging = new HttpLoggingInterceptor();
    logging.setLevel(
        AppConfig.DEBUG ? HttpLoggingInterceptor.Level.BASIC : HttpLoggingInterceptor.Level.NONE);

    val okClient = new OkHttpClient.Builder().connectTimeout(120, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .addInterceptor(logging)
        .build();

    adapterBuilder = new Retrofit.Builder().baseUrl(AppConfig.BASE_URL)
        .client(okClient)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverter.create(gson));
  }

  public <S> S createService(Class<S> serviceClass) {
    return adapterBuilder.build().create(serviceClass);
  }
}
