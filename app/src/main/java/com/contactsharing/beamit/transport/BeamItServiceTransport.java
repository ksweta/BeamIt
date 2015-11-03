package com.contactsharing.beamit.transport;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.io.IOException;

import okio.Buffer;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by Kumari on 11/1/15.
 */
public class BeamItServiceTransport {
    public static final String BASE_URL = "https://blooming-cliffs-9672.herokuapp.com";
//    public static final String BASE_URL = "http://10.0.0.12:5000";
    private static BeamItService service;
    private BeamItServiceTransport(){
        //To make sure other class doesn't create another instance of this class.
    }

    public static BeamItService getService(){
        if (service == null) {
            OkHttpClient client = new OkHttpClient();
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            client.networkInterceptors().add(interceptor);
            // TODO: remove interceptors once development part is done.
            service =  new Retrofit.Builder()
                    .baseUrl(BASE_URL)
//                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(BeamItService.class);
        }
        return service;
    }
}
