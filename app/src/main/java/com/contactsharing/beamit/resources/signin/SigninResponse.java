package com.contactsharing.beamit.resources.signin;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kumari on 10/29/15.
 */
public class SigninResponse {
    @SerializedName("user_id")
    String userId;

    public SigninResponse(){
        //Required by the system.
    }
    public SigninResponse(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SigninResponse)) return false;

        SigninResponse that = (SigninResponse) o;

        return !(getUserId() != null ? !getUserId().equals(that.getUserId()) : that.getUserId() != null);

    }

    @Override
    public int hashCode() {
        return getUserId() != null ? getUserId().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "SigninResponse{" +
                "userId='" + userId + '\'' +
                '}';
    }
}
