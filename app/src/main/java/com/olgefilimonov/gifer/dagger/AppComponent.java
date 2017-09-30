package com.olgefilimonov.gifer.dagger;

import com.olgefilimonov.gifer.domain.job.CheckGifRatingJob;
import com.olgefilimonov.gifer.domain.job.LoadGifsJob;
import com.olgefilimonov.gifer.domain.job.RateGifJob;
import com.olgefilimonov.gifer.presentation.presenter.GifDetailPresenter;
import com.olgefilimonov.gifer.presentation.presenter.SearchPresenter;
import dagger.Component;
import javax.inject.Singleton;

/**
 * @author Oleg Filimonov
 */
@Singleton
@Component(modules = { AppModule.class, ApiModule.class })
public interface AppComponent {
  void inject(SearchPresenter presenter);

  void inject(GifDetailPresenter presenter);

  void inject(LoadGifsJob job);

  void inject(RateGifJob job);

  void inject(CheckGifRatingJob job);
}
