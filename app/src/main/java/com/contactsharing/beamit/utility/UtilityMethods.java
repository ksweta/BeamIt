package com.contactsharing.beamit.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

/**
 * Created by Kumari on 11/2/15.
 */
public class UtilityMethods {
    private static final String TAG = UtilityMethods.class.getSimpleName();
    public static final MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg");

    public static String formatFileString(String photoType, Integer id, String extension){
        return String.format("%s_%d.%s", photoType, id, extension);
    }

    /**
     * This method returns phone number of the device.
     * @param context
     * @return
     */
    public static String getDevicePhoneNumber(Context context){
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return manager.getLine1Number();
    }

    /**
     * Helper method to form the request body for a photo upload.
     * @param bitmap
     * @param id id of the entity. Profile(User) id or contact id.
     * @param type either one of them "profile" or "contact" photo.
     * @return
     */
    public static RequestBody getPhotoRequestBody(Bitmap bitmap, Integer id, String type){
        byte [] data = BitmapUtility.getBitmapToBytes(bitmap);

        Log.d(TAG, String.format("file detilas => id: %d, filename: %s, size of data: %d",
                id,
                UtilityMethods.formatFileString(type, id, "jpeg"),
                data.length));

        RequestBody requestBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart("photo",
                        UtilityMethods.formatFileString(type, id, "jpeg"),
                        RequestBody.create(MEDIA_TYPE_JPEG, data))
                .build();

        return requestBody;
    }
}
