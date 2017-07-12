package com.olgefilimonov.gifer.model;

/**
 * @author Oleg Filimonov
 */

public class Gif {
  private String videoUrl;
  private String previewUrl;
  private int score = 0;

  public Gif(String videoUrl, String previewUrl ) {
    this.videoUrl = videoUrl;
    this.previewUrl = previewUrl;
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

  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }
}
