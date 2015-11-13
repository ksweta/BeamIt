package com.contactsharing.beamit.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.contactsharing.beamit.db.DBHelper;
import com.contactsharing.beamit.model.ProfileDetails;
import com.contactsharing.beamit.resources.user.User;
import com.contactsharing.beamit.transport.BeamItService;
import com.contactsharing.beamit.transport.BeamItServiceTransport;
import com.contactsharing.beamit.utility.ApplicationConstants;
import com.contactsharing.beamit.utility.BitmapUtility;
import com.contactsharing.beamit.utility.UtilityMethods;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.net.HttpURLConnection;

import retrofit.Call;
import retrofit.Response;

/**
 * DownloadUserInfoService to download user information and persists in local storage.
 *
 */
public class DownloadUserInfoService extends IntentService {

    private static final String EXTRA_USER_ID = "DownloadUserInfoService.EXTRA_USER_ID";
    private static final String TAG = DownloadUserInfoService.class.getSimpleName();

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startDownloadUserInfo(Context context, Integer userId) {
        Intent intent = new Intent(context, DownloadUserInfoService.class);
        intent.putExtra(EXTRA_USER_ID, userId);
        context.startService(intent);
    }


    public DownloadUserInfoService() {
        super("DownloadUserInfoService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final Integer userId = intent.getIntExtra(EXTRA_USER_ID, -1);
            if (userId == -1){
                Log.e(TAG, "Couldn't find right userId, not going to sync userinfo");
                return;
            }
            ProfileDetails profileDetails = downloadUserInfo(userId);

            if (profileDetails == null) {
                //Couldn't download profile.
                return;
            }

            if(!downloadUserImage(profileDetails)){
                Log.e(TAG, "couldn't download user photo");
                return;
            }

            //Time to save this information in db.
            DBHelper db = new DBHelper(getApplicationContext());
            db.updateProfile(profileDetails);
            db.close();
        }
    }

    private ProfileDetails downloadUserInfo(Integer userId){

        BeamItService service = BeamItServiceTransport.getService();
        Call<User> call = service.getUserProfile(userId);
        Response<User> userResponse = null;
        try{
            userResponse = call.execute();
        } catch (IOException ex){
            Log.e(TAG, "Couldn't fetch user infor", ex);
            return null;
        }

        if (userResponse.code() != HttpURLConnection.HTTP_OK){
            Log.e(TAG, String.format("couldn't fetch user info, code: %d, body: %s",
                    userResponse.code(),
                    userResponse.body()));
            return null;
        }
        return ProfileDetails.fromUser(userResponse.body());
    }

    public boolean downloadUserImage(ProfileDetails profileDetails){
        BeamItService service = BeamItServiceTransport.getService();
        Call<ResponseBody> call = service.downloadUserProfilePhoto(profileDetails.getUserId());
        Response<ResponseBody> response = null;
        try{
            response = call.execute();
        } catch(IOException ex){
            Log.e(TAG, "Couldn't get contact photo", ex);
            return false;
        }

        if (response.code() != HttpURLConnection.HTTP_OK) {
            Log.e(TAG, String.format("Couldn't fetch user photo => code: %d", response.code()));
            return false;
        }
        try {
            Bitmap bitmap = BitmapUtility.getBytesToBitmap(response.body().bytes());
            if(bitmap == null) {
                Log.e(TAG, "Couldn't convert bytes to bitmap");
                return false;
            }
            String photoFileName = UtilityMethods.photoFileNameFormatter(ApplicationConstants.PROFILE_PHOTO_FILE_PREFIX,
                    ApplicationConstants.PHOTO_FILE_EXTENSION,
                    profileDetails.getUserId());
            if(BitmapUtility.storeImageToInternalStorage(getApplicationContext(),
                    bitmap,
                    ApplicationConstants.PROFILE_PHOTO_DIRECTORY,
                    photoFileName)){
                //After successfully saving image update the uri.
                profileDetails.setPhotoUri(ApplicationConstants.PROFILE_PHOTO_DIRECTORY + "/" + photoFileName);
                return true;
            } else {
                Log.e(TAG, String.format("Couldn't save user(%d) photo", profileDetails.getUserId()));
                return false;
            }
        } catch (IOException ex) {
            Log.e(TAG, "error while converting bytes to bitmap", ex);
            return false;
        }
    }
}
