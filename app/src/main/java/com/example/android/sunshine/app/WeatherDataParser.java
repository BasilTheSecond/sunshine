package com.example.android.sunshine.app;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class WeatherDataParser
{
	private final static String OWM_LIST = "list";
	private final static String OWM_TEMPERATURE = "temp";
	private final static String OWM_MAX = "max";
	private final static String OWM_MIN = "min";
	private final static String OWM_WEATHER = "weather";
	private final static String OWM_DESCRIPTION = "main";
	private final static String OWM_CITY = "city";
	private final static String OWM_CITY_NAME = "name";

	private static JSONArray
	getDaysInfo(String weatherJsonStr)
							throws JSONException
	{
		JSONObject weather = new JSONObject(weatherJsonStr);
		JSONArray daysInfo = weather.getJSONArray(OWM_LIST);
		return daysInfo;
	}

	private static JSONObject
	getCityInfo(String weatherJsonStr)
		throws JSONException
	{
		JSONObject weather = new JSONObject(weatherJsonStr);
		JSONObject cityInfo = weather.getJSONObject(OWM_CITY);
		return cityInfo;
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

	public static String
	getCityName(String weatherJsonStr)
							throws JSONException
	{
		String cityName = getCityInfo(weatherJsonStr).getString(OWM_CITY_NAME);
		return cityName;
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
