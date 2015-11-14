package com.contactsharing.beamit.transport;

import com.contactsharing.beamit.resources.invite.EmailInvite;
import com.contactsharing.beamit.resources.contact.Contact;
import com.contactsharing.beamit.resources.contact.ContactList;
import com.contactsharing.beamit.resources.password.ChangePasswordRequest;
import com.contactsharing.beamit.resources.password.ChangePasswordResponse;
import com.contactsharing.beamit.resources.share.ShareContactRequest;
import com.contactsharing.beamit.resources.signin.SigninRequest;
import com.contactsharing.beamit.resources.signin.SigninResponse;
import com.contactsharing.beamit.resources.signup.SignupRequest;
import com.contactsharing.beamit.resources.signup.SignupResponse;
import com.contactsharing.beamit.resources.user.User;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import retrofit.Call;
import retrofit.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

/**
 * Created by kumari on 10/29/15.
 */
public interface BeamItService {

    /* Contact api */
    @POST("/api/contact")
    Call<Contact> createContact(@Body Contact contact);

    @GET("/api/contact/{contactId}")
    Call<Contact> getContact(@Path("contactId") Integer contactId);

    @DELETE("/api/contact/{contactId}")
    Call<Response> deleteContact(@Path("contactId") Integer contactId);

    @GET("/api/contactlist/user/{ownerId}")
    Call<ContactList> getContactList(@Path("ownerId") Integer ownerId);

    @POST("/api/contact/share")
    Call<Contact> shareContact(@Body ShareContactRequest shareContactRequest);

    /* Invite related */
    @POST("/api/invite/email")
    Call<Void> sendEmailInvite(@Body EmailInvite emailInvite);

    /* Password api */
    @POST("/api/password")
    Call<ChangePasswordResponse> changePassword(@Body ChangePasswordRequest passwordChangeRequest);

    /* Photo api */
    @POST("/api/photo/user/{userId}")
    Call<Void> uploadUserProfilePhoto(@Path("userId") Integer userId, @Body RequestBody photo);

    @GET("/api/photo/user/{userId}")
    Call<ResponseBody> downloadUserProfilePhoto(@Path("userId") Integer userId);

    @GET("/api/photo/contact/{contactId}")
    Call<ResponseBody> downloadContactPhoto(@Path("contactId") Integer contactId);


    @DELETE("/api/photo/user/{userId")
    Call<Response> deleteUserProfilePhoto(@Path("userId") Integer userId);

    @POST("/api/photo/contact/{contactId}")
    Call<Void> uploadContactPhoto(@Path("contactId") Integer contactId, @Body RequestBody photo);


    @DELETE("/api/photo/contact/{contactId}")
    Call<Response> deleteContactPhoto(@Path("contactId") Integer contactId);

    /* Signin api */
    @POST("/api/signin")
    Call<SigninResponse> signin(@Body SigninRequest signinRequest);

    /* Signup api */
    @POST("/api/signup")
    Call<SignupResponse> signup(@Body SignupRequest signupRequest);

    /* User profile api */
    @PUT("/api/user")
    Call<User> updateUserProfile(@Body User user);

    @GET("/api/user/{userId}")
    Call<User> getUserProfile(@Path("userId") Integer userId);

    @DELETE("/api/user/{userId}")
    Call<ResponseBody> deleteUserProfile(@Path("userId") Integer userId);

}