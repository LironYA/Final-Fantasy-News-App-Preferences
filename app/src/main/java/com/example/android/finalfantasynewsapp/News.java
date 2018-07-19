package com.example.android.finalfantasynewsapp;


public class News {

    private String mWebPublicationDate;

    private String mWebTitle;

    private String mWebUrl;

    private String mSectionName;

    private String mThumbnail;

    private String mAuthor;

    public News (String thumbnail, String sectionName, String webTitle, String author, String webPublicationDate, String URL) {

        mWebTitle  = webTitle;
        mWebPublicationDate = webPublicationDate;
        mWebUrl = URL;
        mSectionName = sectionName;
        mThumbnail = thumbnail;
        mAuthor = author;

    }
    public String getSectionName() { return mSectionName; }

    public String getWebTitle() { return mWebTitle; }

    public String getWebPublicationDate() {
        return mWebPublicationDate;
    }

    public String getURL () {
        return mWebUrl;
    }

    public String getAuthor () {
        return mAuthor;
    }

    public String getThumbnail () {
        return mThumbnail;
    }
}