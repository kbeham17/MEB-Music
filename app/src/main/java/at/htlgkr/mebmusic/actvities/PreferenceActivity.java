package at.htlgkr.mebmusic.actvities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import at.htlgkr.mebmusic.fragment.PreferenceFragment;

public class PreferenceActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new PreferenceFragment()).commit();
    }
}
