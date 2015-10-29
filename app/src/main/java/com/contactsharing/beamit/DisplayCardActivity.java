package com.contactsharing.beamit;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.contactsharing.beamit.model.ContactDetails;
import com.google.gson.Gson;

/**
 * Created by kumari on 9/15/15.
 */
public class DisplayCardActivity extends Activity {
    public final static String TAG = DisplayCardActivity.class.getSimpleName();
    TextView tv_name;
    TextView tv_phone;
    TextView tv_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_display_card);
        tv_name =  (TextView) findViewById(R.id.tv_name);
        tv_phone =  (TextView) findViewById(R.id.tv_phone);
        tv_email =  (TextView) findViewById(R.id.tv_email);

//        Gson gson=new Gson();
//        Intent intent = getIntent();
//        String receivedString = intent.getStringExtra(SigninActivity.EXTRA_MESSAGE);
//        ContactDetails cd =  gson.fromJson(receivedString, ContactDetails.class);
//        String s = cd.toString();
//        Log.d(TAG, String.format("Displaycard test: %s", s));
//        tv_name.setText(cd.getName());
//        tv_phone.setText(cd.getPhone());
//        tv_email.setText(cd.getEmail());

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
        Log.d(TAG, String.format("Email: %s", tv_email.getText().toString()));
        intent.putExtra(Intent.EXTRA_EMAIL, tv_email.getText().toString());
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
        Log.d(TAG, String.format("Phone: %s", tv_phone.getText().toString()));
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.fromParts("tel", tv_phone.getText().toString(), null));
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
        Log.d(TAG, String.format("Phone: %s", tv_phone.getText().toString()));
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms",
                tv_phone.getText().toString(),
                null));
        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this,
                    "Sorry couldn't send sms because there is no sms app installed",
                    Toast.LENGTH_SHORT).show();

        }
    }
//    @Override
//    public void onNewIntent(Intent intent) {
//        Gson gson=new Gson();
//        String receivedString = intent.getStringExtra(SigninActivity.EXTRA_MESSAGE);
//        ContactDetails cd =  gson.fromJson(receivedString, ContactDetails.class);
//        String s = cd.toString();
//        Log.d("displaycard test: ", s);
//        tv_name.setText(cd.getName());
//        tv_phone.setText(cd.getPhone());
//        tv_email.setText(cd.getEmail());
//
//    }

}
