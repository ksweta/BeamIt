package com.contactsharing.beamit.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.contactsharing.beamit.db.DBHelper;
import com.contactsharing.beamit.model.ContactDetails;
import com.contactsharing.beamit.resources.contact.Contact;
import com.contactsharing.beamit.resources.contact.ContactList;
import com.contactsharing.beamit.transport.BeamItService;
import com.contactsharing.beamit.transport.BeamItServiceTransport;
import com.contactsharing.beamit.utility.ApplicationConstants;
import com.contactsharing.beamit.utility.BitmapUtility;
import com.contactsharing.beamit.utility.UtilityMethods;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Response;

/**
 * DownloadContactsService to download contacts details
 */
public class DownloadContactsService extends IntentService {
    private static final String EXTRA_USER_ID = "DownloadContactsService.EXTRA_USER_ID";
    private static final String TAG = DownloadContactsService.class.getSimpleName();

    /**
     * Starts this service to download the contacts of given user.
     *
     * @see IntentService
     */
    public static void startDownloadContacts(Context context, Integer userId) {
        Intent intent = new Intent(context, DownloadContactsService.class);
        intent.putExtra(EXTRA_USER_ID, userId);
        context.startService(intent);
    }

    public DownloadContactsService() {
        super("DownloadContactsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final Integer userId = intent.getIntExtra(EXTRA_USER_ID, -1);
            if(userId == -1){
                Log.e(TAG, "Couldn't get the userId");
                return;
            }
            List<ContactDetails> contactDetailsList = downloadContacts(userId);

            if (contactDetailsList == null || contactDetailsList.isEmpty()){
                Log.w(TAG, String.format("Couldn't find any contact associated with user: %d", userId));
                return;
            }

            downloadContactsPhoto(contactDetailsList);
            //Persist the contact details.
            saveContactDetailsList(contactDetailsList);
        }
    }

    private List<ContactDetails> downloadContacts(Integer userId){
        List<ContactDetails> contactDetailsList = null;
        BeamItService service = BeamItServiceTransport.getService();
        Call<ContactList> call = service.getContactList(userId);
        Response<ContactList> contactListResponse = null;

        try {
            contactListResponse = call.execute();   //synchronous call of retrofit
        } catch (IOException ex){
            Log.e(TAG, String.format("Couldn't fetch user's(%d) contact list", userId), ex);
            return null;
        }
        if (contactListResponse.code() != HttpURLConnection.HTTP_OK) {
            Log.e(TAG, String.format("Couldn't fetch user's(%d) contact list => code: %d, body: %s",
                    userId,
                    contactListResponse.code(),
                    contactListResponse.body()));
            return null;
        }

        ContactList contactList = contactListResponse.body();

        if(contactList.getContacts() == null || contactList.getContacts().isEmpty()) {
            Log.i(TAG, String.format("Conatct list is empty or null for user(%d)", userId));
            return null;
        }
        contactDetailsList = new ArrayList<>();
        for(Contact contact: contactList.getContacts()){
            ContactDetails contactDetails = ContactDetails.fromContact(contact);
            contactDetailsList.add(contactDetails);
        }
        return contactDetailsList;
    }

    private void downloadContactsPhoto(List<ContactDetails> contactDetailsList){

        for(ContactDetails contactDetails: contactDetailsList){
            Bitmap bitmap = downloadPhoto(contactDetails.getContactId());
            if(bitmap != null) {
                String photoFileName = UtilityMethods.photoFileNameFormatter(ApplicationConstants.CONTACT_PHOTO_FILE_PREFIX,
                        ApplicationConstants.PHOTO_FILE_EXTENSION,
                        contactDetails.getContactId());

                if(BitmapUtility.storeImageToInternalStorage(getApplicationContext(),
                        bitmap,
                        ApplicationConstants.CONTACT_PHOTO_DIRECTORY,
                        photoFileName)){
                    contactDetails.setPhotoUri(UtilityMethods.photoFilePath(ApplicationConstants.CONTACT_PHOTO_DIRECTORY,
                            photoFileName));
                 } else {
                    Log.w(TAG, String.format("Couldn't save photo for contact(%d)",
                            contactDetails.getContactId()));
                }
            }
        }
    }

    private Bitmap downloadPhoto(Integer contactId){
        BeamItService service = BeamItServiceTransport.getService();
        Call<ResponseBody> call = service.downloadContactPhoto(contactId);
        Response<ResponseBody> response = null;
        try{
            response = call.execute();
        } catch(IOException ex){
            Log.e(TAG, String.format("Couldn't download photo for contact(%d)", contactId), ex);
            return null;
        }

        if (response.code() != HttpURLConnection.HTTP_OK){
            Log.e(TAG, String.format("Couldn't download photo for contact(%d), code: %d",
                    contactId,
                    response.code()));

            return null;
        }

        try {
            Bitmap bitmap = BitmapUtility.getBytesToBitmap(response.body().bytes());
            if (bitmap == null){
                Log.e(TAG, String.format("Couldn't convert image bytes to bitmap for contact(%d)",
                        contactId));
                return null;
            } else {
                return bitmap;
            }
        } catch(IOException ex){
            Log.e(TAG, String.format("Couldn't convert image bytes to bitmap for contact(%d)",
                    contactId),
                    ex);
            return null;
        }
    }

    private void saveContactDetailsList(List<ContactDetails> contactDetailsList){
        DBHelper db = new DBHelper(this);
        for(ContactDetails contactDetails: contactDetailsList) {
            db.updateContactByContactId(contactDetails);
        }
    }
}
