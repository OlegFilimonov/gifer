package com.olgefilimonov.gifer.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Images {

  @SerializedName("fixed_height") @Expose private FixedHeight fixedHeight;
  @SerializedName("fixed_height_still") @Expose private FixedHeightStill fixedHeightStill;
  @SerializedName("fixed_height_downsampled") @Expose private FixedHeightDownsampled
      fixedHeightDownsampled;
  @SerializedName("fixed_width") @Expose private FixedWidth fixedWidth;
  @SerializedName("fixed_width_still") @Expose private FixedWidthStill fixedWidthStill;
  @SerializedName("fixed_width_downsampled") @Expose private FixedWidthDownsampled
      fixedWidthDownsampled;
  @SerializedName("fixed_height_small") @Expose private FixedHeightSmall fixedHeightSmall;
  @SerializedName("fixed_height_small_still") @Expose private FixedHeightSmallStill
      fixedHeightSmallStill;
  @SerializedName("fixed_width_small") @Expose private FixedWidthSmall fixedWidthSmall;
  @SerializedName("fixed_width_small_still") @Expose private FixedWidthSmallStill
      fixedWidthSmallStill;
  @SerializedName("downsized") @Expose private Downsized downsized;
  @SerializedName("downsized_still") @Expose private DownsizedStill downsizedStill;
  @SerializedName("downsized_large") @Expose private DownsizedLarge downsizedLarge;
  @SerializedName("downsized_medium") @Expose private DownsizedMedium downsizedMedium;
  @SerializedName("original") @Expose private Original original;
  @SerializedName("original_still") @Expose private OriginalStill originalStill;
  @SerializedName("looping") @Expose private Looping looping;
  @SerializedName("original_mp4") @Expose private OriginalMp4 originalMp4;
  @SerializedName("preview") @Expose private Preview preview;
  @SerializedName("downsized_small") @Expose private DownsizedSmall downsizedSmall;
  @SerializedName("preview_gif") @Expose private PreviewGif previewGif;
  @SerializedName("preview_webp") @Expose private PreviewWebp previewWebp;
}
