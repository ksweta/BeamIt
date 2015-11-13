package com.contactsharing.beamit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.contactsharing.beamit.db.DBHelper;
import com.contactsharing.beamit.model.ContactDetails;
import com.contactsharing.beamit.utility.ApplicationConstants;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by kumari on 9/15/15.
 */
public class DisplayCardActivity extends Activity {
    public final static String TAG = DisplayCardActivity.class.getSimpleName();
    private TextView tvName;
    private TextView tvPhone;
    private TextView tvEmail;
    private ImageView ivContactPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_display_card);
        tvName =  (TextView) findViewById(R.id.tv_name);
        tvPhone =  (TextView) findViewById(R.id.tv_phone);
        tvEmail =  (TextView) findViewById(R.id.tv_email);
        ivContactPhoto = (ImageView) findViewById(R.id.iv_contact_photo);


        Intent intent = getIntent();
        if (intent != null && intent.getIntExtra(ApplicationConstants.EXTRA_CONTACT_LOCAL_ID, -1) > -1) {
            Integer contactLocalId = intent.getIntExtra(ApplicationConstants.EXTRA_CONTACT_LOCAL_ID, -1);
           new FetchContactDetailsAsyncTask().execute(contactLocalId);
        } else {
            Log.e(TAG, "couldn't fetch the right id from intent");
        }

    }

    public void onClick(View view){
        switch(view.getId()){
            case R.id.iv_email:
                sendEmail();
                break;
            case R.id.iv_phone:
                callPhone();
                break;
            case R.id.iv_sms:
                sendSms();
                break;
            default:
                Log.e(TAG, String.format("Wrong view id (%d) passed in onClick() method",
                        view.getId()));
        }
    }

    /**
     * This method starts an email application using intent.
     */
    private void sendEmail(){
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        Log.d(TAG, String.format("Email: %s", tvEmail.getText().toString()));
        intent.putExtra(Intent.EXTRA_EMAIL, tvEmail.getText().toString());
        intent.putExtra(Intent.EXTRA_SUBJECT, "Hello there!");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent, "Send Email"));
        } else {
            Toast.makeText(this, "Sorry couldn't find any email client", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This method calls phone number using intent.
     */
    private void callPhone(){
        Log.d(TAG, String.format("Phone: %s", tvPhone.getText().toString()));
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.fromParts("tel", tvPhone.getText().toString(), null));
        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Sorry couldn't find any phone app for call", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This method sends an sms using intent.
     */
    private void sendSms(){
        Log.d(TAG, String.format("Phone: %s", tvPhone.getText().toString()));
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms",
                tvPhone.getText().toString(),
                null));
        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this,
                    "Sorry couldn't send sms because there is no sms app installed",
                    Toast.LENGTH_SHORT).show();

        }
    }

    private class FetchContactDetailsAsyncTask extends AsyncTask<Integer, Integer, ContactDetails> {

        @Override
        protected ContactDetails doInBackground(Integer... integers) {
            Integer contactLocalId = integers[0];
            DBHelper dbHelper = new DBHelper(getApplicationContext());
            ContactDetails cd = dbHelper.getContact(contactLocalId);
            if (cd == null) {
                Log.d(TAG, String.format("Coouldn't fetch contact details for %d", contactLocalId));
            }
            dbHelper.close();
            return cd;
        }

        @Override
        protected void onPostExecute(ContactDetails contactDetails){
            if (contactDetails != null) {
                tvName.setText(contactDetails.getName());
                tvPhone.setText(contactDetails.getPhone());
                if (contactDetails.getEmail() != null) {
                    tvEmail.setText(contactDetails.getEmail());
                }
                if (contactDetails.getPhotoUri() != null) {
                    Context context = getApplicationContext();
                    Picasso.with(context)
                            .load(new File(context.getExternalFilesDir(null),
                                    contactDetails.getPhotoUri()))
                            .into(ivContactPhoto);
                }
            }
        }
    }
}
