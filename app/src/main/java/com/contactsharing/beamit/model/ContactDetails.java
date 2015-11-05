package com.contactsharing.beamit.model;

/**
 * Created by kumari on 5/17/15.
 */
import android.graphics.Bitmap;

import com.contactsharing.beamit.resources.contact.Contact;
import com.contactsharing.beamit.resources.user.User;

public class ContactDetails implements Model {
    // This id will be provided by the SQLite.
    // It is auto-increment.
    private Integer id;
    private Integer contactId;
    private String name;
    private String phone;
    private String email;
    private String company;
    private String linkedinUrl;
    private Bitmap photo;
    // indicates whether contact is synced with server or not.
    private boolean synced;


    public ContactDetails() {
        //Required by the system.
    }

    public ContactDetails(Integer contactId,
                          String name,
                          String phone,
                          String email,
                          String company,
                          String linkedinUrl,
                          Bitmap photo,
                          boolean synced) {
        //Passing ID as zero, this value will be ignored later.
        this(0, contactId, name, phone, email, company, linkedinUrl, photo, synced);
    }

    public ContactDetails(Integer id,
                          Integer contactId,
                          String name,
                          String phone,
                          String email,
                          String company,
                          String linkedinUrl,
                          Bitmap photo,
                          boolean synced) {
        super();
        this.id = id;
        this.contactId = contactId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.company = company;
        this.linkedinUrl = linkedinUrl;
        this.photo = photo;
        this.synced = synced;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Bitmap getPhoto(){return photo;}

    public void setPhoto(Bitmap photo) { this.photo = photo; }

    public String getEmail(){ return email ; }

    public void setEmail(String email){
         this.email = email;
     }

    public void setCompany(String company){
        this.company = company;
    }

    public String getCompany(){
        return this.company;
    }

    public void setLinkedinUrl(String linkedinUrl){
        this.linkedinUrl = linkedinUrl;
    }

    public String getLinkedinUrl(){
        return this.linkedinUrl;
    }

    public Integer getContactId() {
        return contactId;
    }

    public void setContactId(Integer contactId) {
        this.contactId = contactId;
    }

    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }

    /**
     * This method convert `Contact` resource to ContactDetails object.
     * @param contact
     * @return
     */
    public static ContactDetails fromContact(Contact contact){
        ContactDetails contactDetails = new ContactDetails();
        contactDetails.setName(contact.getName());
        contactDetails.setEmail(contact.getEmail());
        contactDetails.setPhone(contact.getPhone());
        contactDetails.setCompany(contact.getCompany());
        contactDetails.setLinkedinUrl(contact.getLinkedinUrl());

        return contactDetails;
    }

    /**
     * This method convert `User` resource to ContactDetails object.
     * @param user
     * @return
     */
    public static ContactDetails fromUser(User user){
        ContactDetails contactDetails = new ContactDetails();
        contactDetails.setName(user.getName());
        contactDetails.setEmail(user.getEmail());
        contactDetails.setPhone(user.getPhone());
        contactDetails.setCompany(user.getCompany());
        contactDetails.setLinkedinUrl(user.getLinkedinUrl());

        return contactDetails;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContactDetails)) return false;

        ContactDetails that = (ContactDetails) o;

        if (synced != that.synced) return false;
        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        if (getContactId() != null ? !getContactId().equals(that.getContactId()) : that.getContactId() != null)
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
        result = 31 * result + (getContactId() != null ? getContactId().hashCode() : 0);
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getPhone() != null ? getPhone().hashCode() : 0);
        result = 31 * result + (getEmail() != null ? getEmail().hashCode() : 0);
        result = 31 * result + (getCompany() != null ? getCompany().hashCode() : 0);
        result = 31 * result + (getLinkedinUrl() != null ? getLinkedinUrl().hashCode() : 0);
        result = 31 * result + (synced ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ContactDetails{" +
                "id=" + id +
                ", contactId=" + contactId +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", company='" + company + '\'' +
                ", linkedinUrl='" + linkedinUrl + '\'' +
                ", synced=" + synced +
                '}';
    }
}
