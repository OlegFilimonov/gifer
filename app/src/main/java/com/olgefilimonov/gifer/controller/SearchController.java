package com.olgefilimonov.gifer.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
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

/**
 * @author Oleg Filimonov
 */

public class SearchController extends BaseController implements SearchContract.View {

  public static final int REQUEST_GIF_DETAIL = 974;
  @BindView(R.id.floating_search_view) FloatingSearchView floatingSearchView;
  @BindView(R.id.empty_text_view) TextView emptyTextView;
  @BindView(R.id.recycler_view) RecyclerView searchResultsRecyclerView;
  private SearchContract.Presenter presenter;
  private List<Gif> gifs = new ArrayList<>();
  private String query;
  private int skip = 0;
  private SearchResultAdapter adapter;
  private EndlessRecyclerGridOnScrollListener endlessListener;

  @Override protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
    return inflater.inflate(R.layout.activity_search, container, false);
  }

  @Override protected void onViewBound(@NonNull View view) {
    super.onViewBound(view);

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
    GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
    endlessListener = new EndlessRecyclerGridOnScrollListener(layoutManager) {
      @Override public void onLoadMore(int current_page) {
        loadNextDataFromApi(current_page);
      }
    };
    adapter = new SearchResultAdapter(gifs, getActivity(), new SearchResultAdapter.SearchAdapterListener() {
      @Override public void onItemRated(Gif gif, int rating) {
        presenter.rateGif(gif.getGifId(), rating);
      }

      @Override public void onItemClick(Gif gif) {
        //Intent intent = new Intent(activity, GifDetailActivity.class);
        //intent.putExtra(URL_EXTRA, gif.getVideoUrl());
        //intent.putExtra(GIF_ID_EXTRA, gif.getGifId());
        //activity.startActivityForResult(intent, REQUEST_GIF_DETAIL);

      }
    });
    searchResultsRecyclerView.setLayoutManager(layoutManager);
    searchResultsRecyclerView.setAdapter(adapter);
    searchResultsRecyclerView.addOnScrollListener(endlessListener);
    searchResultsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        // Hide keyboard
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(recyclerView.getWindowToken(), 0);
      }
    });
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
    Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
  }

  @Override public void setPresenter(SearchContract.Presenter presenter) {
    this.presenter = presenter;
  }
}
