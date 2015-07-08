package com.example.android.sunshine.app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private ArrayAdapter<String> mForecastAdapter;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            // Create raw (and fake) data for the ListView
            ArrayList<String> weekForecast = new ArrayList<String>();
            weekForecast.add("Today -- Sunny -- 88/63");
            weekForecast.add("Tomorrow -- Foggy -- 70/40");
            weekForecast.add("Weds -- Cloudy -- 72/63");
            weekForecast.add("Thurs -- Rainy -- 75/65");
            weekForecast.add("Fri -- Foggy -- 65/56");
            weekForecast.add("Sat -- Sunny -- 60/51");
            weekForecast.add("Sun -- Sunny -- 80/68");
            // Add a few more so that you can scroll
            weekForecast.add("+++++++++ 1 ++++++++");
            weekForecast.add("+++++++++ 2 ++++++++");
            weekForecast.add("+++++++++ 3 ++++++++");

            // Create Adapter that will create View(s) from the raw data for the ListView
            // Adapter needs the following parameters:
            // - context (from getActivity())
            // - ID of ListView layout ( R.layout.listview_forecast, this is xml layout file)
            // - ID ID of View layoyt (R.id.list_item_forecast_textview, this is element in layout file)
            // - list of data (weekForecast)
            mForecastAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast,
                    R.id.list_item_forecast_textview, weekForecast);

            // Bind this adapter to the ListView (R.id.listview_forecast)
            // To get the View object we use findViewById(R.id.listview_forecast) method of the Fragment class
            // NOTE: rootView is obtained when the fragment is inflated into the View
            ListView listView = (ListView)rootView.findViewById(R.id.listview_forecast);

            // Set Adapter on ListView
            listView.setAdapter(mForecastAdapter);

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            ConnectivityManager connMgr = (ConnectivityManager)
                    getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected()) {
                Log.e("PlaceholderFragment", "No network connection");
                //return rootView;
                return null;
            }
            Log.i("PlaceholderFragment", "Has network connection!!!");
            /*try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }*/

            return rootView;
        }
    }
}
