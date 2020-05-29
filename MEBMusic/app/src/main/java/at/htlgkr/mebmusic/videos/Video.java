package at.htlgkr.mebmusic.videos;

public class Video {
    private String videoID;
    private VideoSnippet snippet;

    public Video(){
    }

    public Video(String videoID, VideoSnippet snippet) {
        this.videoID = videoID;
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
}
