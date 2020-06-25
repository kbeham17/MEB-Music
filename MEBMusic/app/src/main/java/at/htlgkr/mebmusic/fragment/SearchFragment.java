package at.htlgkr.mebmusic.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import at.htlgkr.mebmusic.thumbnail.MediumThumb;
import at.htlgkr.mebmusic.R;
import at.htlgkr.mebmusic.thumbnail.Thumbnail;
import at.htlgkr.mebmusic.adapter.VideoAdapter;
import at.htlgkr.mebmusic.apitasks.GETTask;
import at.htlgkr.mebmusic.apitasks.YoutubeAPI;
import at.htlgkr.mebmusic.videos.Video;
import at.htlgkr.mebmusic.videos.VideoSnippet;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    private EditText et_search;
    private Button btn_search;
    private VideoAdapter adapter;
    private LinearLayoutManager manager;
    private String order;
    private List<Video> videoList = new ArrayList<>();


    public SearchFragment() {

    }

    public SearchFragment(String order) {
        this.order = order;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        et_search = view.findViewById(R.id.et_search);
        btn_search = view.findViewById(R.id.btn_search);
        RecyclerView rv = view.findViewById(R.id.recycler_search);

        adapter = new VideoAdapter(getContext(), videoList);
        manager = new LinearLayoutManager(getContext());
        rv.setAdapter(adapter);
        rv.setLayoutManager(manager);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(et_search.getText().toString())){
                    getJson(et_search.getText().toString());
                }else{
                    Toast.makeText(getContext(), "Search for a video", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void getJson(String query) {
        String url = YoutubeAPI.BASE + YoutubeAPI.SEARCH  + YoutubeAPI.PART + YoutubeAPI.ORDER + order + YoutubeAPI.QUERY + query + YoutubeAPI.TYPE+ YoutubeAPI.KEY;
        //String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&order=date&q=yung%20hurn&type=video"+ YoutubeAPI.KEY;
        GETTask getTask = new GETTask(url);
        getTask.execute();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String toDoJson = getTask.getJsonResponse();
        if(toDoJson != null) {
            try {
                String split = toDoJson.split("\"items\": " )[1];
                //String[] itemsSplit = split.split("},");
                JSONArray jsonarr = new JSONArray(split);

                for(int i = 0; i< jsonarr.length(); i++){
                    JSONObject base = jsonarr.getJSONObject(i);
                    String id = base.get("id").toString();
                    JSONObject snippetObject = (JSONObject) base.get("snippet");
                    JSONObject thumbnailObject = (JSONObject) snippetObject.get("thumbnails");

                    JSONObject mediumObject = (JSONObject) thumbnailObject.get("medium");

                    VideoSnippet snippet = new VideoSnippet(snippetObject.getString("publishedAt"), snippetObject.getString("title"), snippetObject.getString("description"), new Thumbnail(new MediumThumb(mediumObject.get("url").toString())));

                    videoList.add(new Video(id, snippet));
                }
            }catch(Exception e) {
                e.printStackTrace();
            }

        }
        adapter.notifyDataSetChanged();
    }

}
