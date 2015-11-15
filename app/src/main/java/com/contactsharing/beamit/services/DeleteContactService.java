package com.contactsharing.beamit.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.contactsharing.beamit.db.DBHelper;
import com.contactsharing.beamit.model.ContactDetails;
import com.contactsharing.beamit.transport.BeamItService;
import com.contactsharing.beamit.transport.BeamItServiceTransport;

import java.io.IOException;
import java.net.HttpURLConnection;

import retrofit.Call;
import retrofit.Response;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class DeleteContactService extends IntentService {
    private static final String EXTRA_LOCAL_CONTACT_ID = "DeleteContactService.EXTRA_LOCAL_CONTACT_ID";
    private static final String TAG = DeleteContactService.class.getSimpleName();

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void deleteContact(Context context, Integer localContactId) {
        Intent intent = new Intent(context, DeleteContactService.class);
        intent.putExtra(EXTRA_LOCAL_CONTACT_ID, localContactId);
        context.startService(intent);
    }

    public DeleteContactService() {
        super("DeleteContactService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Integer localContactId = intent.getIntExtra(EXTRA_LOCAL_CONTACT_ID, -1);
            if (localContactId != -1) {
                DBHelper db = new DBHelper(this);
                ContactDetails contactDetails = db.getContact(localContactId);
                deleteContactFromServer(contactDetails);
                deleteContactFromStore(db, contactDetails);
                db.close();
            } else {
                Log.e(TAG, "Couldn't get right local contact id");
            }
        }
    }

    private void deleteContactFromServer(ContactDetails contactDetails){
        if (contactDetails.getContactId() != null && contactDetails.getContactId() > -1) {
            BeamItService service = BeamItServiceTransport.getService();
            Call<Void> call = service.deleteContact(contactDetails.getContactId());
            Response<Void> deleteResponse;
            try {
                deleteResponse = call.execute();
            } catch (IOException ex){
                Log.e(TAG, String.format("Couldn't delete contact (%s) from server.", contactDetails), ex);
                return;
            }

            if (deleteResponse.code() == HttpURLConnection.HTTP_NO_CONTENT){
                Log.i(TAG, String.format("Successfully deleted contact (%s) from server",contactDetails));
            } else {
                Log.e(TAG, String.format("Couldn't delete contact (%s) from server.", contactDetails));
            }
        }
    }

    private void deleteContactFromStore(DBHelper db, ContactDetails contactDetails){
        if (1 == db.deleteContactById(contactDetails.getId())) {
            Log.i(TAG, String.format("Successfully deleted contact (%s) from store", contactDetails));
        } else {
            Log.d(TAG, String.format("Couldn't delete contact details: %s", contactDetails));
        }
    }
}
