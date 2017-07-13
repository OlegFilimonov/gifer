package com.olgefilimonov.gifer.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.olgefilimonov.gifer.R;
import com.olgefilimonov.gifer.adapter.SearchResultAdapter;
import com.olgefilimonov.gifer.adapter.SkipLimitRunnable;
import com.olgefilimonov.gifer.client.ApiClient;
import com.olgefilimonov.gifer.client.DefaultApi;
import com.olgefilimonov.gifer.listener.EndlessRecyclerGridOnScrollListener;
import com.olgefilimonov.gifer.model.Datum;
import com.olgefilimonov.gifer.model.Gif;
import com.olgefilimonov.gifer.model.GiphyResponse;
import com.olgefilimonov.gifer.singleton.Constant;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Oleg Filimonov
 */
public class SearchActivity extends AppCompatActivity {
  /* UI */
  @BindView(R.id.floating_search_view) FloatingSearchView floatingSearchView;
  @BindView(R.id.empty_textview) TextView emptyTextView;
  @BindView(R.id.recycler_view) RecyclerView searchResultsRecyclerView;
  /* API */
  private List<Gif> gifs = new ArrayList<>();
  private String query;
  private int skip = 0;
  private DefaultApi api;
  private SearchResultAdapter adapter;
  private SkipLimitRunnable loadNextDataRunnable;
  private Handler handler = new Handler();
  private EndlessRecyclerGridOnScrollListener endlessListener;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search);
    ButterKnife.bind(this);

    // Setup dependencies
    api = new ApiClient().createService(DefaultApi.class);

    // Setup recyclerView
    GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
    endlessListener = new EndlessRecyclerGridOnScrollListener(layoutManager) {
      @Override public void onLoadMore(int current_page) {
        loadNextDataFromApi(current_page);
      }
    };
    adapter = new SearchResultAdapter(gifs, this);
    searchResultsRecyclerView.setLayoutManager(layoutManager);
    searchResultsRecyclerView.setAdapter(adapter);
    searchResultsRecyclerView.addOnScrollListener(endlessListener);

    // Setup API
    floatingSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
      @Override public void onSearchTextChanged(String oldQuery, String newQuery) {
        // Save query for the endless endlessListener
        query = newQuery;
        // Show progress icon in the search view
        floatingSearchView.showProgress();

        // Clear gifs and let endless endlessListener do it's job
        skip = 0;
        gifs.clear();
        adapter.notifyDataSetChanged();
        endlessListener.reset();

        loadNextDataFromApi(1);
      }
    });

    loadNextDataRunnable = new SkipLimitRunnable(skip, Constant.SEARCH_LIMIT) {
      @Override public void run() {
        api.searchGifs(Constant.GIPHER_API_KEY, query, limit, skip).enqueue(new Callback<GiphyResponse>() {
          @Override public void onResponse(Call<GiphyResponse> call, Response<GiphyResponse> response) {
            List<Gif> newGifs = new ArrayList<Gif>();

            // Convert gifs to the local model
            for (Datum datum : response.body().getData()) {
              String previewUrl = datum.getImages().getDownsized().getUrl();
              String videoUrl = datum.getImages().getOriginalMp4().getMp4();
              Gif gif = new Gif(datum.getId(), videoUrl, previewUrl);
              newGifs.add(gif);
            }

            gifs.addAll(newGifs);
            adapter.notifyDataSetChanged();
            adapter.updateGifRating();
            floatingSearchView.hideProgress();
          }

          @Override public void onFailure(Call<GiphyResponse> call, Throwable t) {

          }
        });
      }
    };
  }

  @Override protected void onResume() {
    super.onResume();
    if (adapter != null) adapter.updateGifRating();
  }

  private void loadNextDataFromApi(int current_page) {
    if (Constant.DEBUG) Log.d("Listener", "onLoadMore: " + current_page);
    skip = (current_page - 1) * Constant.SEARCH_LIMIT;
    handler.removeCallbacks(loadNextDataRunnable);
    handler.post(loadNextDataRunnable);
  }
}
