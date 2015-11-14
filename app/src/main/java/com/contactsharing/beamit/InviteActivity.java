package com.contactsharing.beamit;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.contactsharing.beamit.db.DBHelper;
import com.contactsharing.beamit.model.ProfileDetails;
import com.contactsharing.beamit.resources.invite.EmailInvite;
import com.contactsharing.beamit.transport.BeamItService;
import com.contactsharing.beamit.transport.BeamItServiceTransport;

import java.net.HttpURLConnection;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Kumari on 11/1/15.
 */
public class InviteActivity extends Activity {
    private static final String TAG = InviteActivity.class.getSimpleName();
    private EditText etEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        etEmail = (EditText)findViewById(R.id.et_email);
    }

    public void onClick(View view){
        switch(view.getId()){
            case R.id.bt_invite:
                String emailOrPhone = etEmail.getText().toString();
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(emailOrPhone).matches()) {
                    sendEmailInvite(emailOrPhone);
                }

            default:
                Log.i(TAG, String.format("Wrong view (%d)", view.getId()));
        }
    }
    private void sendEmailInvite(String email) {
        DBHelper db = new DBHelper(this);
        ProfileDetails profileDetails = db.fetchProfileDetails();
        db.close();

        if (profileDetails != null){
            BeamItService service = BeamItServiceTransport.getService();
            EmailInvite emailInvite = new EmailInvite(profileDetails.getUserId(), email);
            Call<Void> call = service.sendEmailInvite(emailInvite);
            call.enqueue(new SendEmailInviteCallback());
        } else {
            Log.e(TAG, "Profile details shouldn't be null here.");
        }
    }

    private class SendEmailInviteCallback implements Callback<Void> {

        @Override
        public void onResponse(Response<Void> response, Retrofit retrofit) {

            if (response.code() == HttpURLConnection.HTTP_OK){
                Toast.makeText(getApplicationContext(),
                        "Invite email successfully sent",
                        Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, String.format("Error while sending invite code: %d, response: %s",
                        response.code(),
                        response.body()));
            }

        }

        @Override
        public void onFailure(Throwable t) {
            Log.e(TAG, "Error while sending invite", t);
        }
    }
}
