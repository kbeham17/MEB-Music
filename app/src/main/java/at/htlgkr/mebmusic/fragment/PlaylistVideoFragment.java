package at.htlgkr.mebmusic.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import at.htlgkr.mebmusic.R;
import at.htlgkr.mebmusic.actvities.MainActivity;
import at.htlgkr.mebmusic.actvities.PreferenceActivity;


public class PlaylistVideoFragment extends Fragment {

    private MainActivity mAct;
    //private Fragment backFragmentPlaylist;

    private View view;

    private String id;

    public PlaylistVideoFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_playlist_video, container, false);

        setUpBackButton(view);

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

    public PlaylistVideoFragment(String id){
        this.id = id;
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
}
