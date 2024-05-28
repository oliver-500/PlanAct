package org.unibl.etf.mr.planact.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class ImageLoadingTarget implements Target {

    @FunctionalInterface
    public interface MyFunction1 {
        void execute(int id, Bitmap bitmap);
    }



    @FunctionalInterface
    public interface MyFunction2 {
        void execute(int id);
    }

    @FunctionalInterface
    public interface MyFunction3 {
        void execute(int id);
    }

    private int imageId = -1;
    private static int numberOfImages = 0;
    private Context context;  // Add this if needed

    public ImageLoadingTarget(Context context, MyFunction1 onBitmapLoadedCustom, MyFunction2 onBitmapFailedCustom, MyFunction3 onPrepareLoadCustom) {
        this.context = context;
        this.onBitmapFailedCustom = onBitmapFailedCustom;
        this.onBitmapLoadedCustom = onBitmapLoadedCustom;
        this.onPrepareLoadCustom = onPrepareLoadCustom;
    }
    private MyFunction1 onBitmapLoadedCustom;
    private MyFunction2 onBitmapFailedCustom;
    private MyFunction3 onPrepareLoadCustom;


    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

        onBitmapLoadedCustom.execute(imageId, bitmap);
        numberOfImages++;
    }

    @Override
    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
        onBitmapFailedCustom.execute(imageId);
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
        // Save the imageId when preparing to load

        imageId = numberOfImages;
        onPrepareLoadCustom.execute(imageId);
        // ... Other code ...
    }
}



