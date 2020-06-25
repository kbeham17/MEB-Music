package at.htlgkr.mebmusic.apitasks;

public class YoutubeAPI {
    public static final String BASE = "https://www.googleapis.com/youtube/v3/";
    public static final String KEY = "&key=AIzaSyBB0Pz6WoNaYZzY4lueiwVgNMXutLBueg4";
    //Vasi API KEY: AIzaSyBB0Pz6WoNaYZzY4lueiwVgNMXutLBueg4
    //Vasi Laptop KEY: AIzaSyCbc0zFTMewIHNh6ADN8sVwSLuesy3RuR4
    //Stefan API KEY: AIzaSyA6c0fMsjJOMwZcsKxGt9JVh8pPiz13rPw
    //Kevin API KEY: AIzaSyB6AupkZ2-GIGeNas6m7pGYwH5nYpcHJ4o

    public static final String PLAYLIST = "playlists?";
    public static final String PART_PLAYLIST = "&part=snippet,contentDetails";
    public static final String CHANNELID = "&channelId=";

    public static final String SEARCH = "search?";
    public static final String MAX = "10";
    public static final String ORDER = "&order=";
    public static final String PART = "&part=snippet";

    public static final String QUERY = "&q=";
    public static final String TYPE = "&type=video";

    public static final String PLAYLISTITEMS = "playlistItems?";
    public static final String PLAYLISTID = "&playlistId=";

    public static final String RATE = "videos/rate?";
    public static final String RATING = "&rating=";
    public static final String ID = "id=";
}
