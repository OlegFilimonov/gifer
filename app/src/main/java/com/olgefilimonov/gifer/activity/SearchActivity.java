package com.olgefilimonov.gifer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.olgefilimonov.gifer.R;
import com.olgefilimonov.gifer.adapter.SearchResultAdapter;
import com.olgefilimonov.gifer.contract.SearchContract;
import com.olgefilimonov.gifer.listener.EndlessRecyclerGridOnScrollListener;
import com.olgefilimonov.gifer.model.Gif;
import com.olgefilimonov.gifer.presenter.SearchPresenter;
import com.olgefilimonov.gifer.singleton.Constant;
import java.util.ArrayList;
import java.util.List;

import static com.olgefilimonov.gifer.activity.GifDetailActivity.GIF_ID_EXTRA;

/**
 * @author Oleg Filimonov
 */
public class SearchActivity extends AppCompatActivity implements SearchContract.View {
  public static final int REQUEST_GIF_DETAIL = 974;
  @BindView(R.id.floating_search_view) FloatingSearchView floatingSearchView;
  @BindView(R.id.empty_text_view) TextView emptyTextView;
  @BindView(R.id.recycler_view) RecyclerView searchResultsRecyclerView;
  private List<Gif> gifs = new ArrayList<>();
  private String query;
  private int skip = 0;
  private SearchResultAdapter adapter;
  private EndlessRecyclerGridOnScrollListener endlessListener;

  private SearchContract.Presenter presenter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search);
    ButterKnife.bind(this);

    // Setup Presenter
    new SearchPresenter(this);
    // Setup recyclerView
    setupRecyclerView();
    // Setup API
    setupSearch();
  }

  private void setupSearch() {
    floatingSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
      @Override public void onSearchTextChanged(String oldQuery, String newQuery) {
        clearSearchResults();
        // Save query for the endless endlessListener
        query = newQuery;
        presenter.loadGifs(query, skip, Constant.SEARCH_LIMIT);
      }
    });
  }

  private void setupRecyclerView() {
    GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
    endlessListener = new EndlessRecyclerGridOnScrollListener(layoutManager) {
      @Override public void onLoadMore(int current_page) {
        loadNextDataFromApi(current_page);
      }
    };
    adapter = new SearchResultAdapter(gifs, this, new SearchResultAdapter.RateListener() {
      @Override public void onVote(Gif gif, int rating) {
        presenter.rateGif(gif, rating);
      }
    });
    searchResultsRecyclerView.setLayoutManager(layoutManager);
    searchResultsRecyclerView.setAdapter(adapter);
    searchResultsRecyclerView.addOnScrollListener(endlessListener);
    searchResultsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        // Hide keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(recyclerView.getWindowToken(), 0);
      }
    });
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_GIF_DETAIL && resultCode == RESULT_OK) {
      // Update opened gif's rating
      String gifId = data.getStringExtra(GIF_ID_EXTRA);
      presenter.updateGifRating(gifId);
    }
  }

  /**
   * Loads next page of the gifs
   *
   * @param current_page page, starts from 1
   */
  private void loadNextDataFromApi(int current_page) {
    if (Constant.DEBUG) Log.d("Listener", "onLoadMore: " + current_page);
    skip = (current_page - 1) * Constant.SEARCH_LIMIT;
    presenter.loadGifs(query, skip, Constant.SEARCH_LIMIT);
  }

  @Override public void clearSearchResults() {
    skip = 0;
    adapter.notifyItemRangeRemoved(0, adapter.getItemCount());
    gifs.clear();
    endlessListener.reset();
  }

  @Override public void showSearchResults(final List<Gif> newGifs) {
    gifs.addAll(newGifs);
    adapter.notifyItemRangeInserted(adapter.getItemCount() - 1, newGifs.size());
    emptyTextView.setVisibility(adapter == null || adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
  }

  @Override public void updateGifRating(final String gifId, final int newRating) {
    adapter.updateGifRating(gifId, newRating);
  }

  @Override public void showProgress() {
    floatingSearchView.showProgress();
  }

  @Override public void hideProgress() {
    floatingSearchView.hideProgress();
  }

  @Override public void showError() {
    Toast.makeText(SearchActivity.this, "Error!", Toast.LENGTH_SHORT).show();
  }

  @Override public void setPresenter(SearchContract.Presenter presenter) {
    this.presenter = presenter;
  }
}
