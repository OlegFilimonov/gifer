package com.olgefilimonov.gifer.activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.olgefilimonov.gifer.mvp.BasePresenter;
import com.olgefilimonov.gifer.mvp.BaseView;
import icepick.Icepick;

/**
 * @author not Oleg Filimonov
 */

public abstract class BaseActivity<T extends BasePresenter> extends AppCompatActivity implements BaseView<T> {
  protected T presenter;
  private Unbinder unbinder;

  public void setPresenter(T presenter) {
    this.presenter = presenter;
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Icepick.restoreInstanceState(this, savedInstanceState);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    Icepick.saveInstanceState(this, outState);
  }

  @Override public void setContentView(@LayoutRes int layoutResID) {
    super.setContentView(layoutResID);
    unbinder = ButterKnife.bind(this);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    unbinder.unbind();
  }
}
