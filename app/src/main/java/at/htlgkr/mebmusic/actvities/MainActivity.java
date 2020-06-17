package at.htlgkr.mebmusic.actvities;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.*;
import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import at.htlgkr.mebmusic.R;
import at.htlgkr.mebmusic.fragment.PlaylistFragment;
import at.htlgkr.mebmusic.fragment.ProfileFragment;
import at.htlgkr.mebmusic.fragment.SearchFragment;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, EasyPermissions.PermissionCallbacks{

    private static String profile_picture_url;
    private static String name;
    private static String email;
    private static String id;

    private PlaylistFragment playlistFragment = new PlaylistFragment("");
    private ProfileFragment profileFragment = new ProfileFragment(null, null);
    private SearchFragment searchFragment = new SearchFragment();

    private FragmentTransaction ft;

    private SharedPreferences prefs;

    public static GoogleApiClient googleApiClient;
    private GoogleSignInOptions gso;

    private BottomNavigationView menuBottomNavigationView;
    private Button buttonLogout;

    private GoogleAccountCredential mCredential;
    ProgressDialog mProgress;

    private static final int RQ_RESULT_START_ACTIVITY = 1;
    private static final int RQ_ACCOUNT_PICKER = 1000;
    private static final int RQ_AUTHORIZATION = 1001;
    private static final int RQ_GOOGLE_PLAY_SERVICES = 1002;
    private static final int RQ_PERMISSION_GET_ACCOUNTS = 1003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        gotoStartActivity();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        menuBottomNavigationView = findViewById(R.id.menu_bottomNavigationView);

        menuBottomNavigationView.setSelectedItemId(R.id.menu_profile);

        MainActivity mAct = this;

        menuBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.isChecked()) {
                    return true;
                } else {
                    switch (menuItem.getItemId()) {
                        case R.id.menu_profile:

                            profileFragment = new ProfileFragment(name, profile_picture_url);
                            setFragment(profileFragment);
                            return true;

                        case R.id.menu_playlist:

                            String channelId = prefs.getString("edit_text_channelId", "UCMnR3J-chev22dTqJEquFcg");

                            if (channelId.equals("")){
                                channelId = "UCMnR3J-chev22dTqJEquFcg";
                            }

                            playlistFragment = new PlaylistFragment(channelId);
                            playlistFragment.setMAct(mAct);
                            setFragment(playlistFragment);
                            return true;

                        case R.id.menu_search:

                            String order = prefs.getString("list_preference_order", "relevance");
                            searchFragment = new SearchFragment(order);
                            setFragment(searchFragment);
                            return true;

                        case R.id.menu_logout:

                            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                                @Override
                                public void onResult(@NonNull Status status) {
                                    if (status.isSuccess()) {
                                        gotoStartActivity();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Login Failed!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                            return true;

                        default:
                            profileFragment = new ProfileFragment(name, profile_picture_url);
                            setFragment(profileFragment);
                            return true;

                    }

                }
            }
        });
    }

    private void gotoStartActivity() {
        Intent intent = new Intent(this, StartActivity.class);
        startActivityForResult(intent, RQ_RESULT_START_ACTIVITY);

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void handleSignInResult(GoogleSignInResult result){
        if(result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();

            profile_picture_url = null;
            name = null;
            email = null;
            id = null;

            if(account.getPhotoUrl() != null){
                profile_picture_url = account.getPhotoUrl().toString();
            }
            name = account.getDisplayName();
            email = account.getEmail();
            id = account.getId();



        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);

        if (opr.isDone()){
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult result) {
                    handleSignInResult(result);
                }
            });
        }
    }

    public void setFragment(Fragment fragment){
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_main, fragment);
        ft.commit();
    }

    public void setPlaylistFragment(){
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_main, playlistFragment);
        ft.commit();
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }
}
