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

    /**
     * Updates the rating from repo on one gif only
     * Should be executed when user rates the gif from somewhere else then the search page
     */
    void updateGifRating(Gif gif);

    /**
     * Changes user's rating of the gif. Can be executed multiple times by the same user
     *
     * @param gif gif to rate
     * @param rating can be -1 (downvote) or 1 (upvote)
     */
    void rateGif(Gif gif, int rating);
  }

  public interface View extends BaseView<Presenter> {

    void clearSearchResults();

    void showSearchResults(List<Gif> gifs);

    void updateGifRating(String gifId, int newRating);

    void showProgress();

    void hideProgress();

    void showError();
  }
}
