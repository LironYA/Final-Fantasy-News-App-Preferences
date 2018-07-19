package com.example.android.finalfantasynewsapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NewsAdapter extends ArrayAdapter<News> {
    private static String url = "";

    public NewsAdapter(@NonNull Context context, List<News> news) {
        super(context, 0, news);
    }
    //Return a list of the games title and the published date

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_list_item, parent, false);
        }
        News currentNews = getItem(position);
        ImageView imageView = (ImageView) listItemView.findViewById(R.id.image);
        url =(String.valueOf(currentNews.getThumbnail()));
        Picasso.get().load(url).into(imageView);
        TextView category = (TextView) listItemView.findViewById(R.id.category);
        category.setText(currentNews.getSectionName());
        TextView author = (TextView)listItemView.findViewById(R.id.author);
        author.setText(currentNews.getAuthor());
        TextView artical_title = (TextView) listItemView.findViewById(R.id.artical_title);
        artical_title.setText(currentNews.getWebTitle());
        //  Date date = new Date (currentNews.getWebPublicationDate());
        TextView publish_date = (TextView) listItemView.findViewById(R.id.publish_date);
        //  String formattedDate = formatDate(date);
        publish_date.setText(currentNews.getWebPublicationDate());

        return listItemView;
    }

}