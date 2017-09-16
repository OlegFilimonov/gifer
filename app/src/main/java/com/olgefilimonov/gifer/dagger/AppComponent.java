package com.olgefilimonov.gifer.dagger;

import com.olgefilimonov.gifer.job.CheckGifRatingJob;
import com.olgefilimonov.gifer.job.LoadGifsJob;
import com.olgefilimonov.gifer.job.RateGifJob;
import com.olgefilimonov.gifer.presenter.GifDetailPresenter;
import com.olgefilimonov.gifer.presenter.SearchPresenter;
import dagger.Component;
import javax.inject.Singleton;

/**
 * @author Oleg Filimonov
 */
@Singleton @Component(modules = { AppModule.class, ApiModule.class }) public interface AppComponent {
  void inject(SearchPresenter presenter);

  void inject(GifDetailPresenter presenter);

  void inject(LoadGifsJob job);

  void inject(RateGifJob job);

  void inject(CheckGifRatingJob job);
}
