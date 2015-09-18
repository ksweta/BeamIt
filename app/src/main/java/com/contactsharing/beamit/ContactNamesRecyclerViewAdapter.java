package com.contactsharing.beamit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.contactsharing.beamit.db.DBHelper;
import com.contactsharing.beamit.model.ContactDetails;
import com.google.gson.Gson;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by kumari on 5/16/15.
 */
public class ContactNamesRecyclerViewAdapter extends RecyclerView.Adapter<ContactNamesRecyclerViewAdapter.ContactNamesViewHolder> {
    private final static String TAG = ContactNamesViewHolder.class.getSimpleName();
    public final static String EXTRA_MESSAGE = "com.contactsharing.beamit.ContactNamesRecyclerViewAdapter";
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
        return new ContactNamesViewHolder(mContext, inflatedView);
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
        Context context;

        public ContactNamesViewHolder(Context context, View itemView) {
            super(itemView);
            this.context = context;
            mContactNameTextView = (TextView) itemView.findViewById(R.id.contact_name_textview);
            mContactNameTextView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
//            Log.d(TAG, " Position: " + getPosition());
            Gson gson = new Gson();
            final ContactDetails contact = mContacts.get(getPosition());
//
//            AlertDialog alert = new AlertDialog.Builder(mContext).create();

//            alert.setTitle("Delete contact");
//            alert.setMessage("Do you really want to delete " + contact.getName() + "(" +contact.getPhone() +")?");
//            //alert.setIcon(R.drawable.delete);
//            alert.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
//
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    //If ok is clicked, delete the contact.
//                    if(mDb.deleteContact(contact) != 0) {
//                        mContacts.remove(contact);
//                        mDataSetChangeHandler.onDataSetChange();
//
//                        Toast.makeText(mContext, "Contact deleted", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(mContext, "Couldn't delete contact", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//
//            alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
//
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    //Do nothing.
//                }
//            });
//            alert.show();
            //String infoString = "Name: " +
            String json = gson.toJson(contact);

            Intent intent = new Intent(context, SendActivity.class);
            intent.putExtra(EXTRA_MESSAGE, json );
            context.startActivity(intent);



        }
    }
}