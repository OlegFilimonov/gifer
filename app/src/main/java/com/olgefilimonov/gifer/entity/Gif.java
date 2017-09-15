package com.olgefilimonov.gifer.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Oleg Filimonov
 */
@Getter @Setter public class Gif {
  private String gifId;
  private String videoUrl;
  private String previewUrl;
  private int score;

  public Gif(String gifId, String videoUrl, String previewUrl) {
    this.gifId = gifId;
    this.videoUrl = videoUrl;
    this.previewUrl = previewUrl;
  }
}
