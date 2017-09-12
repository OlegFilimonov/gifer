package com.olgefilimonov.gifer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter public class Datum {

  @SerializedName("type") @Expose private String type;
  @SerializedName("id") @Expose private String id;
  @SerializedName("slug") @Expose private String slug;
  @SerializedName("url") @Expose private String url;
  @SerializedName("bitly_gif_url") @Expose private String bitlyGifUrl;
  @SerializedName("bitly_url") @Expose private String bitlyUrl;
  @SerializedName("embed_url") @Expose private String embedUrl;
  @SerializedName("username") @Expose private String username;
  @SerializedName("source") @Expose private String source;
  @SerializedName("rating") @Expose private String rating;
  @SerializedName("content_url") @Expose private String contentUrl;
  @SerializedName("user") @Expose private User user;
  @SerializedName("source_tld") @Expose private String sourceTld;
  @SerializedName("source_post_url") @Expose private String sourcePostUrl;
  @SerializedName("import_datetime") @Expose private String importDatetime;
  @SerializedName("trending_datetime") @Expose private String trendingDatetime;
  @SerializedName("images") @Expose private Images images;
}
