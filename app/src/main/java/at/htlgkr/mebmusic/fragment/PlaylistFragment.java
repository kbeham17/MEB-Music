package at.htlgkr.mebmusic.fragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.LightingColorFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import at.htlgkr.mebmusic.actvities.MainActivity;
import at.htlgkr.mebmusic.thumbnail.MediumThumb;
import at.htlgkr.mebmusic.thumbnail.Thumbnail;
import at.htlgkr.mebmusic.apitasks.GETTask;
import at.htlgkr.mebmusic.apitasks.PUTTask;
import at.htlgkr.mebmusic.apitasks.YoutubeAPI;
import at.htlgkr.mebmusic.playlist.Playlist;
import at.htlgkr.mebmusic.R;
import at.htlgkr.mebmusic.adapter.PlaylistAdapter;
import at.htlgkr.mebmusic.playlist.PlaylistDetails;
import at.htlgkr.mebmusic.playlist.PlaylistSnippet;
/*import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;*/

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaylistFragment extends Fragment{

    private PlaylistAdapter adapter;
    private LinearLayoutManager manager;
    private List<Playlist> playlistList = new ArrayList<>();
    private String CHANNELID;
    private MainActivity mAct;
    private int RQ_PLAYLISTVIDEO_ACTIVITY = 111;

    public PlaylistFragment(String channelId) {
        this.CHANNELID = channelId;
    }

    public PlaylistFragment() {
    }

    public void setMAct(MainActivity mAct){
        this.mAct = mAct;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        RecyclerView rv = view.findViewById(R.id.recycler_playlist);
        adapter = new PlaylistAdapter(getContext(), playlistList);
        manager = new LinearLayoutManager(getContext());
        rv.setAdapter(adapter);
        rv.setLayoutManager(manager);

        playlistList.clear();
        getJson();

        rv.setHasFixedSize(true);

        registerForContextMenu(rv);

        Context ctx = this.getContext();

        adapter.setOnPlaylistClickListener(new PlaylistAdapter.OnPlaylistClickListener() {
            @Override
            public void onPlaylistClick(int position) {
                Playlist playlist = playlistList.get(position);
                String id = playlist.getId();

                PlaylistVideoFragment fragment = new PlaylistVideoFragment();
                fragment.setMAct(mAct);

                mAct.setFragment(fragment);
            }
        });

        return view;
    }



    /*@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        int viewId = v.getId();
        if (viewId == R.id.recycler_playlist) {
            getActivity().getMenuInflater().inflate(R.menu.context_menu_playlist, menu);
        }

        super.onCreateContextMenu(menu, v, menuInfo);
    }*/

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        //AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        //int entryID = info.position;

        int entryID = -1;

        try{
            entryID = adapter.getPosition();
        } catch (Exception ex){
            ex.printStackTrace();
        }

        if(item.getItemId() == R.id.context_playlist_bearbeiten){

            final int finalEntryID = entryID;

            final View vDialog = getLayoutInflater().inflate(R.layout.dialog_playlist_edit, null);

            setUpDialog(vDialog, entryID);

            new AlertDialog.Builder(getContext())
                    .setCancelable(false)
                    .setView(vDialog)
                    .setPositiveButton("ok", ((dialog, which) -> handleDialog(vDialog, finalEntryID)))
                    .setNegativeButton("Cancel", null)
                    .show()
                    .getWindow()
                    .getDecorView()
                    .getBackground()
                    .setColorFilter(new LightingColorFilter(0xFF000000, 0xFF36393F));
            return true;
        }
        if(item.getItemId() == R.id.context_playlist_details){

            Playlist playlist = playlistList.get(entryID);
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());

            dialog.setMessage("Title: " + playlist.getSnippet().getTitle().toString() + "\nDescription: " + playlist.getSnippet().getDescription() + "\nVideo Count: " + playlist.getPlaylistDetails().getItemCount());
            dialog.setNeutralButton("OK", null);
            dialog.show();

            return true;
        }

        return super.onContextItemSelected(item);
    }

    private void setUpDialog(View vDialog, int entryID){
        EditText editDialogTitle = vDialog.findViewById(R.id.dialog_playlist_title);
        EditText editDialogDescription = vDialog.findViewById(R.id.dialog_playlist_description);
        TextView textDialogTitle = vDialog.findViewById(R.id.dialog_playlist_dialogtitle);
        textDialogTitle.setText("Titel und Beschreibung ändern");

        Playlist playlist = playlistList.get(entryID);

        editDialogTitle.setText(playlist.getSnippet().getTitle());
        editDialogDescription.setText(playlist.getSnippet().getDescription());
    }

    private void handleDialog(View vDialog, int entryID){
        EditText editDialogTitle = vDialog.findViewById(R.id.dialog_playlist_title);
        EditText editDialogDescription = vDialog.findViewById(R.id.dialog_playlist_description);

        Playlist playlist = playlistList.get(entryID);

        String newTitle = editDialogTitle.getText().toString();
        String newDesc = editDialogDescription.getText().toString();

        String jsonRequest = "{\"id\":\""+ playlist.getId() +"\",\"snippet\":{\"title\":\""+ newTitle+"\",\"description\":\""+ newDesc+"\"}}";
        PUTTask putTask = new PUTTask(YoutubeAPI.BASE + YoutubeAPI.PLAYLIST + YoutubeAPI.PART + YoutubeAPI.KEY);
        putTask.execute(jsonRequest);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String jsonResponse = putTask.getJsonResponse();

        if (jsonResponse != null) {
            try {
                //JSONObject jsonObject = new JSONObject(jsonResponse);
                //String split = jsonResponse.split("\"items\": ")[1];
                //String[] itemsSplit = split.split("},");
                //JSONArray jsonarr = new JSONArray(jsonResponse);
                //JSONObject base = jsonarr.getJSONObject(0);

                JSONObject base = new JSONObject(jsonResponse);
                String id = base.get("id").toString();
                JSONObject snippetObject = (JSONObject) base.get("snippet");
                JSONObject thumbnailObject = (JSONObject) snippetObject.get("thumbnails");
                JSONObject mediumObject = (JSONObject) thumbnailObject.get("medium");
                PlaylistSnippet snippet = new PlaylistSnippet(snippetObject.get("title").toString(), new Thumbnail(new MediumThumb(mediumObject.get("url").toString())), snippetObject.getString("description"));

                playlist = new Playlist(id, snippet, playlistList.get(entryID).getPlaylistDetails());

                playlistList.set(entryID, playlist);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void getJson() {
        String url = YoutubeAPI.BASE + YoutubeAPI.PLAYLIST + YoutubeAPI.PART_PLAYLIST + YoutubeAPI.CHANNELID + CHANNELID + YoutubeAPI.KEY;
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
                    PlaylistSnippet snippet = new PlaylistSnippet(snippetObject.get("title").toString(), new Thumbnail(new MediumThumb(mediumObject.get("url").toString())), snippetObject.getString("description"));
                    JSONObject detailsObject = (JSONObject) base.get("contentDetails");
                    PlaylistDetails playlistDetails = new PlaylistDetails((int) detailsObject.get("itemCount"));

                    playlistList.add(new Playlist(id, snippet, playlistDetails));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        adapter.notifyDataSetChanged();
    }



}





//        Call<ModelPlaylist> data = YoutubeAPI.getPlaylistVideo().getYT("https://www.googleapis.com/youtube/v3/playlists?part=snippet%2C%20contentDetails&channelId=UCMnR3J-chev22dTqJEquFcg&key=AIzaSyC583ei0acTyI6_M1bKLeserE8nJjecrAg");
//        data.enqueue(new Callback<ModelPlaylist>() {
//            @Override
//            public void onResponse(Call<ModelPlaylist> call, Response<ModelPlaylist> response) {
////                if(response.errorBody() != null){
////                    System.out.println(response.errorBody().toString());
////                    Log.w(TAG, "onResponse: "+response.errorBody());
////                }
////                else{
////                    ModelPlaylist mp = response.body();
////                    playlistList.addAll(mp.getPlaylistItems());
////                    adapter.notifyDataSetChanged();
////                }
//            }
//
//            @Override
//            public void onFailure(Call<ModelPlaylist> call, Throwable t) {
//                Log.e(TAG, "onFailure playlist: ", t);
//            }
//        });
//"https://www.googleapis.com/youtube/v3/playlists?part=snippet%2C%20contentDetails&channelId=UCMnR3J-chev22dTqJEquFcg&key=AIzaSyC583ei0acTyI6_M1bKLeserE8nJjecrAg"