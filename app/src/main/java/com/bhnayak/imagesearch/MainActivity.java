package com.bhnayak.imagesearch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageList mImageList;
    private ProgressBar mProgressBar;
    private TextView mTrySearch;
    private SearchView mSearchView;
    private SearchView.OnQueryTextListener mSearchListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String str) {
            if( mFetchMessagesAsyncTask != null )
            {
                mFetchMessagesAsyncTask.cancel(true);
            }
            mImageList.setSearchTag(str);
            mSearchView.clearFocus();
            return true;
        }

        @Override
        public boolean onQueryTextChange(String str) {
            return false;
        }
    };
    private IFetchImagesCompletionHandler mFetchMessageCompletionHandler = new IFetchImagesCompletionHandler() {
        @Override
        public void onCompleted(ArrayList<ImageData> result) {
            mFetchMessagesAsyncTask = null;
            if( result == null || result.size() <= 0 )
            {
                mProgressBar.setVisibility(View.GONE);
                mTrySearch.setVisibility(View.VISIBLE);
                mImageList.setVisibility(View.GONE);
            }
            else
            {
                mProgressBar.setVisibility(View.GONE);
                mTrySearch.setVisibility(View.GONE);
                mImageList.setVisibility(View.VISIBLE);
                mImageList.addImages(result);
            }
        }
    };
    private FetchImagesAsyncTask mFetchMessagesAsyncTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mImageList = (ImageList) findViewById(R.id.imageList);
        mProgressBar = (ProgressBar )findViewById(R.id.progress);
        mTrySearch = (TextView)findViewById(R.id.trySearch);
        mImageList.init();
        loadRandomImages();
        setSupportActionBar(mToolbar);
    }

    private void loadRandomImages() {
        try {
            mFetchMessagesAsyncTask = new FetchImagesAsyncTask();
            mFetchMessagesAsyncTask.setCompletionHandler(mFetchMessageCompletionHandler);
            mFetchMessagesAsyncTask.execute("https://api.flickr.com/services/rest/?method=flickr.photos.getRecent&format=rest");
        } catch (Exception e) {
            e.printStackTrace();
            mImageList.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchView.setOnQueryTextListener(mSearchListener);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
