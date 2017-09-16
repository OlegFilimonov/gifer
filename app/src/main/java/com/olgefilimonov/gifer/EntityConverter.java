package com.olgefilimonov.gifer;

import com.olgefilimonov.gifer.entity.GifEntity;
import com.olgefilimonov.gifer.model.Datum;
import com.olgefilimonov.gifer.model.GiphyResponse;
import com.olgefilimonov.gifer.model.PreviewGif;
import java.util.ArrayList;
import java.util.List;
import lombok.val;

/**
 * @author Oleg Filimonov
 */

public class EntityConverter {
  public static List<GifEntity> convertGifList(GiphyResponse body) {
    List<GifEntity> gifEntities = new ArrayList<>();

    // Convert gifEntities to the local model
    val data = body != null ? body.getData() : null;
    if (data != null) {
      for (Datum datum : data) {
        PreviewGif previewGif = datum.getImages().getPreviewGif();
        val previewUrl = previewGif == null ? datum.getImages().getDownsizedStill().getUrl() : previewGif.getUrl();
        // Sometimes original MP4 is unavailable. If so, don't add the gif
        if (datum.getImages().getOriginalMp4() == null) continue;
        val videoUrl = datum.getImages().getOriginalMp4().getMp4();
        val gif = new GifEntity(datum.getId(), videoUrl, previewUrl);
        gifEntities.add(gif);
      }
    }
    return gifEntities;
  }
}
