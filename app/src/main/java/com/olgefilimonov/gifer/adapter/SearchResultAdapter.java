package com.olgefilimonov.gifer.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.olgefilimonov.gifer.R;
import com.olgefilimonov.gifer.activity.GifDetailActivity;
import com.olgefilimonov.gifer.model.Gif;
import com.olgefilimonov.gifer.model.RatedGif;
import com.olgefilimonov.gifer.singleton.GiferApplication;
import io.objectbox.Box;
import java.util.List;

import static com.olgefilimonov.gifer.activity.GifDetailActivity.GIF_ID_EXTRA;
import static com.olgefilimonov.gifer.activity.GifDetailActivity.URL_EXTRA;

/**
 * @author Oleg Filimonov
 */

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder> {
  private final Box<RatedGif> gifsBox;
  private List<Gif> gifs;
  private Context context;

  public SearchResultAdapter(List<Gif> gifs, Context context) {
    this.gifs = gifs;
    this.context = context;
    gifsBox = GiferApplication.getInstance().getBoxStore().boxFor(RatedGif.class);
  }

  public void updateGifRating() {

    for (int i = 0; i < gifs.size(); i++) {
      Gif gif = gifs.get(i);
      List<RatedGif> ratedGifList = gifsBox.find("gifId", gif.getGifId());
      if (ratedGifList.size() == 0) {
        // No rating found -- don't do anything
      } else if (ratedGifList.size() == 1) {
        // Rating found
        gif.setScore(ratedGifList.get(0).getScore());
        notifyItemChanged(i);
      } else {
        throw new RuntimeException("Database error. gifId must be unique");
      }
    }
  }

  @Override public SearchResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gif, parent, false);
    return new SearchResultViewHolder(view);
  }

  @Override public void onBindViewHolder(final SearchResultViewHolder holder, int position) {
    final Gif gif = gifs.get(position);

    // Load preview image
    Glide.with(context).load(gif.getPreviewUrl()).into(holder.image);
    // Setup click
    holder.card.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        Intent intent = new Intent(context, GifDetailActivity.class);
        intent.putExtra(URL_EXTRA, gif.getVideoUrl());
        intent.putExtra(GIF_ID_EXTRA, gif.getGifId());
        context.startActivity(intent);
      }
    });
    holder.score.setText(String.valueOf(gif.getScore()));
    // Likes & Dislike click
    holder.like.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {

        gif.setScore(gif.getScore() + 1);
        RatedGif ratedGif = getRatedGif(gif.getGifId());
        ratedGif.setScore(gif.getScore());
        notifyItemChanged(holder.getAdapterPosition());
        gifsBox.put(ratedGif);
      }
    });

    holder.dislike.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {

        gif.setScore(gif.getScore() - 1);
        RatedGif ratedGif = getRatedGif(gif.getGifId());
        ratedGif.setScore(gif.getScore());
        notifyItemChanged(holder.getAdapterPosition());
        gifsBox.put(ratedGif);
      }
    });
  }

  /**
   * Helper method for getting rated gifs from the db
   *
   * @param gifId id of the gif from giphy
   * @return a rated gif object from the database if it exists
   */
  private RatedGif getRatedGif(String gifId) {
    RatedGif ratedGif;
    List<RatedGif> ratedGifList = gifsBox.find("gifId", gifId);
    if (ratedGifList.size() == 0) {
      // No rating found
      ratedGif = new RatedGif();
      ratedGif.setGifId(gifId);
    } else if (ratedGifList.size() == 1) {
      // Rating found
      ratedGif = ratedGifList.get(0);
    } else {
      throw new RuntimeException("Database error. gifId must be unique");
    }
    return ratedGif;
  }

  @Override public int getItemCount() {
    return gifs.size();
  }

  class SearchResultViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.gif_card) CardView card;
    @BindView(R.id.gif_image) ImageView image;
    @BindView(R.id.gif_like) ImageView like;
    @BindView(R.id.gif_dislike) ImageView dislike;
    @BindView(R.id.gif_score) TextView score;

    SearchResultViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
