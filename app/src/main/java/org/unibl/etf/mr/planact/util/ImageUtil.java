package org.unibl.etf.mr.planact.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ImageUtil {
    public static Bitmap loadBitmapFromUri(Context context, Uri imageUri) {
        try {
            // Use ContentResolver to open an InputStream from the Uri
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);

            // Decode the InputStream into a Bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            // Close the InputStream
            if (inputStream != null) {
                inputStream.close();
            }

            return bitmap;
        } catch (IOException e) {

            return null;
        }
    }

 

    public static ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        // Calculate the number of bytes required to store the bitmap
        int byteCount = bitmap.getByteCount();



        // Allocate a ByteBuffer with the same size as the bitmap
        ByteBuffer byteBuffer = ByteBuffer.allocate(byteCount);

        // Copy the bitmap data into the ByteBuffer
        bitmap.copyPixelsToBuffer(byteBuffer);

        // Rewind the ByteBuffer to the beginning
        byteBuffer.rewind();

        return byteBuffer;
    }
    public static Bitmap createBitmapFromByteBuffer(ByteBuffer byteBuffer, int width, int height) {


        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(byteBuffer);


        return bitmap;
    }
}
