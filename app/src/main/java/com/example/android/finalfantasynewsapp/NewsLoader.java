package com.example.android.finalfantasynewsapp;


import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<News>> {
    private String mURL;

    public NewsLoader(Context context, String URL) {
        super(context);
        mURL = URL;
    }
    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<News> loadInBackground() {
        if (mURL == null) {
            return null;
        }
        List<News> result = QueryUtils.fetchNewsData(mURL);
        return result;
    }}
