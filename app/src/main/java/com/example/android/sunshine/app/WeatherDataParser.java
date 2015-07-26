package com.example.android.sunshine.app;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class WeatherDataParser
{
	final static String OWM_LIST = "list";
	final static String OWM_TEMPERATURE = "temp";
	final static String OWM_MAX = "max";
	final static String OWM_MIN = "min";
	final static String OWM_WEATHER = "weather";
	final static String OWM_DESCRIPTION = "main";

	private static JSONArray
	getDaysInfo(String weatherJsonStr)
							throws JSONException
	{
		JSONObject weather = new JSONObject(weatherJsonStr);
		JSONArray days = weather.getJSONArray(OWM_LIST);
		return days;
	}

	/**
	 * Given a string of the form returned by the api call:
	 * http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7
	 * retrieve the maximum temperature for the day indicated by dayIndex
	 * (Note: 0-indexed, so 0 would refer to the first day).
	 */
	private static double
	getMaxTemperatureForDay(JSONObject dayInfo)
													throws JSONException
	{
		JSONObject temperatureInfo = dayInfo.getJSONObject(OWM_TEMPERATURE);
		double temperatureMax = temperatureInfo.getDouble(OWM_MAX);
		return temperatureMax;
	}

	private static double
	getMinTemperatureForDay(JSONObject dayInfo)
													throws JSONException
	{
		JSONObject temperatureInfo = dayInfo.getJSONObject(OWM_TEMPERATURE);
		double temperatureMin = temperatureInfo.getDouble(OWM_MIN);
		return temperatureMin;
	}

	private static String
	getDescriptionForDay(	JSONObject dayInfo)
												throws JSONException
	{
		JSONObject weatherInfo = dayInfo.getJSONArray(OWM_WEATHER).getJSONObject(0);
		String description = weatherInfo.getString(OWM_DESCRIPTION);
		return description;
	}

	private static String
	getLocalDate(	int dayIndex)
								throws JSONException
	{
		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
		gregorianCalendar.add(GregorianCalendar.DATE, dayIndex);
		Date time = gregorianCalendar.getTime();
		String day = shortenedDateFormat.format(time);
		return day;
	}

	public static String[]
	getResultsString(	String weatherJsonStr,
										int numberOfDays)
										throws JSONException
	{
		JSONArray days = getDaysInfo(weatherJsonStr);
		String[] resultsString = new String[numberOfDays];
		for (int i = 0; i < numberOfDays; i++) {
			JSONObject dayInfo = days.getJSONObject(i);
			String description = getDescriptionForDay(dayInfo);
			long low = Math.round(getMinTemperatureForDay(dayInfo));
			long high = Math.round(getMaxTemperatureForDay(dayInfo));
			String day = getLocalDate(i);
			resultsString[i] = day + " - " + description + " - " + high + "/" + low;
		}
		return resultsString;
	}
}
