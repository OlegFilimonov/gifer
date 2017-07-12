package com.olgefilimonov.gifer.adapter;

import com.olgefilimonov.gifer.singleton.Constant;

/**
 * @author Oleg Filimonov
 */

public abstract class SkipLimitRunnable implements Runnable {
  protected int skip = 0;
  protected int limit = Constant.SEARCH_LIMIT;

  public SkipLimitRunnable(int skip, int limit) {
    this.skip = skip;
    this.limit = limit;
  }

  public int getSkip() {
    return skip;
  }

  public void setSkip(int skip) {
    this.skip = skip;
  }

  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }
}
