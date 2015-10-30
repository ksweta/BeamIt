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
 */
public class SendActivity extends Activity {
    private static final String TAG = SendActivity.class.getSimpleName();


    private NfcAdapter mNfcAdapter;
    private TextView mTextView;
    private NdefMessage mNdefMessage;

    @Override
    public void onCreate(Bundle savedState) {
        TextView tv_name;
        TextView tv_phone;
        TextView tv_email;

        Gson gson = new Gson();

        Intent intent = getIntent();
        String jsonContact = intent.getStringExtra(ContactNamesRecyclerViewAdapter.EXTRA_MESSAGE);
        ContactDetails cd =  gson.fromJson(jsonContact, ContactDetails.class);
        String s = cd.toString();
        Log.d("displaycard test: ", s);



        //convert java object to JSON format
        String json = gson.toJson(cd);

        Log.d(TAG, "Json string: " + json.toString());
        super.onCreate(savedState);

        setContentView(R.layout.activity_send);

        tv_name =  (TextView) findViewById(R.id.send_tv_name_value);
        tv_phone =  (TextView) findViewById(R.id.send_tv_phone_value);
        tv_email =  (TextView) findViewById(R.id.send_tv_email_value);

        tv_name.setText(cd.getName());
        tv_phone.setText(cd.getPhone());
        tv_email.setText(cd.getEmail());


        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter != null) {
            Log.e(TAG, "Tap to beam to another NFC device");
        } else {
            Log.e(TAG, "This phone is not NFC enabled.");
        }

        // create an NDEF message with two records of plain text type
        mNdefMessage = new NdefMessage(
                new NdefRecord[] {
                        createNewTextRecord(json.toString(), Locale.ENGLISH, true) } );
                        //createNewTextRecord("This is my email id: ksweta@", Locale.ENGLISH, true) });
    }

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


    public void onClick(View view) {
        if (mNfcAdapter != null)
            mNfcAdapter.enableForegroundNdefPush(this, mNdefMessage);

    }

    @Override
    public void onPause() {
        super.onPause();

        if (mNfcAdapter != null)
            mNfcAdapter.disableForegroundNdefPush(this);
    }

}
