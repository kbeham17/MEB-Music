package at.htlgkr.mebmusic.fragment;


import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import at.htlgkr.mebmusic.actvities.CredentialSetter;
import at.htlgkr.mebmusic.actvities.PreferenceActivity;
import at.htlgkr.mebmusic.R;

public class ProfileFragment extends Fragment {

    private static final int RQ_PREFERENCES = 101;
    private static View view;

    private static TextView textGoogleName;
    private static ImageView googleProfilePicture;
    private String name;
    private String profilePictureUrl;

    public ProfileFragment(String name, String profile_picture_url) {
        this.profilePictureUrl = profile_picture_url;
        this.name = name;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        initializeView(view);
        setupProfile(view);
        return view;
    }

    public void initializeView(View view) {
        Button settings = view.findViewById(R.id.buttonPreference);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PreferenceActivity.class);
                startActivityForResult(intent, RQ_PREFERENCES);
            }
        });
    }

    public void setupProfile(View view) {
        textGoogleName = view.findViewById(R.id.text_google_name);
        googleProfilePicture = view.findViewById(R.id.iv_google_profile_picture);
        textGoogleName.setText(CredentialSetter.getName());
    }
}