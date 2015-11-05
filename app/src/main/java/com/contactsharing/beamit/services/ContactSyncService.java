package com.contactsharing.beamit.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.contactsharing.beamit.model.ContactDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class ContactSyncService extends IntentService {
    /**
     * Starts this service to sync the contact details.
     *
     * @see IntentService
     */
    public static void startActionFoo(Context context) {
        context.startService(new Intent(context, ContactSyncService.class));
    }


    public ContactSyncService() {
        super("ContactSyncService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            List<ContactDetails> contactDetailsList = getCotnactDetailsToSync();
            for(ContactDetails contactDetails: contactDetailsList) {
                if(handleContactSync(contactDetails) && handlePhotoUpload(contactDetails)){
                }
            }
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

        return false;
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

        return false;
    }

    private List<ContactDetails> getCotnactDetailsToSync(){

        return new ArrayList<ContactDetails>();
    }
}
