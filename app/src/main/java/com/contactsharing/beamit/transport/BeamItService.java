package com.contactsharing.beamit.transport;

import com.contactsharing.beamit.resources.contact.Contact;
import com.contactsharing.beamit.resources.password.PasswordChangeRequest;
import com.contactsharing.beamit.resources.password.PasswordChangeResponse;
import com.contactsharing.beamit.resources.signin.SigninRequest;
import com.contactsharing.beamit.resources.signin.SigninResponse;
import com.contactsharing.beamit.resources.signup.SignupRequest;
import com.contactsharing.beamit.resources.signup.SignupResponse;
import com.contactsharing.beamit.resources.user.User;
import com.squareup.okhttp.RequestBody;

import retrofit.Call;
import retrofit.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;

/**
 * Created by kumari on 10/29/15.
 */
public interface BeamItService {

    /* Contact api */
    @POST("/api/contact")
    Call<Contact> createContact(@Body Contact contact);

    @GET("/api/contact/{contactId")
    Call<Contact> getContact(@Path("contactId") Integer contactId);

    @DELETE("/api/contact/{contactId}")
    Call<Response> deleteContact(@Path("contactId") Integer contactId);

    /* Password api */
    @POST("/api/password")
    Call<PasswordChangeResponse> changePassword(@Body PasswordChangeRequest passwordChangeRequest);

    /* Photo api */
    @Multipart
    @POST("/api/photo/user/{userId}")
    Call<Response> uploadUserProfilePhoto(@Path("userId") Integer userId, @Part("photo") RequestBody photo);

    // TODO: downloadUserProfilePhoto() need to be defined

    @DELETE("/api/photo/user/{userId")
    Call<Response> deleteUserProfilePhoto(@Path("userId") Integer userId);

    @Multipart
    @POST("/api/photo/contact/{contactId")
    Call<Response> uploadContactPhoto(@Path("contactId") Integer contactId, @Part("photo") RequestBody photo);

    // TODO: downloadContactPhoto() need to be defined

    @DELETE("/api/photo/contact/{contactId}")
    Call<Response> deleteContactPhoto(@Path("contactId") Integer contactId);

    /* Signin api */
    @POST("/api/signin")
    Call<SigninResponse> signin(@Body SigninRequest signinRequest);

    /* Signup api */
    @POST("/api/signup")
    Call<SignupResponse> signup(@Body SignupRequest signupRequest);

    /* User profile api */
    @POST("/api/user")
    Call<User> updateUserProfile(@Body User user);

    @GET("/api/user/{userId}")
    Call<User> getUpserProfile(@Path("userId") Integer userId);

    @DELETE("/api/user/{userId}")
    Call<Response> deleteUserProfile(@Path("userId") Integer userId);

    }
