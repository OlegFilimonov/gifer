package com.olgefilimonov.gifer.listener;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.olgefilimonov.gifer.singleton.Constant;

public abstract class EndlessRecyclerGridOnScrollListener extends RecyclerView.OnScrollListener {
  public static String TAG = EndlessRecyclerGridOnScrollListener.class.getSimpleName();

  private int previousTotal = 0;
  private boolean loading = true;
  int firstVisibleItem, visibleItemCount, totalItemCount;
  private int visibleThreshold = Constant.VISIBLE_TRESHHOLD; // The minimum amount of items to have below your current scroll position before loading more.

  private int current_page = 1;

  private GridLayoutManager mLinearLayoutManager;

  public EndlessRecyclerGridOnScrollListener(GridLayoutManager linearLayoutManager) {
    this.mLinearLayoutManager = linearLayoutManager;
  }

  @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
    super.onScrolled(recyclerView, dx, dy);

    visibleItemCount = recyclerView.getChildCount();
    totalItemCount = mLinearLayoutManager.getItemCount();
    firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

    if (loading) {
      if (totalItemCount > previousTotal) {
        loading = false;
        previousTotal = totalItemCount;
      }
    }
    if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
      // End has been reached

      // Do something
      current_page++;

      onLoadMore(current_page);

      loading = true;
    }
  }

  public abstract void onLoadMore(int current_page);

  public void reset() {
    previousTotal = 0;
    current_page = 1;
    loading = true;
  }
}