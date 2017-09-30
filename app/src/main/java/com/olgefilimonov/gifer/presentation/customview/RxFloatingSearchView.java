package com.olgefilimonov.gifer.presentation.customview;

import android.support.annotation.CheckResult;
import com.arlib.floatingsearchview.FloatingSearchView;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;

public final class RxFloatingSearchView {

  @CheckResult
  @NonNull
  public static Observable<CharSequence> queryChanges(@NonNull FloatingSearchView view) {
    return queryChanges(view, 0);
  }

  @CheckResult
  @NonNull
  public static Observable<CharSequence> queryChanges(@NonNull FloatingSearchView view,
      int characterLimit) {
    checkNotNull(view, "view == null");
    return new QueryObservable(view, characterLimit);
  }

  public static void checkNotNull(Object value, String message) {
    if (value == null) {
      throw new NullPointerException(message);
    }
  }
}