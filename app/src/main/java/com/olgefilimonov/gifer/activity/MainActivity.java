package com.olgefilimonov.gifer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bluelinelabs.conductor.Conductor;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.olgefilimonov.gifer.R;
import com.olgefilimonov.gifer.controller.SearchController;

public class MainActivity extends AppCompatActivity {

  @BindView(R.id.controller_container) ViewGroup container;

  private Router router;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    router = Conductor.attachRouter(this, container, savedInstanceState);
    if (!router.hasRootController()) {
      router.setRoot(RouterTransaction.with(new SearchController()));
    }
  }

  @Override public void onBackPressed() {
    if (!router.handleBack()) {
      super.onBackPressed();
    }
  }
}
