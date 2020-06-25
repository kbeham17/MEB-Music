package at.htlgkr.mebmusic.thumbnail;

public class Thumbnail {

    private MediumThumb medium;

    public Thumbnail() {
    }

    public Thumbnail(MediumThumb medium) {
        this.medium = medium;
    }

    public MediumThumb getMedium() {
        return medium;
    }


    public void setMedium(MediumThumb medium) {
        this.medium = medium;
    }
}
