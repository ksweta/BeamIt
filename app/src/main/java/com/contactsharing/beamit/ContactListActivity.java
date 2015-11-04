package com.contactsharing.beamit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
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
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcF;
import android.os.Build;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;

import com.contactsharing.beamit.db.DBHelper;
import com.contactsharing.beamit.model.ContactDetails;
import com.contactsharing.beamit.model.ProfileDetails;
import com.contactsharing.beamit.utility.ApplicationConstants;
import com.contactsharing.beamit.utility.JsonConverter;
import com.google.gson.Gson;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class ContactListActivity extends ActionBarActivity {
    private static final String TAG = ContactListActivity.class.getSimpleName();
    private static final int CONTACT_PICKER_RESULT = 1503;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ImageButton FAB;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        //NFC releated
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
//            Toast.makeText(this, "Android Beam is supported on your device.",
//                    Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Android Beam is supported on this device");
        }
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if(mNfcAdapter != null) {
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
            mIntentFilters = new IntentFilter[] { ndefIntent };
        } catch (Exception e) {
            Log.e("TagDispatch", e.toString());
        }

        mNFCTechLists = new String[][] { new String[] { NfcF.class.getName() } };

        // Debug
        print2DStringArray(mNFCTechLists);

        mDb = new DBHelper(this);
        mContacts = mDb.readAllContacts();

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mContactNamesRecyclerViewAdapter = new ContactNamesRecyclerViewAdapter(mContacts,
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
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 2500);
            }
        });

        FAB = (ImageButton) findViewById(R.id.imageButton);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareContact();
            }
        });
    }

    @Override
    public void onNewIntent(Intent intent) {
        String action = intent.getAction();
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String receivedString = "";
        String s = action + "\n\n" + tag.toString();
        String s1 = "UTF-8";
        String s2 = "UTF-16";

        // parse through all NDEF messages and their records and pick text type only
        Parcelable[] data = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (data != null) {
            try {
                for (int i = 0; i < data.length; i++) {
                    NdefRecord[] recs = ((NdefMessage)data[i]).getRecords();
                    for (int j = 0; j < recs.length; j++) {
                        if (recs[j].getTnf() == NdefRecord.TNF_WELL_KNOWN &&
                                Arrays.equals(recs[j].getType(), NdefRecord.RTD_TEXT)) {
                            byte[] payload = recs[j].getPayload();
                            String textEncoding = ((payload[0] & 0200) == 0) ?  s1 : s2;
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
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mIntentFilters, mNFCTechLists);
        } else {
            Log.d(TAG, "mNFCAdapter is null");
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }

    private void shareContact(){
        if (mNfcAdapter == null) {
            Toast.makeText(this,
                    "Contact sharing will not work because device is not a NFC device",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        ProfileDetails profile = mDb.fetchProfileDetails();
        Log.d(TAG, String.format("shareContact=> userId: %s", profile.getUserId()));

        NdefMessage ndefMessage = new NdefMessage(
                new NdefRecord[] {
                        createNewTextRecord(profile.getUserId().toString(),
                                Locale.ENGLISH,
                                true)
                }
        );

        mNfcAdapter.enableForegroundNdefPush(this, ndefMessage);

    }

    /**
     * This method creates the NdefRecord from the given string.
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
        char status = (char)(utfBit + langBytes.length);

        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte)status;
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
                saveNewContact(0L,
                        name,
                        phoneNumber,
                        email,
                        "", //company name,
                        "", //linkedin url,
                        null);
                Log.d(TAG, String.format("name: %s, email: %s", name, email));
            }
        }
    }

    /**
     * This method launches DisplayCardActivity.
     * @param contactDetails
     */
    private void showContactDetails(ContactDetails contactDetails){
        Log.d(TAG, String.format("showContactDetails() contactDetails: %s", contactDetails.toString()));
        Intent intent = new Intent(this, DisplayCardActivity.class);
        intent.putExtra(ApplicationConstants.EXTRA_CONTACT_DETAILS,
                JsonConverter.toJson(contactDetails));
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
     *
     * @param name Contact name.
     */
    private void saveNewContact(Long contactId,
                                String name,
                                String phoneNumber,
                                String email,
                                String company,
                                String linkedinUrl,
                                Bitmap photo) {
        ContactDetails contact = new ContactDetails(
                contactId,
                name,
                phoneNumber,
                email,
                company,        // TODO:  Company
                linkedinUrl,    // TODO:  Linkedin url
                photo,          // TODO: Contact photo.
                new Date());

        if ( mContactNamesRecyclerViewAdapter.add(contact)) {
            //Notify user.
            Toast.makeText(this, "Contact added successfully", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Contact added successfully");
        } else {
            Toast.makeText(this, "Couldn't add contact", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Couldn't add contact");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact_list, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id){

            case R.id.action_invite:
                // TODO: Need to handle invite flow
                launchInviteActivity();
                return true;
            case R.id.action_import_contact:
                importContact();
                return true;

            case R.id.action_edit_profile:
                launchEditProfileActivity();
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

            case R.id.action_test:
                startActivity(new Intent(this, TestActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void launchEditProfileActivity(){
        startActivity(new Intent(this, EditProfileActivity.class));
    }

    private void launchInviteActivity(){

    }

    /**
     * Helper method for debugging to print 2d string array.
     * @param values
     */
    private void print2DStringArray(String[][] values){
        StringBuilder result = new StringBuilder();
        String separator = ",";

        for (int i = 0; i < values.length; ++i)
        {
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
}

