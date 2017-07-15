package com.olgefilimonov.gifer.dagger;

import com.olgefilimonov.gifer.mvp.UseCase;
import com.olgefilimonov.gifer.presenter.GifDetailPresenter;
import com.olgefilimonov.gifer.presenter.SearchPresenter;
import dagger.Component;
import javax.inject.Singleton;

/**
 * @author Oleg Filimonov
 */
@Singleton @Component(modules = { AppModule.class, ApiModule.class }) public interface AppComponent {
  void inject(GifDetailPresenter presenter);

  void inject(SearchPresenter presenter);

  void inject(UseCase<UseCase.RequestValues, UseCase.ResponseValue> useCase);
}
