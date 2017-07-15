package com.olgefilimonov.gifer.singleton;

import android.app.Application;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.olgefilimonov.gifer.client.ApiClient;
import com.olgefilimonov.gifer.model.MyObjectBox;
import io.objectbox.BoxStore;

/**
 * @author Oleg Filimonov
 */

public class GiferApplication extends Application {

  private static GiferApplication instance;
  private BoxStore boxStore;
  private JobManager jobManager;
  private ApiClient apiClient;

  public static synchronized GiferApplication getInstance() {
    return instance;
  }

  @Override public void onCreate() {
    super.onCreate();
    instance = this;
    configureApiClient();
    configureBoxStore();
    configureJobManager();
  }

  private void configureBoxStore() {
    boxStore = MyObjectBox.builder().androidContext(this).build();
  }

  private void configureJobManager() {
    // Custom configuration if needed
    Configuration.Builder builder = new Configuration.Builder(this).minConsumerCount(1)//always keep at least one consumer alive
        .maxConsumerCount(3)//up to 3 consumers at a time
        .loadFactor(3)//3 jobs per consumer
        .consumerKeepAlive(120);//wait 2 minute

    jobManager = new JobManager(builder.build());
  }

  private void configureApiClient() {
    apiClient = new ApiClient();
  }

  public synchronized JobManager getJobManager() {
    if (jobManager == null) {
      configureJobManager();
    }
    return jobManager;
  }

  public synchronized ApiClient getApiClient() {
    if (apiClient == null) {
      configureApiClient();
    }
    return apiClient;
  }

  public synchronized BoxStore getBoxStore() {
    if (boxStore == null) {
      configureBoxStore();
    }
    return boxStore;
  }
}
