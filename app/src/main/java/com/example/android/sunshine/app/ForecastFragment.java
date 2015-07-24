package com.example.android.sunshine.app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
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
import java.util.List;

public class
ForecastFragment extends Fragment
{
	public ForecastFragment() {}

	@Override
	public View
	onCreateView(LayoutInflater inflater,
	             ViewGroup container,
	             Bundle savedInstanceState)
	{
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
		ArrayAdapter<String> forecastAdapter = new ArrayAdapter<String>(	getActivity(),
																																R.layout.list_item_forecast,
																																R.id.list_item_forecast_textview,
																																weekForecast);

		// Bind this adapter to the ListView (R.id.listview_forecast)
		// To get the View object we use findViewById(R.id.listview_forecast) method of the Fragment class
		// NOTE: rootView is obtained when the fragment is inflated into the View
		ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);

		// Set Adapter on ListView
		listView.setAdapter(forecastAdapter);

		// Check that there is a network connection before attempting
		// to issue http GET request (to read data)
		ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo == null || !networkInfo.isConnected()) {
			Log.e("ForecastFragment", "No network connection");
			return null;
		}
		Log.i("ForecastFragment", "Has network connection!!!");

		// Allow networking I/O to be done on the main thread
		// NOTE:
		// This is a workaround (and for debugging). The standard practice is
		// to perform networking I/O on a separate thread
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		// These two need to be declared outside the try/catch
		// so that they can be closed in the finally block.
		HttpURLConnection urlConnection = null;
		BufferedReader reader = null;

		// Will contain the raw JSON response
		List<String> forecastJsonStr = new ArrayList<String>();
		try {
			// Construct the URL for the OpenWeatherMap query
			// Possible parameters are avaiable at OWM's forecast API page, at
			// http://openweathermap.org/API#forecast
			URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");

			// Create the request to OpenWeatherMap, and open the connection
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.connect();

			// Read the input stream into the string list, line by line
			InputStream inputStream = urlConnection.getInputStream();
			if (inputStream == null) {
				// Nothing to do.
				return null;
			}
			reader = new BufferedReader(new InputStreamReader(inputStream));

			//String line;
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}
				forecastJsonStr.add(line);
			}
			if (forecastJsonStr.size() == 0) {
				// Stream was empty.  No point in parsing.
				return null;
			}

//                Log.d("ForecastFragment", "forecastJsonStr.size()=" + forecastJsonStr.size());
//                for (String line : forecastJsonStr) {
//                    Log.d("ForecastFragment", line);
//                }

		} catch (IOException e) {
			Log.e("ForecastFragment", "Error ", e);
			// If the code didn't successfully get the weather data, there's no point in attemping
			// to parse it.
			return null;
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (final IOException e) {
					Log.e("ForecastFragment", "Error closing stream", e);
				}
			}
		}

		return rootView;
	}

	public void foo() {

	}
}

