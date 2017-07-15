package com.olgefilimonov.gifer.controller;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.bluelinelabs.conductor.Controller;

/**
 * @author not Oleg Filimonov
 */

public abstract class BaseController extends Controller {
  private Unbinder unbinder;

  protected BaseController() {
  }

  protected abstract View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container);

  @NonNull @Override protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
    View view = inflateView(inflater, container);
    unbinder = ButterKnife.bind(this, view);
    onViewBound(view);
    return view;
  }

  protected void onViewBound(@NonNull View view) {
  }

  @Override protected void onDestroyView(@NonNull View view) {
    super.onDestroyView(view);
    unbinder.unbind();
    unbinder = null;
  }
}
