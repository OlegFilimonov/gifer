package com.olgefilimonov.gifer.data.api;

import android.support.annotation.NonNull;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.lang.reflect.Type;
import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * This wrapper is to take care of this case:
 * when the deserialization fails due to JsonParseException and the
 * expected type is String, then just return the body string
 */
public class GsonResponseConverter<T> implements Converter<ResponseBody, T> {
  private final Gson gson;
  private final Type type;

  GsonResponseConverter(Gson gson, Type type) {
    this.gson = gson;
    this.type = type;
  }

  @Override
  public T convert(@NonNull ResponseBody value) throws IOException {
    String returned = value.string();
    try {
      return gson.fromJson(returned, type);
    } catch (JsonParseException e) {
      return (T) returned;
    }
  }
}
