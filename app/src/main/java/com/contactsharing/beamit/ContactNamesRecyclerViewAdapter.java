package com.contactsharing.beamit;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.contactsharing.beamit.db.DBHelper;
import com.contactsharing.beamit.model.ContactDetails;

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
    private OnRecyclerViewItemClickListener<ContactDetails> mItemClickListener;

    public ContactNamesRecyclerViewAdapter(List<ContactDetails> contacts, DBHelper db) {
        mContacts = contacts;
        mDb = db;
    }

    @Override
    public ContactDetailsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View inflatedView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contact_list_item, viewGroup, false);
        return new ContactDetailsViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(ContactDetailsViewHolder viewHolder, int i) {
        ContactDetails contact = mContacts.get(i);
        viewHolder.mTvContactName.setText(contact.getName());
        viewHolder.mTvContactEmail.setText(contact.getEmail());
        viewHolder.mTvContactCompany.setText(contact.getCompany());
        viewHolder.mLinkedinUrl.setText(contact.getLinkedinUrl());
        if (contact.getPhoto() != null) {
            viewHolder.mIvContactImage.setImageBitmap(contact.getPhoto());
        } else {
            viewHolder.mIvContactImage.setImageResource(R.drawable.default_contact_icon);
        }
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
        long contactId = mDb.insertContact(contactDetails);
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
        long contactId = mDb.insertContact(contactDetails);
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
    public class ContactDetailsViewHolder extends RecyclerView.ViewHolder{
        public ImageView mIvContactImage;
        public TextView mTvContactName;
        public TextView mTvContactEmail;
        public TextView mTvContactCompany;
        public TextView mLinkedinUrl;

        public ContactDetailsViewHolder(View itemView) {
            super(itemView);
            mIvContactImage = (ImageView) itemView.findViewById(R.id.iv_contact_item_photo);
            mTvContactName = (TextView) itemView.findViewById(R.id.tv_contact_item_name);
            mTvContactEmail = (TextView) itemView.findViewById(R.id.tv_contact_item_email);
            mTvContactCompany = (TextView) itemView.findViewById(R.id.tv_contact_item_company);
            mLinkedinUrl = (TextView) itemView.findViewById(R.id.tv_contact_item_linkedinurl);
        }
    }
}