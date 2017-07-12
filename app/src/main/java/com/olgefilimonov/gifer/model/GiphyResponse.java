package com.olgefilimonov.gifer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GiphyResponse {

  @SerializedName("data") @Expose private List<Datum> data = null;
  @SerializedName("pagination") @Expose private Pagination pagination;
  @SerializedName("meta") @Expose private Meta meta;

  public List<Datum> getData() {
    return data;
  }

  public void setData(List<Datum> data) {
    this.data = data;
  }

  public Pagination getPagination() {
    return pagination;
  }

  public void setPagination(Pagination pagination) {
    this.pagination = pagination;
  }

  public Meta getMeta() {
    return meta;
  }

  public void setMeta(Meta meta) {
    this.meta = meta;
  }
}
