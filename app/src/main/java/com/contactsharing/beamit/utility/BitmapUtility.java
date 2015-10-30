package com.contactsharing.beamit.utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.ByteArrayOutputStream;

/**
 * Created by kumari on 10/24/15.
 */
public class BitmapUtility {
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

        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

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
}
