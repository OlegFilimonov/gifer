package com.olgefilimonov.gifer.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Looping {

  @SerializedName("mp4") @Expose private String mp4;
  @SerializedName("mp4_size") @Expose private String mp4Size;
}
