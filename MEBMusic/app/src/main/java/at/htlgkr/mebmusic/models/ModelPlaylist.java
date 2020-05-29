package at.htlgkr.mebmusic.models;

import java.util.List;

import at.htlgkr.mebmusic.playlist.Playlist;

public class ModelPlaylist {
    private String nextPageToken;
    private List<Playlist> playlistItems;

    public ModelPlaylist() {
    }

    public ModelPlaylist(String nextPageToken, List<Playlist> playlistItems) {
        this.nextPageToken = nextPageToken;
        this.playlistItems = playlistItems;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public List<Playlist> getPlaylistItems() {
        return playlistItems;
    }

    public void setPlaylistItems(List<Playlist> playlistItems) {
        this.playlistItems = playlistItems;
    }
}
