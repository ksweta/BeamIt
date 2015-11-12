package com.contactsharing.beamit.resources.share;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kumari on 11/11/15.
 */
public class ShareContactRequest {
    @SerializedName("owner_id")
    Integer ownerId;
    @SerializedName("subject_id")
    Integer subjectId;

    public ShareContactRequest(){
        //Required by the system
    }

    public ShareContactRequest(Integer ownerId, Integer subjectId){
        this.ownerId = ownerId;
        this.subjectId = subjectId;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShareContactRequest)) return false;

        ShareContactRequest that = (ShareContactRequest) o;

        if (getOwnerId() != null ? !getOwnerId().equals(that.getOwnerId()) : that.getOwnerId() != null)
            return false;
        return !(getSubjectId() != null ? !getSubjectId().equals(that.getSubjectId()) : that.getSubjectId() != null);

    }

    @Override
    public int hashCode() {
        int result = getOwnerId() != null ? getOwnerId().hashCode() : 0;
        result = 31 * result + (getSubjectId() != null ? getSubjectId().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ShareContactRequest{" +
                "ownerId=" + ownerId +
                ", subjectId=" + subjectId +
                '}';
    }
}
