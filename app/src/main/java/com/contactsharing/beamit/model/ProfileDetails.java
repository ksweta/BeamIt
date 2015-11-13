package com.contactsharing.beamit.model;

import com.contactsharing.beamit.resources.user.User;

/**
 * Created by Kumari on 10/24/15.
 */
public class ProfileDetails {

    //This id field is used by sqlite.
    private Integer id;
    //This id is provided by the server when app register with server.
    private Integer userId;
    private String name;
    private String phone;
    private String email;
    private String company;
    private String linkedinUrl;
    private String photoUri;

    //indicates whether profile details are sync with server or not.
    private boolean sync;

    public ProfileDetails(){
        //Required by the system.
        this.id = 1;
    }

    /**
     * Helper constructor for signup activity.
     * @param userId
     * @param email
     */
    public ProfileDetails(Integer userId, String email){

        this(null, userId, null, null, email, null, null, null, false);
    }

    public ProfileDetails(Integer id,
                          Integer userId,
                          String name,
                          String phone,
                          String email,
                          String company,
                          String linkedinUrl,
                          String photoUri,
                          boolean sync) {
        super();
        if (id == null) {
            this.id = 1;
        } else {
            this.id = id;
        }
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.company = company;
        this.linkedinUrl = linkedinUrl;
        this.photoUri = photoUri;
        this.sync = sync;
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
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

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public boolean isSync() {
        return sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }


    public User toUser(){
        User user = new User();
        user.setId(getUserId());
        user.setEmail(getEmail());
        user.setName(getName());
        user.setPhone(getPhone());
        user.setCompany(getCompany());
        user.setLinkedinUrl(getLinkedinUrl());
        return user;
    }

    public static ProfileDetails fromUser(User user){
        return new ProfileDetails(null,
                user.getId(),
                user.getName(),
                user.getPhone(),
                user.getEmail(),
                user.getCompany(),
                user.getLinkedinUrl(),
                null, // Photo uri may not be available so set to null.
                true);
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
