package com.contactsharing.beamit;

import android.view.View;

/**
 * Created by Kumari on 10/21/15.
 */
public interface OnRecyclerViewItemClickListener<Model> {
    void onItemClick(View view, Model model);
}
