package com.contactsharing.beamit;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcF;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.contactsharing.beamit.model.ContactDetails;
import com.google.gson.Gson;

import java.io.File;
import java.util.Arrays;

public class SigninActivity extends Activity {
    private static final String TAG = SigninActivity.class.getSimpleName();
    NfcAdapter nfcAdapter;
    private EditText etEmail;
    private EditText etPassword;
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mIntentFilters;
    private String[][] mNFCTechLists;

    public final static String EXTRA_MESSAGE = "com.contactsharing.beamit.SigninActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        etEmail = (EditText) findViewById(R.id.et_email);
        etPassword = (EditText) findViewById(R.id.et_password);

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
            Toast.makeText(this, "Android Beam is supported on your device.",
                    Toast.LENGTH_SHORT).show();
        }

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter != null) {
            Log.i(TAG, "Read an NFC tag");
        } else {
            Log.e(TAG,"This phone is not NFC enabled.");
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

}
    @Override
    public void onNewIntent(Intent intent) {
        String action = intent.getAction();
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Gson gson=new Gson();
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
                            //received NDEF record is in text form. So converting it into JSON object
                            //JSONObject jsonObj = new JSONObject(receivedString);
                            ContactDetails cd =  gson.fromJson(receivedString, ContactDetails.class);
                            s += "Name: " + cd.getName() + ", Phone: "+ cd.getPhone() + ", email: " +
                                    cd.getEmail();
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("TagDispatch", e.toString());
            }
        }
        Intent displayIntent = new Intent(this, DisplayCardActivity.class);
        displayIntent.putExtra(EXTRA_MESSAGE, receivedString );
        startActivity(displayIntent);
        Log.d(TAG, s);
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }
    public void sendFile(View view) {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        // Check whether NFC is enabled on device
        if(!nfcAdapter.isEnabled()){
            // NFC is disabled, show the settings UI
            // to enable NFC
            Toast.makeText(this, "Please enable NFC.",
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
        }
        // Check whether Android Beam feature is enabled on device
        else if(!nfcAdapter.isNdefPushEnabled()) {
            // Android Beam is disabled, show the settings UI
            // to enable Android Beam
            Toast.makeText(this, "Please enable Android Beam.",
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_NFCSHARING_SETTINGS));
        }
        else {
            // NFC and Android Beam both are enabled

            // File to be transferred
            // For the sake of this tutorial I've placed an image
            // named 'wallpaper.png' in the 'Pictures' directory
            String fileName = "wallpaper.png";

            // Retrieve the path to the user's public pictures directory
            File fileDirectory = Environment
                    .getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES);

            // Create a new file using the specified directory and name
            File fileToTransfer = new File(fileDirectory, fileName);
            fileToTransfer.setReadable(true, false);

            nfcAdapter.setBeamPushUris(
                    new Uri[]{Uri.fromFile(fileToTransfer)}, this);
        }
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

        if (mNfcAdapter != null)
            mNfcAdapter.disableForegroundDispatch(this);
    }

    public void onClick(View view){
        Intent intent = null;
        switch(view.getId()){
            case R.id.bt_sign_in:
                if (authenticateSignIn()) {
                    startActivity(new Intent(this, ContactListActivity.class));
                } else {
                    Toast.makeText(this, "Please check your email or password", Toast.LENGTH_SHORT).show();
                }
                return;
            case R.id.tv_sign_up:
                startActivity(new Intent(this, SignUpActivity.class));
                return;
            default:
                return;
        }
    }
    private boolean authenticateSignIn(){
        String pwd = "madam0";
        String username = "ksweta1007@gmail.com";
        return pwd.equals(etPassword.getText().toString()) && username.equals(etEmail.getText().toString());
    }
}
