package com.contactsharing.beamit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.contactsharing.beamit.db.DBHelper;
import com.contactsharing.beamit.model.ProfileDetails;
import com.contactsharing.beamit.resources.password.ChangePasswordRequest;
import com.contactsharing.beamit.resources.password.ChangePasswordResponse;
import com.contactsharing.beamit.transport.BeamItService;
import com.contactsharing.beamit.transport.BeamItServiceTransport;

import java.net.HttpURLConnection;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class ChangePasswordActivity extends AppCompatActivity {

    private static final String TAG = ChangePasswordActivity.class.getSimpleName();
    private EditText etOldPassword;
    private EditText etNewPassword;
    private EditText etConfirmNewPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        etOldPassword = (EditText) findViewById(R.id.et_old_password);
        etNewPassword = (EditText) findViewById(R.id.et_new_password);
        etConfirmNewPassword = (EditText) findViewById(R.id.et_confirm_new_password);
    }

    public void onClick(View view){

        switch(view.getId()){
            case R.id.bt_change_password:
                if (etNewPassword.getText().toString().equals(etConfirmNewPassword.getText().toString())){
                    changePassword(etOldPassword.getText().toString(), etNewPassword.getText().toString());
                } else {
                    Toast.makeText(this,
                            "Please make sure confirm password matches with new one.",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                Log.w(TAG, String.format("Wrong key %s",view.toString()));
        }
    }

    private void changePassword(String oldPassword, String newPassword){
        DBHelper db = new DBHelper(this);
        ProfileDetails userProfile = db.fetchProfileDetails();
        if(userProfile != null && userProfile.getEmail() != null && !userProfile.getEmail().isEmpty()){
            BeamItService service = BeamItServiceTransport.getService();
            ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(userProfile.getEmail(),
                    oldPassword,
                    newPassword);
            Call<ChangePasswordResponse> call = service.changePassword(changePasswordRequest);
            call.enqueue(new ChangePasswordCallback());
        } else{
            Log.e(TAG, "User should exist here, something is wrong");
        }
    }

   public class ChangePasswordCallback implements Callback<ChangePasswordResponse>{

       @Override
       public void onResponse(Response<ChangePasswordResponse> response, Retrofit retrofit) {

           if(response.code() == HttpURLConnection.HTTP_OK){
                Toast.makeText(getApplicationContext(),
                        "Changed password successfully",
                        Toast.LENGTH_SHORT).show();
           }
           else if(response.code() == HttpURLConnection.HTTP_NOT_FOUND){
               Toast.makeText(getApplicationContext(),
                       "Please make sure you are a registered user",
                       Toast.LENGTH_SHORT).show();
           } else if(response.code() == HttpURLConnection.HTTP_UNAUTHORIZED){
               Toast.makeText(getApplicationContext(),
                       "Please make sure your old password is correct",
                       Toast.LENGTH_SHORT).show();
           } else {
               Toast.makeText(getApplicationContext(), "Not sure what happened, but try again.", Toast.LENGTH_SHORT).show();
           }

       }

       @Override
       public void onFailure(Throwable t) {
            Log.e(TAG, "Change password failed", t);
       }
   }
}
