package com.contactsharing.beamit.resources.invite;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kumari on 11/14/15.
 */
public class EmailInvite {

    //Sender's user id.
    @SerializedName("user_id")
    private Integer userId;
    @SerializedName("invitee_email")
    private String inviteeEmail;

    public EmailInvite() {
        // Required by the system
    }

    public EmailInvite(Integer userId, String inviteeEmail) {
        this.userId = userId;
        this.inviteeEmail = inviteeEmail;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getInviteeEmail() {
        return inviteeEmail;
    }

    public void setInviteeEmail(String inviteeEmail) {
        this.inviteeEmail = inviteeEmail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmailInvite)) return false;

        EmailInvite that = (EmailInvite) o;

        if (!getUserId().equals(that.getUserId())) return false;
        return getInviteeEmail().equals(that.getInviteeEmail());

    }

    @Override
    public int hashCode() {
        int result = getUserId().hashCode();
        result = 31 * result + getInviteeEmail().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "EmailInvite{" +
                "userId=" + userId +
                ", inviteeEmail='" + inviteeEmail + '\'' +
                '}';
    }
}
