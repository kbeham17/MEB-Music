package at.htlgkr.mebmusic.apitasks;

import at.htlgkr.mebmusic.models.ModelPlaylist;
import at.htlgkr.mebmusic.playlist.Playlist;
/*import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Url;*/

public class YoutubeAPI {
    public static final String BASE = "https://www.googleapis.com/youtube/v3/";
    public static final String KEY = "&key=AIzaSyB6AupkZ2-GIGeNas6m7pGYwH5nYpcHJ4o";
    public static final String PLAYLIST = "playlists?";
    //Vasi API Key
    //Kevin API Key
    //Stefan API Key
    public static final String PART_PLAYLIST = "&part=snippet,contentDetails";
    public static final String CHANNELID = "&channelId=UCMnR3J-chev22dTqJEquFcg";
    //https://www.googleapis.com/youtube/v3/playlists?key=AIzaSyC583ei0acTyI6_M1bKLeserE8nJjecrAg&part=snippet,contentDetails&channelId=UCMnR3J-chev22dTqJEquFcg

    public static final String SEARCH = "search?";
    public static final String MAX = "&maxResults=10";
    public static final String ORDER = "&order=date";
    public static final String PART = "&part=snippet";



    /*public interface PlaylistVideo{
        @GET
        Call<ModelPlaylist> getYT(@Url String url);
    }

    private static PlaylistVideo playlistVideo = null;

    public static PlaylistVideo getPlaylistVideo(){
        if(playlistVideo == null){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            playlistVideo = retrofit.create(PlaylistVideo.class);
        }
        return playlistVideo;
    }*/
}
