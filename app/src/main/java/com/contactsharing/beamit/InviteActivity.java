package com.contactsharing.beamit;

import android.app.Activity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Kumari on 11/1/15.
 */
public class InviteActivity extends Activity {
    private static final String TAG = InviteActivity.class.getSimpleName();
    private EditText etEmailOrPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        etEmailOrPhone = (EditText)findViewById(R.id.et_email_or_phone);
    }

    public void onClick(View view){
        switch(view.getId()){
            case R.id.bt_invite:
                String emailOrPhone = etEmailOrPhone.getText().toString();
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(emailOrPhone).matches()) {
                    sendEmailInvite(emailOrPhone);
                }
                else if(PhoneNumberUtils.isWellFormedSmsAddress(emailOrPhone)){
                    sendSmsInvite(emailOrPhone);
                } else {
                    Toast.makeText(this, "Please provide valid email or phone", Toast.LENGTH_SHORT).show();
                }
            default:
                Log.i(TAG, String.format("Wrong view (%d)", view.getId()));
        }
    }

    private void sendSmsInvite(String phone) {
        //TODO: Implement sms invite.
        Toast.makeText(this, String.format("Invite SMS sent to %s", phone), Toast.LENGTH_SHORT).show();
    }

    private void sendEmailInvite(String email) {
        //TODO: Implement email invite
        Toast.makeText(this, String.format("Invite email sent to %s", email), Toast.LENGTH_SHORT).show();
    }
}
