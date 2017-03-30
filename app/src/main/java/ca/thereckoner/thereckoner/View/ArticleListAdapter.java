package ca.thereckoner.thereckoner.View;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ca.thereckoner.thereckoner.Article;
import ca.thereckoner.thereckoner.R;

import java.util.List;

/**
 * Created by Varun Venkataramanan on 2016-12-13.
 *
 * Adapter class to display articles in a Recycler View. This view format is the main one for the
 * Reckoner App.
 * Presents articles with a thumbnail, title, and description.
 */

public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListAdapter.ArticleViewHolder> {

  private final static String TAG = "ArticleListAdapter";

  private List<Article> articles; //Articles displayed
  private final OnItemClickListener mOnItemClickListener; //Onclick listener

  /**
   * ViewHolder for an item in the RecyclerView.
   * Contains a CardView.
   */
  protected static class ArticleViewHolder extends RecyclerView.ViewHolder {
    TextView title;
    CardView cv;
    TextView description;
    ImageView thumbnail;

    ArticleViewHolder(View itemView) {
      super(itemView);
      //ButterKnife.bind(this, itemView);

      cv = (CardView) itemView.findViewById(R.id.card_view);
      title = (TextView) itemView.findViewById(R.id.articleTitle);
      description = (TextView) itemView.findViewById(R.id.description);
      thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
    }

    public void bind(final Article article, final OnItemClickListener listener) {
      //Set the title, author and description (excerpt)
      title.setText(article.getTitle());
      description.setText(article.getDescription());

      //Set the image from URL using Glide library

      Log.v(TAG, "Image URL: " + article.getImageURL());

      if(!article.getImageURL().isEmpty()) { //If the imageurl is empty
        Picasso.with(thumbnail.getContext())
            .load(article.getImageURL())
            .error(R.drawable.logo_full)
            .placeholder(R.drawable.logo_full)
            .fit()
            .centerInside()
            .into(thumbnail);
      } else {
        Picasso.with(thumbnail.getContext())
            .load(R.drawable.logo_full)
            .error(R.drawable.logo_full)
            .placeholder(R.drawable.logo_full)
            .fit()
            .centerInside()
            .into(thumbnail);
      }

      cv.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          listener.onItemClick(article);
        }
      }); //Set the onClickListener that was passed in
    }
  }

  public ArticleListAdapter(List<Article> articles, OnItemClickListener listener) {
    this.articles = articles;
    this.mOnItemClickListener = listener;
  }

  @Override public int getItemCount() {
    return articles.size();
  }

  @Override public ArticleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
    View v = LayoutInflater.from(viewGroup.getContext())
        .inflate(R.layout.article_list_view, viewGroup, false);
    return new ArticleViewHolder(v);
  }

  @Override public void onBindViewHolder(ArticleViewHolder holder, int position) {
    holder.bind(articles.get(position), mOnItemClickListener);
  }

  @Override public void onAttachedToRecyclerView(RecyclerView recyclerView) {
    super.onAttachedToRecyclerView(recyclerView);
  }

  /**
   * Adds a List of articles to the end of the current List. Used when loading more items on scroll
   *
   * @param newArticles List of Articles to add to the end of the current ones in the adapter
   */
  public void addArticles(List<Article> newArticles) {
    articles.addAll(newArticles);
  }
}