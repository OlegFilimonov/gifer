package com.olgefilimonov.gifer.presentation.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.olgefilimonov.gifer.R;
import com.olgefilimonov.gifer.app.AppConfig;
import com.olgefilimonov.gifer.domain.entity.GifEntity;
import com.olgefilimonov.gifer.presentation.adapter.SearchResultAdapter;
import com.olgefilimonov.gifer.presentation.contract.SearchContract;
import com.olgefilimonov.gifer.presentation.customview.RxFloatingSearchView;
import com.olgefilimonov.gifer.presentation.listener.EndlessRecyclerGridOnScrollListener;
import com.olgefilimonov.gifer.presentation.presenter.SearchPresenter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.val;

import static com.olgefilimonov.gifer.app.Utils.hideKeyboard;

/**
 * @author Oleg Filimonov
 */

public class SearchActivity extends BaseActivity<SearchContract.Presenter>
    implements SearchContract.View {

  @BindView(R.id.floating_search_view) FloatingSearchView floatingSearchView;
  @BindView(R.id.empty_text_view) TextView emptyTextView;
  @BindView(R.id.recycler_view) RecyclerView searchResultsRecyclerView;

  private List<GifEntity> gifEntities = new ArrayList<>();
  private String query;
  private int skip = 0;
  private SearchResultAdapter adapter;
  private EndlessRecyclerGridOnScrollListener endlessListener;
  /**
   * Stored id of the clicked gif in order to update it when gif detail page is closed
   */
  private String clickedGifId;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search);
    setPresenter(new SearchPresenter(this));

    setupRecyclerView();
    setupSearch();
  }

  @Override
  protected void onResume() {
    super.onResume();

    if (clickedGifId != null) {
      presenter.updateGifRating(clickedGifId);
      clickedGifId = null;
    }

    updateEmptyText();
  }

  private void setupSearch() {
    RxFloatingSearchView.queryChanges(floatingSearchView)
        .debounce(400, TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(charSequence -> {
          clearSearchResults();
          // Save query for the endless endlessListener
          query = charSequence.toString();
          presenter.loadGifs(query, skip, AppConfig.SEARCH_LIMIT);
        })
        .subscribe();
  }

  private void setupRecyclerView() {

    val layoutManager = new GridLayoutManager(this, 2);
    endlessListener = new EndlessRecyclerGridOnScrollListener(layoutManager) {
      @Override
      public void onLoadMore(int current_page) {
        loadNextDataFromApi(current_page);
      }
    };
    adapter =
        new SearchResultAdapter(gifEntities, this, new SearchResultAdapter.SearchAdapterListener() {
          @Override
          public void onItemRated(GifEntity gifEntity, int rating) {
            presenter.rateGif(gifEntity.getGifId(), rating);
          }

          @Override
          public void onItemClick(GifEntity gifEntity) {
            hideKeyboard(SearchActivity.this, searchResultsRecyclerView);
            // Save gifEntity id to update it later
            clickedGifId = gifEntity.getGifId();
            GifDetailActivity.start(SearchActivity.this, gifEntity.getVideoUrl(),
                gifEntity.getGifId());
          }
        });
    searchResultsRecyclerView.setLayoutManager(layoutManager);
    searchResultsRecyclerView.setAdapter(adapter);
    searchResultsRecyclerView.setHasFixedSize(true);
    searchResultsRecyclerView.addOnScrollListener(endlessListener);
    searchResultsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        // Hide keyboard
        hideKeyboard(SearchActivity.this, recyclerView);
      }
    });
  }

  /**
   * Loads next page of the list
   *
   * @param current_page page, starts from 1
   */
  private void loadNextDataFromApi(int current_page) {
    if (AppConfig.DEBUG) Log.d("Listener", "onLoadMore: " + current_page);
    skip = (current_page - 1) * AppConfig.SEARCH_LIMIT;
    presenter.loadGifs(query, skip, AppConfig.SEARCH_LIMIT);
  }

  private void updateEmptyText() {
    // Update empty text drawable
    boolean empty = gifEntities.isEmpty();
    if (empty) {
      emptyTextView.setVisibility(View.VISIBLE);
      // If text is empty show an empty string. Otherwise show "no results"
      if (query == null || query.equals("")) {
        emptyTextView.setText(R.string.search_empty);
      } else {
        String text = String.format(this.getString(R.string.search_no_results), query);
        emptyTextView.setText(text);
      }
    } else {
      emptyTextView.setVisibility(View.GONE);
    }
  }

  @Override
  public void clearSearchResults() {
    skip = 0;
    adapter.notifyItemRangeRemoved(0, adapter.getItemCount());
    gifEntities.clear();
    endlessListener.reset();
  }

  @Override
  public void showSearchResults(final List<GifEntity> newGifEntities) {
    gifEntities.addAll(newGifEntities);
    updateEmptyText();
    adapter.notifyItemRangeInserted(adapter.getItemCount() - 1, newGifEntities.size());
  }

  @Override
  public void showGifRating(final String gifId, final int newRating) {
    //adapter.updateGifRating(gifId, newRating);

    for (int i = 0; i < gifEntities.size(); i++) {
      val gif = gifEntities.get(i);
      if (gif.getGifId().equals(gifId)) {
        gif.setScore(newRating);
        ((SearchResultAdapter.SearchResultViewHolder) searchResultsRecyclerView.findViewHolderForAdapterPosition(
            i)).updateRating(newRating);
        return;
      }
    }
  }

  @Override
  public void showProgress() {
    floatingSearchView.showProgress();
  }

  @Override
  public void hideProgress() {
    floatingSearchView.hideProgress();
  }

  @Override
  public void showError() {
    Toast.makeText(this, R.string.general_error, Toast.LENGTH_SHORT).show();
  }
}
