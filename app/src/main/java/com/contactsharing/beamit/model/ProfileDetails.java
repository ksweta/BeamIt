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

    //When profile detail was synced last time.
    private Date syncDate;

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

        this(null, userId, null, null, email, null, null, null, null);
    }

    public ProfileDetails(Long id,
                          Long userId,
                          String name,
                          String phone,
                          String email,
                          String company,
                          String linkedinUrl,
                          Bitmap photo,
                          Date syncDate) {
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
        this.syncDate = syncDate;
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

    public Date getSyncDate() {
        return syncDate;
    }

    public void setSyncDate(Date syncDate) {
        this.syncDate = syncDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProfileDetails)) return false;

        ProfileDetails that = (ProfileDetails) o;

        if (!getId().equals(that.getId())) return false;
        if (getUserId() != null ? !getUserId().equals(that.getUserId()) : that.getUserId() != null)
            return false;
        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null)
            return false;
        if (getPhone() != null ? !getPhone().equals(that.getPhone()) : that.getPhone() != null)
            return false;
        if (!getEmail().equals(that.getEmail())) return false;
        if (getCompany() != null ? !getCompany().equals(that.getCompany()) : that.getCompany() != null)
            return false;
        if (getLinkedinUrl() != null ? !getLinkedinUrl().equals(that.getLinkedinUrl()) : that.getLinkedinUrl() != null)
            return false;
        return !(getSyncDate() != null ? !getSyncDate().equals(that.getSyncDate()) : that.getSyncDate() != null);

    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + (getUserId() != null ? getUserId().hashCode() : 0);
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getPhone() != null ? getPhone().hashCode() : 0);
        result = 31 * result + getEmail().hashCode();
        result = 31 * result + (getCompany() != null ? getCompany().hashCode() : 0);
        result = 31 * result + (getLinkedinUrl() != null ? getLinkedinUrl().hashCode() : 0);
        result = 31 * result + (getSyncDate() != null ? getSyncDate().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ProfileDetails{" +
                "linkedinUrl='" + linkedinUrl + '\'' +
                ", company='" + company + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", name='" + name + '\'' +
                ", userId=" + userId +
                ", id=" + id +
                ", syncDate=" + syncDate +
                '}';
    }
}
