package at.htlgkr.mebmusic.fragment;

import android.os.Bundle;


import androidx.preference.PreferenceFragmentCompat;

import at.htlgkr.mebmusic.R;
//Stefan
public class PreferenceFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }


}
