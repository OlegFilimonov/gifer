package com.olgefilimonov.gifer.model;

import io.objectbox.annotation.Entity;

/**
 * @author Oleg Filimonov
 */
public class Gif {
  private String gifId;
  private String videoUrl;
  private String previewUrl;
  private int score;

  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public Gif() {
  }

  public Gif(String gifId, String videoUrl, String previewUrl) {
    this.gifId = gifId;
    this.videoUrl = videoUrl;
    this.previewUrl = previewUrl;
  }

  public String getGifId() {
    return gifId;
  }

  public void setGifId(String gifId) {
    this.gifId = gifId;
  }

  public String getVideoUrl() {
    return videoUrl;
  }

  public void setVideoUrl(String videoUrl) {
    this.videoUrl = videoUrl;
  }

  public String getPreviewUrl() {
    return previewUrl;
  }

  public void setPreviewUrl(String previewUrl) {
    this.previewUrl = previewUrl;
  }
}
