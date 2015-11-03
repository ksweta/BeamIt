package com.contactsharing.beamit.utility;

/**
 * Created by Kumari on 11/2/15.
 */
public class UtilityMethods {

    public static String formatFileString(String photoType, Long id, String extension){
        return String.format("%s_%d.%s", photoType, id, extension);
    }
}
