package com.contactsharing.beamit.model;

/**
 * Created by kumari on 5/17/15.
 */
import java.util.Date;

public class ContactDetails {
    //This id will be provided by the SQLite.
    //It is auto-increment.
    private Long id;

    //Recipient name.
    private String name;

    //Recipient Phone number.
    private String phone;

    //Recipient email id.
    private String email;

    //'true' if the phone number is registered with server
    // Otherwise 'false'
    private Boolean registered;

    //When this contact was last synced with server.
    private Date syncDate;

    public ContactDetails() {
        //Required by the system.
    }
    public ContactDetails(String name,
                          String phone,
                          String email,
                          Boolean registered,
                          Date syncDate) {
        //Passing ID as zero, this value will be ignored later.
        this(0L, name, phone, email, registered, syncDate);
    }

    public ContactDetails(Long id,
                          String name,
                          String phone,
                          String email,
                          Boolean registered,
                          Date syncDate) {
        super();
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.registered = registered;
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

    public String getEmail(){ return email ; }

    public void setEmail(String email){
         this.email = email;
     }

    public Boolean getRegistered() {
        return registered;
    }

    public void setRegistered(Boolean registered) {
        this.registered = registered;
    }

    public boolean isRegistered() {
        return this.registered;
    }
    public Date getSyncDate() {
        return syncDate;
    }

    public void setSyncDate(Date syncDate) {
        this.syncDate = syncDate;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((phone == null) ? 0 : phone.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ContactDetails other = (ContactDetails) obj;
        if (phone == null) {
            if (other.phone != null)
                return false;
        } else if (!phone.equals(other.phone))
            return false;
        return true;
    }
    @Override
    public String toString() {
        return "ContactDetails [id=" + id + ", name=" + name + ", phone="
                + phone + ", registered=" + registered + ", syncDate="
                + syncDate + "]";
    }
}
