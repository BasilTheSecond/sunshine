package com.example.android.sunshine.app;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherDataParser
{
	final static String OWM_LIST = "list";
	final static String OWM_TEMPERATURE = "temp";
	final static String OWM_MAX = "max";
	final static String OWM_MIN = "min";
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
		JSONObject weather = new JSONObject(weatherJsonStr);
		JSONArray days = weather.getJSONArray(OWM_LIST);
		JSONObject dayInfo = days.getJSONObject(dayIndex);
		JSONObject temperatureInfo = dayInfo.getJSONObject(OWM_TEMPERATURE);
		return temperatureInfo.getDouble(OWM_MAX);
	}

	private static double
	getMinTemperatureForDay(String weatherJsonStr,
													int dayIndex)
		throws JSONException
	{
		JSONObject weather = new JSONObject(weatherJsonStr);
		JSONArray days = weather.getJSONArray(OWM_LIST);
		JSONObject dayInfo = days.getJSONObject(dayIndex);
		JSONObject temperatureInfo = dayInfo.getJSONObject(OWM_TEMPERATURE);
		return temperatureInfo.getDouble(OWM_MIN);
	}

	public static String[]
	getResultsString(	String weatherJsonStr,
										int numberOfDays)
										throws JSONException
	{
		String[] resultsString = new String[numberOfDays];

		for (int i = 0; i < numberOfDays; i++) {
			String day = "Mod, Jun 1";
			String description = "Clear";
			long low = Math.round(getMinTemperatureForDay(weatherJsonStr, i));
			long high = Math.round(getMaxTemperatureForDay(weatherJsonStr, i));
			resultsString[i] = day + " - " + description + " - " + high + "/" + low;
		}

		return resultsString;
	}
}
