package com.contactsharing.beamit.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.contactsharing.beamit.utility.BitmapUtility;
import com.contactsharing.beamit.model.ProfileDetails;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Kumari on 10/24/15.
 */
public class ProfileDBHelper extends SQLiteOpenHelper {
    private static final String TAG = ProfileDBHelper.class.getSimpleName();
    public static final String TABLE_NAME_PROFILE = "profile";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_COMPANY = "company";
    public static final String COLUMN_LINKEDIN_URL = "linkedinUrl";
    public static final String COLUMN_PHOTO = "photo";
    public static final String COLUMN_SYNC_DATE = "syncDate";
    public static final String DATABASE_NAME = "beamit.db";
    public static final int DATABASE_VERSION = 1;

    /**
     * SQLite stores the date as string format, so need SimpleDateFormat
     * to convert string to date object.
     */
    private SimpleDateFormat simpleDateFormat;

    private static final String DATABASE_CREATE_PROFILE_TABLE = "CREATE TABLE " + TABLE_NAME_PROFILE +
            "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NAME + " TEXT NOT NULL, "
            + COLUMN_PHONE + " TEXT, "
            + COLUMN_EMAIL + " TEXT NOT NULL, "
            + COLUMN_COMPANY + " TEXT, "
            + COLUMN_LINKEDIN_URL + " TEXT, "
            + COLUMN_PHOTO + " BLOB, "
            + COLUMN_SYNC_DATE + " TEXT);";

    public ProfileDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create profile table
        db.execSQL(DATABASE_CREATE_PROFILE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO Dropping the table and recreating is not a good idea.
        // need to write a better logic to handle DB upgrade.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_PROFILE);
        onCreate(db);
    }

    public long updateProfile(ProfileDetails profileDetails) {
        Log.d(TAG, String.format("ProfileDetails : %s", profileDetails.toString()));
        long result = getWritableDatabase().insertWithOnConflict(
                ProfileDBHelper.TABLE_NAME_PROFILE,
                ProfileDBHelper.COLUMN_ID,
                getValues(profileDetails),
                SQLiteDatabase.CONFLICT_REPLACE);
        Log.i(TAG, String.format("Updated row: %d", result));
        return result;
    }

    /**
     * This method deletes the profile details from db.
     * @param profileDetails
     * @return
     */
    public int deleteProfile(ProfileDetails profileDetails) {
        return getWritableDatabase().delete(ProfileDBHelper.TABLE_NAME_PROFILE,
                ProfileDBHelper.COLUMN_ID + "=?",
                new String[]{profileDetails.getId().toString()});
    }


    public ProfileDetails fetchProfileDetails(){
        //Fetch all columns from profile table.
        String[] allColumns = new String[]{ProfileDBHelper.COLUMN_ID,
                ProfileDBHelper.COLUMN_NAME,
                ProfileDBHelper.COLUMN_PHONE,
                ProfileDBHelper.COLUMN_EMAIL,
                ProfileDBHelper.COLUMN_COMPANY,
                ProfileDBHelper.COLUMN_LINKEDIN_URL,
                ProfileDBHelper.COLUMN_PHOTO,
                ProfileDBHelper.COLUMN_SYNC_DATE};

        Cursor cursor = getReadableDatabase().query(ProfileDBHelper.TABLE_NAME_PROFILE,
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
            int idColIndex = cursor.getColumnIndex(ProfileDBHelper.COLUMN_ID);
            int nameColIndex = cursor.getColumnIndex(ProfileDBHelper.COLUMN_NAME);
            int phoneColIndex = cursor.getColumnIndex(ProfileDBHelper.COLUMN_PHONE);
            int emailColIndex = cursor.getColumnIndex(ProfileDBHelper.COLUMN_EMAIL);
            int companyIndex = cursor.getColumnIndex(ProfileDBHelper.COLUMN_COMPANY);
            int linkedinUrlIndex = cursor.getColumnIndex(ProfileDBHelper.COLUMN_LINKEDIN_URL);
            int photoIndex = cursor.getColumnIndex(ProfileDBHelper.COLUMN_PHOTO);
            int syncDateIndex = cursor.getColumnIndex(ProfileDBHelper.COLUMN_SYNC_DATE);


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
        values.put(ProfileDBHelper.COLUMN_ID, profileDetails.getId());
        values.put(ProfileDBHelper.COLUMN_NAME, profileDetails.getName());
        values.put(ProfileDBHelper.COLUMN_PHONE, profileDetails.getPhone());
        values.put(ProfileDBHelper.COLUMN_EMAIL, profileDetails.getEmail());
        values.put(ProfileDBHelper.COLUMN_COMPANY, profileDetails.getCompany());
        values.put(ProfileDBHelper.COLUMN_LINKEDIN_URL, profileDetails.getLinkedinUrl());

        values.put(ProfileDBHelper.COLUMN_PHOTO,
                BitmapUtility.getBitmapToBytes(profileDetails.getPhoto()));
        values.put(ProfileDBHelper.COLUMN_SYNC_DATE,
                    profileDetails.getSyncDate() == null ? null: simpleDateFormat.format(profileDetails.getSyncDate()));
        return values;
    }

}
