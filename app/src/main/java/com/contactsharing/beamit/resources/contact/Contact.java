package com.contactsharing.beamit.resources.contact;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kumari on 10/29/15.
 */
public class Contact {
    Integer id;
    @SerializedName("owner_id")
    Integer ownerId;
    String name;
    String phone;
    String email;
    String company;
    @SerializedName("linkedin_url")
    String linkedinUrl;

    public Contact(){
        //Required by the system.
    }

    public Contact(Integer id,
                   Integer ownerId,
                   String name,
                   String phone,
                   String email,
                   String company,
                   String linkedinUrl) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.company = company;
        this.linkedinUrl = linkedinUrl;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
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
        if (!(o instanceof Contact)) return false;

        Contact contact = (Contact) o;

        if (getId() != null ? !getId().equals(contact.getId()) : contact.getId() != null)
            return false;
        if (getOwnerId() != null ? !getOwnerId().equals(contact.getOwnerId()) : contact.getOwnerId() != null)
            return false;
        if (getName() != null ? !getName().equals(contact.getName()) : contact.getName() != null)
            return false;
        if (getPhone() != null ? !getPhone().equals(contact.getPhone()) : contact.getPhone() != null)
            return false;
        if (getEmail() != null ? !getEmail().equals(contact.getEmail()) : contact.getEmail() != null)
            return false;
        if (getCompany() != null ? !getCompany().equals(contact.getCompany()) : contact.getCompany() != null)
            return false;
        return !(getLinkedinUrl() != null ? !getLinkedinUrl().equals(contact.getLinkedinUrl()) : contact.getLinkedinUrl() != null);

    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getOwnerId() != null ? getOwnerId().hashCode() : 0);
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getPhone() != null ? getPhone().hashCode() : 0);
        result = 31 * result + (getEmail() != null ? getEmail().hashCode() : 0);
        result = 31 * result + (getCompany() != null ? getCompany().hashCode() : 0);
        result = 31 * result + (getLinkedinUrl() != null ? getLinkedinUrl().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "id=" + id +
                ", ownerId=" + ownerId +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", company='" + company + '\'' +
                ", linkedinUrl='" + linkedinUrl + '\'' +
                '}';
    }
}
