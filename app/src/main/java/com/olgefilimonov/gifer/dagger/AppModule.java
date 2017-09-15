package com.olgefilimonov.gifer.dagger;

import android.content.Context;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.olgefilimonov.gifer.entity.MyObjectBox;
import dagger.Module;
import dagger.Provides;
import io.objectbox.BoxStore;
import javax.inject.Singleton;

/**
 * Dagger module for all dependencies
 *
 * @author Oleg Filimonov
 */
@Module public class AppModule {

  private Context context;

  public AppModule(Context context) {
    this.context = context;
  }

  @Provides @Singleton BoxStore provideBoxStore() {
    return MyObjectBox.builder().androidContext(context).build();
  }

  @Provides @Singleton JobManager provideJobManager() {
    // Custom configuration if needed
    Configuration.Builder builder = new Configuration.Builder(context).minConsumerCount(1)//always keep at least one consumer alive
        .maxConsumerCount(3)//up to 3 consumers at a time
        .loadFactor(3)//3 jobs per consumer
        .consumerKeepAlive(120);//wait 2 minute

    return new JobManager(builder.build());
  }
}
