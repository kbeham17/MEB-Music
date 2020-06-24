package at.htlgkr.mebmusic.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.LightingColorFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
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

import com.google.android.youtube.player.YouTubeIntents;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.youtube.model.Comment;
import com.google.api.services.youtube.model.CommentSnippet;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadSnippet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import at.htlgkr.mebmusic.R;
import at.htlgkr.mebmusic.actvities.CredentialSetter;
import at.htlgkr.mebmusic.actvities.MainActivity;
import at.htlgkr.mebmusic.adapter.PlaylistAdapter;
import at.htlgkr.mebmusic.adapter.VideoAdapter;
import at.htlgkr.mebmusic.apitasks.DELETETask;
import at.htlgkr.mebmusic.apitasks.GETTask;
import at.htlgkr.mebmusic.apitasks.POSTTask;
import at.htlgkr.mebmusic.apitasks.YoutubeAPI;
import at.htlgkr.mebmusic.playlist.Playlist;
import at.htlgkr.mebmusic.sensor.ShakeDetector;
import at.htlgkr.mebmusic.thumbnail.MediumThumb;
import at.htlgkr.mebmusic.thumbnail.Thumbnail;
import at.htlgkr.mebmusic.videos.Video;
import at.htlgkr.mebmusic.videos.VideoSnippet;


public class PlaylistVideoFragment extends Fragment {

    private MainActivity mAct;
    //private Fragment backFragmentPlaylist;

    private com.google.api.services.youtube.YouTube mService;

    private View view;

    private String id;

    private VideoAdapter adapter;
    private LinearLayoutManager manager;
    private List<Video> videoList = new ArrayList<>();
    Bundle extra;

    private String channelID;

    private static final int RQ_ACCOUNT_PICKER = 1000;
    private static final int RQ_AUTHORIZATION = 2001;
    private static final int RQ_GOOGLE_PLAY_SERVICES = 1002;
    private static final int RQ_PERMISSION_GET_ACCOUNTS = 1003;

    public PlaylistVideoFragment() {}

    public PlaylistVideoFragment(String id, String channelID){
        this.id = id;
        this.channelID = channelID;
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
                Intent intent = YouTubeIntents.createPlayVideoIntent(getContext(), videoList.get(position).getVideoID());
                startActivity(intent);
            }
        });

        mService = CredentialSetter.getmService();

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
            dialog.setPositiveButton("Like", ((vDialog, which) -> handleDialogLike(video.getVideoID())));
                    dialog.setNegativeButton("Dislike", ((vDialog, which) -> handleDialogDislike(video.getVideoID())));
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
            //GEHT NED WEIL FALSCHE ID

            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            final int finalEntryID = entryID;

            Video video = videoList.get(entryID);

            new DeleteVideoItem(mService, video.getPlaylistVideoId()).execute();

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

        Video video = videoList.get(entryID);

        new CommentVideoItem(mService, video.getVideoID(), comment).execute();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void intializeView(View view){
        RecyclerView rv = view.findViewById(R.id.recycler_playlistvideos);
    }

    private void handleDialogLike(String vidID){
        String like = "like";

        new RateVideoTask(mService, like, vidID).execute();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleDialogDislike(String vidID){
        String dislike = "dislike";

        new RateVideoTask(mService, dislike, vidID).execute();

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
                    String playlistVideoId = base.getString("id");
                    JSONObject snippetObject = (JSONObject) base.get("snippet");
                    JSONObject resourceIdObject = (JSONObject) snippetObject.get("resourceId");
                    String id = resourceIdObject.get("videoId").toString();
                    JSONObject thumbnailObject = (JSONObject) snippetObject.get("thumbnails");
                    JSONObject mediumObject = (JSONObject) thumbnailObject.get("medium");
                    VideoSnippet snippet = new VideoSnippet(snippetObject.getString("publishedAt"), snippetObject.get("title").toString(),snippetObject.getString("description"), new Thumbnail(new MediumThumb(mediumObject.get("url").toString())));


                    videoList.add(new Video(id,playlistVideoId, snippet));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        adapter.notifyDataSetChanged();
    }

    private class RateVideoTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.youtube.YouTube mService = null;
        private String rating;
        private String vidID;
        private Exception mLastError = null;

        RateVideoTask(com.google.api.services.youtube.YouTube mService, String rating, String vidID) {
            this.mService = mService;
            this.rating = rating;
            this.vidID = vidID;
        }

        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        private List<String> getDataFromApi() throws IOException {
            mService.videos().rate(vidID, rating).execute();

            return null;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(List<String> output) {
            if (output == null || output.size() == 0) {
            } else {
                output.add(0, "Data retrieved using the YouTube Data API:");
                for(String s : output){
                    System.out.println(s);
                }
            }
            CredentialSetter.setmService(mService);

        }

        @Override
        protected void onCancelled() {

            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    /*showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());*/

                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            RQ_AUTHORIZATION);
                } else {
                    System.out.println("The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                System.out.println("Request cancelled.");
            }
        }
    }

    private class DeleteVideoItem extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.youtube.YouTube mService = null;
        private String vidID;
        private Exception mLastError = null;

        DeleteVideoItem(com.google.api.services.youtube.YouTube mService, String vidID) {
            this.mService = mService;
            this.vidID = vidID;
        }

        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        private List<String> getDataFromApi() throws IOException {
            mService.playlistItems().delete(vidID).execute();

            return null;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(List<String> output) {
            if (output == null || output.size() == 0) {
            } else {
                output.add(0, "Data retrieved using the YouTube Data API:");
                for(String s : output){
                    System.out.println(s);
                }
            }
            CredentialSetter.setmService(mService);

        }

        @Override
        protected void onCancelled() {

            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    /*showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());*/

                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            RQ_AUTHORIZATION);
                } else {
                    System.out.println("The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                System.out.println("Request cancelled.");
            }
        }
    }

    private class CommentVideoItem extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.youtube.YouTube mService = null;
        private String vidID;
        private String text;
        private Exception mLastError = null;

        CommentVideoItem(com.google.api.services.youtube.YouTube mService, String vidID, String text) {
            this.mService = mService;
            this.vidID = vidID;
            this.text = text;
        }

        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        private List<String> getDataFromApi() throws IOException {

            CommentSnippet cs = new CommentSnippet();
            cs.setTextOriginal(text);
            Comment c = new Comment();
            c.setSnippet(cs);

            CommentThreadSnippet cts = new CommentThreadSnippet();
            cts.setChannelId(channelID).setVideoId(vidID).setTopLevelComment(c);

            CommentThread ct = new CommentThread();
            ct.setSnippet(cts);

            mService.commentThreads().insert("snippet", ct).execute();

            return null;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(List<String> output) {
            if (output == null || output.size() == 0) {
            } else {
                output.add(0, "Data retrieved using the YouTube Data API:");
                for(String s : output){
                    System.out.println(s);
                }
            }
            CredentialSetter.setmService(mService);

        }

        @Override
        protected void onCancelled() {

            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    /*showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());*/

                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            RQ_AUTHORIZATION);
                } else {
                    System.out.println("The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                System.out.println("Request cancelled.");
            }
        }
    }

    //Sensor

    public void shake(Context context){
        Intent intent = YouTubeIntents.createPlayPlaylistIntent(context, id);
        startActivity(intent);
    }
}
