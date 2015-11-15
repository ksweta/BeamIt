package com.contactsharing.beamit.db;

/**
 * Created by kumari on 5/17/15.
 */
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.contactsharing.beamit.model.ProfileDetails;
import com.contactsharing.beamit.utility.BitmapUtility;
import com.contactsharing.beamit.model.ContactDetails;

/**
 * This a helper class which method to access SQLite database access.
 *
 *
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG = DBHelper.class.getSimpleName();
    public static final String TABLE_NAME_CONTACTS = "contacts";
    public static final String TABLE_NAME_PROFILE = "profile";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CONTACT_ID = "contactId";
    public static final String COLUMN_OWNER_ID = "ownerId";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_COMPANY = "company";
    public static final String COLUMN_LINKEDIN_URL = "linkedinUrl";
    public static final String COLUMN_PHOTO_URI = "photo";
    public static final String COLUMN_SYNC = "sync";
    public static final String COLUMN_USER_ID = "userId";
    private static final String DATABASE_NAME = "beamit.db";
    private static final int DATABASE_VERSION = 1;

    String[] ALL_CONTACT_COLUMNS = new String[]{DBHelper.COLUMN_ID,
            DBHelper.COLUMN_CONTACT_ID,
            DBHelper.COLUMN_OWNER_ID,
            DBHelper.COLUMN_NAME,
            DBHelper.COLUMN_PHONE,
            DBHelper.COLUMN_EMAIL,
            DBHelper.COLUMN_COMPANY,
            DBHelper.COLUMN_LINKEDIN_URL,
            DBHelper.COLUMN_PHOTO_URI,
            DBHelper.COLUMN_SYNC};
    /**
     * SQLite stores the date as string format, so need SimpleDateFormat
     * to convert string to date object.
     */
    private SimpleDateFormat simpleDateFormat;

    private static final String DATABASE_CREATE_CONTACT_TABLE = "CREATE TABLE " + TABLE_NAME_CONTACTS +
            "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_CONTACT_ID + " INTEGER, "
            + COLUMN_OWNER_ID + " INTEGER, "
            + COLUMN_NAME + " TEXT, "
            + COLUMN_PHONE + " TEXT, "
            + COLUMN_EMAIL + " TEXT, "
            + COLUMN_COMPANY + " TEXT, "
            + COLUMN_LINKEDIN_URL + " TEXT, "
            + COLUMN_PHOTO_URI + " TEXT, "
            + COLUMN_SYNC + " BOOLEAN);";

    private static final String DATABASE_CREATE_PROFILE_TABLE = "CREATE TABLE " + TABLE_NAME_PROFILE +
            "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_USER_ID + " INTEGER, "
            + COLUMN_NAME + " TEXT, "
            + COLUMN_PHONE + " TEXT, "
            + COLUMN_EMAIL + " TEXT NOT NULL, "
            + COLUMN_COMPANY + " TEXT, "
            + COLUMN_LINKEDIN_URL + " TEXT, "
            + COLUMN_PHOTO_URI + " TEXT, "
            + COLUMN_SYNC + " BOOLEAN);";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create Contact table
        db.execSQL(DATABASE_CREATE_CONTACT_TABLE);
        db.execSQL(DATABASE_CREATE_PROFILE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO Dropping the table and recreating is not a good idea.
        // need to write a better logic to handle DB upgrade.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_PROFILE);
        onCreate(db);
    }

    public Integer insertContact(ContactDetails contact) {

         Long result = getWritableDatabase().insert(DBHelper.TABLE_NAME_CONTACTS,
                null,
                getValues(contact));
        Log.d(TAG, "InsertData->result : " + result);
        return result.intValue();
    }


    private List<ContactDetails> getContacts(Cursor cursor) {
        List<ContactDetails> contactList = new LinkedList<>();
        if (cursor != null) {
            // Get the index of the various columns. This helps to get
            // the column value from the cursor object.
            int idColIndex = cursor.getColumnIndex(DBHelper.COLUMN_ID);
            int contactIdIndex = cursor.getColumnIndex(DBHelper.COLUMN_CONTACT_ID);
            int ownerIdIndex = cursor.getColumnIndex(DBHelper.COLUMN_OWNER_ID);
            int nameColIndex = cursor.getColumnIndex(DBHelper.COLUMN_NAME);
            int phoneColIndex = cursor.getColumnIndex(DBHelper.COLUMN_PHONE);
            int emailColIndex = cursor.getColumnIndex(DBHelper.COLUMN_EMAIL);
            int companyIndex = cursor.getColumnIndex(DBHelper.COLUMN_COMPANY);
            int linkedinUrlIndex = cursor.getColumnIndex(DBHelper.COLUMN_LINKEDIN_URL);
            int photoUriIndex = cursor.getColumnIndex(DBHelper.COLUMN_PHOTO_URI);
            int syncIndex = cursor.getColumnIndex(DBHelper.COLUMN_SYNC);

            while (cursor.moveToNext()) {
                //simpleDateFormat.parse() throws exception, necessary to have try-catch block here.
                try {
                    ContactDetails contact = new ContactDetails(cursor.getInt(idColIndex),
                            cursor.getInt(contactIdIndex),
                            cursor.getInt(ownerIdIndex),
                            cursor.getString(nameColIndex),
                            cursor.getString(phoneColIndex),
                            cursor.getString(emailColIndex),
                            cursor.getString(companyIndex),
                            cursor.getString(linkedinUrlIndex),
                            cursor.getString(photoUriIndex),
                            cursor.getInt(syncIndex) == 1);

                    contactList.add(contact);
                } catch (Exception e) {
                    Log.e(TAG, "error while fetching the records", e);
                }
            }
            //Close the DB connection once work is done.
            cursor.close();
        }

        return contactList;
    }


    public List<ContactDetails> readAllContacts() {
        Cursor cursor = getReadableDatabase().query(DBHelper.TABLE_NAME_CONTACTS,
                ALL_CONTACT_COLUMNS,
                null,
                null,
                null,
                null,
                null);
        return getContacts(cursor);
    }

    public int updateContactById(ContactDetails contact) {
        return getWritableDatabase().update(DBHelper.TABLE_NAME_CONTACTS,
                getValues(contact),
                //Where clause
                DBHelper.COLUMN_ID + "=?",
                new String[]{contact.getId().toString()});
    }

    /**
     * Upate contact details based on the ContactId.
     * @param contactDetails
     * @return
     */
    public long updateContactByContactId(ContactDetails contactDetails) {
        Log.d(TAG, String.format("ProfileDetails : %s", contactDetails.toString()));
        long result = getWritableDatabase().insertWithOnConflict(
                DBHelper.TABLE_NAME_CONTACTS,
                DBHelper.COLUMN_CONTACT_ID,
                getValues(contactDetails),
                SQLiteDatabase.CONFLICT_REPLACE);
        Log.i(TAG, String.format("Updated row: %d", result));
        return result;
    }

    /**
     * This Method deletes all contacts.
     */
    public void deleteAllContact(){
        int count = getWritableDatabase().delete(DBHelper.TABLE_NAME_CONTACTS, null, null);
        Log.d(TAG, String.format("deleteAllContact => deleted %d records", count));
    }

    /**
     * Deletes the contacts which are not associated with current user.
     * @param ownerId
     */
    public void deleteContactWithoutCurrentOwner(Integer ownerId){
        int count = getWritableDatabase().delete(DBHelper.TABLE_NAME_CONTACTS,
                DBHelper.COLUMN_OWNER_ID + "<>?",  // Not equal to operator, null safe
                new String[]{String.valueOf(ownerId)});
        Log.d(TAG, String.format("deleteContactWithoutCurrentOwner => deleted %d records", count));
    }

    /**
     * Delete the provided method from the store.
     * @param id Local contact id.
     * @return
     */
    public int deleteContactById(Integer id) {
        return getWritableDatabase().delete(DBHelper.TABLE_NAME_CONTACTS,
                DBHelper.COLUMN_ID + "=?",
                new String[]{id.toString()});
    }

    /**
     * This method check if the given phone number is already present in the contact list.
     *
     * @param phone Contact's phone number.
     * @return returns true if the phone number is already present in database.
     */
    public boolean isContactPresent(String phone) {
        String[] columns = new String[]{DBHelper.COLUMN_ID};
        String[] selectionArgs = new String[]{phone};
        Cursor cursor = null;
        try {

            cursor = getReadableDatabase().query(DBHelper.TABLE_NAME_CONTACTS,
                    columns,
                    DBHelper.COLUMN_PHONE + "=?",
                    selectionArgs,
                    null,
                    null,
                    null);

            if (cursor == null) {
                Log.e(TAG, "isContactPresent(): Serious problem, cursor is null");
            }
            if (cursor.getCount() > 0) {

                return true;
            } else {
                return false;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Get the contact details which matches with the given contact id.
     * @param id
     * @return
     */
    public ContactDetails getContact(Integer id){

        //Get all columns from Contacts  table.

        String[] selectionArgs = new String[]{id.toString()};

        Cursor cursor = getReadableDatabase().query(DBHelper.TABLE_NAME_CONTACTS,
                ALL_CONTACT_COLUMNS,
                DBHelper.COLUMN_ID + "=?",
                selectionArgs,
                null,
                null,
                null);

        List<ContactDetails> contactList = getContacts(cursor);

        if(contactList.isEmpty()){
            return null;
        } else {
            return contactList.get(0);
        }
    }

    /**
     * This method returns the contactDetails list which needs to be synced.
     * @param synced
     * @return
     */
    public List<ContactDetails> getCotnactsTobeSynced(boolean synced){
        int syncedValue;
        if (synced) {
            syncedValue = 1;
        } else {
            syncedValue = 0;
        }

        String[] selectionArgs = new String[] {String.valueOf(syncedValue)};

        Cursor cursor = getReadableDatabase().query(DBHelper.TABLE_NAME_CONTACTS,
                ALL_CONTACT_COLUMNS,
                DBHelper.COLUMN_SYNC + "=?",
                selectionArgs,
                null,
                null,
                null);

        return getContacts(cursor);
    }
    /**
     * This is a helper method to convert ContactDetails object to ContentValues
     * This contentValues object is used in various CRUD methods.
     *
     * @param contact
     * @return
     */
    private ContentValues getValues(ContactDetails contact) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_CONTACT_ID, contact.getContactId());
        values.put(DBHelper.COLUMN_OWNER_ID, contact.getOwnerId());
        values.put(DBHelper.COLUMN_NAME, contact.getName());
        values.put(DBHelper.COLUMN_PHONE, contact.getPhone());
        values.put(DBHelper.COLUMN_EMAIL, contact.getEmail());
        values.put(DBHelper.COLUMN_COMPANY, contact.getCompany());
        values.put(DBHelper.COLUMN_LINKEDIN_URL, contact.getLinkedinUrl());
        values.put(DBHelper.COLUMN_PHOTO_URI, contact.getPhotoUri());
        if (contact.isSynced()) {
            values.put(DBHelper.COLUMN_SYNC, 1);
        } else {
            values.put(DBHelper.COLUMN_SYNC, 0);
        }
        return values;
    }


    public List<ContactDetails> getNameMatches(String query) {


        String[] selectionArgs = new String[]{"%" + query + "%"};
        //Cursor cursor = null;
        Cursor cursor = getReadableDatabase().query(DBHelper.TABLE_NAME_CONTACTS,
                ALL_CONTACT_COLUMNS,
                DBHelper.COLUMN_NAME + " LIKE ?",
                selectionArgs,
                null,
                null,
                null);
        return getContacts(cursor);

    }

    public long updateProfile(ProfileDetails profileDetails) {
        Log.d(TAG, String.format("ProfileDetails : %s", profileDetails.toString()));
        long result = getWritableDatabase().insertWithOnConflict(
                DBHelper.TABLE_NAME_PROFILE,
                DBHelper.COLUMN_ID,
                getValues(profileDetails),
                SQLiteDatabase.CONFLICT_REPLACE);
        Log.i(TAG, String.format("Updated row: %d", result));
        return result;
    }

    /**
     * Fetch User details.
     * @return
     */
    public ProfileDetails fetchProfileDetails(){
        //Fetch all columns from profile table.
        String[] allColumns = new String[]{DBHelper.COLUMN_ID,
                DBHelper.COLUMN_USER_ID,
                DBHelper.COLUMN_NAME,
                DBHelper.COLUMN_PHONE,
                DBHelper.COLUMN_EMAIL,
                DBHelper.COLUMN_COMPANY,
                DBHelper.COLUMN_LINKEDIN_URL,
                DBHelper.COLUMN_PHOTO_URI,
                DBHelper.COLUMN_SYNC};

        Cursor cursor = getReadableDatabase().query(DBHelper.TABLE_NAME_PROFILE,
                allColumns,
                null,
                null,
                null,
                null,
                null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            Log.d(TAG, String.format("Cursor record count: %d", cursor.getCount()));



            // Get the index of the various columns. This helps to get
            // the column value from the cursor object.
            int idColIndex = cursor.getColumnIndex(DBHelper.COLUMN_ID);
            int userIdColIndex = cursor.getColumnIndex(DBHelper.COLUMN_USER_ID);
            int nameColIndex = cursor.getColumnIndex(DBHelper.COLUMN_NAME);
            int phoneColIndex = cursor.getColumnIndex(DBHelper.COLUMN_PHONE);
            int emailColIndex = cursor.getColumnIndex(DBHelper.COLUMN_EMAIL);
            int companyIndex = cursor.getColumnIndex(DBHelper.COLUMN_COMPANY);
            int linkedinUrlIndex = cursor.getColumnIndex(DBHelper.COLUMN_LINKEDIN_URL);
            int photoUriIndex = cursor.getColumnIndex(DBHelper.COLUMN_PHOTO_URI);
            int syncIndex = cursor.getColumnIndex(DBHelper.COLUMN_SYNC);


            //simpleDateFormat.parse() throws exception, necessary to have try-catch block here.
            try {
                ProfileDetails profileDetails = new ProfileDetails(cursor.getInt(idColIndex),
                        cursor.getInt(userIdColIndex),
                        cursor.getString(nameColIndex),
                        cursor.getString(phoneColIndex),
                        cursor.getString(emailColIndex),
                        cursor.getString(companyIndex),
                        cursor.getString(linkedinUrlIndex),
                        cursor.getString(photoUriIndex),
                        cursor.getInt(syncIndex) == 1);

                return profileDetails;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return null;
    }

    /**
     * This is a helper method to convert ProfileDetails object to ContentValues
     * This contentValues object is used in various CRUD methods.
     *
     * @param profileDetails
     * @return
     */

    private ContentValues getValues(ProfileDetails profileDetails) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_ID, profileDetails.getId());
        values.put(DBHelper.COLUMN_USER_ID, profileDetails.getUserId());
        values.put(DBHelper.COLUMN_NAME, profileDetails.getName());
        values.put(DBHelper.COLUMN_PHONE, profileDetails.getPhone());
        values.put(DBHelper.COLUMN_EMAIL, profileDetails.getEmail());
        values.put(DBHelper.COLUMN_COMPANY, profileDetails.getCompany());
        values.put(DBHelper.COLUMN_LINKEDIN_URL, profileDetails.getLinkedinUrl());

        values.put(DBHelper.COLUMN_PHOTO_URI,profileDetails.getPhotoUri());
        if(profileDetails.isSync()) {
            values.put(DBHelper.COLUMN_SYNC, 1);
        } else {
            values.put(DBHelper.COLUMN_SYNC, 0);
        }
        return values;
    }
}
