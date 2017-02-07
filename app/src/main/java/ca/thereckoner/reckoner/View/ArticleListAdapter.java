package ca.thereckoner.reckoner.View;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import ca.thereckoner.reckoner.Article;
import ca.thereckoner.reckoner.R;

import java.util.List;

/**
 * Created by Varun Venkataramanan on 2016-12-13.
 *
 * Adapter class to display articles in a Recycler View. This view format is the main one for the Reckoner App.
 * Presents articles with e athumbnail, title, and description.
 */

public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListAdapter.ArticleViewHolder>{

    private List<Article> articles; //Articles displayed
    private final OnItemClickListener mOnItemClickListener; //Onclick listener

    /**
     * ViewHolder for an item in the RecyclerView.
     * Contains a CardView.
     */
    protected static class ArticleViewHolder extends RecyclerView.ViewHolder {
        private CardView cv;
        private TextView title;
        private TextView author;
        private TextView description;
        private ImageView thumbnail;

        ArticleViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.card_view);
            title = (TextView) itemView.findViewById(R.id.title);
            //author = (TextView) itemView.findViewById(R.id.author);
            description = (TextView) itemView.findViewById(R.id.description);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
        }

        public void bind(final Article article, final OnItemClickListener listener){
            //Set the title, author and description (excerpt)
            //holder.author.setText(article.getAuthor());
            title.setText(article.getTitle());
            description.setText(article.getDescription());

            //Set the image from URL using Glide library
            Glide.with(thumbnail.getContext())
                    .load(article.getImageURL())
                    //.placeholder(R.drawable.logo_full) //Needs to be a loading animation/image
                    .error(R.drawable.logo_full) //Set the fallback to the reckoner logo
                    .fallback(R.drawable.logo_full)
                    .fitCenter()
                    .crossFade()
                    .into(thumbnail);

            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(article);
                }
            }); //Set the onClickListener that was passed in
        }
    }

    public ArticleListAdapter(List<Article> articles, OnItemClickListener listener) {
        this.articles = articles;
        this.mOnItemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    @Override
    public ArticleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.article_list_view, viewGroup, false);
        return new ArticleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ArticleViewHolder holder, int position) {
        holder.bind(articles.get(position), mOnItemClickListener);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    /**
     * Adds a List of articles to the end of the current List. Used when loading more items on scroll
     * @param newArticles List of Articles to add to the end of the current ones in the adapter
     */
    public void addArticles(List<Article> newArticles){
        articles.addAll(newArticles);
    }
}