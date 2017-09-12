package com.olgefilimonov.gifer.dagger;

import com.olgefilimonov.gifer.mvp.BasePresenter;
import com.olgefilimonov.gifer.mvp.UseCase;
import dagger.Component;
import javax.inject.Singleton;

/**
 * @author Oleg Filimonov
 */
@Singleton @Component(modules = { AppModule.class, ApiModule.class }) public interface AppComponent {
  void inject(BasePresenter presenter);

  void inject(UseCase<UseCase.RequestValues, UseCase.ResponseValue> useCase);
}
