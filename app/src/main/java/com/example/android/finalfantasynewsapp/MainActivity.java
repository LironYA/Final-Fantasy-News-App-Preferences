package com.example.android.finalfantasynewsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>,
        SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String FF_NEWS = "https://content.guardianapis.com/search";
    private ProgressBar progressBar;
    private int LOADER_ID = 1;
    // TextView that is displayed when the list is empty
    private TextView emptyStateTextView;
    private NewsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView newsView = (ListView) findViewById(R.id.list);
        // Obtain a reference to the SharedPreferences file for this app
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // And register to be notified of preference changes
        // So we know when the user has adjusted the query settings

        prefs.registerOnSharedPreferenceChangeListener(this);
        // If there's no data to display, show the empty_view TextBox

        emptyStateTextView = (TextView) findViewById(R.id.empty_view);
        newsView.setEmptyView(emptyStateTextView);
        //Progress Bar
        progressBar = (ProgressBar) findViewById(R.id.loading_spinner);
        //Check if there is internet connection
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();
            // Initialize the loader.
            loaderManager.initLoader(LOADER_ID, null, this);
            // Find a reference to the {@link ListView} in the layout
        } else {
            progressBar.setVisibility(View.GONE);
            emptyStateTextView.setText("No internet connection");
        }
        adapter = new NewsAdapter(MainActivity.this, new ArrayList<News>());
        newsView.setAdapter(adapter);
        newsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                News currentNews = adapter.getItem(position);
                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri newsUri = Uri.parse(currentNews.getURL());

                // Create a new intent
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String order_by = sharedPrefs.getString(getString(R.string.settings_order_by_key),getString(R.string.settings_order_by_newest_value));
        String query = sharedPrefs.getString(getString(R.string.settings_query_key),getString(R.string.setting_query_default_value));

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(FF_NEWS);
        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();
        // Append query parameter and its value. For example, the `format=geojson`
        // https://content.guardianapis.com/search?section=games&from-date=2017-01-01
        // &to-date=2018-01-01&order-by=newest&use-date=published&q=%22final%20fantasy%22&api-key=c018038d-3e83-48f8-9208-c09dd8c08614
        // &show-tags=contributor&show-fields=thumbnail

        uriBuilder.appendQueryParameter("section", "games");
        uriBuilder.appendQueryParameter("from-date", "2018-01-01");
        uriBuilder.appendQueryParameter("to-date", "2018-06-06");
        uriBuilder.appendQueryParameter("order-by", order_by);
        uriBuilder.appendQueryParameter("use-date", "published");
        uriBuilder.appendQueryParameter("q", query);
        uriBuilder.appendQueryParameter("api-key", "c018038d-3e83-48f8-9208-c09dd8c08614");
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("show-fields", "thumbnail");
        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        progressBar.setVisibility(View.GONE);
        emptyStateTextView.setText("No news found, please try again later");
        adapter.clear();

        if (news != null && !news.isEmpty()) {
            adapter.addAll(news);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        adapter.clear();
    }

    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
    if(key.equals(getString(R.string.settings_query_key)) || key.equals(getString(R.string.settings_order_by_key)))
        {
        adapter.clear();
            // Hide the empty state text view as the loading indicator will be displayed
            emptyStateTextView.setVisibility(View.GONE);
            // Show the loading indicator while new data is being fetched
            View loadingIndicator = findViewById(R.id.loading_spinner);
            loadingIndicator.setVisibility(View.VISIBLE);

            // Restart the loader to requery the USGS as the query settings have been updated
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }
    }
}
