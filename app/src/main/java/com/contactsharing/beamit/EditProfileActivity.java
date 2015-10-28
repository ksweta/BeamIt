package com.contactsharing.beamit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.contactsharing.beamit.db.ProfileDBHelper;
import com.contactsharing.beamit.model.ProfileDetails;

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

/**
 * Created by Kumari on 10/23/15.
 */
public class EditProfileActivity extends ActionBarActivity {

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
    private ProfileDBHelper mProfileDb;

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
            mProfileDb = new ProfileDBHelper(this);
        }
        //Fetch the profile details.
        mProfileDetails = mProfileDb.fetchProfileDetails();

        updateUI();

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
            case R.id.tv_linkedin_import:
                linkedinImport();
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
        mProfileDb.updateProfile(mProfileDetails);
        Toast.makeText(this, "Updated profile information", Toast.LENGTH_SHORT).show();

    }

    /**
     * This method triggers Linkedin activity to import information.
     */
    private void linkedinImport() {
        adapter.authorize(this, Provider.LINKEDIN);
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
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            ivProfilePhoto.setImageBitmap(selectedImage);
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
            etName.setText(String.format("%s %s", profile.getFirstName(), profile.getLastName()));
            etEmail.setText(profile.getEmail());
            if (profile.getProfileImageURL() != null) {
                new DownloadLinkedinProifilImage().execute(profile.getProfileImageURL());
            }
        }

        @Override
        public void onError(SocialAuthError e) {

        }
    }

    //Async task to download the Linkedin profile
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
}