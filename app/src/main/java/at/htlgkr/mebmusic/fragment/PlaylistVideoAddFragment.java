package at.htlgkr.mebmusic.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemSnippet;
import com.google.api.services.youtube.model.PlaylistStatus;
import com.google.api.services.youtube.model.ResourceId;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import at.htlgkr.mebmusic.R;
import at.htlgkr.mebmusic.actvities.CredentialSetter;
import at.htlgkr.mebmusic.actvities.MainActivity;
import at.htlgkr.mebmusic.adapter.PlaylistVideoAddAdapter;
import at.htlgkr.mebmusic.apitasks.GETTask;
import at.htlgkr.mebmusic.apitasks.YoutubeAPI;
import at.htlgkr.mebmusic.playlist.Playlist;
import at.htlgkr.mebmusic.playlist.PlaylistDetails;
import at.htlgkr.mebmusic.playlist.PlaylistSnippet;
import at.htlgkr.mebmusic.thumbnail.MediumThumb;
import at.htlgkr.mebmusic.thumbnail.Thumbnail;
import at.htlgkr.mebmusic.videos.Video;

public class PlaylistVideoAddFragment extends Fragment {


    private static final String KIND = "youtube#video";
    private String CHANNELID;

    private static final int RQ_AUTHORIZATION = 1001;

    private List<Playlist> playlistList = new ArrayList<>();
    private Video video;
    private MainActivity mAct;
    private com.google.api.services.youtube.YouTube mService;

    private PlaylistVideoAddAdapter mAdapt;
    private ListView lv;

    public PlaylistVideoAddFragment() {

    }

    public PlaylistVideoAddFragment(String CHANNELID, Video video) {
        this.video = video;
        this.CHANNELID = CHANNELID;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlistvideoadd, container, false);

        this.mService = CredentialSetter.getmService();

        lv = view.findViewById(R.id.listview_playlistvideoadd);

        Context ctx = getContext();
        mAdapt = new PlaylistVideoAddAdapter(ctx, R.layout.row_item_playlistvideoadd, playlistList);

        lv.setAdapter(mAdapt);

        playlistList.clear();

        PlaylistSnippet playlistSnippet = new PlaylistSnippet("neue Playlist erstellen", null, null);
        PlaylistDetails playlistDetails = new PlaylistDetails(0);
        playlistList.add(new Playlist("-1", playlistSnippet, playlistDetails));

        getJson();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Playlist playlist = playlistList.get(i);

                if (playlist.getId().equals("-1")) {
                    new CreatePlaylistTask(mService, playlist).execute();

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//                    fragmentTransaction.replace(R.id.playlistaddlayout, new PlaylistFragment());
//                    fragmentTransaction.commit();
                } else {
                    new AddPlaylistVideo(mService, playlist, video).execute();

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//                    fragmentTransaction.replace(R.id.playlistaddlayout, new PlaylistFragment());
//                    fragmentTransaction.commit();
                }
            }
        });

        return view;
    }

    public void setMAct(MainActivity mAct) {
        this.mAct = mAct;
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
        mAdapt.notifyDataSetChanged();
    }

    private class CreatePlaylistTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.youtube.YouTube mService = null;
        private Playlist playlist;
        private Exception mLastError = null;

        CreatePlaylistTask(com.google.api.services.youtube.YouTube mService, Playlist playlist) {
            this.mService = mService;
            this.playlist = playlist;
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
            com.google.api.services.youtube.model.Playlist ytPlaylist = new com.google.api.services.youtube.model.Playlist();
            com.google.api.services.youtube.model.PlaylistSnippet ytPlaylistSnippet = new com.google.api.services.youtube.model.PlaylistSnippet();

            ytPlaylistSnippet.setTitle("neue Playlist");
            ytPlaylist.setSnippet(ytPlaylistSnippet);

            PlaylistStatus playlistStatus = new PlaylistStatus();
            playlistStatus.setPrivacyStatus("public");

            ytPlaylist.setStatus(playlistStatus);

            mService.playlists().insert("snippet, status", ytPlaylist).execute();

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

    private class AddPlaylistVideo extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.youtube.YouTube mService = null;
        private Playlist playlist;
        private Exception mLastError = null;
        private Video video;

        AddPlaylistVideo(com.google.api.services.youtube.YouTube mService, Playlist playlist, Video video) {
            this.mService = mService;
            this.playlist = playlist;
            this.video = video;
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
            PlaylistItem playlistItem = new PlaylistItem();
            PlaylistItemSnippet playlistItemSnippet = new PlaylistItemSnippet();
            playlistItemSnippet.setChannelId(CHANNELID);
            playlistItemSnippet.setPlaylistId(playlist.getId());

            ResourceId resourceId = new ResourceId();
            resourceId.setChannelId(CHANNELID).setKind(KIND).setVideoId(video.getVideoID()).setPlaylistId(playlist.getId());
            playlistItemSnippet.setResourceId(resourceId);

            playlistItem.setSnippet(playlistItemSnippet);

            mService.playlistItems().insert("snippet", playlistItem).execute();

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
