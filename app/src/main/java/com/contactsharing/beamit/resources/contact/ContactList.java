package com.contactsharing.beamit.resources.contact;

import java.util.List;

/**
 * Created by kumari on 11/8/15.
 */
public class ContactList {
    private List<Contact> contacts;
    private Integer totalCount;
    private Integer offset;
    private Integer limit;

    public ContactList(){
        //Required by system.
    }

    public ContactList(List<Contact> contacts, Integer totalCount, Integer offset, Integer limit){
        this.contacts = contacts;
        this.totalCount = totalCount;
        this.offset = offset;
        this.limit = limit;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContactList)) return false;

        ContactList that = (ContactList) o;

        if (getContacts() != null ? !getContacts().equals(that.getContacts()) : that.getContacts() != null)
            return false;
        if (getTotalCount() != null ? !getTotalCount().equals(that.getTotalCount()) : that.getTotalCount() != null)
            return false;
        if (getOffset() != null ? !getOffset().equals(that.getOffset()) : that.getOffset() != null)
            return false;
        return !(getLimit() != null ? !getLimit().equals(that.getLimit()) : that.getLimit() != null);

    }

    @Override
    public int hashCode() {
        int result = getContacts() != null ? getContacts().hashCode() : 0;
        result = 31 * result + (getTotalCount() != null ? getTotalCount().hashCode() : 0);
        result = 31 * result + (getOffset() != null ? getOffset().hashCode() : 0);
        result = 31 * result + (getLimit() != null ? getLimit().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ContactList{" +
                "contacts=" + contacts +
                ", totalCount=" + totalCount +
                ", offset=" + offset +
                ", limit=" + limit +
                '}';
    }
}
