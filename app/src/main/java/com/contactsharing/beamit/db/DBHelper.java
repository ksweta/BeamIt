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
import android.graphics.Bitmap;
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
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_COMPANY = "company";
    public static final String COLUMN_LINKEDIN_URL = "linkedinUrl";
    public static final String COLUMN_PHOTO = "photo";
    public static final String COLUMN_SYNC_DATE = "syncDate";
    private static final String DATABASE_NAME = "beamit.db";
    private static final int DATABASE_VERSION = 1;

    /**
     * SQLite stores the date as string format, so need SimpleDateFormat
     * to convert string to date object.
     */
    private SimpleDateFormat simpleDateFormat;

    private static final String DATABASE_CREATE_CONTACT_TABLE = "CREATE TABLE " + TABLE_NAME_CONTACTS +
            "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NAME + " TEXT NOT NULL, "
            + COLUMN_PHONE + " TEXT, "
            + COLUMN_EMAIL + " TEXT, "
            + COLUMN_COMPANY + " TEXT, "
            + COLUMN_LINKEDIN_URL + " TEXT, "
            + COLUMN_PHOTO + " BLOB, "
            + COLUMN_SYNC_DATE + " TEXT NOT NULL);";

    private static final String DATABASE_CREATE_PROFILE_TABLE = "CREATE TABLE " + TABLE_NAME_PROFILE +
            "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NAME + " TEXT NOT NULL, "
            + COLUMN_PHONE + " TEXT, "
            + COLUMN_EMAIL + " TEXT NOT NULL, "
            + COLUMN_COMPANY + " TEXT, "
            + COLUMN_LINKEDIN_URL + " TEXT, "
            + COLUMN_PHOTO + " BLOB, "
            + COLUMN_SYNC_DATE + " TEXT);";

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

    public long insertContact(ContactDetails contact) {

        long result = getWritableDatabase().insert(DBHelper.TABLE_NAME_CONTACTS,
                null,
                getValues(contact));
        Log.d(TAG, "InsertData->result : " + result);
        return result;
    }




    public List<ContactDetails> readAllContacts() {
        //Get all columns from Contacts  table.
        String[] allColumns = new String[]{DBHelper.COLUMN_ID,
                DBHelper.COLUMN_NAME,
                DBHelper.COLUMN_PHONE,
                DBHelper.COLUMN_EMAIL,
                DBHelper.COLUMN_COMPANY,
                DBHelper.COLUMN_LINKEDIN_URL,
                DBHelper.COLUMN_SYNC_DATE};

        Cursor cursor = getReadableDatabase().query(DBHelper.TABLE_NAME_CONTACTS,
                allColumns,
                null,
                null,
                null,
                null,
                null);
        List<ContactDetails> contactList = new LinkedList<ContactDetails>();
        if (cursor != null) {
            //cursor.moveToFirst();

            // Get the index of the various columns. This helps to get
            // the column value from the cursor object.
            int idColIndex = cursor.getColumnIndex(DBHelper.COLUMN_ID);
            int nameColIndex = cursor.getColumnIndex(DBHelper.COLUMN_NAME);
            int phoneColIndex = cursor.getColumnIndex(DBHelper.COLUMN_PHONE);
            int emailColIndex = cursor.getColumnIndex(DBHelper.COLUMN_EMAIL);
            int companyIndex = cursor.getColumnIndex(DBHelper.COLUMN_COMPANY);
            int linkedinUrlIndex = cursor.getColumnIndex(DBHelper.COLUMN_LINKEDIN_URL);
            int syncDateIndex = cursor.getColumnIndex(DBHelper.COLUMN_SYNC_DATE);


            while (cursor.moveToNext()) {
                //simpleDateFormat.parse() throws exception, necessary to have try-catch block here.
                try {
                    ContactDetails contact = new ContactDetails(cursor.getLong(idColIndex),
                            cursor.getString(nameColIndex),
                            cursor.getString(phoneColIndex),
                            cursor.getString(emailColIndex),
                            cursor.getString(companyIndex),
                            cursor.getString(linkedinUrlIndex),
                            null,
                            simpleDateFormat.parse(cursor.getString(syncDateIndex)));

                    contactList.add(contact);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            //Close the DB connection once work is done.
            cursor.close();
        }
        return contactList;
    }

    public int updateContact(ContactDetails contact) {
        return getWritableDatabase().update(DBHelper.TABLE_NAME_CONTACTS,
                getValues(contact),
                //Where clause
                DBHelper.COLUMN_ID + "=?",
                new String[]{contact.getId().toString()});
    }

    public int deleteContact(ContactDetails contact) {
        return getWritableDatabase().delete(DBHelper.TABLE_NAME_CONTACTS,
                DBHelper.COLUMN_ID + "=?",
                new String[]{contact.getId().toString()});
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
     * This is a helper method to convert ContactDetails object to ContentValues
     * This contentValues object is used in various CRUD methods.
     *
     * @param contact
     * @return
     */
    private ContentValues getValues(ContactDetails contact) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_NAME, contact.getName());
        values.put(DBHelper.COLUMN_PHONE, contact.getPhone());
        values.put(DBHelper.COLUMN_EMAIL, contact.getEmail());
        values.put(DBHelper.COLUMN_COMPANY, contact.getCompany());
        values.put(DBHelper.COLUMN_LINKEDIN_URL, contact.getLinkedinUrl());
        values.put(DBHelper.COLUMN_PHOTO, BitmapUtility.getBitmapToBytes(contact.getPhoto()));
        values.put(DBHelper.COLUMN_SYNC_DATE, simpleDateFormat.format(contact.getSyncDate()));
        return values;
    }


    public List<ContactDetails> getNameMatches(String query) {
        //Get all columns from Contacts  table.
        String[] columns = new String[]{DBHelper.COLUMN_ID,
                DBHelper.COLUMN_NAME,
                DBHelper.COLUMN_PHONE,
                DBHelper.COLUMN_EMAIL,
                DBHelper.COLUMN_COMPANY,
                DBHelper.COLUMN_LINKEDIN_URL,
                DBHelper.COLUMN_PHOTO,
                DBHelper.COLUMN_SYNC_DATE};

        String[] selectionArgs = new String[]{"%" + query + "%"};
        //Cursor cursor = null;
        Cursor cursor = getReadableDatabase().query(DBHelper.TABLE_NAME_CONTACTS,
                columns,
                DBHelper.COLUMN_NAME + " LIKE ?",
                selectionArgs,
                null,
                null,
                null);

        List<ContactDetails> contactList = new LinkedList<ContactDetails>();
        if (cursor != null) {
            //cursor.moveToFirst();

            // Get the index of the various columns. This helps to get
            // the column value from the cursor object.
            int idColIndex = cursor.getColumnIndex(DBHelper.COLUMN_ID);
            int nameColIndex = cursor.getColumnIndex(DBHelper.COLUMN_NAME);
            int phoneColIndex = cursor.getColumnIndex(DBHelper.COLUMN_PHONE);
            int emailColIndex = cursor.getColumnIndex(DBHelper.COLUMN_EMAIL);
            int companyIndex = cursor.getColumnIndex(DBHelper.COLUMN_COMPANY);
            int linkedinUrlIndex = cursor.getColumnIndex(DBHelper.COLUMN_LINKEDIN_URL);
            int photoIndex = cursor.getColumnIndex(DBHelper.COLUMN_PHOTO);
            int syncDateIndex = cursor.getColumnIndex(DBHelper.COLUMN_SYNC_DATE);


            while (cursor.moveToNext()) {
                //simpleDateFormat.parse() throws exception, necessary to have try-catch block here.
                try {
                    byte[] rowPhoto = cursor.getBlob(photoIndex);
                    Bitmap photo = rowPhoto == null? null : BitmapFactory.decodeByteArray(rowPhoto, 0, rowPhoto.length);
                    ContactDetails contact = new ContactDetails(cursor.getLong(idColIndex),
                            cursor.getString(nameColIndex),
                            cursor.getString(phoneColIndex),
                            cursor.getString(emailColIndex),
                            cursor.getString(companyIndex),
                            cursor.getString(linkedinUrlIndex),
                            photo,
                            simpleDateFormat.parse(cursor.getString(syncDateIndex)));

                    contactList.add(contact);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            //Close the DB connection once work is done.
            cursor.close();
        }
        return contactList;

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
     * Fetch Profile details.
     * @return
     */
    public ProfileDetails fetchProfileDetails(){
        //Fetch all columns from profile table.
        String[] allColumns = new String[]{DBHelper.COLUMN_ID,
                DBHelper.COLUMN_NAME,
                DBHelper.COLUMN_PHONE,
                DBHelper.COLUMN_EMAIL,
                DBHelper.COLUMN_COMPANY,
                DBHelper.COLUMN_LINKEDIN_URL,
                DBHelper.COLUMN_PHOTO,
                DBHelper.COLUMN_SYNC_DATE};

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
            int nameColIndex = cursor.getColumnIndex(DBHelper.COLUMN_NAME);
            int phoneColIndex = cursor.getColumnIndex(DBHelper.COLUMN_PHONE);
            int emailColIndex = cursor.getColumnIndex(DBHelper.COLUMN_EMAIL);
            int companyIndex = cursor.getColumnIndex(DBHelper.COLUMN_COMPANY);
            int linkedinUrlIndex = cursor.getColumnIndex(DBHelper.COLUMN_LINKEDIN_URL);
            int photoIndex = cursor.getColumnIndex(DBHelper.COLUMN_PHOTO);
            int syncDateIndex = cursor.getColumnIndex(DBHelper.COLUMN_SYNC_DATE);


            //simpleDateFormat.parse() throws exception, necessary to have try-catch block here.
            try {
                byte[] rowPhoto = cursor.getBlob(photoIndex);
                Bitmap photo = rowPhoto == null? null : BitmapFactory.decodeByteArray(rowPhoto, 0, rowPhoto.length);
                String syncDate = cursor.getString(syncDateIndex);
                ProfileDetails profileDetails = new ProfileDetails(cursor.getLong(idColIndex),
                        cursor.getString(nameColIndex),
                        cursor.getString(phoneColIndex),
                        cursor.getString(emailColIndex),
                        cursor.getString(companyIndex),
                        cursor.getString(linkedinUrlIndex),
                        photo,
                        syncDate == null? null : simpleDateFormat.parse(syncDate));

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
        values.put(DBHelper.COLUMN_NAME, profileDetails.getName());
        values.put(DBHelper.COLUMN_PHONE, profileDetails.getPhone());
        values.put(DBHelper.COLUMN_EMAIL, profileDetails.getEmail());
        values.put(DBHelper.COLUMN_COMPANY, profileDetails.getCompany());
        values.put(DBHelper.COLUMN_LINKEDIN_URL, profileDetails.getLinkedinUrl());

        values.put(DBHelper.COLUMN_PHOTO,
                BitmapUtility.getBitmapToBytes(profileDetails.getPhoto()));
        values.put(DBHelper.COLUMN_SYNC_DATE,
                profileDetails.getSyncDate() == null ? null : simpleDateFormat.format(profileDetails.getSyncDate()));
        return values;
    }


}
