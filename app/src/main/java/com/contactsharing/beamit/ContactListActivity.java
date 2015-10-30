package com.contactsharing.beamit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
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
import com.contactsharing.beamit.utility.ApplicationConstants;
import com.contactsharing.beamit.utility.JsonConverter;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ContactListActivity extends ActionBarActivity {
    private static final String TAG = ContactListActivity.class.getSimpleName();
    private static final int CONTACT_PICKER_RESULT = 1503;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    //Recylcer view related variables
    private RecyclerView mRecyclerView;
    private ContactNamesRecyclerViewAdapter mContactNamesRecyclerViewAdapter;

    ImageButton FAB;
    private List<ContactDetails> mContacts;
    private DBHelper mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

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
                importContact();
            }
        });
    }

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
                saveNewContact(name,
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
    private void saveNewContact(String name,
                                String phoneNumber,
                                String email,
                                String company,
                                String linkedinUrl,
                                Bitmap photo) {
        ContactDetails contact = new ContactDetails(name,
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
            case R.id.action_settings:
                // TODO: Need to handle settings.
                return true;

            case R.id.action_logout:
                Intent intent = new Intent(this, SigninActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_edit_profile:
                launchEditProfileActivity();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void launchEditProfileActivity(){
        Intent intent = new Intent(this, EditProfileActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
    }
}

