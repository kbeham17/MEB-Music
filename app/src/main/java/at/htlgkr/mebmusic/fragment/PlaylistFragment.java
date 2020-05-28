package at.htlgkr.mebmusic.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import at.htlgkr.mebmusic.apitasks.YoutubeAPI;
import at.htlgkr.mebmusic.models.ModelPlaylist;
import at.htlgkr.mebmusic.playlist.Playlist;
import at.htlgkr.mebmusic.R;
import at.htlgkr.mebmusic.adapter.PlaylistAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaylistFragment extends Fragment {

    private PlaylistAdapter adapter;
    private LinearLayoutManager manager;
    private List<Playlist> playlistList = new ArrayList<>();

    public PlaylistFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container,false);
        // Inflate the layout for this fragment
        RecyclerView rv = view.findViewById(R.id.recycler_playlist);
        adapter = new PlaylistAdapter(getContext(), playlistList);
        manager = new LinearLayoutManager(getContext());
        rv.setAdapter(adapter);
        rv.setLayoutManager(manager);

        if(playlistList.size() == 0 ){
            getJson();
        }

        return view;
    }

    private void getJson(){
        String url = YoutubeAPI.BASE + YoutubeAPI.PLAYLIST + YoutubeAPI.PART_PLAYLIST + YoutubeAPI.CHANNELID + YoutubeAPI.KEY ;
        Call<ModelPlaylist> data = YoutubeAPI.getPlaylistVideo().getYT("https://www.googleapis.com/youtube/v3/playlists?part=snippet%2C%20contentDetails&channelId=UCMnR3J-chev22dTqJEquFcg&key=AIzaSyC583ei0acTyI6_M1bKLeserE8nJjecrAg");
        data.enqueue(new Callback<ModelPlaylist>() {
            @Override
            public void onResponse(Call<ModelPlaylist> call, Response<ModelPlaylist> response) {
                if(response.errorBody() != null){
                    System.out.println(response.errorBody().toString());
                    Log.w(TAG, "onResponse: "+response.errorBody());
                }
                else{
                    ModelPlaylist mp = response.body();
                    playlistList.addAll(mp.getPlaylistItems());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ModelPlaylist> call, Throwable t) {
                Log.e(TAG, "onFailure playlist: ", t);
            }
        });
    }
}
