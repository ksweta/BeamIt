package com.contactsharing.beamit;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.contactsharing.beamit.model.ContactDetails;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kumari on 9/4/15.
 * The functionality of sharing contact using NFC has been moved to ContactListActivity.
 */
@Deprecated
public class SendActivity extends Activity {
    private static final String TAG = SendActivity.class.getSimpleName();
    private TextView tv_name;
    private TextView tv_phone;
    private TextView tv_email;

    //NFC related
    private NfcAdapter mNfcAdapter;
    private NdefMessage mNdefMessage;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.activity_send);
        tv_name =  (TextView) findViewById(R.id.send_tv_name_value);
        tv_phone =  (TextView) findViewById(R.id.send_tv_phone_value);
        tv_email =  (TextView) findViewById(R.id.send_tv_email_value);

        Gson gson = new Gson();

        Intent intent = getIntent();
        String jsonContact = intent.getStringExtra(ContactNamesRecyclerViewAdapter.EXTRA_MESSAGE);
        ContactDetails cd =  gson.fromJson(jsonContact, ContactDetails.class);
        String s = cd.toString();
        Log.d("displaycard test: ", s);



        //convert java object to JSON format
        String json = gson.toJson(cd);

        Log.d(TAG, "Json string: " + json.toString());


        tv_name.setText(cd.getName());
        tv_phone.setText(cd.getPhone());
        tv_email.setText(cd.getEmail());

    }




    public void onClick(View view) {

    }

    @Override
    public void onPause() {
        super.onPause();
    }

}
