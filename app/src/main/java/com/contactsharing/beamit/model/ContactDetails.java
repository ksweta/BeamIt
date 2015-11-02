package com.contactsharing.beamit.model;

/**
 * Created by kumari on 5/17/15.
 */
import android.graphics.Bitmap;

import java.util.Date;

public class ContactDetails implements Model {
    // This id will be provided by the SQLite.
    // It is auto-increment.
    private Long id;
    private Long contactId;
    private String name;
    private String phone;
    private String email;
    private String company;
    private String linkedinUrl;
    private Bitmap photo;

    //When this contact was last synced with server.
    private Date syncDate;

    public ContactDetails() {
        //Required by the system.
    }

    public ContactDetails(Long contactId,
                          String name,
                          String phone,
                          String email,
                          String company,
                          String linkedinUrl,
                          Bitmap photo,
                          Date syncDate) {
        //Passing ID as zero, this value will be ignored later.
        this(0L, contactId, name, phone, email, company, linkedinUrl, photo, syncDate);
    }

    public ContactDetails(Long id,
                          Long contactId,
                          String name,
                          String phone,
                          String email,
                          String company,
                          String linkedinUrl,
                          Bitmap photo,
                          Date syncDate) {
        super();
        this.id = id;
        this.contactId = contactId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.company = company;
        this.linkedinUrl = linkedinUrl;
        this.photo = photo;
        this.syncDate = syncDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Date getSyncDate() { return syncDate; }

    public void setSyncDate(Date syncDate) { this.syncDate = syncDate;}


    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContactDetails)) return false;

        ContactDetails that = (ContactDetails) o;

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
        if (getLinkedinUrl() != null ? !getLinkedinUrl().equals(that.getLinkedinUrl()) : that.getLinkedinUrl() != null)
            return false;
        return !(getSyncDate() != null ? !getSyncDate().equals(that.getSyncDate()) : that.getSyncDate() != null);

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
        result = 31 * result + (getSyncDate() != null ? getSyncDate().hashCode() : 0);
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
                ", syncDate=" + syncDate +
                '}';
    }
}
