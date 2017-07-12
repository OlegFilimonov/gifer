package com.olgefilimonov.gifer.singleton;

import android.app.Application;
import com.olgefilimonov.gifer.model.MyObjectBox;
import io.objectbox.BoxStore;

/**
 * @author Oleg Filimonov
 */

public class GiferApplication extends Application {

  public BoxStore boxStore;
  private static GiferApplication instance;

  public static synchronized GiferApplication getInstance() {
    return instance;
  }

  @Override public void onCreate() {
    super.onCreate();
    instance = this;

    boxStore = MyObjectBox.builder().androidContext(this).build();
  }

  public BoxStore getBoxStore() {
    return boxStore;
  }
}
