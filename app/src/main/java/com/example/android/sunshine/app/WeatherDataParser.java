package com.example.android.sunshine.app;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherDataParser
{
	/**
	 * Given a string of the form returned by the api call:
	 * http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7 
	 * retrieve the maximum temperature for the day indicated by dayIndex
	 * (Note: 0-indexed, so 0 would refer to the first day).
	 */
	private static double
	getMaxTemperatureForDay(String weatherJsonStr,
													int dayIndex)
													throws JSONException
	{
		final String OWM_LIST = "list";
		final String OWM_TEMPERATURE = "temp";
		final String OWM_MAX = "max";

		JSONObject weather = new JSONObject(weatherJsonStr);
		JSONArray days = weather.getJSONArray(OWM_LIST);
		JSONObject dayInfo = days.getJSONObject(dayIndex);
		JSONObject temperatureInfo = dayInfo.getJSONObject(OWM_TEMPERATURE);
		return temperatureInfo.getDouble(OWM_MAX);
	}

	public static String[]
	getResultsString(	String weatherJsonStr,
										int numberOfDays)
										throws JSONException
	{
		String[] resultsString = new String[numberOfDays];

		for (int i = 0; i < numberOfDays; i++) {
			double temperature = 0.0;
			String day = "Mod, Jun 1";
			String description = "Clear";
			long low = Math.round(13.0);
			long high = Math.round(getMaxTemperatureForDay(weatherJsonStr, i));
			resultsString[i] = day + " - " + description + " - " + high + "/" + low;
		}

		return resultsString;
	}
}
