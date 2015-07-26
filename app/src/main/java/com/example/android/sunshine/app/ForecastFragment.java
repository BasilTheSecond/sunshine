package com.example.android.sunshine.app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;

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
				new FetchWeatherTask().execute("94043,us");
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public class
	FetchWeatherTask extends AsyncTask<String, Void, Void>
	{
		private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

		@Override
		protected Void doInBackground(String... params)
		{
			// Check that there is a network connection before attempting
			// to issue http GET request (to read data)
			ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			if (networkInfo == null || !networkInfo.isConnected()) {
				Log.e(LOG_TAG, "No network connection");
				return null;
			}
			Log.i(LOG_TAG, "Has network connection, launching network task!!!");

			// These two need to be declared outside the try/catch
			// so that they can be closed in the finally block.
			HttpURLConnection urlConnection = null;
			BufferedReader reader = null;

			// Will contain the raw JSON response
			String forecastJsonStr = null;
			try {
				final String BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily";
				final String QUERY_KEY = "q";
				final String MODE_KEY = "mode";
				final String UNITS_KEY = "units";
				final String COUNT_KEY = "cnt";
				// Construct the URL for the OpenWeatherMap query
				// Possible parameters are avaiable at OWM's forecast API page, at
				// http://openweathermap.org/API#forecast
				Uri.Builder uriBuilder = Uri.parse(BASE_URL)
																		.buildUpon()
																		.appendQueryParameter(QUERY_KEY, params[0])
																		.appendQueryParameter(MODE_KEY, "json")
																		.appendQueryParameter(UNITS_KEY, "metric")
																		.appendQueryParameter(COUNT_KEY, "7");

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
				//Log.d(LOG_TAG, "forecastJsonStr: " + forecastJsonStr);

				final String WEATHER_DATA_MTV_JUN_4 = "{\"cod\":\"200\",\"message\":4.2116,\"city\":{\"id\":\"5375480\",\"name\":\"Mountain View\",\"coord\":{\"lon\":-122.075,\"lat\":37.4103},\"country\":\"United States of America\",\"population\":0},\"cnt\":7,\"list\":[{\"dt\":1401912000,\"temp\":{\"day\":20.17,\"min\":12.3,\"max\":20.17,\"night\":12.3,\"eve\":17.74,\"morn\":14.05},\"pressure\":1012.43,\"humidity\":77,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"sky is clear\",\"icon\":\"01d\"}],\"speed\":1.67,\"deg\":253,\"clouds\":0},{\"dt\":1401998400,\"temp\":{\"day\":18.9,\"min\":10.74,\"max\":18.9,\"night\":10.74,\"eve\":15.54,\"morn\":14.02},\"pressure\":1009.89,\"humidity\":76,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"sky is clear\",\"icon\":\"01d\"}],\"speed\":1.51,\"deg\":225,\"clouds\":0},{\"dt\":1402084800,\"temp\":{\"day\":13.59,\"min\":13.57,\"max\":14.1,\"night\":14.1,\"eve\":14.04,\"morn\":13.57},\"pressure\":1022.58,\"humidity\":0,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"sky is clear\",\"icon\":\"01d\"}],\"speed\":8.92,\"deg\":325,\"clouds\":0},{\"dt\":1402171200,\"temp\":{\"day\":13.71,\"min\":13.71,\"max\":13.93,\"night\":13.93,\"eve\":13.73,\"morn\":13.93},\"pressure\":1021.29,\"humidity\":0,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"sky is clear\",\"icon\":\"01d\"}],\"speed\":6.41,\"deg\":326,\"clouds\":0},{\"dt\":1402257600,\"temp\":{\"day\":13.55,\"min\":13.52,\"max\":13.72,\"night\":13.52,\"eve\":13.72,\"morn\":13.62},\"pressure\":1022.14,\"humidity\":0,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"sky is clear\",\"icon\":\"01d\"}],\"speed\":9.72,\"deg\":320,\"clouds\":0},{\"dt\":1402344000,\"temp\":{\"day\":12.72,\"min\":12.72,\"max\":13.22,\"night\":13.22,\"eve\":13.19,\"morn\":13.06},\"pressure\":1027.87,\"humidity\":0,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"sky is clear\",\"icon\":\"01d\"}],\"speed\":7.85,\"deg\":322,\"clouds\":7},{\"dt\":1402430400,\"temp\":{\"day\":13.11,\"min\":12.89,\"max\":13.35,\"night\":13.26,\"eve\":13.35,\"morn\":12.89},\"pressure\":1029.35,\"humidity\":0,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"sky is clear\",\"icon\":\"01d\"}],\"speed\":11.01,\"deg\":330,\"clouds\":0}]}";
				final String WEATHER_DATA_FREMONT_JUN_4 = "{\"cod\":\"200\",\"message\":2.5405,\"city\":{\"id\":\"5350734\",\"name\":\"Fremont\",\"coord\":{\"lon\":-121.982,\"lat\":37.5509},\"country\":\"United States of America\",\"population\":0},\"cnt\":7,\"list\":[{\"dt\":1401912000,\"temp\":{\"day\":28.12,\"min\":12.74,\"max\":28.12,\"night\":12.74,\"eve\":23.73,\"morn\":28.12},\"pressure\":1004.41,\"humidity\":52,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"sky is clear\",\"icon\":\"01d\"}],\"speed\":2.41,\"deg\":263,\"clouds\":0},{\"dt\":1401998400,\"temp\":{\"day\":28.58,\"min\":10.3,\"max\":28.58,\"night\":10.3,\"eve\":22.76,\"morn\":16.08},\"pressure\":1002.75,\"humidity\":47,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"sky is clear\",\"icon\":\"01d\"}],\"speed\":3.13,\"deg\":261,\"clouds\":0},{\"dt\":1402084800,\"temp\":{\"day\":26.73,\"min\":11.2,\"max\":27.55,\"night\":11.2,\"eve\":22.94,\"morn\":12.53},\"pressure\":1001.79,\"humidity\":50,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"sky is clear\",\"icon\":\"01d\"}],\"speed\":2.01,\"deg\":267,\"clouds\":0},{\"dt\":1402171200,\"temp\":{\"day\":30.67,\"min\":11.81,\"max\":31.25,\"night\":11.81,\"eve\":25.92,\"morn\":15.5},\"pressure\":1001.95,\"humidity\":48,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"sky is clear\",\"icon\":\"01d\"}],\"speed\":1.91,\"deg\":271,\"clouds\":0},{\"dt\":1402257600,\"temp\":{\"day\":16.6,\"min\":10.32,\"max\":17.52,\"night\":12.62,\"eve\":17.52,\"morn\":10.32},\"pressure\":1003.29,\"humidity\":0,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"sky is clear\",\"icon\":\"01d\"}],\"speed\":2.84,\"deg\":292,\"clouds\":0},{\"dt\":1402344000,\"temp\":{\"day\":14.82,\"min\":10.66,\"max\":16.14,\"night\":11.97,\"eve\":16.14,\"morn\":10.66},\"pressure\":1009.02,\"humidity\":0,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":3.72,\"deg\":305,\"clouds\":12},{\"dt\":1402430400,\"temp\":{\"day\":15.26,\"min\":9.84,\"max\":16.75,\"night\":12.76,\"eve\":16.75,\"morn\":9.84},\"pressure\":1009.9,\"humidity\":0,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"sky is clear\",\"icon\":\"01d\"}],\"speed\":5.87,\"deg\":325,\"clouds\":0}]}";

				try {
					double max = WeatherDataParser.getMaxTemperatureForDay(WEATHER_DATA_MTV_JUN_4, 2);
					Log.i(LOG_TAG, "max=" + max);
					max = WeatherDataParser.getMaxTemperatureForDay(WEATHER_DATA_FREMONT_JUN_4, 6);
					Log.i(LOG_TAG, "max=" + max);
				} catch (JSONException e) {
					e.printStackTrace();
				}

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
			return null;
		}
	}
}

