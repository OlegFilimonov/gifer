package com.olgefilimonov.gifer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter public class GiphyResponse {

  @SerializedName("data") @Expose private List<Datum> data = null;
  @SerializedName("pagination") @Expose private Pagination pagination;
  @SerializedName("meta") @Expose private Meta meta;
}
