package com.example.cristianoyl.restaurant.services;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.cristianoyl.restaurant.R;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by CristianoYL on 11/19/17.
 */

public class ImageLoader extends AsyncTask<Void, Void, Bitmap> {

    private static final String TAG = "ImageLoader";
    private static final String FORMAT_JPG = ".jpg";
    private static final String FORMAT_PNG = ".png";
    private static final String FORMAT_GIF = ".gif";
    private static final String FORMAT_JPEG = ".jpeg";

    String url;
    int resId;
    ImageView imageView;
    Context context;
    boolean isResource;
    int width, height;

    /**
     * Static method, used to load an image from a URL on a separate thread.
     * If the {@param width} and {@param height} are provided, the image will be scaled
     * according to the dimension before loading into memory, thus improve the performance.
     *
     * @param imageView the ImageView to display the image
     * @param url       the url of the image to display
     * @param context   context of the activity
     * @param width     requested width of the image
     * @param height    requested height of the image
     */
    public static void load(ImageView imageView, String url, Context context, int width, int height) {
        new ImageLoader(imageView, url, context, width, height);
    }

    /**
     *  Overloading static method, used to load an image from a URL on a separate thread without
     *  specifying the ImageView dimensions, thus no performance optimization.
     */
    public static void load(ImageView imageView, String url, Context context) {
        new ImageLoader(imageView, url, context);
    }

    /**
     * Static method, used to load an image from a URL on a separate thread.
     * If the {@param width} and {@param height} are provided, the image will be scaled
     * according to the dimension before loading into memory, thus improve the performance.
     *
     * @param imageView the ImageView to display the image
     * @param resId the resource id of the image to display
     * @param context   context of the activity
     * @param width requested width of the image
     * @param height    requested height of the image
     */
    public static void load(ImageView imageView, int resId, Context context, int width, int height) {
        new ImageLoader(imageView, resId, context, width, height);
    }

    /**
     *  Overloading static method, used to load an image from resource on a separate thread without
     *  specifying the ImageView dimensions, thus no performance optimization.
     */
    public static void load(ImageView imageView, int resId, Context context) {
        new ImageLoader(imageView, resId, context);
    }

    /**
     * Constructor for ImageLoader for url-specified image
     * @param imageView the ImageView to display the image
     * @param url       the url of the image to display
     * @param context   context of the activity
     * @param width     requested width of the image
     * @param height    requested height of the image
     */
    private ImageLoader(ImageView imageView, String url, Context context, int width, int height) {
        this.imageView = imageView;
        this.url = url;
        this.context = context;
        this.isResource = false;
        this.width = width;
        this.height = height;
        Log.d(TAG, "Load image from URL: " + url);
        this.execute();
    }

    /**
     * Constructor for ImageLoader for resource-specified image
     * @param imageView the ImageView to display the image
     * @param resId the resource id of the image to display
     * @param context   context of the activity
     * @param width     requested width of the image
     * @param height    requested height of the image
     */
    private ImageLoader(ImageView imageView, int resId, Context context, int width, int height) {
        this.imageView = imageView;
        this.resId = resId;
        this.context = context;
        this.isResource = true;
        this.width = width;
        this.height = height;
        this.execute();
    }

    /**
     *  Overloading with default dimension
     */
    private ImageLoader(ImageView imageView, String url, Context context) {
        this(imageView, url, context, 0, 0);
    }

    /**
     *  Overloading
     */
    private ImageLoader(ImageView imageView, int resId, Context context) {
        this(imageView, resId, context, 0, 0);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
//        if ( progressBar != null ) {
//            progressBar.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        // try to use local cache first
//        bitmap = this.dbHelper.getCachedImage(url);
//        if ( bitmap != null ) {
//            Log.d(TAG,"Use cached image for " + url);
//            return bitmap;
//        }
        // if no cache found, load from internet
        Log.d(TAG, "Load image from internet.");
        return loadImage();
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
//        if ( progressBar != null ) {
//            progressBar.setVisibility(View.INVISIBLE);
//        }
        if (imageView.getVisibility() == View.VISIBLE) {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                // use default image
                Log.e(TAG, "Failed to load image.");
                imageView.setImageResource(R.drawable.food);
            }
        }
    }

    /**
     * if a width and a height are provided, we will try to scale the image according to the
     * dimension and then load the scaled version into the memory.
     */
    private Bitmap loadImage() {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        URL url = null;
        if (this.width > 0 && this.height > 0) {
            // First decode with inJustDecodeBounds=true to check dimensions
            options.inJustDecodeBounds = true;
            if (this.isResource) {
                BitmapFactory.decodeResource(this.context.getResources(), this.resId, options);
            } else {
                try {
                    url = new URL(this.url);
                    BitmapFactory.decodeStream(url.openConnection().getInputStream(), null, options);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            options.inSampleSize = calculateInSampleSize(options, this.width, this.height);
            options.inJustDecodeBounds = false;
            // now that options.inSampleSize is set, the image will be scaled when downloading (if option is used)
        }
        if (this.isResource) {
            return BitmapFactory.decodeResource(this.context.getResources(), this.resId, options);
        } else {
            try {
                url = new URL(this.url);
                return BitmapFactory.decodeStream(url.openConnection().getInputStream(), null, options);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * ***Important***
     * Before calling this method, use BitmapFactory to decode the image with options.inJustDecodeBounds = true first to
     * only retrieve the dimension of the original image, and the dimension gets stored into the
     * options automatically.
     * ****************
     * <p>
     * This method will calculate the ratio of the original dimension and requested dimension of
     * the image and set the proper scale into the options.inSampleSize. Next time when using
     * the BitmapFactory to decode the image with these options, the image will be scaled and then
     * loaded into the memory for better performance.
     *
     * @param options   BitmapFactory decoding options
     * @param reqWidth  requested view width
     * @param reqHeight requested view height
     * @return  scale of the requested image compared to the original one
     */
    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        Log.d(TAG, "Image scale: " + inSampleSize);
        return inSampleSize;
    }
}
