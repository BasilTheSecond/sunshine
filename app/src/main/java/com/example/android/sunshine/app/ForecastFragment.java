package com.example.android.sunshine.app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import org.json.JSONException;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class
ForecastFragment extends Fragment
{
	ArrayAdapter<String> mForecastAdapter;

	@Override
	public View
	onCreateView(LayoutInflater inflater,
	             ViewGroup container,
	             Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		// Create Adapter that will create View(s) from the raw data for the ListView
		// Adapter needs the following parameters:
		// - context (from getActivity())
		// - ID of ListView layout ( R.layout.listview_forecast, this is xml layout file)
		// - ID ID of View layout (R.id.list_item_forecast_textview, this is element in layout file)
		// - list of data (initially empty)
		mForecastAdapter = new ArrayAdapter<String>(getActivity(),
																								R.layout.list_item_forecast,
																								R.id.list_item_forecast_textview,
																								new ArrayList<String>());
		// Bind this adapter to the ListView (R.id.listview_forecast)
		// To get the View object we use findViewById(R.id.listview_forecast) method of the Fragment class
		// NOTE: rootView is obtained when the fragment is inflated into the View
		ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
		// Set Adapter on ListView
		listView.setAdapter(mForecastAdapter);
		return rootView;
	}

	@Override
	public void
	onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void
	onCreateOptionsMenu(Menu menu,
											MenuInflater inflater)
	{
		inflater.inflate(R.menu.forecastfragment, menu);
	}

	@Override
	public boolean
	onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId()) {
			case R.id.action_refresh:
				//new FetchWeatherTask().execute("94043,us"); // Mountain View, CA, USA
				new FetchWeatherTask().execute("m9a1g7,ca"); // Etobicoke, ON, CA
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public class
	FetchWeatherTask extends AsyncTask<	String,
																			Void,
																			String[]>
	{
		private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
		private final String BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily";
		private final String QUERY_KEY = "q";
		private final String MODE_KEY = "mode";
		private final String UNITS_KEY = "units";
		private final String COUNT_KEY = "cnt";
		private final int numberOfDays = 7;

		@Override
		protected String[] doInBackground(String... params)
		{
			// Check that there is a network connection before attempting
			// to issue http GET request (to read data)
			ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			if (networkInfo == null || !networkInfo.isConnected()) {
				Log.e(LOG_TAG, "No network connection");
				return null;
			}
			// These two need to be declared outside the try/catch
			// so that they can be closed in the finally block.
			HttpURLConnection urlConnection = null;
			BufferedReader reader = null;

			// Will contain raw JSON response
			String forecastJsonStr = null;
			try {
				// Construct the URL for the OpenWeatherMap query
				// Possible parameters are avaiable at OWM's forecast API page, at
				// http://openweathermap.org/API#forecast
				Uri.Builder uriBuilder = Uri.parse(BASE_URL)
																		.buildUpon()
																		.appendQueryParameter(QUERY_KEY, params[0])
																		.appendQueryParameter(MODE_KEY, "json")
																		.appendQueryParameter(UNITS_KEY, "metric")
																		.appendQueryParameter(COUNT_KEY, String.valueOf(numberOfDays));

				URL url = new URL(uriBuilder.build().toString());

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
				StringBuffer buffer = new StringBuffer();

				while (true) {
					String line = reader.readLine();
					if (line == null) break;
					// Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
					// But it does make debugging a *lot* easier if you print out the completed
					// buffer for debugging.
					buffer.append(line + '\n');
				}
				if (buffer.length() == 0) {
					// Stream was empty.  No point in parsing.
					return null;
				}
				forecastJsonStr = buffer.toString();
			} catch (IOException e) {
				Log.e(LOG_TAG, "Error ", e);
				return null;
			} finally {
				if (urlConnection != null) {
					urlConnection.disconnect();
				}
				if (reader != null) {
					try {
						reader.close();
					} catch (final IOException e) {
						Log.e(LOG_TAG, "Error closing stream", e);
					}
				}
			}
			try {
				String cityName = WeatherDataParser.getCityName(forecastJsonStr);
				Log.i(LOG_TAG, "cityName=" + cityName);
				String[] resultsString = WeatherDataParser.getResultsString(forecastJsonStr, numberOfDays);
				return resultsString;
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String[] resultsString)
		{
			mForecastAdapter.clear();
			for (String s: resultsString) {
				mForecastAdapter.add(s);
			}
			mForecastAdapter.notifyDataSetChanged();
		}
	}
}
