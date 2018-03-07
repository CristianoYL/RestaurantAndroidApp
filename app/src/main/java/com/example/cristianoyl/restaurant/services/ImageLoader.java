package com.example.cristianoyl.restaurant.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.cristianoyl.restaurant.R;
import com.example.cristianoyl.restaurant.utils.Constants;
import com.example.cristianoyl.restaurant.utils.LocalDBHelper;

import java.util.ArrayList;
import java.util.HashSet;

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
//    ProgressBar progressBar;
    LocalDBHelper dbHelper;
//    MyAmazonS3Service myAmazonS3Service;
//    MyAmazonS3Service.OnUploadResultListener listener;
    Bitmap bitmap;

    public ImageLoader(ImageView imageView, String url, Context context){
        this.imageView = imageView;
//        this.progressBar = progressBar;
        this.url = url;
        this.context = context;
    }

    public ImageLoader(ImageView imageView, int resId, Context context){
        this.imageView = imageView;
//        this.progressBar = progressBar;
        this.resId = resId;
        this.context = context;
    }

    public void loadImage(){
        this.execute();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
//        if ( progressBar != null ) {
//            progressBar.setVisibility(View.VISIBLE);
//        }
//        this.imageView.setImageResource(R.drawable.ic_image_default_background);
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
        Log.d(TAG,"Load image from internet.");
        if ( url != null ) {
            // load from url
        } else {
            // load from res
            bitmap = decodeSampledBitmapFromResource(context.getResources(),resId,300,200);
        }
        // load from S3
        //Log.d(TAG,"Load image from AWS S3.");
//        listener = new MyAmazonS3Service.OnUploadResultListener() {
//            @Override
//            public void onFinished(int responseCode, String message) {
//                if ( responseCode == 200 ) {
//                    bitmap = dbHelper.getCachedImage(url);
//                    imageView.setImageBitmap(bitmap);
//                    Log.d(TAG,message);
//                } else {
//                    Log.e(TAG,"Failed to load image:"+url);
//                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
//                }
//            }
//        };
//        this.myAmazonS3Service = new MyAmazonS3Service(context,listener);
//        myAmazonS3Service.downloadFromS3(url,true);
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
//        if ( progressBar != null ) {
//            progressBar.setVisibility(View.INVISIBLE);
//        }
        if ( bitmap != null && imageView.getVisibility() == View.VISIBLE ) {
            imageView.setImageBitmap(bitmap);
        }
    }
    private static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {


        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

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

        return inSampleSize;
    }
}
