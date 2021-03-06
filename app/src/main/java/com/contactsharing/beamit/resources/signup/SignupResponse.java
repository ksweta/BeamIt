package com.contactsharing.beamit.resources.signup;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kumari on 10/29/15.
 */
public class SignupResponse {
    @SerializedName("user_id")
    Integer userId;

    public SignupResponse(){
        //Required by the system
    }

    public SignupResponse(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SignupResponse)) return false;

        SignupResponse that = (SignupResponse) o;

        return !(getUserId() != null ? !getUserId().equals(that.getUserId()) : that.getUserId() != null);

    }

    @Override
    public int hashCode() {
        return getUserId() != null ? getUserId().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "SignupResponse{" +
                "userId='" + userId + '\'' +
                '}';
    }
}
