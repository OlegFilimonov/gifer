package com.olgefilimonov.gifer.dagger;

import com.olgefilimonov.gifer.api.ApiClient;
import com.olgefilimonov.gifer.api.RestApi;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/**
 * Dagger module for everything API related
 *
 * @author Oleg Filimonov
 */

@Module public class ApiModule {

  public ApiModule() {
  }

  @Provides @Singleton ApiClient provideApiClient() {
    return new ApiClient();
  }

  @Provides @Singleton RestApi provideDefaultApi(ApiClient apiClient) {
    return apiClient.createService(RestApi.class);
  }
}
