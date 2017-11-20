package com.example.cristianoyl.restaurant.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

/**
 * Created by CristianoYL on 11/19/17.
 */

public class LocalDBHelper extends SQLiteOpenHelper {

    public static final String TAG = "LocalDBHelper";

    public static final String LOCAL_DATABASE_NAME = "restaurant.db";   // db name
    public static final String TABLE_IMAGE = "IMAGE_CACHE"; // table name
    public static final String IMAGE_COL_URL = "url";   // the url of the image
    public static final String IMAGE_COL_LOCAL_PATH = "path";   // the local absolute path

    private static LocalDBHelper instance;

    Context context;
    SQLiteDatabase db;

    private LocalDBHelper(Context context) {
        super(context, LOCAL_DATABASE_NAME, null, 1);
        this.db = this.getWritableDatabase();
        this.context = context;
    }

    public void init(Context context) throws Exception {
        if ( instance != null ) {
            throw new Exception(TAG+" already initiated!");
        } else {
            instance = new LocalDBHelper(context.getApplicationContext());
        }
    }

    public static LocalDBHelper getInstance() throws Exception {
        if ( instance == null ) {
            throw new Exception(TAG + "haven't been initiated.");
        } else {
            return instance;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_IMAGE + " ("
                + IMAGE_COL_URL + " TEXT PRIMARY KEY, "
                + IMAGE_COL_LOCAL_PATH + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGE);
        onCreate(db);
    }

    public Bitmap getCachedImage(String url) {
        String sql = "SELECT * FROM "+ TABLE_IMAGE + " WHERE " + IMAGE_COL_URL + "=?";
        Cursor cursor = db.rawQuery(sql ,new String[] {url});
        if ( cursor.getCount() == 1 ) {
            cursor.moveToFirst();
            String localPath = cursor.getString(1);  // col 1 is bitmap
            cursor.close();
            Log.d(TAG,"Use cache:" + localPath);
            return BitmapFactory.decodeFile(localPath);
        } else {
            Log.d(TAG,"No cached result");
            return null;
        }
    }

    /**
     *  Cache the web image to local DB.
     * @param url the web url of the image
     * @param localPath the local path to cache the image
     * @return whether the operation is successful
     */
    public boolean cacheImage(String url, String localPath) {
        ContentValues cv = new  ContentValues();
        cv.put(IMAGE_COL_URL,url);
        cv.put(IMAGE_COL_LOCAL_PATH,localPath);
        if ( db.update(TABLE_IMAGE,cv,IMAGE_COL_URL+"=?",new String[]{url}) != 1 ) {    // there should be only 1 affected entry if update successfully
            Log.d(TAG,"Image cached for " + url +" does not exist, try to insert");
            if ( db.insert(TABLE_IMAGE, null, cv) != -1 ) {
                Log.d(TAG,"Image "+url+" cached at " + localPath);
                return true;
            }
        } else {    // update success
            Log.d(TAG,"Update image "+url+" cache to " + localPath);
            return true;
        }
        Log.e(TAG,"Failed to cache image "+url+" cached at " + localPath);
        return false;
    }

    /**
     * clear all cached images files and DB entries
     */
    public void clearAllImageCache(){
        clearImageCache(null);
    }

    /**
     *  clear the local cached image file as well as local DB entry.
     *  If no url specified, clear all caches
     * @param url the url of the cached image to clear
     */
    public void clearImageCache(String url){
        Cursor cursor;
        if ( url == null ) {    // delete all cached images
            cursor = db.rawQuery("SELECT * FROM "+ TABLE_IMAGE,null);
        } else {    // delete specified cache
            String sql = "SELECT * FROM "+ TABLE_IMAGE +" WHERE " + IMAGE_COL_URL + "=?";
            cursor = db.rawQuery(sql,new String[]{url});
        }
        // delete local files
        if ( cursor != null && cursor.getCount() > 0 ) {
            for ( int i = 0; i < cursor.getCount(); i++ ) {
                cursor.moveToNext();
                String path = cursor.getString(1);
                File file = new File(path);
                if ( file.exists() ) {
                    if ( !file.delete() ) {
                        Log.e(TAG,"Delete file<"+path+"> failed.");
                    } else {
                        Log.d(TAG,"Delete file<"+path+">.");
                    }
                }
            }
            cursor.close();
        }
        // delete DB entries
        Toast.makeText(context, "Cleared " + db.delete(TABLE_IMAGE,"1",null) +
                "  cached image(s)", Toast.LENGTH_SHORT).show();
    }
}
