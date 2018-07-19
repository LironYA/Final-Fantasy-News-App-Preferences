package com.example.android.finalfantasynewsapp;


import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QueryUtils {

    private  QueryUtils() {
    }

    public static List<News> fetchNewsData(String requestUrl){
        // Create URL object
        URL url = createUrl(requestUrl);
        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e("QueryUtils.this", "Problem making the HTTP request.", e);
        }
        List<News> news = extractFeatureFromJson(jsonResponse);
        return news;
    }
    // Returns new URL object from the given string URL.
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e("QueryUtils", "Problem building the URL ", e);
        }
        return url;
    }
    // Make an HTTP request to the given URL and return a String as the response.
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e("QueryUtils", "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e("QueryUtils", "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }}
        return output.toString();
    }
    private static List<News> extractFeatureFromJson(String newsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }
        // Create an empty ArrayList
        List<News> news = new ArrayList<>();
        try {
            // Create a JSONObject from the JSON results string
            JSONObject baseJsonResponse = new JSONObject(newsJSON);
            JSONObject response = baseJsonResponse.getJSONObject("response");
            // Extract the JSONArray associated with the key called "results",
            // which represents a list of news result
            JSONArray newsArray = response.getJSONArray("results");
            // For each earthquake in the earthquakeArray, create an {@link Earthquake} object
            for (int i = 0; i < newsArray.length(); i++) {
                // Get a single earthquake at position i within the list of earthquakes
                JSONObject currentNews = (JSONObject) newsArray.get(i);
                // For a given entry, extract the JSONObject associated with the
                // key called "results"
                String sectionName = currentNews.getString("sectionName");
                // Extract the value for the key called "webTitle"
                String webTitle = currentNews.getString("webTitle");
                // Extract the value for the key called "webPublicationDate"
                String webPublicationDate = currentNews.getString("webPublicationDate");
                String formattedDate = formatDate(webPublicationDate);
                // Extract the value for the key called "webUrl"
                String url = currentNews.getString("webUrl");
                JSONObject fields = currentNews.getJSONObject("fields");
                String thumbnail = fields.getString("thumbnail");
                JSONArray tagsArray = currentNews.getJSONArray("tags");

                String author;
                //Get the tag value at the 0th place of the array
                if (tagsArray.getJSONObject(0) != null) {
                    JSONObject tags = tagsArray.getJSONObject(0);
                    author = tags.getString("webTitle");

                } else {
                    author = "No author for this artical";
                }

                // Create a new object with
                News newsN = new News(thumbnail, sectionName, webTitle, author, formattedDate, url);

                // Add the new object to the list of news
                news.add(newsN);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Return the list of earthquakes
        return news;
    }
    private static String formatDate (String date) throws ParseException {

        String artical_date = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        try {
            Date newDate = format.parse(date);
            format = new SimpleDateFormat("EEE, MMM dd yyyy 'at' HH:mm ");
            artical_date = format.format(newDate);
        }
        catch (ParseException  e) {
            Log.e("QueryUtils.this", "Date formatting error");
        }
        return artical_date;

    }}