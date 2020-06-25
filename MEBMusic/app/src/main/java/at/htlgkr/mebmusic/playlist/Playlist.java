package at.htlgkr.mebmusic.playlist;

public class Playlist {
    private String id;
    private PlaylistSnippet snippet;
    private PlaylistDetails playlistDetails;

    public Playlist() {
    }

    public Playlist(String id, PlaylistSnippet snippet, PlaylistDetails playlistDetails) {
        this.id = id;
        this.snippet = snippet;
        this.playlistDetails = playlistDetails;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PlaylistSnippet getSnippet() {
        return snippet;
    }

    public void setSnippet(PlaylistSnippet snippet) {
        this.snippet = snippet;
    }

    public PlaylistDetails getPlaylistDetails() {
        return playlistDetails;
    }

    public void setPlaylistDetails(PlaylistDetails playlistDetails) {
        this.playlistDetails = playlistDetails;
    }
}
