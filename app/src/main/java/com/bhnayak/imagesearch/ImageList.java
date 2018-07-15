package com.bhnayak.imagesearch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import java.util.ArrayList;

public class ImageList extends RecyclerView {
    private ImageListAdapter mAdapter;
    private String mCurrentTag = "random";
    private int mPage = 1;
    private OnScrollListener mOnScrollListener = new OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = mLayoutManager.getChildCount();
            int totalItemCount = mAdapter.getItemCount();
            int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();

            if (!mRequestInProgress) {
                if (firstVisibleItemPosition >= 0 && totalItemCount - (visibleItemCount + firstVisibleItemPosition) <=  getThreshold()) {
                    requestImages();
                }
            }
        }
    };
    private GridLayoutManager mLayoutManager;
    private IFetchImagesCompletionHandler mFetchImageCompletionHandler = new IFetchImagesCompletionHandler() {
        @Override
        public void onCompleted(ArrayList<ImageData> result) {
            if( result != null && !result.isEmpty() )
            {
                mPage++;
                addImages(result);
            }
            mRequestInProgress = false;
            mFetchImagesAsyncTask = null;

        }
    };
    private boolean mRequestInProgress = false;
    private FetchImagesAsyncTask mFetchImagesAsyncTask = null;

    public ImageList(@NonNull Context context) {
        super(context);
    }

    public ImageList(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageList(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void addImages(ArrayList<ImageData> images) {
        mAdapter.add(images);
    }

    private void requestImages()
    {
        try {
            String url = "https://api.flickr.com/services/rest/?method=flickr.photos.search&text=" + mCurrentTag + "&page=" + mPage;
            mFetchImagesAsyncTask = new FetchImagesAsyncTask();
            mFetchImagesAsyncTask.setCompletionHandler(mFetchImageCompletionHandler);
            mFetchImagesAsyncTask.execute(url);
            mRequestInProgress = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getThreshold() {
        return 50;
    }
    public void init()
    {
        mLayoutManager = new GridLayoutManager(getContext(), 3);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        this.setLayoutManager(mLayoutManager);
        this.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new ImageListAdapter( getContext(), new ArrayList<ImageData>());
        this.setAdapter(mAdapter);
        this.addOnScrollListener(mOnScrollListener);

    }

    public void setSearchTag( String tag )
    {
        mCurrentTag = tag;
        mPage = 1;
        mAdapter.clear();
        DownloadedImages.getInstance().clear();
        if( mFetchImagesAsyncTask != null )
            mFetchImagesAsyncTask.cancel(true);
        requestImages();
    }
}
