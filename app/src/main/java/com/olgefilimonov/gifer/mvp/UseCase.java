package com.olgefilimonov.gifer.mvp;

import android.content.Context;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.olgefilimonov.gifer.client.DefaultApi;
import com.olgefilimonov.gifer.singleton.Constant;
import java.util.UUID;

/**
 * Use cases are the entry points to the domain layer.
 *
 * @param <Q> the request type
 * @param <P> the response type
 * @author Oleg Filimonov
 */
public abstract class UseCase<Q extends UseCase.RequestValues, P extends UseCase.ResponseValue> extends Job {
  private static final String TAG = "JOB";
  protected DefaultApi defaultApi;
  private Context context;
  private Q requestValues;
  private UseCaseCallback<P> useCaseCallback;

  protected UseCase() {
    this(Constant.DEFAULT_PRIORITY);
  }

  protected UseCase(int priority) {
    super(new Params(priority).addTags(UUID.randomUUID().toString()));
  }

  public Q getRequestValues() {
    return requestValues;
  }

  public void setRequestValues(Q requestValues) {
    this.requestValues = requestValues;
  }

  public UseCaseCallback<P> getUseCaseCallback() {
    return useCaseCallback;
  }

  public void setUseCaseCallback(UseCaseCallback<P> useCaseCallback) {
    this.useCaseCallback = useCaseCallback;
  }

  @Override public void onRun() throws Throwable {
    executeUseCase(requestValues);
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
