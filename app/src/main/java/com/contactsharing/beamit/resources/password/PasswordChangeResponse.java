package com.contactsharing.beamit.resources.password;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kumari on 10/30/15.
 */
public class PasswordChangeResponse {
    @SerializedName("user_id")
    private String userId;

    public PasswordChangeResponse(){
        //Required by the system
    }

    public PasswordChangeResponse(String userId) {
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
        if (!(o instanceof PasswordChangeResponse)) return false;

        PasswordChangeResponse that = (PasswordChangeResponse) o;

        return !(getUserId() != null ? !getUserId().equals(that.getUserId()) : that.getUserId() != null);

    }

    @Override
    public int hashCode() {
        return getUserId() != null ? getUserId().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "PasswordChangeResponse{" +
                "userId='" + userId + '\'' +
                '}';
    }
}
