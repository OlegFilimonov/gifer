package com.olgefilimonov.gifer.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.olgefilimonov.gifer.R;
import com.olgefilimonov.gifer.adapter.SearchResultAdapter;
import com.olgefilimonov.gifer.listener.EndlessRecyclerGridOnScrollListener;
import com.olgefilimonov.gifer.model.Gif;
import com.olgefilimonov.gifer.mvp.contract.SearchContract;
import com.olgefilimonov.gifer.presenter.SearchPresenter;
import com.olgefilimonov.gifer.rxjava.RxFloatingSearchView;
import com.olgefilimonov.gifer.singleton.Constant;
import io.reactivex.functions.Consumer;
import java.util.ArrayList;
import java.util.List;
import lombok.val;

/**
 * @author Oleg Filimonov
 */

public class SearchActivity extends BaseActivity<SearchContract.Presenter> implements SearchContract.View {

  @BindView(R.id.floating_search_view) FloatingSearchView floatingSearchView;
  @BindView(R.id.empty_text_view) TextView emptyTextView;
  @BindView(R.id.recycler_view) RecyclerView searchResultsRecyclerView;

  private List<Gif> gifs = new ArrayList<>();
  private String query;
  private int skip = 0;
  private SearchResultAdapter adapter;
  private EndlessRecyclerGridOnScrollListener endlessListener;
  /**
   * Stored id of the clicked gif in order to update it when gif detail page is closed
   */
  private String clickedGifId;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search);
    setPresenter(new SearchPresenter(this));

    setupRecyclerView();
    setupSearch();
  }

  @Override protected void onResume() {
    super.onResume();

    if (clickedGifId != null) {
      presenter.updateGifRating(clickedGifId);
      clickedGifId = null;
    }
    updateEmptyText();
  }

  private void setupSearch() {
    RxFloatingSearchView.queryChanges(floatingSearchView).doOnNext(new Consumer<CharSequence>() {
      @Override public void accept(@io.reactivex.annotations.NonNull CharSequence charSequence) throws Exception {
        clearSearchResults();
        // Save query for the endless endlessListener
        query = charSequence.toString();
        presenter.loadGifs(query, skip, Constant.SEARCH_LIMIT);
      }
    }).subscribe();
  }

  private void setupRecyclerView() {

    val layoutManager = new GridLayoutManager(this, 2);
    endlessListener = new EndlessRecyclerGridOnScrollListener(layoutManager) {
      @Override public void onLoadMore(int current_page) {
        loadNextDataFromApi(current_page);
      }
    };
    adapter = new SearchResultAdapter(gifs, this, new SearchResultAdapter.SearchAdapterListener() {
      @Override public void onItemRated(Gif gif, int rating) {
        presenter.rateGif(gif.getGifId(), rating);
      }

      @Override public void onItemClick(Gif gif) {
        // Save gif id to update it later
        clickedGifId = gif.getGifId();
        GifDetailActivity.start(SearchActivity.this, gif.getVideoUrl(), gif.getGifId());
      }
    });
    searchResultsRecyclerView.setLayoutManager(layoutManager);
    searchResultsRecyclerView.setAdapter(adapter);
    searchResultsRecyclerView.setHasFixedSize(true);
    searchResultsRecyclerView.addOnScrollListener(endlessListener);
    searchResultsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        // Hide keyboard
        val imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(recyclerView.getWindowToken(), 0);
      }
    });
  }

  /**
   * Loads next page of the list
   *
   * @param current_page page, starts from 1
   */
  private void loadNextDataFromApi(int current_page) {
    if (Constant.DEBUG) Log.d("Listener", "onLoadMore: " + current_page);
    skip = (current_page - 1) * Constant.SEARCH_LIMIT;
    presenter.loadGifs(query, skip, Constant.SEARCH_LIMIT);
  }

  private void updateEmptyText() {
    // Update empty text drawable
    boolean empty = gifs.isEmpty();
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

  @Override public void clearSearchResults() {
    skip = 0;
    adapter.notifyItemRangeRemoved(0, adapter.getItemCount());
    gifs.clear();
    endlessListener.reset();
  }

  @Override public void showSearchResults(final List<Gif> newGifs) {
    gifs.addAll(newGifs);
    updateEmptyText();
    adapter.notifyItemRangeInserted(adapter.getItemCount() - 1, newGifs.size());
  }

  @Override public void showGifRating(final String gifId, final int newRating) {
    adapter.updateGifRating(gifId, newRating);
  }

  @Override public void showProgress() {
    floatingSearchView.showProgress();
  }

  @Override public void hideProgress() {
    floatingSearchView.hideProgress();
  }

  @Override public void showError() {
    Toast.makeText(this, R.string.general_error, Toast.LENGTH_SHORT).show();
  }
}
