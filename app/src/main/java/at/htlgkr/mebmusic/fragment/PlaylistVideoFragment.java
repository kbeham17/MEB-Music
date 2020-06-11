package at.htlgkr.mebmusic.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import at.htlgkr.mebmusic.R;
import at.htlgkr.mebmusic.actvities.MainActivity;
import at.htlgkr.mebmusic.actvities.PreferenceActivity;
import at.htlgkr.mebmusic.adapter.PlaylistAdapter;
import at.htlgkr.mebmusic.adapter.VideoAdapter;
import at.htlgkr.mebmusic.apitasks.GETTask;
import at.htlgkr.mebmusic.apitasks.YoutubeAPI;
import at.htlgkr.mebmusic.thumbnail.MediumThumb;
import at.htlgkr.mebmusic.thumbnail.Thumbnail;
import at.htlgkr.mebmusic.videos.Video;
import at.htlgkr.mebmusic.videos.VideoSnippet;


public class PlaylistVideoFragment extends Fragment {

    private MainActivity mAct;
    //private Fragment backFragmentPlaylist;

    private View view;

    private String id;

    private VideoAdapter adapter;
    private LinearLayoutManager manager;
    private List<Video> videoList = new ArrayList<>();
    Bundle extra;

    public PlaylistVideoFragment() {

    }

    public PlaylistVideoFragment(String id){
        this.id = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_playlist_video, container, false);

        setUpBackButton(view);

        RecyclerView rv = view.findViewById(R.id.recycler_playlistvideos);
        adapter = new VideoAdapter(getContext(), videoList);
        manager = new LinearLayoutManager(getContext());
        rv.setAdapter(adapter);
        rv.setLayoutManager(manager);

        videoList.clear();

        getJson();

        return view;
    }

    private void setUpBackButton(View view){
        Button btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAct.setPlaylistFragment();
            }
        });
    }



    public void setId(String id) {
        this.id = id;
    }

    public void setMAct(MainActivity mAct){
        this.mAct = mAct;
    }

    /*public void setBackFragmentPlaylist(PlaylistFragment fragment){
        this.backFragmentPlaylist = fragment;
    }*/

    private void getJson() {

        String url = YoutubeAPI.BASE + YoutubeAPI.PLAYLISTITEMS + YoutubeAPI.PART + YoutubeAPI.PLAYLISTID + id + YoutubeAPI.KEY;
        GETTask getTask = new GETTask(url);
        getTask.execute();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String toDoJson = getTask.getJsonResponse();
        if (toDoJson != null) {
            try {
                String split = toDoJson.split("\"items\": ")[1];
                //String[] itemsSplit = split.split("},");
                JSONArray jsonarr = new JSONArray(split);

                for (int i = 0; i < jsonarr.length(); i++) {
                    JSONObject base = jsonarr.getJSONObject(i);
                    String id = base.get("id").toString();
                    JSONObject snippetObject = (JSONObject) base.get("snippet");
                    JSONObject thumbnailObject = (JSONObject) snippetObject.get("thumbnails");
                    JSONObject mediumObject = (JSONObject) thumbnailObject.get("medium");
                    VideoSnippet snippet = new VideoSnippet(snippetObject.getString("publishedAt"), snippetObject.get("title").toString(),snippetObject.getString("description"), new Thumbnail(new MediumThumb(mediumObject.get("url").toString())));

                    videoList.add(new Video(id, snippet));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        adapter.notifyDataSetChanged();
    }
}
