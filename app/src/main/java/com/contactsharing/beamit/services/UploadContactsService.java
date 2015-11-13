package com.contactsharing.beamit.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.contactsharing.beamit.db.DBHelper;
import com.contactsharing.beamit.model.ContactDetails;
import com.contactsharing.beamit.resources.contact.Contact;
import com.contactsharing.beamit.transport.BeamItService;
import com.contactsharing.beamit.transport.BeamItServiceTransport;
import com.contactsharing.beamit.utility.UtilityMethods;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

import retrofit.Call;
import retrofit.Response;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class UploadContactsService extends IntentService {
    private static final String TAG = UploadContactsService.class.getSimpleName();

    /**
     * Starts this service to sync the contact details.
     *
     * @see IntentService
     */
    public static void uploadContacts(Context context) {
        context.startService(new Intent(context, UploadContactsService.class));
    }


    public UploadContactsService() {
        super("UploadContactsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            DBHelper db = new DBHelper(getApplicationContext());
            List<ContactDetails> contactDetailsList = db.getCotnactsTobeSynced(false);
            for(ContactDetails contactDetails: contactDetailsList) {
                if(handleContactSync(contactDetails) && handlePhotoUpload(contactDetails)){
                    contactDetails.setSynced(true);
                    db.updateContactById(contactDetails);
                }
            }
            db.close();
        }
    }

    /**
     * This method will sync contact details with server. Once contacts details are successfully
     * synced, it updates the contactId of the current object based on the return contactId of
     * the RESTful api.
     *
     * @param contactDetails
     * @return return true if synced successfully otherwise false.
     */
    private boolean handleContactSync(ContactDetails contactDetails){
        BeamItService service = BeamItServiceTransport.getService();

        // Upload contact
        Contact contact = contactDetails.toContact();
        Call<Contact> call = service.createContact(contact);

        Response<Contact> contactResponse;
        try {
            contactResponse = call.execute();
        } catch (IOException e) {
            Log.e(TAG, "Error while fetching contact", e);
            return false;
        }
        if (contactResponse.code() != HttpURLConnection.HTTP_CREATED) {
            Log.i(TAG, String.format("Couldn't upload contact details, code: %d, body: %s",
                    contactResponse.code(),
                    contactResponse.body()));
            return false;
        }
        // update contact_id
        contactDetails.setContactId(contact.getId());
        return true;
    }

    /**
     * This method will sync contact photo with server. Once photo is uploaded successfully,
     * it updates the contactId of the current object based on the return contactId of
     * the RESTful api.
     *
     * @param contactDetails
     * @return retrun true if uploaded otherwise false.
     */
    private boolean handlePhotoUpload(ContactDetails contactDetails){
        if(contactDetails.getPhotoUri() == null) {
            Log.e(TAG, String.format("Didn't find any photo for local contact id: %d, contact id: %d, owner:%d  ",
                    contactDetails.getId(),
                    contactDetails.getContactId(),
                    contactDetails.getOwnerId()));
            return true;
        }
        // Upload photo
        BeamItService service = BeamItServiceTransport.getService();
        // TODO: Fetch the bitmap
        Bitmap bitmap = null;
        RequestBody requestBody = UtilityMethods.getPhotoRequestBody(bitmap,
                contactDetails.getContactId(),
                "contact");
        Call<Void> call = service.uploadContactPhoto(contactDetails.getContactId(), requestBody);
        Response<Void> response;
        try {
            response = call.execute();
        } catch(IOException e){
            Log.e(TAG, "Couldn't upload contact phoot", e);
            return false;
        }
        if (response.code() != HttpURLConnection.HTTP_OK) {
            Log.e(TAG, String.format("Upload contact (%d) was not successful from server code: %d, body: %s",
                    contactDetails.getContactId(),
                    response.code(),
                    response.body()));
            return false;
        }
        return true;
    }
}
