package com.contactsharing.beamit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.contactsharing.beamit.db.DBHelper;
import com.contactsharing.beamit.model.ContactDetails;

import java.util.Collections;
import java.util.List;

/**
 * Created by kumari on 5/16/15.
 */
public class ContactNamesRecyclerViewAdapter extends RecyclerView.Adapter<ContactNamesRecyclerViewAdapter.ContactNamesViewHolder> {
    private final static String TAG = ContactNamesViewHolder.class.getSimpleName();
    private Context mContext;
    List<ContactDetails> mContacts;
    private DataSetChange mDataSetChangeHandler;
    private DBHelper mDb;

    public ContactNamesRecyclerViewAdapter(Context context, List<ContactDetails> contacts, DataSetChange dataSetChangeHandler, DBHelper db) {
        mContext = context;
        mContacts = contacts;
        mDataSetChangeHandler = dataSetChangeHandler;
        mDb = db;
    }

    @Override
    public ContactNamesViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View inflatedView = LayoutInflater.from(mContext).inflate(R.layout.contact_name_view, viewGroup, false);
        return new ContactNamesViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(ContactNamesViewHolder viewHolder, int i) {
        String catName = getItem(i);
        viewHolder.mContactNameTextView.setText(catName);
    }

    public String getItem(int position) {
        return mContacts.get(position).getName();
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }


    public class ContactNamesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView mContactNameTextView;

        public ContactNamesViewHolder(View itemView) {
            super(itemView);
            mContactNameTextView = (TextView) itemView.findViewById(R.id.contact_name_textview);
            mContactNameTextView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, " Position: " + getPosition());
            final ContactDetails contact = mContacts.get(getPosition());

            AlertDialog alert = new AlertDialog.Builder(mContext).create();

            alert.setTitle("Delete contact");
            alert.setMessage("Do you really want to delete " + contact.getName() + "(" +contact.getPhone() +")?");
            //alert.setIcon(R.drawable.delete);
            alert.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //If ok is clicked, delete the contact.
                    if(mDb.deleteContact(contact) != 0) {
                        mContacts.remove(contact);
                        mDataSetChangeHandler.onDataSetChange();

                        Toast.makeText(mContext, "Contact deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "Couldn't delete contact", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Do nothing.
                }
            });
            alert.show();

        }
    }
}