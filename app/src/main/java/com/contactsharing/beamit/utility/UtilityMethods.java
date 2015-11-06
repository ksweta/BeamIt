package com.contactsharing.beamit.utility;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * Created by Kumari on 11/2/15.
 */
public class UtilityMethods {

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
}
