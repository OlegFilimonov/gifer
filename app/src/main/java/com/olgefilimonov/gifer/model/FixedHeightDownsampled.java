package com.olgefilimonov.gifer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter public class FixedHeightDownsampled {

  @SerializedName("url") @Expose private String url;
  @SerializedName("width") @Expose private String width;
  @SerializedName("height") @Expose private String height;
  @SerializedName("size") @Expose private String size;
  @SerializedName("webp") @Expose private String webp;
  @SerializedName("webp_size") @Expose private String webpSize;
}
