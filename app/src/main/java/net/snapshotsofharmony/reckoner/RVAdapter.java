package net.snapshotsofharmony.reckoner;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Varun on 2016-12-13.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ArticleViewHolder>{

    private List<Article> articles;

    public static class ArticleViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView title;
        TextView author;
        TextView description;
        ImageView thumbnail;

        ArticleViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.card_view);
            title = (TextView) itemView.findViewById(R.id.title);
            //author = (TextView) itemView.findViewById(R.id.author);
            description = (TextView) itemView.findViewById(R.id.description);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);

        }
    }

    public RVAdapter(List<Article> articles) {
        this.articles = articles;
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
        Article article = articles.get(position);
        //Set the title, author and description (excerpt)
        //holder.author.setText(article.getAuthor());
        holder.title.setText(article.getTitle());
        holder.description.setText(article.getDescription());

        //Set the image from URL using Glide library
        Glide.with(holder.thumbnail.getContext())
                .load(article.getImageURL())
                //.fallback()
                .centerCrop()
                .crossFade()
                .into(holder.thumbnail);
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