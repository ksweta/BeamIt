package com.contactsharing.beamit.utility;

import com.contactsharing.beamit.model.ContactDetails;
import com.google.gson.Gson;

/**
 * Created by kumari on 10/29/15.
 */
public class JsonConverter {

    /**
     * This method converts the contact details json string to ContactDetails object.
     * @param contactDetailsJson
     * @return
     */
    public static ContactDetails toConcatDetails(String contactDetailsJson){
        return new Gson().fromJson(contactDetailsJson, ContactDetails.class);
    }

    /**
     * This method converts the contactDetails object to json string.
     * @param contactDetails
     * @return
     */
    public static String toJson(ContactDetails contactDetails){
        return new Gson().toJson(contactDetails);
    }
}
