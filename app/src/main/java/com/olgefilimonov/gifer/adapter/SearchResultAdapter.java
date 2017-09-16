package com.olgefilimonov.gifer.adapter;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.koushikdutta.ion.Ion;
import com.olgefilimonov.gifer.R;
import com.olgefilimonov.gifer.entity.GifEntity;
import java.util.List;
import lombok.val;

/**
 * @author Oleg Filimonov
 */

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder> {
  private List<GifEntity> gifEntities;
  private Activity activity;
  private SearchAdapterListener searchAdapterListener;

  public SearchResultAdapter(List<GifEntity> gifEntities, Activity activity, SearchAdapterListener searchAdapterListener) {
    this.gifEntities = gifEntities;
    this.activity = activity;
    this.searchAdapterListener = searchAdapterListener;
  }

  @Override public SearchResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    val view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gif, parent, false);
    return new SearchResultViewHolder(view);
  }

  @Override public void onBindViewHolder(final SearchResultViewHolder holder, int position) {
    val gif = gifEntities.get(position);

    // Load preview image
    Ion.with(activity).load(gif.getPreviewUrl()).intoImageView(holder.image);
    // Setup click
    holder.card.setOnClickListener(view -> searchAdapterListener.onItemClick(gif));
    holder.score.setText(String.valueOf(gif.getScore()));
    // Likes & Dislike click
    holder.like.setOnClickListener(view -> searchAdapterListener.onItemRated(gif, 1));
    holder.dislike.setOnClickListener(view -> searchAdapterListener.onItemRated(gif, -1));
  }

  @Override public int getItemCount() {
    return gifEntities.size();
  }

  public interface SearchAdapterListener {
    void onItemRated(GifEntity gifEntity, int rating);

    void onItemClick(GifEntity gifEntity);
  }

  public class SearchResultViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.gif_card) CardView card;
    @BindView(R.id.gif_image) ImageView image;
    @BindView(R.id.gif_like) ImageView like;
    @BindView(R.id.gif_dislike) ImageView dislike;
    @BindView(R.id.gif_score) TextView score;

    SearchResultViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    public void updateRating(int newRating) {
      score.setText(String.valueOf(newRating));
    }
  }
}
