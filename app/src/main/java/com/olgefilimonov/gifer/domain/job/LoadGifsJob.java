package com.olgefilimonov.gifer.domain.job;

import com.birbit.android.jobqueue.Params;
import com.olgefilimonov.gifer.app.App;
import com.olgefilimonov.gifer.data.reporitory.GifRepository;
import com.olgefilimonov.gifer.domain.entity.EntityConverter;
import com.olgefilimonov.gifer.domain.entity.GifEntity;
import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;
import java.util.List;
import javax.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Oleg Filimonov
 */

public class LoadGifsJob extends UseCase<LoadGifsJob.Request, LoadGifsJob.Response> {
  @Inject GifRepository gifRepository;
  private long addedTimestamp;

  public LoadGifsJob(Request request, DisposableObserver<Response> observer, Params params) {
    super(request, observer, params);
    addedTimestamp = System.currentTimeMillis();
    App.getInstance().getComponent().inject(this);
  }

  @Override
  protected Observable<Response> buildObservable(Request request) {
    return gifRepository.searchGifs(request.getQuery(), request.getLimit(), request.getSkip())
        .flatMap(response -> gifRepository.getGifRatings(response), EntityConverter::convertGifList)
        .map(gifEntities -> new Response(gifEntities, addedTimestamp));
  }

  @Getter
  @Setter
  @AllArgsConstructor
  public static final class Request implements UseCase.Request {
    private String query;
    private int skip;
    private int limit;
  }

  @Getter
  @Setter
  @AllArgsConstructor
  public static final class Response implements UseCase.Response {
    private List<GifEntity> gifEntities;
    private long addedTimestamp;
  }
}
