package com.olgefilimonov.gifer.dagger;

import android.content.Context;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.olgefilimonov.gifer.data.api.RestApi;
import com.olgefilimonov.gifer.data.reporitory.GifRepository;
import com.olgefilimonov.gifer.data.reporitory.GifRepositoryImpl;
import com.olgefilimonov.gifer.domain.entity.MyObjectBox;
import com.olgefilimonov.gifer.domain.entity.RatedGifEntity;
import dagger.Module;
import dagger.Provides;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import javax.inject.Singleton;

/**
 * Dagger module for all dependencies
 *
 * @author Oleg Filimonov
 */
@Module
public class AppModule {

  private Context context;

  public AppModule(Context context) {
    this.context = context;
  }

  @Provides
  @Singleton
  BoxStore provideBoxStore() {
    return MyObjectBox.builder().androidContext(context).build();
  }

  @Provides
  @Singleton
  JobManager provideJobManager() {
    // Custom configuration if needed
    Configuration.Builder builder = new Configuration.Builder(context).minConsumerCount(
        1)//always keep at least one consumer alive
        .maxConsumerCount(3)//up to 3 consumers at a time
        .loadFactor(3)//3 jobs per consumer
        .consumerKeepAlive(120);//wait 2 minute

    return new JobManager(builder.build());
  }

  @Provides
  @Singleton
  Box<RatedGifEntity> provideGifsBox(BoxStore boxStore) {
    return boxStore.boxFor(RatedGifEntity.class);
  }

  @Provides
  @Singleton
  GifRepository provideGifRepository(Box<RatedGifEntity> gifsBox, RestApi restApi) {
    return new GifRepositoryImpl(gifsBox, restApi);
  }
}
