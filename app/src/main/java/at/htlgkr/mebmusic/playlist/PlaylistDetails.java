package at.htlgkr.mebmusic.playlist;

public class PlaylistDetails {
    private int itemCount;

    public PlaylistDetails() {
    }

    public PlaylistDetails(int itemCount) {
        this.itemCount = itemCount;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }
}
