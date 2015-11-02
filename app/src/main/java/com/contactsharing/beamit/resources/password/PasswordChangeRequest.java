package com.contactsharing.beamit.resources.password;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kumari on 10/30/15.
 */
public class PasswordChangeRequest {
    private String email;
    private String password;
    @SerializedName("new_password")
    private String newPassword;

    public PasswordChangeRequest(){
        //Required by the system
    }

    public PasswordChangeRequest(String email, String password, String newPassword) {
        this.email = email;
        this.password = password;
        this.newPassword = newPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PasswordChangeRequest)) return false;

        PasswordChangeRequest that = (PasswordChangeRequest) o;

        if (getEmail() != null ? !getEmail().equals(that.getEmail()) : that.getEmail() != null)
            return false;
        if (getPassword() != null ? !getPassword().equals(that.getPassword()) : that.getPassword() != null)
            return false;
        return !(getNewPassword() != null ? !getNewPassword().equals(that.getNewPassword()) : that.getNewPassword() != null);

    }

    @Override
    public int hashCode() {
        int result = getEmail() != null ? getEmail().hashCode() : 0;
        result = 31 * result + (getPassword() != null ? getPassword().hashCode() : 0);
        result = 31 * result + (getNewPassword() != null ? getNewPassword().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PasswordChangeRequest{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", newPassword='" + newPassword + '\'' +
                '}';
    }
}
