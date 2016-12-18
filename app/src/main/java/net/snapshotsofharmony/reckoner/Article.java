package net.snapshotsofharmony.reckoner;

/**
 * Created by Varun on 2016-12-13.
 */

public class Article {
    private String title;
    private String author;
    private String description;
    private String imageURL;

    public Article(String title, String author, String description, String imageURL) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.imageURL = imageURL;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String getImageURL() {
        return imageURL;
    }
}
