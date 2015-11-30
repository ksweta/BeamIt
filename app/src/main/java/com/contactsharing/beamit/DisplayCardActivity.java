package com.contactsharing.beamit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.contactsharing.beamit.db.DBHelper;
import com.contactsharing.beamit.model.ContactDetails;
import com.contactsharing.beamit.services.DeleteContactService;
import com.contactsharing.beamit.utility.ApplicationConstants;
import com.contactsharing.beamit.utility.BitmapUtility;
import com.squareup.okhttp.internal.framed.FrameReader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by kumari on 9/15/15.
 */
public class DisplayCardActivity extends AppCompatActivity {
    public final static String TAG = DisplayCardActivity.class.getSimpleName();
    private TextView tvName;
    private TextView tvPhone;
    private TextView tvEmail;
    private ImageView ivContactPhoto;
    private TextView tvLinkedinUrl;
    private ContactDetails mContactDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_display_card);
        tvName =  (TextView) findViewById(R.id.tv_name);
        tvPhone =  (TextView) findViewById(R.id.tv_phone);
        tvEmail =  (TextView) findViewById(R.id.tv_email);
        ivContactPhoto = (ImageView) findViewById(R.id.iv_contact_photo);
        tvLinkedinUrl = (TextView) findViewById(R.id.tv_linkedin_url);

        Intent intent = getIntent();
        if (intent != null && intent.getIntExtra(ApplicationConstants.EXTRA_CONTACT_LOCAL_ID, -1) > -1) {
            Integer contactLocalId = intent.getIntExtra(ApplicationConstants.EXTRA_CONTACT_LOCAL_ID, -1);
           new FetchContactDetailsAsyncTask().execute(contactLocalId);
        } else {
            Log.e(TAG, "Couldn't fetch the right id from intent");
        }

        // Set up toolbar
        setSupportActionBar((Toolbar)findViewById(R.id.display_card_toolbar));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_display_card, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();

        switch(id){

            case R.id.action_delete_contact:

                new AlertDialog.Builder(this)
                        .setTitle("Delete Contact ")
                        .setMessage(String.format("Do you really want to delete %s's contact details?", mContactDetails.getName()))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteContactDetails(mContactDetails);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();

                return true;

            case R.id.action_export_contact:

                new AlertDialog.Builder(this)
                        .setTitle("Export Contact")
                        .setMessage(String.format("Do you want to export %s's contact to system contact list",mContactDetails.getName()))
                        .setIcon(R.drawable.ic_export_contact)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                exportContact(mContactDetails);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This method export the given contact to Android contact list.
     * @param contactDetails
     */
    private void exportContact(ContactDetails contactDetails) {

        if (contactDetails == null) {
            Log.e(TAG, "Contact details not found, not doing anything.");
        }
        ArrayList<ContentValues> data = new ArrayList<>();

        if (!contactDetails.getName().isEmpty()) {
            ContentValues rowName = new ContentValues();
            rowName.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE);
            rowName.put(ContactsContract.CommonDataKinds.Nickname.DISPLAY_NAME, contactDetails.getName());
            data.add(rowName);
        }
        if (!contactDetails.getCompany().isEmpty()) {
            ContentValues rowCompany = new ContentValues();
            rowCompany.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE);
            rowCompany.put(ContactsContract.CommonDataKinds.Organization.COMPANY, contactDetails.getCompany());
            data.add(rowCompany);

        }

        if (!contactDetails.getPhone().isEmpty()) {
            ContentValues rowPhone = new ContentValues();
            rowPhone.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            rowPhone.put(ContactsContract.CommonDataKinds.Phone.NUMBER, contactDetails.getPhone());
            data.add(rowPhone);
        }

        if (!contactDetails.getLinkedinUrl().isEmpty()) {
            ContentValues rowLinkedin = new ContentValues();
            rowLinkedin.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE);
            rowLinkedin.put(ContactsContract.CommonDataKinds.Website.TYPE, ContactsContract.CommonDataKinds.Website.TYPE_PROFILE);
            rowLinkedin.put(ContactsContract.CommonDataKinds.Website.URL, contactDetails.getLinkedinUrl());
            data.add(rowLinkedin);
        }

        if (!contactDetails.getPhotoUri().isEmpty()) {
            ContentValues rowPhoto = new ContentValues();
            rowPhoto.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);

            rowPhoto.put(ContactsContract.CommonDataKinds.Photo.PHOTO,
                    BitmapUtility.getBitmapToBytes(((BitmapDrawable) ivContactPhoto.getDrawable()).getBitmap()));
            data.add(rowPhoto);
        }

        ContentValues rowEmail = new ContentValues();
        rowEmail.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
        rowEmail.put(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK);
        rowEmail.put(ContactsContract.CommonDataKinds.Email.ADDRESS, contactDetails.getEmail());
        data.add(rowEmail);

        Intent intent = new Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI);
        intent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, data);

        if (!contactDetails.getName().isEmpty()) {
            intent.putExtra(ContactsContract.Intents.Insert.NAME, contactDetails.getName());
        }
        startActivity(intent);

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

    private void deleteContactDetails(final ContactDetails contactDetails) {
        DeleteContactService.deleteContact(getApplicationContext(), contactDetails.getId());
        DelayHandler dh = new DelayHandler(this);
        dh.sendEmptyMessageDelayed(0, 1000);
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
                mContactDetails = contactDetails;
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

                if (contactDetails.getLinkedinUrl() != null && !contactDetails.getLinkedinUrl().isEmpty()){
                    tvLinkedinUrl.setText(contactDetails.getLinkedinUrl());
                }
            }
        }
    }

    class DelayHandler extends Handler {
        private Activity activity;

        public DelayHandler(Activity activity){
            this.activity = activity;
        }
        @Override
        public void handleMessage(Message msg){
            this.activity.finish();
        }
    }
}
