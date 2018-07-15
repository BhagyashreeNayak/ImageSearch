package com.bhnayak.imagesearch;

import android.graphics.Bitmap;

import java.util.HashMap;

public class DownloadedImages {

    private HashMap<String,Bitmap> mDownloadedImages;
    private static DownloadedImages sInstance = null;

    private DownloadedImages()
    {
        mDownloadedImages = new HashMap<String, Bitmap>();
    }

    public static DownloadedImages getInstance()
    {
        if( sInstance == null )
        {
            sInstance = new DownloadedImages();
        }
        return sInstance;
    }

    public boolean contains( String key )
    {
        return mDownloadedImages.containsKey(key);
    }

    public void add( String url, Bitmap bitmap )
    {
        mDownloadedImages.put(url, bitmap );
    }

    public Bitmap get(String url) {
        return mDownloadedImages.get(url);
    }

    public void clear()
    {
        mDownloadedImages.clear();
    }
}
