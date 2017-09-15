package com.olgefilimonov.gifer.singleton;

import android.app.Application;
import com.olgefilimonov.gifer.dagger.ApiModule;
import com.olgefilimonov.gifer.dagger.AppComponent;
import com.olgefilimonov.gifer.dagger.AppModule;
import com.olgefilimonov.gifer.dagger.DaggerAppComponent;

/**
 * @author Oleg Filimonov
 */

public class App extends Application {

  private static App instance;
  private AppComponent component;

  public static synchronized App getInstance() {
    return instance;
  }

  @Override public void onCreate() {
    super.onCreate();
    instance = this;

    component = DaggerAppComponent.builder().appModule(new AppModule(getApplicationContext())).apiModule(new ApiModule()).build();
  }

  public AppComponent getComponent() {
    return component;
  }
}

