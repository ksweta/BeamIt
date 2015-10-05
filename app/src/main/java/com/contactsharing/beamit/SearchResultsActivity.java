package com.contactsharing.beamit;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

//public class SearchResultsActivity extends Activity {
//
//    private static final String TAG = SearchResultsActivity.class.getSimpleName();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_search_results);
//        handleIntent(getIntent());
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_search_results, menu);
//
////        // Get the SearchView and set the searchable configuration
////        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
////        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
////        // Assumes current activity is the searchable activity
////        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
////        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
//
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//
//    @Override
//    protected void onNewIntent(Intent intent) {
//
//        setIntent(intent);
//
//        handleIntent(intent);
//    }
//
//    private void handleIntent(Intent intent) {
//
//        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
//            String query = intent.getStringExtra(SearchManager.QUERY);
//            Log.d(TAG, "query: "+query);
//            //use the query to search your data somehow
//
//           // doMySearch(query);
//
////            Cursor phoneCursor = getContentResolver().query(intent.getData(), null, null, null, null);
////
////            phoneCursor.moveToFirst();
////            int idDisplayName = phoneCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
////            String name = phoneCursor.getString(idDisplayName);
////            phoneCursor.close();
////            Log.d(TAG, name);
//
//           // return name;
//
//            //Cursor cursor = db.rawQuery("select _id,city,code
//                    //FROM city where city_name like '"+s+"%' order by city", null);
//        }
//    }
//}
//
