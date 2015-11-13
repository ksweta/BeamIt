package com.contactsharing.beamit.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by kumari on 10/24/15.
 */
public class BitmapUtility {
    private static final String TAG = BitmapUtility.class.getSimpleName();

    /**
     * This method convert bitmap to bytes.
     *
     * @param bitmap
     * @return
     */
    public static byte[] getBitmapToBytes(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    /**
     * This method converts bytes to bitmap object.
     *
     * @param bytes
     * @return
     */
    public static Bitmap getBytesToBitmap(byte[] bytes) {

        if (bytes == null || bytes.length == 0) {
            return null;
        }

        Bitmap bitmap =  BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        if(bitmap == null){
            Log.d(TAG, "bitmap is null");
        }
        return bitmap;
    }

    /**
     * This method compress the old bitmap to new size.
     * @param oldBitmap
     * @param newHeight
     * @param newWidth
     * @return
     */
    public static Bitmap getResizedBitmap(Bitmap oldBitmap, int newHeight, int newWidth) {

        int width = oldBitmap.getWidth();
        int height = oldBitmap.getHeight();
        float scaleWidth = ((float)newWidth)/width;
        float scaleHeight = ((float)newHeight)/height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(oldBitmap, 0, 0, width, height, matrix, false);
    }

    /**
     * This method stores the image in
     * @param context
     * @param bitmap
     * @param directory
     * @return
     */
    public static boolean storeImageToInternalStorage(Context context, Bitmap bitmap, String directory, String photoName ){
        File fileDirectory = new File(context.getApplicationContext().getExternalFilesDir(null), directory);
        if(!(fileDirectory.mkdirs() || fileDirectory.isDirectory())){
            Log.d(TAG, String.format("couldn't create directory: %s", directory));
            return false;
        }
        try {
            FileOutputStream out = new FileOutputStream(new File(fileDirectory, photoName));

            if (bitmap == null) {
                Log.e(TAG, "bitmap is null");
            }

            bitmap.compress(Bitmap.CompressFormat.JPEG,
                    100, //TODO: Need to
                    out);
            out.flush();
            out.close();
        } catch (Exception e) {
            Log.e(TAG, "Error while saving image", e);
            return false;
        }
        return true;
    }
}
