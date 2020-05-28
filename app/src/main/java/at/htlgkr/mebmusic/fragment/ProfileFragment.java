package at.htlgkr.mebmusic.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import at.htlgkr.mebmusic.PreferenceActivity;
import at.htlgkr.mebmusic.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

   private static final int RQ_PREFERENCES = 101;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        initializeView(view);
        return view;
    }

    public void initializeView(View view){
        Button settings = view.findViewById(R.id.buttonPreference);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PreferenceActivity.class);
                startActivityForResult(intent, RQ_PREFERENCES);
            }
        });
    }
}
