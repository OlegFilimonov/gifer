package com.olgefilimonov.gifer.job;

import com.birbit.android.jobqueue.Params;
import com.olgefilimonov.gifer.reporitory.GifRepository;
import com.olgefilimonov.gifer.singleton.App;
import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;
import javax.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Oleg Filimonov
 */

public class CheckGifRatingJob extends UseCase<CheckGifRatingJob.Request, CheckGifRatingJob.Response> {
  @Inject GifRepository gifRepository;

  public CheckGifRatingJob(Request request, DisposableObserver<Response> observer, Params params) {
    super(request, observer, params);
    App.getInstance().getComponent().inject(this);
  }

  @Override protected Observable<Response> buildObservable(Request request) {
    return gifRepository.getGifRating(request.getGifId()).map(this::convertToResponse);
  }

  private Response convertToResponse(Integer rating) {
    return new Response(request.getGifId(), rating);
  }

  @Getter @Setter @AllArgsConstructor public static final class Request implements UseCase.Request {
    private String gifId;
  }

  @Getter @Setter @AllArgsConstructor public static final class Response implements UseCase.Response {
    private String gifId;
    private int newRating;
  }
}
