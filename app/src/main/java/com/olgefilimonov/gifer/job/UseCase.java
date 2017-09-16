package com.olgefilimonov.gifer.job;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import timber.log.Timber;

/**
 * Use cases are the entry points to the domain layer.
 *
 * @author Oleg Filimonov
 */
public abstract class UseCase<Q extends UseCase.Request, P> extends Job {

  Q request;
  private DisposableObserver<P> observer;

  UseCase(Q request, DisposableObserver<P> observer, Params params) {
    super(params);
    this.request = request;
    this.observer = observer;
  }

  @Override public void onRun() throws Throwable {
    buildObservable(request).observeOn(AndroidSchedulers.mainThread()).subscribeWith(observer);
  }

  protected abstract Observable<P> buildObservable(Q requestValues);

  @Override protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
    Timber.d("Job %s cancelled", getClass().getSimpleName());
  }

  @Override public void onAdded() {
    Timber.d("Job %s added", getClass().getSimpleName());
  }

  @Override protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {

    // TODO: 14-Jul-17 Put some logic here to retry if we got 500

    // Cancel
    return RetryConstraint.CANCEL;
  }

  interface Request {
  }

  interface Response {
  }
}