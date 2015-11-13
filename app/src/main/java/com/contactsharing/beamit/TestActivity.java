package com.contactsharing.beamit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.contactsharing.beamit.transport.BeamItService;
import com.contactsharing.beamit.transport.BeamItServiceTransport;
import com.contactsharing.beamit.utility.BitmapUtility;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.net.HttpURLConnection;

import retrofit.Call;
import retrofit.Response;

public class TestActivity extends ActionBarActivity {
    private static final String TAG = TestActivity.class.getSimpleName();
    private ImageView iv_test;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        iv_test = (ImageView) findViewById(R.id.iv_test);
    }

    @Override
    public void onNewIntent(Intent intent) {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test, menu);
        return true;
       // return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void onClick(View view){
     new TestAsyncTask().execute();

    }

    private class TestAsyncTask extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... voids) {

            BeamItService service = BeamItServiceTransport.getService();
            Call<ResponseBody> responseCall = service.downloadContactPhoto(12);
            Response<ResponseBody> response = null;
            Bitmap bitmap = null;
            try {
                response = responseCall.execute();
            } catch(IOException e){
                Log.e(TAG, "Coudln't get contact photo ",e);
            }
            if (response == null || response.code() != HttpURLConnection.HTTP_OK){
                Log.i(TAG, String.format("Coudln't get contact photo => code: %d", response.code()));
            } else {

                try {

                    bitmap =  BitmapUtility.getBytesToBitmap(response.body().bytes());

                    if(bitmap == null) {
                        Log.d(TAG, "bitmap is still null");
                    } else {
                        Log.d(TAG, "bitmap is not null");
                    }

                } catch (IOException e) {
                    Log.e(TAG, "couldn't fetch image", e);
                }
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap){
            iv_test.setImageBitmap(bitmap);
        }
    }
}
