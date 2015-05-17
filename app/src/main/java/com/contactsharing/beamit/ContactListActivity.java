package com.contactsharing.beamit;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ContactListActivity extends ActionBarActivity {
    private static final String TAG = ContactListActivity.class.getSimpleName();
    private static final int CONTACT_PICKER_RESULT = 1503;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private ContactNamesRecyclerViewAdapter mCatNamesRecyclerViewAdapter;
    ImageButton FAB;
    private List<String> mContactNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.activity_main_recyclerview);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mContactNames = getContactNamesResource();
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
            cursor.moveToFirst();
            String name = cursor.getString(nameColIndex);
            //Some cleanup job
            cursor.close();

            if (name != null && !name.isEmpty()) {
                mContactNames.add(name);
                Log.d(TAG, "name : " + name);
                setupAdapter();
            }
        }
    }


    private List<String> getContactNamesResource() {
        return  new LinkedList(Arrays.asList(this.getResources().getStringArray(R.array.contact_names)));
    }

    private void setupAdapter() {
        mCatNamesRecyclerViewAdapter = new ContactNamesRecyclerViewAdapter(this, mContactNames);
        mRecyclerView.setAdapter(mCatNamesRecyclerViewAdapter);
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
}
