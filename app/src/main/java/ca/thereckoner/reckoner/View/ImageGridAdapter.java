package ca.thereckoner.reckoner.View;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import ca.thereckoner.reckoner.Article;
import ca.thereckoner.reckoner.R;

import java.util.List;

/**
 * Created by Varun on 2016-12-18.
 *
 * Adapter for a grid view of images using Recycler View.
 * Used for the Humans of Garneau browse page.
 */

public class ImageGridAdapter extends RecyclerView.Adapter<ImageGridAdapter.HOGThumbHolder>{

    private List<Article> articles;

    protected static class HOGThumbHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;

        HOGThumbHolder(View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.hog_image);
        }
    }

    public ImageGridAdapter(List<Article> articles) {
        this.articles = articles;
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    @Override
    public ImageGridAdapter.HOGThumbHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_grid_view, viewGroup, false);
        return new ImageGridAdapter.HOGThumbHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageGridAdapter.HOGThumbHolder holder, int position) {
        Article article = articles.get(position);

        //Set the image from URL using Glide library
        Glide.with(holder.thumbnail.getContext())
                .load(article.getImageURL())
                .error(R.drawable.logo_full) //Set the fallback to the hog logo
                .fallback(R.drawable.logo_full)
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
