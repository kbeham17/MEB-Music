package at.htlgkr.mebmusic.videos;

import at.htlgkr.mebmusic.thumbnail.Thumbnail;

public class VideoSnippet {
    private String publishedAt;
    private String title;
    private String description;
    private Thumbnail thumbnail;

    public VideoSnippet() {
    }

    public VideoSnippet(String publishedAt, String title, String description, Thumbnail thumbnail) {
        this.publishedAt = publishedAt;
        this.title = title;
        this.description = description;
        this.thumbnail = thumbnail;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Thumbnail thumbnail) {
        this.thumbnail = thumbnail;
    }
}

