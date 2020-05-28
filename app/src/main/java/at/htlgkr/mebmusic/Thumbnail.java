package at.htlgkr.mebmusic;

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




    public class MediumThumb {
        private String url;

        public MediumThumb() {
        }

        public MediumThumb(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
