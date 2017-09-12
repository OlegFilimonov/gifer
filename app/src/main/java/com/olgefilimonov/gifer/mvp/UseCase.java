package com.olgefilimonov.gifer.mvp;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.olgefilimonov.gifer.client.DefaultApi;
import com.olgefilimonov.gifer.singleton.Constant;
import com.olgefilimonov.gifer.singleton.GiferApplication;
import javax.inject.Inject;

/**
 * Use cases are the entry points to the domain layer.
 *
 * @author Oleg Filimonov
 */
public abstract class UseCase<Q extends UseCase.RequestValues, P extends UseCase.ResponseValue> extends Job {
  @Inject protected DefaultApi defaultApi;
  private UseCaseCallback<P> useCaseCallback;
  private Q requestValues;
  private Handler handler;

  protected UseCase(Q requestValues, UseCaseCallback<P> useCaseCallback, Params params) {
    super(params);
    this.requestValues = requestValues;
    this.useCaseCallback = useCaseCallback;
    this.handler = new Handler(Looper.getMainLooper());
    GiferApplication.getInstance().getComponent().inject((UseCase<RequestValues, ResponseValue>) this);
  }

  /**
   * Executed when usecase has completed successfully or if job was cancelled in the progress
   * This replaces thread pool scheduler to execute callbacks on the main thread
   */
  protected void onSuccess(final P response) {
    if (isCancelled()) {
      if (Constant.DEBUG) Log.d("USECASE", "onSuccess: cancelled");
    } else {
      handler.post(new Runnable() {
        @Override public void run() {
          useCaseCallback.onSuccess(response);
        }
      });
    }
  }

  protected void onError() {
    handler.post(new Runnable() {
      @Override public void run() {
        useCaseCallback.onError();
      }
    });
  }

  @Override public void onRun() throws Throwable {
    executeUseCase(requestValues);
  }

  @Override public void onAdded() {

  }

  @Override protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {

    // TODO: 14-Jul-17 Put some logic here to retry if we got 500

    // Cancel
    return RetryConstraint.CANCEL;
  }

  protected abstract void executeUseCase(Q requestValues) throws Throwable;

  /**
   * Data passed to a request.
   */
  public interface RequestValues {
  }

  /**
   * Data received from a request.
   */
  public interface ResponseValue {
  }

  public interface UseCaseCallback<R> {
    void onSuccess(R response);

    void onError();
  }
}
