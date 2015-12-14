package com.contactsharing.beamit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.contactsharing.beamit.db.DBHelper;
import com.contactsharing.beamit.model.ProfileDetails;
import com.contactsharing.beamit.resources.signup.SignupRequest;
import com.contactsharing.beamit.resources.signup.SignupResponse;
import com.contactsharing.beamit.transport.BeamItService;
import com.contactsharing.beamit.transport.BeamItServiceTransport;

import java.net.HttpURLConnection;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class SignUpActivity extends Activity {
    private static final String TAG = SignUpActivity.class.getSimpleName();
    private EditText etEmail;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        etEmail = (EditText)findViewById(R.id.et_email);
        etPassword = (EditText)findViewById(R.id.et_password);
    }

    public void onClick(View view){
       Intent intent = null;
       switch(view.getId()){
           case R.id.bt_sign_up:
//               intent = new Intent(this, ContactListActivity.class);
               signup();
               return;
           case R.id.tv_sign_in:
               startActivity(new Intent(this, SigninActivity.class));
               return;
           default:
               return;
       }
    }

    private void goToContactListActivit(){
        startActivity(new Intent(this, ContactListActivity.class));
        finish();
    }

    private void saveProfileDetails(ProfileDetails profileDetails){
        DBHelper db = new DBHelper(this);
        if (db.updateProfile(profileDetails) > 0) {
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }
    private void signup() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        SignupRequest signupRequest = new SignupRequest(email, password);
        BeamItService service = BeamItServiceTransport.getService();

        Call<SignupResponse> call = service.signup(signupRequest);
        ProfileDetails profileDetails = new ProfileDetails();
        profileDetails.setEmail(email);
        call.enqueue(new SignupCallback(profileDetails));   // asynchronous call of retrofit
    }

    private void deleteOldContacts(Integer userId){
        DBHelper db = new DBHelper(this);
        db.deleteContactWithoutCurrentOwner(userId);
        db.close();
    }

    private class SignupCallback implements Callback<SignupResponse> {

        private ProfileDetails profileDetails;
        public SignupCallback(ProfileDetails profileDetails){
            this.profileDetails = profileDetails;
        }
        @Override
        public void onResponse(Response<SignupResponse> response, Retrofit retrofit) {

            Log.d(TAG,
                    String.format("Signup successful=> status code: %d, body: %s ",
                            response.code(),
                            response.body()));
            if (response.code() == HttpURLConnection.HTTP_CREATED) {
                SignupResponse signupResponse = response.body();
                profileDetails.setUserId(signupResponse.getUserId());
                // Delete other user's contacts.
                deleteOldContacts(signupResponse.getUserId());
                //Save the user id first.
                saveProfileDetails(profileDetails);
                goToContactListActivit();
            }
        }

        @Override
        public void onFailure(Throwable t) {
            Log.e(TAG, "signup failed", t);
        }
    }
}

