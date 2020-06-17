package at.htlgkr.mebmusic.actvities;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.youtube.YouTube;

public class CredentialSetter {
    public static com.google.api.services.youtube.YouTube mService;

    public static YouTube getmService() {
        return mService;
    }

    public static void setmService(YouTube mServicee) {
        mService = mServicee;
    }
}
