package com.contactsharing.beamit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcF;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.widget.ViewSwitcher;

import com.contactsharing.beamit.db.DBHelper;
import com.contactsharing.beamit.model.ContactDetails;
import com.contactsharing.beamit.model.ProfileDetails;
import com.contactsharing.beamit.resources.contact.Contact;
import com.contactsharing.beamit.resources.share.ShareContactRequest;
import com.contactsharing.beamit.transport.BeamItService;
import com.contactsharing.beamit.transport.BeamItServiceTransport;
import com.contactsharing.beamit.utility.ApplicationConstants;
import com.contactsharing.beamit.utility.BitmapUtility;
import com.contactsharing.beamit.utility.UtilityMethods;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import retrofit.Call;
import retrofit.Response;

public class ContactListActivity extends AppCompatActivity {
    private static final String TAG = ContactListActivity.class.getSimpleName();
    private static final String ACTION_NDEF_DISCOVERED = "android.nfc.action.NDEF_DISCOVERED";
    private static final String ACTION_REFRESH_CONTACTS = "ContactListActivity.ACTION_CONTACT_REFRESH";
    private static final int CONTACT_PICKER_RESULT = 1503;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ImageButton FAB;
    private TextView tvEmptyView;
    private ViewSwitcher mVSContactList;

    private List<ContactDetails> mContacts;
    private DBHelper mDb;

    //Recylcer view related variables
    private RecyclerView mRecyclerView;
    private ContactNamesRecyclerViewAdapter mContactNamesRecyclerViewAdapter;

    //NFC related
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mIntentFilters;
    private String[][] mNFCTechLists;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        tvEmptyView = (TextView) findViewById(R.id.tv_empty_view);
        mVSContactList = (ViewSwitcher) findViewById(R.id.switcher);
        //NFC related
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
        } else {
            Log.d(TAG, "Android Beam is supported on this device");
        }
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter != null) {
            Log.i(TAG, "Can read an NFC tag");
        } else {
            Log.e(TAG, "This phone is not NFC enabled");
        }

        // create an intent with tag data and deliver to this activity
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        // set an intent filter for all MIME data
        IntentFilter ndefIntent = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndefIntent.addDataType("*/*");
            mIntentFilters = new IntentFilter[]{ndefIntent};
        } catch (Exception e) {
            Log.e("TagDispatch", e.toString());
        }

        mNFCTechLists = new String[][]{new String[]{NfcF.class.getName()}};

        // Debug
//        print2DStringArray(mNFCTechLists);

        mDb = new DBHelper(this);
        mContacts = mDb.readAllContacts();

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mContactNamesRecyclerViewAdapter = new ContactNamesRecyclerViewAdapter(this,
                mContacts,
                R.layout.contact_list_item,
                mDb);
        mRecyclerView = (RecyclerView) findViewById(R.id.activity_main_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mContactNamesRecyclerViewAdapter);

        mContactNamesRecyclerViewAdapter.setOnItemClickListener(new OnRecyclerViewItemClickListener<ContactDetails>() {
            @Override
            public void onItemClick(View view, ContactDetails contactDetails) {

                showContactDetails(contactDetails);
            }
        });


        mSwipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        handleContactsDownloaded();
                        mSwipeRefreshLayout.setRefreshing(false);

                    }
                }, 0);
            }
        });

        //If Recycler view is empty then display message.
        if (mContactNamesRecyclerViewAdapter.getItemCount() > 0 ) {
            if (R.id.activity_main_recyclerview == mVSContactList.getNextView().getId()) {
                mVSContactList.showNext();
            }
        } else {
            if (R.id.tv_empty_view== mVSContactList.getNextView().getId()) {
                mVSContactList.showNext();
            }
        }

        FAB = (ImageButton) findViewById(R.id.imageButton);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareContact();
            }
        });

        //Set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.contact_list_toolbar);
        setSupportActionBar(toolbar);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onNewIntent(Intent intent) {
        String action = intent.getAction();
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        Log.d(TAG, String.format("onNewIntent() action: %s, tag: %s", action, tag.toString()));
        if (ACTION_NDEF_DISCOVERED.equals(action)) {
            handleNFCData(intent);
        } else if (ACTION_REFRESH_CONTACTS.equals(action)) {
            handleContactsDownloaded();
        } else {
            super.onNewIntent(intent);
        }
    }

    private void handleContactsDownloaded() {
        Log.d(TAG, "Updated contact list.");
        mContactNamesRecyclerViewAdapter.setContacts(mDb.readAllContacts());
        //If Recycler view is empty then display message.
        if (mContactNamesRecyclerViewAdapter.getItemCount() > 0 ) {
            if (R.id.activity_main_recyclerview == mVSContactList.getNextView().getId()) {
                mVSContactList.showNext();
            }
        } else {
            if (R.id.tv_empty_view== mVSContactList.getNextView().getId()) {
                mVSContactList.showNext();
            }
        }
    }

    private void handleNFCData(Intent intent) {
        String receivedString = "";
        String s1 = "UTF-8";
        String s2 = "UTF-16";

        // parse through all NDEF messages and their records and pick text type only
        Parcelable[] data = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (data != null) {
            try {
                for (int i = 0; i < data.length; i++) {
                    NdefRecord[] recs = ((NdefMessage) data[i]).getRecords();
                    for (int j = 0; j < recs.length; j++) {
                        if (recs[j].getTnf() == NdefRecord.TNF_WELL_KNOWN &&
                                Arrays.equals(recs[j].getType(), NdefRecord.RTD_TEXT)) {
                            byte[] payload = recs[j].getPayload();
                            String textEncoding = ((payload[0] & 0200) == 0) ? s1 : s2;
                            int langCodeLen = payload[0] & 0077;
                            receivedString = new String(payload, langCodeLen + 1, payload.length - langCodeLen - 1,
                                    textEncoding);
                            Log.d(TAG, "receivedString: " + receivedString);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "exception in onNewIntent", e);
            }
        }
        Toast.makeText(this, "Received string: " + receivedString, Toast.LENGTH_LONG).show();
        Integer sharedContactId = Integer.parseInt(receivedString);
        new DownloadSharedContactTask().execute(sharedContactId);

    }

    @Override
    public void onResume() {
        super.onResume();

        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mIntentFilters, mNFCTechLists);
        } else {
            Log.d(TAG, "mNFCAdapter is null");
        }

        // In case other activity has updated the contact list.
        if (mContactNamesRecyclerViewAdapter != null) {
            //Refresh contact list.
            mContactNamesRecyclerViewAdapter.setContacts(mDb.readAllContacts());
            //If Recycler view is empty then display message.
            if (mContactNamesRecyclerViewAdapter.getItemCount() > 0 ) {
                if (R.id.activity_main_recyclerview == mVSContactList.getNextView().getId()) {
                    mVSContactList.showNext();
                }
            } else {
                if (R.id.tv_empty_view== mVSContactList.getNextView().getId()) {
                    mVSContactList.showNext();
                }
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }

    private void shareContact() {
        if (mNfcAdapter == null) {
            Toast.makeText(this,
                    "Contact sharing will not work because device is not a NFC device",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //Prompt user to bring the device closer to other device.
        new AlertDialog.Builder(this)
                .setTitle("Share contact")
                .setMessage("Now bring this device closer to other device")
                .setPositiveButton(android.R.string.ok, null)
                .show();

        ProfileDetails profile = mDb.fetchProfileDetails();
        Log.d(TAG, String.format("shareContact=> userId: %s", profile.getUserId()));

        NdefMessage ndefMessage = new NdefMessage(
                new NdefRecord[]{
                        createNewTextRecord(profile.getUserId().toString(),
                                Locale.ENGLISH,
                                true)
                }
        );

        mNfcAdapter.enableForegroundNdefPush(this, ndefMessage);

    }


    /**
     * This method creates the NdefRecord from the given string.
     *
     * @param text
     * @param locale
     * @param encodeInUtf8
     * @return
     */
    public static NdefRecord createNewTextRecord(String text, Locale locale, boolean encodeInUtf8) {
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));

        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
        byte[] textBytes = text.getBytes(utfEncoding);

        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        char status = (char) (utfBit + langBytes.length);

        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
    }

    /**
     * This method launches the Google contact list to import the contact.
     */
    private void importContact() {
        //Import contact using contact picker
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(intent, CONTACT_PICKER_RESULT);
    }

    private void importContactResult(Intent intent) {
        Uri uri = intent.getData();
        Log.d(TAG, "Contact URI : " + uri.toString());

        Cursor cursor = getContentResolver().query(uri,
                null,
                null,
                null,
                null);
        if (cursor != null) {

            int nameColIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            int phoneColIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
//            int emailIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
            cursor.moveToFirst();
            String name = cursor.getString(nameColIndex);
            String phoneNumber = cursor.getString(phoneColIndex);
            String email = null; //cursor.getString(emailIndex);

            //Some cleanup job
            cursor.close();

            if (name != null && !name.isEmpty()) {
                ContactDetails contactDetails = new ContactDetails(
                        null,
                        null,
                        name,
                        phoneNumber,
                        email,
                        "",             // TODO:  Company
                        "",             // TODO:  Linkedin url
                        null,           // TODO: Contact photo.
                        false);

                saveNewContact(contactDetails);
                Log.d(TAG, String.format("name: %s, email: %s", name, email));
            }
        }
    }

    /**
     * This method launches DisplayCardActivity.
     *
     * @param contactDetails
     */
    private void showContactDetails(ContactDetails contactDetails) {
        Log.d(TAG, String.format("showContactDetails() contactDetails: %s", contactDetails.toString()));
        Intent intent = new Intent(this, DisplayCardActivity.class);
        intent.putExtra(ApplicationConstants.EXTRA_CONTACT_LOCAL_ID,
                contactDetails.getId());
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestResult, int resultCode, Intent intent) {

        if (resultCode == Activity.RESULT_OK) {

            switch (requestResult) {
                case CONTACT_PICKER_RESULT:
                    importContactResult(intent);
                    break;


                default:
                    Log.w(TAG, "Different requestResult : " + requestResult);
            }
        } else {
            //User didn't select the contact correctly
            Log.w(TAG, "Contact picker activity result NOT OK");
        }
    }

    /**
     * This is a helper method to save the contact in contact list.
     */
    private void saveNewContact(ContactDetails contactDetails) {
        if (contactDetails.getOwnerId() == null) {
            ProfileDetails profile = mDb.fetchProfileDetails();
            contactDetails.setOwnerId(profile.getUserId());
        }

        if (mContactNamesRecyclerViewAdapter.add(contactDetails)) {
            //Notify user.
            Toast.makeText(this, "Contact added successfully", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Contact added successfully");
        } else {
            Toast.makeText(this, "Couldn't add contact", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Couldn't add contact");
        }

        //If Recycler view is empty then display message.
        if (mContactNamesRecyclerViewAdapter.getItemCount() > 0 ) {
            if (R.id.activity_main_recyclerview == mVSContactList.getNextView().getId()) {
                mVSContactList.showNext();
            }
        } else {
            if (R.id.tv_empty_view== mVSContactList.getNextView().getId()) {
                mVSContactList.showNext();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact_list, menu);

        // Associate searchable configuration with the SearchView
//        SearchManager searchManager =
//                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView =
//                (SearchView) menu.findItem(R.id.search).getActionView();
//        searchView.setSearchableInfo(
//                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {

            case R.id.action_invite:
                launchInviteActivity();
                return true;

            case R.id.action_share_contact:
                shareContact();
                return true;

            case R.id.action_import_contact:
                importContact();
                return true;

            case R.id.action_edit_profile:
                launchEditProfileActivity();
                return true;

            case R.id.action_change_password:
                launchChangePasswordActivity();
                return true;

            case R.id.action_delete_account:
                deleteAccount();
                return true;

            case R.id.action_settings:
                // TODO: Need to handle settings.
                return true;

            case R.id.action_logout:
                Intent intent = new Intent(this, SigninActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return true;

//            case R.id.action_test:
//                startActivity(new Intent(this, TestActivity.class));
//                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteAccount() {

        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_account)
                .setMessage("Do you really want to delete account?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new DeleteUserProfile().execute();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void launchEditProfileActivity() {
        startActivity(new Intent(this, EditProfileActivity.class));
    }

    private void launchChangePasswordActivity() {
        startActivity(new Intent(this, ChangePasswordActivity.class));
    }

    private void launchInviteActivity() {
        startActivity(new Intent(this, InviteActivity.class));
    }

    /**
     * Helper method for debugging to print 2d string array.
     *
     * @param values
     */
    private void print2DStringArray(String[][] values) {
        StringBuilder result = new StringBuilder();
        String separator = ",";

        for (int i = 0; i < values.length; ++i) {
            result.append('[');
            for (int j = 0; j < values[i].length; ++j) {
                if (j > 0)
                    result.append(values[i][j]);
                else
                    result.append(values[i][j]).append(separator);
            }
            result.append(']');
        }
        Log.d(TAG, String.format("2DString=> %s", result));
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ContactList Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.contactsharing.beamit/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ContactList Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.contactsharing.beamit/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    /**
     * Async task to download contacts
     */
    private class DownloadSharedContactTask extends AsyncTask<Integer, Integer, ContactDetails> {

        @Override
        protected ContactDetails doInBackground(Integer... integers) {
            Integer subjectId = integers[0];
            BeamItService service = BeamItServiceTransport.getService();
            DBHelper db = new DBHelper(getApplicationContext());
            ProfileDetails profileDetails = db.fetchProfileDetails();

            ShareContactRequest shareContactRequest = new ShareContactRequest(profileDetails.getUserId(), subjectId);
            Call<Contact> contactCall = service.shareContact(shareContactRequest);
            Response<Contact> contactResponse;
            try {
                contactResponse = contactCall.execute();
            } catch (IOException e) {
                Log.e(TAG, "Error while fetching contact", e);
                return null;
            }
            if (contactResponse.code() != HttpURLConnection.HTTP_CREATED) {
                Log.i(TAG, String.format("Couldn't get contact details => code: %d, response: %s",
                        contactResponse.code(),
                        contactResponse.body()));
                return null;
            }
            ContactDetails contactDetails = ContactDetails.fromContact(contactResponse.body());
            Log.d(TAG, String.format("Contact id: %d", contactDetails.getContactId()));

            //Download contact photo
            Call<ResponseBody> responseCall = service.downloadContactPhoto(contactDetails.getContactId());
            Response<ResponseBody> response = null;
            try {
                response = responseCall.execute();
            } catch (IOException e) {
                Log.e(TAG, "Coudln't get contact photo ", e);
            }
            if (response == null || response.code() != HttpURLConnection.HTTP_OK) {
                Log.i(TAG, String.format("Coudln't get contact photo => code: %d", response.code()));
            } else {

                try {
                    Bitmap bitmap = BitmapUtility.getBytesToBitmap(response.body().bytes());
                    if (bitmap != null) {
                        Log.d(TAG, "bitmap is not null");
                    } else {
                        Log.e(TAG, "bitmap is null");
                    }
                    String photoFileName = UtilityMethods.photoFileNameFormatter(ApplicationConstants.CONTACT_PHOTO_FILE_PREFIX,
                            ApplicationConstants.PHOTO_FILE_EXTENSION,
                            contactDetails.getContactId());

                    BitmapUtility.storeImageToInternalStorage(getApplicationContext(),
                            bitmap,
                            ApplicationConstants.CONTACT_PHOTO_DIRECTORY,
                            photoFileName);

                    contactDetails.setPhotoUri(UtilityMethods.photoFilePath(
                            ApplicationConstants.CONTACT_PHOTO_DIRECTORY,
                            photoFileName));
                } catch (IOException e) {
                    Log.e(TAG, "couldn't fetch image", e);
                }
            }
            return contactDetails;
        }

        @Override
        protected void onPostExecute(ContactDetails contactDetails) {
            if (contactDetails != null) {
                saveNewContact(contactDetails);
            }
        }
    }

    private class DeleteUserProfile extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            DBHelper db = new DBHelper(getApplicationContext());
            ProfileDetails profile = db.fetchProfileDetails();

            if (profile != null) {
                BeamItService service = BeamItServiceTransport.getService();
                Call<ResponseBody> call = service.deleteUserProfile(profile.getUserId());
                Response<ResponseBody> response;
                try {
                    response = call.execute();
                } catch (IOException e) {
                    Log.e(TAG, "Failed to delete user profile", e);
                    return false;
                }

                return response.code() == HttpURLConnection.HTTP_NO_CONTENT;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(getApplicationContext(), "Successfully deleted account", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), SigninActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "Failed to delete account", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

