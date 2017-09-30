package com.olgefilimonov.gifer.app;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import lombok.val;

/**
 * @author Oleg Filimonov
 */

public class Utils {
  public static void hideKeyboard(Context context, View view) {
    val imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
  }
}
