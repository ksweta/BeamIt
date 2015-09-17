package com.contactsharing.beamit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;

import com.contactsharing.beamit.db.DBHelper;
import com.contactsharing.beamit.model.ContactDetails;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ContactListActivity extends ActionBarActivity
        implements DataSetChange{
    private static final String TAG = ContactListActivity.class.getSimpleName();
    private static final int CONTACT_PICKER_RESULT = 1503;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private ContactNamesRecyclerViewAdapter mContactNamesRecyclerViewAdapter;
    ImageButton FAB;
        private List<ContactDetails> mContacts;
    private DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        db = new DBHelper(this);
        mContacts = db.readAllContacts();

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.activity_main_recyclerview);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
       // mContacts = getContactNamesResource();
        setupAdapter();


        mSwipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setupAdapter();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 2500);
            }
        });

        FAB = (ImageButton) findViewById(R.id.imageButton);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Import Contact
                importContact();
            }
        });
    }

    private void importContact(){
        //Import contact using contact picker
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(intent, CONTACT_PICKER_RESULT);
    }

    private void importContactResult(Intent intent){
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

            cursor.moveToFirst();
            String name = cursor.getString(nameColIndex);
            String phoneNumber = cursor.getString(phoneColIndex);
            //Some cleanup job
            cursor.close();

            if (name != null && !name.isEmpty()) {
                saveNewContact(name, phoneNumber, ""); //TODO  for email
                Log.d(TAG, "name : " + name);

            }
        }
    }


    private List<String> getContactNamesResource() {
        return  new LinkedList(Arrays.asList(this.getResources().getStringArray(R.array.contact_names)));
    }

    private void setupAdapter() {
        mContacts = db.readAllContacts();
        mContactNamesRecyclerViewAdapter = new ContactNamesRecyclerViewAdapter(this, mContacts, this, db);
        mRecyclerView.setAdapter(mContactNamesRecyclerViewAdapter);
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
     * @param name  Contact name.

     */
    private void saveNewContact(String name, String phoneNumber, String email) {
        ContactDetails contact = new ContactDetails(name,
                phoneNumber,
                email,  //TODO  for email
                false,
                new Date());

        long conId = db.insertContact(contact);
        if (conId > 0) {
            //If contact is added successfully then add it in the
            //contactList and notify the adapter.
            contact.setId(conId);
            mContacts.add(contact);
            setupAdapter();

            //Notify user.
            Toast.makeText(this, "Contact added successfully", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Contact added successfully");
            //Time to sync contact with ETA-Server.
            //syncContact(contact);
        } else {
            Toast.makeText(this, "Couldn't add contact", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Couldn't add contact");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


   @Override
    public void onDataSetChange(){
      setupAdapter();
   }
}

