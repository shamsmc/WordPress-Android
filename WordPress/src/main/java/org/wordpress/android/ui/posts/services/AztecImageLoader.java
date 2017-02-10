package org.wordpress.android.ui.posts.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import org.wordpress.android.WordPress;
import org.wordpress.android.editor.Utils;
import org.wordpress.aztec.Html;

import java.io.File;

public class AztecImageLoader implements Html.ImageGetter {

    private Context context;

    public AztecImageLoader(Context context) {
        this.context = context;
    }
    @Override
    public void loadImage(String url, final Callbacks callbacks, int maxWidth) {
        // TODO: if a local file then load it directly. This is a quick fix though.
        if (new File(url).exists()) {
            // Load a correctly scaled bitmap
            Bitmap bitmap = Utils.decodeFileAs160dp(url);

            BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);
            callbacks.onImageLoaded(bitmapDrawable);
            return;
        }

        WordPress.imageLoader.get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                Bitmap bitmap = response.getBitmap();

                if (bitmap == null) {
                    // the loader tries to let us know to just use the default image for now
                    callbacks.onUseDefaultImage();
                } else {
                    // Volley doesn't set the density to default (160) so let's do it ourselves
                    Utils.adjustToDensity160dp(bitmap);

                    BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);
                    callbacks.onImageLoaded(bitmapDrawable);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                callbacks.onImageLoadingFailed();
            }
        }, maxWidth, 0);
    }
}
