package com.contactsharing.beamit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.contactsharing.beamit.db.DBHelper;
import com.contactsharing.beamit.model.ProfileDetails;
import com.contactsharing.beamit.resources.signin.SigninRequest;
import com.contactsharing.beamit.resources.signin.SigninResponse;
import com.contactsharing.beamit.transport.BeamItService;
import com.contactsharing.beamit.transport.BeamItServiceTransport;
import com.contactsharing.beamit.utility.BitmapUtility;
import com.contactsharing.beamit.utility.UtilityMethods;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import org.apache.http.HttpStatus;


import org.brickred.socialauth.Profile;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Kumari on 10/23/15.
 */
public class EditProfileActivity extends Activity {

    private final String TAG = EditProfileActivity.class.getSimpleName();
    private final int PHOTO_PICKER_REQUEST_ID = 12000;
    private final int LINKEDIN_PROFILE_IMPORT_REQUEST_ID = 1300;
    private ImageView ivProfilePhoto;
    private EditText etName;
    private EditText etEmail;
    private EditText etCompany;
    private EditText etLinkeninUrl;
    private ProfileDetails mProfileDetails;

    //LinkedIn import report
    private SocialAuthAdapter adapter;

    //Db
    private DBHelper mProfileDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ivProfilePhoto = (ImageView) findViewById(R.id.iv_profile_photo);
        etName = (EditText) findViewById(R.id.et_name);
        etEmail = (EditText) findViewById(R.id.et_email);
        etCompany = (EditText) findViewById(R.id.et_company);
        etLinkeninUrl = (EditText) findViewById(R.id.et_linkedin_url);

        if (mProfileDb == null) {
            mProfileDb = new DBHelper(this);
        }
        //Fetch the profile details.
        new FetchProfileDetailsAsyncTask().execute();

        //LinkedInt import
        adapter = new SocialAuthAdapter(new ResponseListener());

    }

    private void updateUI() {
        if (mProfileDetails != null) {
            if (mProfileDetails.getPhoto() != null) {
                ivProfilePhoto.setImageBitmap(mProfileDetails.getPhoto());
            }
            etName.setText(mProfileDetails.getName());
            etEmail.setText(mProfileDetails.getEmail());
            etCompany.setText(mProfileDetails.getCompany());
            etLinkeninUrl.setText(mProfileDetails.getLinkedinUrl());
        }
    }

    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.bt_cancel:
                finish();
                break;
            case R.id.bt_save:
                saveProfile();
                break;
            case R.id.iv_profile_photo:
                importProfileImage();
                break;
            case R.id.tv_linkedin_import:
//                linkedinImport();
                downloadProfilePhoto();
                break;
            default:
                Log.e(TAG, String.format("Wrong onClick option: %d", view.getId()));
        }
    }

    /**
     * This method saves the profile information
     */
    private void saveProfile() {
        if (mProfileDetails == null) {
            mProfileDetails = new ProfileDetails();
        }
        mProfileDetails.setPhoto(((BitmapDrawable) ivProfilePhoto.getDrawable()).getBitmap());
        mProfileDetails.setName(etName.getText().toString());
        mProfileDetails.setEmail(etEmail.getText().toString());
        mProfileDetails.setCompany(etCompany.getText().toString());
        mProfileDetails.setLinkedinUrl(etLinkeninUrl.getText().toString());
        uploadProfilePhoto();
        new SaveProfileDetailsAsyncTask().execute(mProfileDetails);
    }

    /**
     * This method triggers Linkedin activity to import information.
     */
    private void linkedinImport() {
//        adapter.authorize(this, Provider.LINKEDIN);
        new DownloadLinkedinProifilImage().execute("https://blooming-cliffs-9672.herokuapp.com/api/photo/user/5");

    }

    /**
     * This method imports profile picture.
     */
    private void importProfileImage() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, PHOTO_PICKER_REQUEST_ID);
    }

    /**
     * This method extract the information form intent and set the profile image.
     *
     * @param intent
     */
    private void setProfileImage(Intent intent) {
        final Uri imageUri = intent.getData();
        try {
            final InputStream imageStream = getContentResolver().openInputStream(imageUri);
            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            ivProfilePhoto.setImageBitmap(BitmapUtility.getResizedBitmap(selectedImage, 150, 150));
        } catch (FileNotFoundException e) {
            Log.w(TAG, e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PHOTO_PICKER_REQUEST_ID:
                if (resultCode == RESULT_OK) {
                    setProfileImage(data);
                }
                break;
            case LINKEDIN_PROFILE_IMPORT_REQUEST_ID:
                break;

            default:
                Log.i(TAG, String.format("Not handled request code: %d ", requestCode));
        }

    }


    private final class ResponseListener implements DialogListener {
        public void onComplete(Bundle values) {

            adapter.getUserProfileAsync(new ProfileDataListener());
        }

        @Override
        public void onError(SocialAuthError error) {
            Log.d("Custom-UI", "Error");
            error.printStackTrace();
        }

        @Override
        public void onCancel() {
            Log.d("Custom-UI", "Cancelled");
        }

        @Override
        public void onBack() {
            Log.d("Custom-UI", "Dialog Closed by pressing Back Key");

        }
    }

    // To get status of message after authentication
    private final class ProfileDataListener implements SocialAuthListener<Profile> {

        @Override
        public void onExecute(String provider, Profile profile) {

            Log.d("Custom-UI", "Receiving Data");
            Log.d(TAG, String.format("LinedIn=> first name: %s", profile.getFirstName()));
            Log.d(TAG, String.format("LinedIn=> last name: %s", profile.getLastName()));
            Log.d(TAG, String.format("LinedIn=> email: %s", profile.getEmail()));
            Log.d(TAG, String.format("LinedIn=> url: %s", profile.getProfileImageURL()));
            Log.d(TAG, String.format("LinkedIn=> id: %s", profile.getProviderId()));
            etName.setText(String.format("%s %s", profile.getFirstName(), profile.getLastName()));
            etEmail.setText(profile.getEmail());
            if (profile.getProfileImageURL() != null) {
//                new DownloadLinkedinProifilImage().execute(profile.getProfileImageURL());
            }

            Toast.makeText(getApplicationContext(), "Successfully imported!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SocialAuthError e) {
            Toast.makeText(getApplicationContext(), "Coulnd't import LinkedIn profile info", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Async task to download the Linkedin profile
     */

    private class DownloadLinkedinProifilImage extends AsyncTask<String, Integer, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls){
            HttpURLConnection urlConnection = null;
            try {
                URL uri = new URL(urls[0]);
                urlConnection = (HttpURLConnection) uri.openConnection();

                int statusCode = urlConnection.getResponseCode();
                if (statusCode != HttpStatus.SC_OK) {
                    return null;
                }

                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream != null) {

                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                }
            } catch (Exception e) {
                Log.d("URLCONNECTIONERROR", e.toString());
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                Log.w("ImageDownloader", "Error downloading image from " + urls[0]);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();

                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap){

            if (ivProfilePhoto != null && bitmap != null) {
                ivProfilePhoto.setImageBitmap(bitmap);
            }
        }
    }

    /**
     * Async Task to fetch profile information.
     */

    private class FetchProfileDetailsAsyncTask extends AsyncTask<Void, Integer, ProfileDetails> {

        @Override
        protected ProfileDetails doInBackground(Void... nothing){
            DBHelper db = new DBHelper(getApplicationContext());
            ProfileDetails profileDetails = null;
            try {
                profileDetails = db.fetchProfileDetails();
            } catch (Exception e) {
                Log.e(TAG, "couldn't fetch profile details", e);

            } finally {
                if (db != null) {
                    db.close();
                }
            }
            return profileDetails;
        }

        @Override
        protected void onPostExecute(ProfileDetails profileDetails){

            if (profileDetails != null){
                mProfileDetails = profileDetails;
                updateUI();
            }
        }
    }

    /**
     * Async task to save profile details
     */

    private class SaveProfileDetailsAsyncTask extends AsyncTask<ProfileDetails, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(ProfileDetails... profileDetails){
            DBHelper db = new DBHelper(getApplicationContext());
            Boolean result = false;
            ProfileDetails profile = profileDetails[0];
            try {
                if  (db.updateProfile(profile) > 0L){
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                Log.e(TAG, "couldn't fetch profile details", e);

            } finally {
                if (db != null) {
                    db.close();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result){

            if (result){
                Toast.makeText(getApplicationContext(), "User details saved successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Coudln't save user details", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadProfilePhoto(){
        BeamItService service = BeamItServiceTransport.getService();

        MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg");
        byte [] data = BitmapUtility.getBitmapToBytes(((BitmapDrawable) ivProfilePhoto.getDrawable()).getBitmap());
        ProfileDetails profile = mProfileDb.fetchProfileDetails();
        Log.d(TAG, String.format("Profile detals => user_id: %d, filename: %s, size of data: %d",
                profile.getUserId(),
                UtilityMethods.formatFileString("profile", profile.getUserId(), "jpeg"),
                data.length));

        RequestBody requestBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart("photo",
                        UtilityMethods.formatFileString("profile", profile.getUserId(), "jpeg"),
                        RequestBody.create(MEDIA_TYPE_JPEG, data))
                .build();

        Call<Void> call = service.uploadUserProfilePhoto(profile.getUserId().intValue(),
                requestBody);
        call.enqueue(new ProfilePhotoUploadCallback());
    }

    private class ProfilePhotoUploadCallback implements Callback<Void> {

        @Override
        public void onResponse(Response<Void> response, Retrofit retrofit) {
            Log.d(TAG, String.format("ProfilePhotoUploadCallback=> code: %d", response.code()));
        }

        @Override
        public void onFailure(Throwable t) {

        }
    }

    private void downloadProfilePhoto(){
        BeamItService service = BeamItServiceTransport.getService();
        Call<ResponseBody> call = service.downloadUserProfilePhoto(5);
        call.enqueue(new ProfilePhotoDownloadCallback());
    }

    private class ProfilePhotoDownloadCallback implements Callback<ResponseBody>{

        @Override
        public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
            Log.d(TAG, String.format("ProfilePhotoDownloadCallback=> code: %d", response.code()));
            if (response.code() == HttpURLConnection.HTTP_OK) {
                try {
                    byte[] data = response.body().bytes();
                    Log.d(TAG, String.format("data size: %d", data.length));
                    Bitmap bitmap = BitmapUtility.getBytesToBitmap(data);
                    if (bitmap != null){
                        ivProfilePhoto.setImageBitmap(bitmap);
                    }
                } catch(Exception e){
                    Log.e(TAG, "Exception wile extracting image", e);
                }
            }
        }

        @Override
        public void onFailure(Throwable t) {
            Log.e(TAG, "onFailure=> exception", t);
        }
    }
}