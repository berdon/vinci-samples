package com.vinci.samples.simple;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;

import com.vinci.Bucket;
import com.vinci.BucketListener;
import com.vinci.Vinci;
import com.vinci.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * TODO : austinh : JavaDocs
 */
public class MainActivity extends Activity {
    private static final String DEFAULT_IMAGE = "http://lorempixel.com/gray/%d/%d/abstract/";
    private static final String DEFAULT_PATH = "/sdcard/simple/cache-defaults";
    private static final String CACHE_PATH = "/sdcard/simple/cache";

    private int mImageSize;
    private String mDefaultImagePath;

    private static final List<String> IMAGE_TYPES = new LinkedList<String>();

    static {
        IMAGE_TYPES.add("sports");
        IMAGE_TYPES.add("city");
        IMAGE_TYPES.add("animals");
        IMAGE_TYPES.add("people");
        IMAGE_TYPES.add("abstract");
        IMAGE_TYPES.add("business");
        IMAGE_TYPES.add("cats");
        IMAGE_TYPES.add("food");
        IMAGE_TYPES.add("nature");
        IMAGE_TYPES.add("technics");
        IMAGE_TYPES.add("fashion");
        IMAGE_TYPES.add("nightlife");
    }

    private Bucket mDefaultBucket;
    private Bucket mAdapterBucket;

    @InjectView(R.id.grid_view)
    GridView mGridView;
    private ListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        final Display display = getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
        mImageSize = Math.round(size.x / 2f);
        mDefaultImagePath = String.format(DEFAULT_IMAGE, mImageSize, mImageSize);

        // Kill all images
        try {
            final File oldCache = new File(CACHE_PATH);
            if (oldCache.exists()) {
                FileUtil.deleteDirectory(oldCache);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        mDefaultBucket = Vinci.createBucket(this, DEFAULT_PATH, mImageSize, mImageSize, 1);
        mDefaultBucket.precache(mDefaultImagePath, mImageSize, mImageSize);
        mAdapterBucket = Vinci.createBucket(this, CACHE_PATH, mImageSize, mImageSize, 32);

        initializeViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mDefaultBucket.destroy();
        mAdapterBucket.destroy();
    }

    private void initializeViews() {
        mAdapter = new SimpleAdapter();
        mGridView.setAdapter(mAdapter);
    }

    private Drawable getDefaultImage() {
        return mDefaultBucket.get(mDefaultImagePath, mImageSize, mImageSize, null);
    }

    private class SimpleAdapter extends BaseAdapter {
        final List<String> mPaths = new LinkedList<String>();

        {
            for (String type : IMAGE_TYPES) {
                for (int i = 0; i < 10; i++) {
                    mPaths.add(String.format("http://lorempixel.com/%d/%d/%s/%d/%d", mImageSize, mImageSize, type, i, i));
                }
                for (int i = 0; i < 10; i++) {
                    mPaths.add(String.format("http://lorempixel.com/%d/%d/%s/%d/%d", mImageSize, mImageSize, type, i, i));
                }
            }
        }

        @Override
        public int getCount() {
            return mPaths.size();
        }

        @Override
        public String getItem(int position) {
            return mPaths.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(MainActivity.this, R.layout.item_simple, null);
                convertView.setMinimumHeight(mImageSize);
            }

            final ViewHolder viewHolder;
            if (convertView.getTag() == null) {
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final String path = getItem(position);

            if (viewHolder.shouldUpdate(path)) {
                viewHolder.mImageView.setBackgroundColor(0);
                final Drawable drawable = mAdapterBucket.get(path, mImageSize, mImageSize, new BucketListener() {
                    @Override
                    public void onLoaded(String path, Drawable drawable, int width, int height) {
                        synchronized (viewHolder) {
                            if (viewHolder.mPath.equals(path)) {
                                viewHolder.mArtworkLoaded = true;
                                viewHolder.mImageView.setImageDrawable(drawable);
                                viewHolder.mImageView.setBackgroundColor(Color.GREEN);
                            }
                        }
                    }

                    @Override
                    public void onFailure(String path, int width, int height) {
                        synchronized (viewHolder) {
                            if (viewHolder.mPath.equals(path)) {
                                viewHolder.mImageView.setBackgroundColor(Color.RED);
                            }
                        }
                    }
                });
                if (drawable == null) {
                    viewHolder.setArtwork(getDefaultImage());
                } else {
                    viewHolder.setArtwork(drawable);
                }
            }

            return convertView;
        }
    }

    public static class ViewHolder implements BucketListener {
        private volatile String mPath = null;
        private volatile boolean mArtworkLoaded = false;
        @InjectView(R.id.image) ImageView mImageView;


        private ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

        private synchronized boolean shouldUpdate(String path) {
            if (mPath == null || !mPath.equals(path)) {
                mPath = path;
                mArtworkLoaded = false;
                return true;
            }

            return false;
        }

        private synchronized void setArtwork(Drawable drawable) {
            if (!mArtworkLoaded) {
                mImageView.setImageDrawable(drawable);
            }
        }

        @Override
        public synchronized void onLoaded(String path, Drawable drawable, int width, int height) {
            if (mPath.equals(path)) {
                mArtworkLoaded = true;
                mImageView.setImageDrawable(drawable);
            }
        }

        @Override
        public void onFailure(String path, int width, int height) { }
    }
}
