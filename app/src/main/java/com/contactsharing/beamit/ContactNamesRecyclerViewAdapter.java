package com.contactsharing.beamit;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.contactsharing.beamit.db.DBHelper;
import com.contactsharing.beamit.model.ContactDetails;
import com.contactsharing.beamit.utility.ApplicationConstants;
import com.contactsharing.beamit.utility.JsonConverter;

import java.util.List;

/**
 * Created by kumari on 5/16/15.
 */
public class ContactNamesRecyclerViewAdapter
        extends RecyclerView.Adapter<ContactNamesRecyclerViewAdapter.ContactDetailsViewHolder> implements View.OnClickListener {
    private final static String TAG = ContactNamesRecyclerViewAdapter.class.getSimpleName();
    public final static String EXTRA_MESSAGE = "com.contactsharing.beamit.ContactNamesRecyclerViewAdapter";
    private List<ContactDetails> mContacts;
    private DataSetChange mDataSetChangeHandler;
    private DBHelper mDb;
    private int mItemLayout;
    private OnRecyclerViewItemClickListener<ContactDetails> mItemClickListener;

    public ContactNamesRecyclerViewAdapter(List<ContactDetails> contacts, int itemLayout, DBHelper db) {
        mContacts = contacts;
        mItemLayout = itemLayout;
        mDb = db;
    }

    @Override
    public ContactDetailsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View inflatedView = LayoutInflater.from(viewGroup.getContext()).inflate(mItemLayout, viewGroup, false);
        return new ContactDetailsViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(ContactDetailsViewHolder viewHolder, int i) {
        ContactDetails contact = mContacts.get(i);
        viewHolder.mTvContactName.setText(contact.getName());
        viewHolder.mTvContactPhone.setText(contact.getPhone());
        viewHolder.mTvContactCompany.setText(contact.getCompany());
        viewHolder.mLinkedinUrl.setText(contact.getLinkedinUrl());
        if (contact.getPhoto() != null) {
            viewHolder.mIvContactImage.setImageBitmap(contact.getPhoto());
        } else {
            viewHolder.mIvContactImage.setImageResource(R.drawable.default_contact_icon);
        }

        viewHolder.contactDetails = contact;
    }

    public String getItem(int position) {
        return mContacts.get(position).getName();
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    @Override
    public void onClick(View view){
        if(mItemClickListener != null) {

            ContactDetails contactDetails = (ContactDetails) view.getTag();
            mItemClickListener.onItemClick(view, contactDetails);
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener<ContactDetails> listener){
        mItemClickListener = listener;
    }

    public void remove(ContactDetails contactDetails) {
        int position = mContacts.indexOf(contactDetails);
        if (position > -1) {
            mContacts.remove(position);
            notifyItemRemoved(position);
        }
    }

    public boolean add(ContactDetails contactDetails) {
        int position = mContacts.size();
        Integer contactId = mDb.insertContact(contactDetails);
        Log.d(TAG, String.format("contactId: %d", contactId));
        if(contactId > -1) {
            contactDetails.setId(contactId);
            mContacts.add(position, contactDetails);
            notifyItemInserted(position);
           return true;
        }

        return false;
    }

    public boolean add(ContactDetails contactDetails, int position){
        Integer contactId = mDb.insertContact(contactDetails);
        if(contactId > -1) {
            contactDetails.setId(contactId);
            mContacts.add(position, contactDetails);
            notifyItemInserted(position);
            return true;
        }
        return false;
    }

    /**
     * View Holder class
     */
    public class ContactDetailsViewHolder extends RecyclerView.ViewHolder {
        public ImageView mIvContactImage;
        public TextView mTvContactName;
        public TextView mTvContactPhone;
        public TextView mTvContactCompany;
        public TextView mLinkedinUrl;
        public  ContactDetails contactDetails;

        public ContactDetailsViewHolder(View itemView) {
            super(itemView);
            mIvContactImage = (ImageView) itemView.findViewById(R.id.iv_contact_item_photo);
            mTvContactName = (TextView) itemView.findViewById(R.id.tv_contact_item_name);
            mTvContactPhone = (TextView) itemView.findViewById(R.id.tv_contact_item_phone);
            mTvContactCompany = (TextView) itemView.findViewById(R.id.tv_contact_item_company);
            mLinkedinUrl = (TextView) itemView.findViewById(R.id.tv_contact_item_linkedinurl);
            itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), DisplayCardActivity.class);
                    intent.putExtra(ApplicationConstants.EXTRA_CONTACT_LOCAL_ID,
                            contactDetails.getId());
                    view.getContext().startActivity(intent);
                }
            });
        }
    }
}