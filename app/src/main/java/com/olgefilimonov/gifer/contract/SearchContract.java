package com.olgefilimonov.gifer.contract;

import com.olgefilimonov.gifer.entity.GifEntity;
import com.olgefilimonov.gifer.presenter.BasePresenter;
import com.olgefilimonov.gifer.view.BaseView;
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
    void updateGifRating(String gifId);

    /**
     * Changes user's rating of the gif. Can be executed multiple times by the same user
     *
     * @param gifId gif to rate
     * @param rating can be -1 (downvote) or 1 (upvote)
     */
    void rateGif(String gifId, int rating);
  }

  public interface View extends BaseView<Presenter> {

    void clearSearchResults();

    /**
     * Adds search results to the bottom of the list
     *
     * @param gifEntities search results to add
     */
    void showSearchResults(List<GifEntity> gifEntities);

    /**
     * Updates rating of the search result in the list, if present
     *
     * @param gifId id of the gif to set new rating
     * @param newRating new rating to set
     */
    void showGifRating(String gifId, int newRating);

    void showProgress();

    void showError();

    void hideProgress();
  }
}
