package com.yathams.loginsystem.pojo;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;

/**
 * Created by vyatham on 17/03/16.
 */
public class ImageItem {

    public ImageItem(Uri uri) {
        this.uri = uri;
    }

    public Bitmap bitmap;

    public ImageItem(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Uri uri;
}
