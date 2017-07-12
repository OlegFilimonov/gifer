package com.olgefilimonov.gifer.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.olgefilimonov.gifer.singleton.Constant;
import java.util.concurrent.TimeUnit;
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
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();

    // Add logging inteceptor
    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    logging.setLevel(Constant.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

    OkHttpClient okClient =
        new OkHttpClient.Builder().connectTimeout(120, TimeUnit.SECONDS).readTimeout(120, TimeUnit.SECONDS).writeTimeout(120, TimeUnit.SECONDS).addInterceptor(logging).build();

    String baseUrl = Constant.BASE_URL;

    if (!baseUrl.endsWith("/")) baseUrl = baseUrl + "/";

    adapterBuilder = new Retrofit.Builder().baseUrl(baseUrl).client(okClient).addConverterFactory(GsonCustomConverterFactory.create(gson));
  }

  public <S> S createService(Class<S> serviceClass) {
    return adapterBuilder.build().create(serviceClass);
  }
}
