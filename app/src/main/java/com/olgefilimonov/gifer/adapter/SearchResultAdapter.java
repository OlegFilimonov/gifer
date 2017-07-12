package com.olgefilimonov.gifer.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.olgefilimonov.gifer.R;
import com.olgefilimonov.gifer.model.Gif;
import java.util.List;

/**
 * @author Oleg Filimonov
 */

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder> {
  private List<Gif> gifs;
  private Context context;

  public SearchResultAdapter(List<Gif> gifs, Context context) {
    this.gifs = gifs;
    this.context = context;
  }

  @Override public SearchResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gif, parent, false);
    return new SearchResultViewHolder(view);
  }

  @Override public void onBindViewHolder(SearchResultViewHolder holder, int position) {
    Gif gif = gifs.get(position);

    // Load preview image
    Glide.with(context).load(gif.getPreviewUrl()).into(holder.image);
    // Setup click
    holder.card.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        // TODO: 12-Jul-17 Open video activity
      }
    });
  }

  @Override public int getItemCount() {
    return gifs.size();
  }

  public class SearchResultViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.gif_card) CardView card;
    @BindView(R.id.gif_image) ImageView image;

    public SearchResultViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
