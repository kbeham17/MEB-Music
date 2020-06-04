package at.htlgkr.mebmusic.playlist;

import at.htlgkr.mebmusic.Thumbnail;

public class PlaylistSnippet {
    private String title;
    private Thumbnail thumbnail;

    public PlaylistSnippet() {
    }

    public PlaylistSnippet(String title, Thumbnail thumbnail ){
        this.title = title;
        this.thumbnail=thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Thumbnail thumbnail) {
        this.thumbnail = thumbnail;
    }
}
