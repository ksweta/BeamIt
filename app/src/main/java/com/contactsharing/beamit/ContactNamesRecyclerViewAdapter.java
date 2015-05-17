package com.contactsharing.beamit;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by kumari on 5/16/15.
 */
public class ContactNamesRecyclerViewAdapter extends RecyclerView.Adapter<ContactNamesRecyclerViewAdapter.ContactNamesViewHolder> {

    private Context mContext;
    List<String> mContactNames;

    public ContactNamesRecyclerViewAdapter(Context context) {
        mContext = context;
        randomizeCatNames();
    }

    public void randomizeCatNames() {
        mContactNames = Arrays.asList(getCatNamesResource());
        Collections.shuffle(mContactNames);
    }

    public class ContactNamesViewHolder extends RecyclerView.ViewHolder {
        TextView mContactNameTextView;

        public ContactNamesViewHolder(View itemView) {
            super(itemView);
            mContactNameTextView = (TextView) itemView.findViewById(R.id.contact_name_textview);
        }
    }

    private String[] getCatNamesResource() {
        return mContext.getResources().getStringArray(R.array.contact_names);
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
        return mContactNames.get(position);
    }

    @Override
    public int getItemCount() {
        return mContactNames.size();
    }
}