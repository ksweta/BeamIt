package com.contactsharing.beamit.model;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by Kumari on 10/24/15.
 */
public class ProfileDetails {

    //This id field is used by sqlite.
    private Long id;
    //This id is provided by the server when app register with server.
    private Long userId;
    private String name;
    private String phone;
    private String email;
    private String company;
    private String linkedinUrl;
    private Bitmap photo;

    //indiates whether profile details are sync with server or not.
    private boolean sync;

    public ProfileDetails(){
        //Required by the system.
        this.id = 1L;
    }

    /**
     * Helper constructor for signup activity.
     * @param userId
     * @param email
     */
    public ProfileDetails(Long userId, String email){

        this(null, userId, null, null, email, null, null, null, false);
    }

    public ProfileDetails(Long id,
                          Long userId,
                          String name,
                          String phone,
                          String email,
                          String company,
                          String linkedinUrl,
                          Bitmap photo,
                          boolean sync) {
        super();
        if (id == null) {
            this.id = 1L;
        } else {
            this.id = id;
        }
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.company = company;
        this.linkedinUrl = linkedinUrl;
        this.photo = photo;
        this.sync = sync;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
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

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public boolean isSync() {
        return sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProfileDetails)) return false;

        ProfileDetails that = (ProfileDetails) o;

        if (isSync() != that.isSync()) return false;
        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        if (getUserId() != null ? !getUserId().equals(that.getUserId()) : that.getUserId() != null)
            return false;
        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null)
            return false;
        if (getPhone() != null ? !getPhone().equals(that.getPhone()) : that.getPhone() != null)
            return false;
        if (getEmail() != null ? !getEmail().equals(that.getEmail()) : that.getEmail() != null)
            return false;
        if (getCompany() != null ? !getCompany().equals(that.getCompany()) : that.getCompany() != null)
            return false;
        return !(getLinkedinUrl() != null ? !getLinkedinUrl().equals(that.getLinkedinUrl()) : that.getLinkedinUrl() != null);

    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getUserId() != null ? getUserId().hashCode() : 0);
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getPhone() != null ? getPhone().hashCode() : 0);
        result = 31 * result + (getEmail() != null ? getEmail().hashCode() : 0);
        result = 31 * result + (getCompany() != null ? getCompany().hashCode() : 0);
        result = 31 * result + (getLinkedinUrl() != null ? getLinkedinUrl().hashCode() : 0);
        result = 31 * result + (isSync() ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ProfileDetails{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", company='" + company + '\'' +
                ", linkedinUrl='" + linkedinUrl + '\'' +
                ", sync=" + sync +
                '}';
    }
}
