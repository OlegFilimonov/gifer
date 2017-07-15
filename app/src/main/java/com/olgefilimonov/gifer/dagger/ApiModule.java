package com.olgefilimonov.gifer.dagger;

import com.olgefilimonov.gifer.client.ApiClient;
import com.olgefilimonov.gifer.client.DefaultApi;
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

  @Provides @Singleton DefaultApi provideDefaultApi(ApiClient apiClient) {
    return apiClient.createService(DefaultApi.class);
  }
}
