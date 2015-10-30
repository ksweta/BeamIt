package com.contactsharing.beamit.resources.profile;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kumari on 10/29/15.
 */
public class Profile {
    @SerializedName("user_id")
    String userId;
    String name;
    String phone;
    String email;
    String company;
    @SerializedName("linkedin_url")
    String linkedinUrl;

    public Profile(){
        //Required by the system.
    }

    public Profile(String userId, String name, String phone, String email, String company, String linkedinUrl) {
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.company = company;
        this.linkedinUrl = linkedinUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Profile)) return false;

        Profile profile = (Profile) o;

        if (getUserId() != null ? !getUserId().equals(profile.getUserId()) : profile.getUserId() != null)
            return false;
        if (getName() != null ? !getName().equals(profile.getName()) : profile.getName() != null)
            return false;
        if (getPhone() != null ? !getPhone().equals(profile.getPhone()) : profile.getPhone() != null)
            return false;
        if (getEmail() != null ? !getEmail().equals(profile.getEmail()) : profile.getEmail() != null)
            return false;
        if (getCompany() != null ? !getCompany().equals(profile.getCompany()) : profile.getCompany() != null)
            return false;
        return !(getLinkedinUrl() != null ? !getLinkedinUrl().equals(profile.getLinkedinUrl()) : profile.getLinkedinUrl() != null);

    }

    @Override
    public int hashCode() {
        int result = getUserId() != null ? getUserId().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getPhone() != null ? getPhone().hashCode() : 0);
        result = 31 * result + (getEmail() != null ? getEmail().hashCode() : 0);
        result = 31 * result + (getCompany() != null ? getCompany().hashCode() : 0);
        result = 31 * result + (getLinkedinUrl() != null ? getLinkedinUrl().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", company='" + company + '\'' +
                ", linkedinUrl='" + linkedinUrl + '\'' +
                '}';
    }
}
