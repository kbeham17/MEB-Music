package at.htlgkr.mebmusic.fragment;


import android.graphics.LightingColorFilter;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import at.htlgkr.mebmusic.actvities.CredentialSetter;
import at.htlgkr.mebmusic.actvities.MainActivity;
import at.htlgkr.mebmusic.thumbnail.MediumThumb;
import at.htlgkr.mebmusic.thumbnail.Thumbnail;
import at.htlgkr.mebmusic.apitasks.GETTask;
import at.htlgkr.mebmusic.apitasks.YoutubeAPI;
import at.htlgkr.mebmusic.playlist.Playlist;
import at.htlgkr.mebmusic.R;
import at.htlgkr.mebmusic.adapter.PlaylistAdapter;
import at.htlgkr.mebmusic.playlist.PlaylistDetails;
import at.htlgkr.mebmusic.playlist.PlaylistSnippet;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaylistFragment extends Fragment {

    private com.google.api.services.youtube.YouTube mService;
    private PlaylistAdapter adapter;
    private LinearLayoutManager manager;
    private List<Playlist> playlistList = new ArrayList<>();
    private String CHANNELID;
    private MainActivity mAct;
    private static final int RQ_AUTHORIZATION = 2001;

    public PlaylistFragment(String channelId) {
        this.CHANNELID = channelId;
    }

    public PlaylistFragment() {
    }

    public void setMAct(MainActivity mAct) {
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


        adapter.setOnPlaylistClickListener(new PlaylistAdapter.OnPlaylistClickListener() {
            @Override
            public void onPlaylistClick(int position) {
                Playlist playlist = playlistList.get(position);
                String id = playlist.getId();

                PlaylistVideoFragment fragment = new PlaylistVideoFragment(id, CHANNELID);
                fragment.setMAct(mAct);

                mAct.setFragment(fragment);
            }
        });

        mService = CredentialSetter.getmService();

        return view;
    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int entryID = -1;

        try {
            entryID = adapter.getPosition();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (item.getItemId() == R.id.context_playlist_bearbeiten) {

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
        if (item.getItemId() == R.id.context_playlist_details) {
            Playlist playlist = playlistList.get(entryID);
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());

            dialog.setMessage("Title: " + playlist.getSnippet().getTitle().toString() + "\nDescription: " + playlist.getSnippet().getDescription() + "\nVideo Count: " + playlist.getPlaylistDetails().getItemCount());
            dialog.setNeutralButton("OK", null);
            dialog.show();

            return true;
        }
        if (item.getItemId() == R.id.context_playlist_delete) {

            Playlist playlist = playlistList.get(entryID);

            new DeletePlaylistTask(mService, playlist.getId()).execute();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String name = playlistList.get(entryID).getSnippet().getTitle();

            playlistList.remove(entryID);
            adapter.notifyDataSetChanged();

            Toast.makeText(getContext(), "Item " + name + " has been removed.", Toast.LENGTH_LONG).show();
        }

        return super.onContextItemSelected(item);
    }

    private void setUpDialog(View vDialog, int entryID) {
        EditText editDialogTitle = vDialog.findViewById(R.id.dialog_playlist_title);
        EditText editDialogDescription = vDialog.findViewById(R.id.dialog_playlist_description);
        TextView textDialogTitle = vDialog.findViewById(R.id.dialog_playlist_dialogtitle);
        textDialogTitle.setText("Titel und Beschreibung ändern");

        Playlist playlist = playlistList.get(entryID);

        editDialogTitle.setText(playlist.getSnippet().getTitle());
        editDialogDescription.setText(playlist.getSnippet().getDescription());
    }

    private void handleDialog(View vDialog, int entryID) {
        EditText editDialogTitle = vDialog.findViewById(R.id.dialog_playlist_title);
        EditText editDialogDescription = vDialog.findViewById(R.id.dialog_playlist_description);

        Playlist playlist = playlistList.get(entryID);

        String newTitle = editDialogTitle.getText().toString();
        String newDesc = editDialogDescription.getText().toString();

        new EditTitleTask(mService, playlist.getId(), newTitle, newDesc).execute();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
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

    private class DeletePlaylistTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.youtube.YouTube mService = null;
        private String playlistID;
        private Exception mLastError = null;

        DeletePlaylistTask(com.google.api.services.youtube.YouTube mService, String playlistID) {
            this.mService = mService;
            this.playlistID = playlistID;
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
            mService.playlists().delete(playlistID).execute();

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
                for (String s : output) {
                    System.out.println(s);
                }
            }
            CredentialSetter.setmService(mService);
        }

        @Override
        protected void onCancelled() {
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {

                    System.out.println("Service not Available");
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            RQ_AUTHORIZATION);
                } else {
                    System.out.println("The following error occurred:\n" + mLastError.getMessage());
                }
            } else {
                System.out.println("Request cancelled.");
            }
        }
    }

    private class EditTitleTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.youtube.YouTube mService = null;
        private String playlistID;
        private String title;
        private String desc;
        private Exception mLastError = null;

        EditTitleTask(com.google.api.services.youtube.YouTube mService, String playlistID, String title, String desc) {
            this.mService = mService;
            this.playlistID = playlistID;
            this.title = title;
            this.desc = desc;
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

            com.google.api.services.youtube.model.Playlist playlist = new com.google.api.services.youtube.model.Playlist();

            com.google.api.services.youtube.model.PlaylistSnippet ps = new com.google.api.services.youtube.model.PlaylistSnippet();
            ps.setTitle(title).setDescription(desc);

            playlist.setId(playlistID).setSnippet(ps);

            mService.playlists().update("snippet,contentDetails", playlist).execute();

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
                for (String s : output) {
                    System.out.println(s);
                }
            }
            CredentialSetter.setmService(mService);

        }

        @Override
        protected void onCancelled() {

            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {

                    System.out.println("Service not Available");
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
}