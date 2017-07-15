package com.olgefilimonov.gifer.mvp;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
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
  private static final String TAG = "JOB";
  protected Q requestValues;
  @Inject protected DefaultApi defaultApi;
  private UseCaseCallback<P> useCaseCallback;
  private Handler handler;

  protected UseCase(Q requestValues, String tag, UseCaseCallback<P> useCaseCallback) {
    this(requestValues, Constant.DEFAULT_PRIORITY, tag, useCaseCallback);
  }

  protected UseCase(Q requestValues, int priority, String tag, UseCaseCallback<P> useCaseCallback) {
    super(new Params(priority).addTags(tag));
    this.requestValues = requestValues;
    this.useCaseCallback = useCaseCallback;
    this.handler = new Handler(Looper.getMainLooper());
    GiferApplication.getInstance().getComponent().inject((UseCase<RequestValues, ResponseValue>) this);
  }

  @Override public void onAdded() {

  }

  @Override public void onRun() throws Throwable {
    executeUseCase(requestValues);
  }

  @Override protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {

    // TODO: 14-Jul-17 Put some logic here to retry if we got 500

    // Cancel
    return RetryConstraint.CANCEL;
  }

  /**
   * Should be executed when usecase has completed successfully
   * This replaces thread pool scheduler to execute callbacks on the main thread
   */
  protected void onSuccess(final P response) {
    handler.post(new Runnable() {
      @Override public void run() {
        useCaseCallback.onSuccess(response);
      }
    });
  }

  protected void onError() {
    handler.post(new Runnable() {
      @Override public void run() {
        useCaseCallback.onError();
      }
    });
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
