package com.olgefilimonov.gifer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter public class User {

  @SerializedName("avatar_url") @Expose private String avatarUrl;
  @SerializedName("banner_url") @Expose private String bannerUrl;
  @SerializedName("profile_url") @Expose private String profileUrl;
  @SerializedName("username") @Expose private String username;
  @SerializedName("display_name") @Expose private String displayName;
}
