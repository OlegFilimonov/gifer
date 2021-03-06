package com.olgefilimonov.gifer.app;

import com.olgefilimonov.gifer.BuildConfig;

/**
 * @author Oleg Filimonov
 */

public class AppConfig {

  public static final boolean DEBUG = BuildConfig.FLAVOR.equals("develop");

  public static final String GIPHER_API_KEY = "7ef43d1e15c745b88ee7c9b7503c2f36";
  public static final String BASE_URL = "https://api.giphy.com/";
  public static final String API_V1 = "/v1/";

  public static final int SEARCH_LIMIT = 20;
  /**
   * The minimum amount of items to have BELOW your current scroll position before loading more.
   */
  public static final int VISIBLE_TRESHHOLD = 10;

  public static final int DEFAULT_PRIORITY = 0;
}
