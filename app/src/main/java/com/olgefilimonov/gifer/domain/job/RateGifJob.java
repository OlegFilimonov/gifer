package com.olgefilimonov.gifer.domain.job;

import com.birbit.android.jobqueue.Params;
import com.olgefilimonov.gifer.app.App;
import com.olgefilimonov.gifer.data.reporitory.GifRepository;
import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;
import javax.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Oleg Filimonov
 */

public class RateGifJob extends UseCase<RateGifJob.Request, RateGifJob.Response> {
  @Inject GifRepository gifRepository;

  public RateGifJob(Request request, DisposableObserver<Response> observer, Params params) {
    super(request, observer, params);
    App.getInstance().getComponent().inject(this);
  }

  @Override
  protected Observable<Response> buildObservable(Request requestValues) {
    return gifRepository.rateGif(requestValues.getGifId(), requestValues.getRating())
        .map(this::convertToResponse);
  }

  private Response convertToResponse(Integer newRating) {
    return new Response(request.getGifId(), newRating);
  }

  @Getter
  @Setter
  @AllArgsConstructor
  public static final class Request implements UseCase.Request {
    private String gifId;
    private int rating;
  }

  @Getter
  @Setter
  @AllArgsConstructor
  public static final class Response implements UseCase.Response {
    private String gifId;
    private int newRating;
  }
}
