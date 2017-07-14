package com.olgefilimonov.gifer.contract;

import com.olgefilimonov.gifer.model.Gif;
import com.olgefilimonov.gifer.mvp.BasePresenter;
import com.olgefilimonov.gifer.mvp.BaseView;
import java.util.List;

/**
 * @author Oleg Filimonov
 */

public class SearchContract {

  public interface Presenter extends BasePresenter {

    void loadGifs(String query, int page, int limit);

    void updateGifRating();
  }

  public interface View extends BaseView<Presenter> {

    void clearSearchResults();

    void showSearchResults(List<Gif> gifs);

    void showProgress();

    void hideProgress();

    void showEmptyText();

    void hideEmptyText();
  }
}
