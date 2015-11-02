package com.contactsharing.beamit.transport;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by Kumari on 11/1/15.
 */
public class BeamItServiceTransport {
    public static final String BASE_URL = "https://blooming-cliffs-9672.herokuapp.com";
    private static BeamItService service;
    private BeamItServiceTransport(){
        //To make sure other class doesn't create another instance of this class.
    }

    public static BeamItService getService(){
        if (service == null) {
            service =  new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(BeamItService.class);
        }
        return service;
    }
}
