package com.contactsharing.beamit;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcF;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.contactsharing.beamit.db.DBHelper;
import com.contactsharing.beamit.model.ContactDetails;
import com.contactsharing.beamit.model.ProfileDetails;
import com.contactsharing.beamit.resources.signin.SigninRequest;
import com.contactsharing.beamit.resources.signin.SigninResponse;
import com.contactsharing.beamit.services.DownloadUserInfoService;
import com.contactsharing.beamit.transport.BeamItService;
import com.contactsharing.beamit.transport.BeamItServiceTransport;
import com.google.gson.Gson;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.Arrays;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class SigninActivity extends Activity {
    private static final String TAG = SigninActivity.class.getSimpleName();
    private EditText etEmail;
    private EditText etPassword;

    public final static String EXTRA_MESSAGE = "com.contactsharing.beamit.SigninActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        etEmail = (EditText) findViewById(R.id.et_email);
        etPassword = (EditText) findViewById(R.id.et_password);

        PackageManager pm = this.getPackageManager();
        // Check whether NFC is available on device
        if (!pm.hasSystemFeature(PackageManager.FEATURE_NFC)) {
            // NFC is not available on the device.
            Toast.makeText(this, "The device does not has NFC hardware.",
                    Toast.LENGTH_SHORT).show();
        }
        // Check whether device is running Android 4.1 or higher
        else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            // Android Beam feature is not supported.
            Toast.makeText(this, "Android Beam is not supported.",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            // NFC and Android Beam file transfer is supported.
            Toast.makeText(this, "Android Beam is supported on your device.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void onClick(View view){
        switch(view.getId()){
            case R.id.bt_sign_in:
                authenticateSignIn();
                return;
            case R.id.tv_sign_up:
                startActivity(new Intent(this, SignUpActivity.class));
                return;
            default:
                return;
        }
    }

    private void goToContactListActivit(){
        startActivity(new Intent(this, ContactListActivity.class));
        finish();
    }

    public void syncUserProfile(Integer userId){
        DBHelper db = new DBHelper(this);
        ProfileDetails userProfile = db.fetchProfileDetails();
        if (userProfile == null || !userProfile.getUserId().equals(userId)) {
            Log.d(TAG, "User profile not found on phone syncing information.");

            DownloadUserInfoService.startDownloadUserInfo(getApplicationContext(), userId);
            //TODO: Need to sync contact details as well.

        }
    }
    private void authenticateSignIn(){
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        SigninRequest signinRequest = new SigninRequest(email, password);
        BeamItService service = BeamItServiceTransport.getService();

        Call<SigninResponse> call = service.signin(signinRequest);
        call.enqueue(new Callback<SigninResponse>() {
            @Override
            public void onResponse(Response<SigninResponse> response, Retrofit retrofit) {
                SigninResponse signinResponse = response.body();
                Log.d(TAG,
                        String.format("Signup successful=> status code: %d, body: %s ",
                                response.code(),
                                response.body()));

                if(response.code() == HttpURLConnection.HTTP_OK) {
                    //Sync user profile.
                    syncUserProfile(signinResponse.getUserId());
                    goToContactListActivit();
                } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    Toast.makeText(getApplicationContext(),
                            "Wrong email address or password.",
                            Toast.LENGTH_SHORT).show();
                } else if (response.code() == HttpURLConnection.HTTP_NOT_FOUND) {
                    Toast.makeText(getApplicationContext(),
                            "Account is not register. Please signup",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "signup failed", t);
            }
        });
    }
}
