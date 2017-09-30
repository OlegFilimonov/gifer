package com.olgefilimonov.gifer.data.api;

import com.google.gson.Gson;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Oleg Filimonov
 */
public class GsonConverter extends Converter.Factory {
  private final Gson gson;
  private final GsonConverterFactory gsonConverterFactory;

  private GsonConverter(Gson gson) {
    if (gson == null) throw new NullPointerException("gson == null");
    this.gson = gson;
    this.gsonConverterFactory = GsonConverterFactory.create(gson);
  }

  public static GsonConverter create(Gson gson) {
    return new GsonConverter(gson);
  }

  @Override
  public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
      Retrofit retrofit) {
    if (type.equals(String.class)) {
      return new GsonResponseConverter<Object>(gson, type);
    } else {
      return gsonConverterFactory.responseBodyConverter(type, annotations, retrofit);
    }
  }

  @Override
  public Converter<?, RequestBody> requestBodyConverter(Type type,
      Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
    return gsonConverterFactory.requestBodyConverter(type, parameterAnnotations, methodAnnotations,
        retrofit);
  }
}
