package net.snapshotsofharmony.reckoner;

import java.io.Serializable;

/**
 * Created by Varun on 2016-12-13.
 */

public class Article implements Serializable{
    private String title;
    private String author;
    private String description;
    private String imageURL;
    private String content;
    private String contentURL;

    public Article(String title, String author, String description, String imageURL, String content, String url) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.imageURL = imageURL;
        this.content = content;
        contentURL = url;
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

    public String getContent(){
        return content;
    }

    public String getContentURL() {
        return contentURL;}
}
