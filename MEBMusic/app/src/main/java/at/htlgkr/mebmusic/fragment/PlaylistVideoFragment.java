package at.htlgkr.mebmusic.fragment;

import android.graphics.LightingColorFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import at.htlgkr.mebmusic.R;
import at.htlgkr.mebmusic.actvities.MainActivity;
import at.htlgkr.mebmusic.adapter.PlaylistAdapter;
import at.htlgkr.mebmusic.adapter.VideoAdapter;
import at.htlgkr.mebmusic.apitasks.DELETETask;
import at.htlgkr.mebmusic.apitasks.GETTask;
import at.htlgkr.mebmusic.apitasks.POSTTask;
import at.htlgkr.mebmusic.apitasks.YoutubeAPI;
import at.htlgkr.mebmusic.playlist.Playlist;
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

    public PlaylistVideoFragment() {}

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

        registerForContextMenu(rv);

        videoList.clear();

        intializeView(view);
        getJson();


        adapter.setOnVideoClickListener(new VideoAdapter.OnVideoClickListener() {
            @Override
            public void onVideoClick(int position) {

            }
        });

        return view;
    }

   /* @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {

        int viewId = v.getId();
        if (viewId == R.id.recycler_playlistvideos){
            getMenuInflater().inflate(R.menu.context_menu_playlistvideos, menu)
        }

        super.onCreateContextMenu(menu, v, menuInfo);
    }*/

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int entryID = -1;

        try{
            entryID = adapter.getPosition();
//            entryID = item.getItemId();
        } catch (Exception ex){
            ex.printStackTrace();
        }

        if(item.getItemId() == R.id.context_playlistvideos_details){
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            final int finalEntryID = entryID;
            Video video = videoList.get(entryID);

            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());

            dialog.setMessage("Title: " + video.getSnippet().getTitle().toString() + "\nDescription: " + video.getSnippet().getDescription() + "\nPublished At: " + video.getSnippet().getPublishedAt());
            dialog.setPositiveButton("Like", ((vDialog, which) -> handleDialogLike(finalEntryID)));
                    dialog.setNegativeButton("Dislike", ((vDialog, which) -> handleDialogDislike(finalEntryID)));
                    dialog.setNeutralButton("Cancel", null);
            dialog.show().getWindow().getDecorView().getBackground().setColorFilter(new LightingColorFilter(0xFF000000, 0xFF36393F));


   /*         final View vDialog = getLayoutInflater().inflate(R.layout.dialog_, null);

            new AlertDialog.Builder(getContext())
                    .setCancelable(true)
                    .setView(vDialog)
                    .setMessage("Title: " + video.getSnippet().getTitle().toString() + "\nDescription: " + video.getSnippet().getDescription() + "\nPublished At: " + video.getSnippet().getPublishedAt())
                    .setPositiveButton("Like", ((dialog, which) -> handleDialogLike(vDialog, finalEntryID)))
                    .setNegativeButton("Dislike", ((dialog, which) -> handleDialogDislike(vDialog, finalEntryID)))
                    .setNeutralButton("Cancel", null)
                    .show()
                    .getWindow()
                    .getDecorView()
                    .getBackground()
                    .setColorFilter(new LightingColorFilter(0xFF000000, 0xFF36393F));*/

            return true;
        }
        if(item.getItemId() == R.id.context_playlistvideos_comment){

            final int finalEntryID = entryID;

            final View vDialog = getLayoutInflater().inflate(R.layout.dialog_playlistvideos_comment, null);

            handleDialogComment(vDialog, finalEntryID);

            new AlertDialog.Builder(getContext())
                    .setCancelable(false)
                    .setView(vDialog)
                    .setPositiveButton("Send", ((dialog, which) -> handleDialogComment(vDialog, finalEntryID)))
                    .setNegativeButton("Cancel", null)
                    .show()
                    .getWindow()
                    .getDecorView()
                    .getBackground()
                    .setColorFilter(new LightingColorFilter(0xFF000000, 0xFF36393F));;

            return true;
        }
        if(item.getItemId() == R.id.context_playlistvideos_delete){
            //NED AUSPROBIERN WEIL SONST GIBTS BOOM

            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            final int finalEntryID = entryID;

            Video video = videoList.get(entryID);
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());

            String url = YoutubeAPI.BASE + YoutubeAPI.ID + video.getVideoID() + YoutubeAPI.KEY;

            DELETETask delTask = new DELETETask(url);
            delTask.execute();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String name = videoList.get(entryID).getSnippet().getTitle();

            videoList.remove(entryID);
            adapter.notifyDataSetChanged();

            Toast.makeText(getContext() , "Item " + name + " has been removed.", Toast.LENGTH_LONG).show();
        }


        return super.onContextItemSelected(item);
    }

    private void handleDialogComment(View vDialog, int entryID){
        EditText editComment = vDialog.findViewById(R.id.dialog_playlistvideos_comment);
        String comment = editComment.getText().toString();



    }

    private void intializeView(View view){
        RecyclerView rv = view.findViewById(R.id.recycler_playlistvideos);
    }

    private void handleDialogLike(int entryID){
        String like = "like";
        String url = YoutubeAPI.BASE + YoutubeAPI.RATE + YoutubeAPI.ID + videoList.get(entryID).getVideoID() + YoutubeAPI.RATING + like + YoutubeAPI.KEY;
        POSTTask postTask = new POSTTask(url);
        postTask.execute();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleDialogDislike(int entryID){
        String dislike = "dislike";
        String url = YoutubeAPI.BASE + YoutubeAPI.RATE + YoutubeAPI.ID + videoList.get(entryID).getVideoID() + YoutubeAPI.RATING + dislike + YoutubeAPI.KEY;
        POSTTask postTask = new POSTTask(url);
        postTask.execute();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
                    JSONObject snippetObject = (JSONObject) base.get("snippet");
                    JSONObject resourceIdObject = (JSONObject) snippetObject.get("resourceId");
                    String id = resourceIdObject.get("videoId").toString();
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
