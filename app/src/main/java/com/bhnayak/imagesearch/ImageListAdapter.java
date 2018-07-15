package com.bhnayak.imagesearch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class ImageListAdapter extends RecyclerView.Adapter<ImageItemHolder> {

    private final Context mContext;
    private final ArrayList<ImageData> mImages;

    ImageListAdapter(Context context, ArrayList<ImageData> images) {
        this.mContext = context;
        this.mImages = images;
    }
    @NonNull
    @Override
    public ImageItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        return new ImageItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageItemHolder imageItemHolder, int position) {
        ImageData imageData = mImages.get(position);
        imageItemHolder.setProperties( imageData );
    }

    @Override
    public void onViewRecycled(@NonNull ImageItemHolder holder) {
        super.onViewRecycled(holder);
        //holder.setProperties(null);
    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }

    public void add( ArrayList<ImageData> images )
    {
        mImages.addAll( images );
        notifyItemRangeInserted( mImages.size() - images.size(), images.size()  );
    }

    public void remove(int position) {
        mImages.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        int size = mImages.size();
        mImages.clear();
        notifyItemRangeRemoved(0, size );
    }
}
