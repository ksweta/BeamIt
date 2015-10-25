package com.contactsharing.beamit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.contactsharing.beamit.model.ContactDetails;
import com.google.gson.Gson;

/**
 * Created by kumari on 9/15/15.
 */
public class DisplayCardActivity extends Activity {
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

        Gson gson=new Gson();
        Intent intent = getIntent();
        String receivedString = intent.getStringExtra(SigninActivity.EXTRA_MESSAGE);
        ContactDetails cd =  gson.fromJson(receivedString, ContactDetails.class);
        String s = cd.toString();
        Log.d("displaycard test: ", s);
        tv_name.setText(cd.getName());
        tv_phone.setText(cd.getPhone());
        tv_email.setText(cd.getEmail());

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
