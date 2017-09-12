package com.olgefilimonov.gifer.mvp.contract;

import com.olgefilimonov.gifer.mvp.BasePresenter;
import com.olgefilimonov.gifer.mvp.BaseView;

/**
 * @author Oleg Filimonov
 */

public class GifDetailContract {
  public interface Presenter extends BasePresenter {
    /**
     * Updates the rating from repo on one gif only
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
    /**
     * Updates rating the opened gif
     *
     * @param newRating new rating to set
     */
    void showGifRating(int newRating);

    void showError();
  }
}
