package com.bhnayak.imagesearch;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageItemHolder extends RecyclerView.ViewHolder {
    private ImageView mImage;
    private DownloadImageTask mDownloadImageTask;

    public ImageItemHolder(@NonNull View itemView) {
        super(itemView);
        mImage = itemView.findViewById(R.id.image);
    }

    public void setProperties(ImageData imageData) {

        if (imageData == null) {
            mImage.setImageResource(android.R.drawable.ic_menu_gallery);
            return;
        }
        String url = "http://farm" + imageData.farm + ".static.flickr.com/" + imageData.server + "/" + imageData.id + "_" + imageData.secret + ".jpg";

        if (mDownloadImageTask != null) {
            mDownloadImageTask.cancel(true);
        }

        if( DownloadedImages.getInstance().contains(url) )
        {
            mImage.setImageBitmap(DownloadedImages.getInstance().get(url));
            mImage.setVisibility(View.VISIBLE);
        }
        else
        {
            mImage.setImageResource(android.R.drawable.ic_menu_gallery);
            mDownloadImageTask = new DownloadImageTask();
            mDownloadImageTask.execute(new ImageViewWraper(url, mImage));
        }

    }

    private class ImageViewWraper
    {
        String imageUrl;
        ImageView view;

        ImageViewWraper(String url, ImageView view) {
            this.imageUrl = url;
            this.view = view;
        }
    }
    private static class DownloadImageTask extends AsyncTask<ImageViewWraper, Void, Bitmap> {

        private ImageViewWraper mImageViewWraper;
        @Override
        protected Bitmap doInBackground(ImageViewWraper... params) {
            try {
                if(params[0] == null )
                    return null;
                mImageViewWraper = params[0];
                URL url = new URL(params[0].imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if(result!=null)
            {
                mImageViewWraper.view.setImageBitmap(result);
                mImageViewWraper.view.setVisibility(View.VISIBLE);
                DownloadedImages.getInstance().add(mImageViewWraper.imageUrl, result);
            }
        }

    }
}
