package at.htlgkr.mebmusic.videos;

public class Video {
    private String videoID;
    private String playlistVideoId;
    private VideoSnippet snippet;

    public Video(){
    }

    public Video(String videoID, VideoSnippet snippet) {
        this.videoID = videoID;
        this.snippet = snippet;
    }

    public Video(String videoID, String playlistVideoId,  VideoSnippet snippet) {
        this.videoID = videoID;
        this.playlistVideoId = playlistVideoId;
        this.snippet = snippet;
    }

    public String getVideoID() {
        return videoID;
    }

    public void setVideoID(String videoID) {
        this.videoID = videoID;
    }

    public VideoSnippet getSnippet() {
        return snippet;
    }

    public void setSnippet(VideoSnippet snippet) {
        this.snippet = snippet;
    }

    public String getPlaylistVideoId() {
        return playlistVideoId;
    }

    public void setPlaylistVideoId(String playlistVideoId) {
        this.playlistVideoId = playlistVideoId;
    }
}
