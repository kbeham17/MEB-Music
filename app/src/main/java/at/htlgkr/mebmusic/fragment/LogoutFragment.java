package at.htlgkr.mebmusic.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import at.htlgkr.mebmusic.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LogoutFragment extends Fragment {
    private Button buttonLogout;

    public LogoutFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_logout, container, false);

    }

}
