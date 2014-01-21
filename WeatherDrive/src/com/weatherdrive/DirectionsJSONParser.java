package com.weatherdrive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.format.Time;
import android.util.Log;

import com.weatherdrive.model.DirectionItem;

public class DirectionsJSONParser {

	Time now = new Time();

	int CurrentHour = 0;
	int SecondsElapsed = 0;

	int lastElapsedTimeValue = -1;
	int lastTemperature;
	String lastCondition;
	String lastWeatherIconURL;

	/** Receives a JSONObject and returns a list */
	public ArrayList<DirectionItem> parse(JSONObject jObject) {

		Log.d("WeatherDrive", "Valid Parse Object: "
				+ ((jObject != null) ? "True" : "False"));

		JSONArray jRoutes = null;
		JSONObject jRoute = null;
		JSONArray jLegs = null;

		try {
			jRoutes = jObject.getJSONArray("routes");
			jRoute = jRoutes.getJSONObject(0);
			/** Retrieves all the elements in the 'Legs' array */
			jLegs = jRoute.getJSONArray("legs");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		Log.d("WeatherDrive", "jLegs Count: " + jLegs.length());

		return getDirections(jLegs);
	}

	private ArrayList<DirectionItem> getDirections(JSONArray jLegs) {
		now.setToNow();
		CurrentHour = now.hour;
		SecondsElapsed = now.minute * 60 + now.second;

		int legsCount = jLegs.length();
		ArrayList<DirectionItem> directionsList = new ArrayList<DirectionItem>();
		JSONObject leg = null;
		JSONArray steps = null;
		JSONObject step = null;
		int stepsCount;

		for (int i = 0; i < legsCount; i++) {
			try {
				leg = jLegs.getJSONObject(i);
				steps = leg.getJSONArray("steps");
				stepsCount = steps.length();

				for (int j = 0; j < stepsCount; j++) {
					step = steps.getJSONObject(j);
					directionsList.add(getDirectionItem(step));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return directionsList;
	}

	private DirectionItem getDirectionItem(JSONObject jDirection) {

		JSONObject location;

		String instruction = "";
		String distance = "";
		String duration = "";

		double latitude = 0;
		double longitude = 0;

		int tempElapsed = 0;

		try {

			instruction = jDirection.getString("html_instructions");
			distance = jDirection.getJSONObject("distance").getString("text");
			duration = jDirection.getJSONObject("duration").getString("text");
			tempElapsed = jDirection.getJSONObject("duration").getInt("value");
			location = jDirection.getJSONObject("start_location");
			latitude = location.getDouble("lat");
			longitude = location.getDouble("lng");

		} catch (JSONException e) {
			e.printStackTrace();
		}

		DirectionItem direction = new DirectionItem(instruction, distance,
				duration);

		if (lastElapsedTimeValue != -1
				&& (SecondsElapsed - lastElapsedTimeValue) < 900) {
			Log.d("WeatherDrive", "API Call Skipped");

			direction.Temperature = lastTemperature;
			direction.Condition = lastCondition;
			direction.WeatherIconURL = lastWeatherIconURL;
			SecondsElapsed += tempElapsed;
			return direction;
		}

		Log.d("WeatherDrive", "API Call");

		lastElapsedTimeValue = SecondsElapsed;
		SecondsElapsed += tempElapsed;

		// http://api.wunderground.com/api/Your_Key/hourly/q/CA/San_Francisco.json

		// For storing data from web service
		String data = "";

		String key = "7828df2babbfd8c0";

		// Building the url to the web service
		String url = "http://api.wunderground.com/api/" + key + "/hourly/q/"
				+ latitude + "," + longitude + ".json";

		JSONObject jWeather;
		JSONArray forecast = null;

		try {
			// Fetching the data from we service
			data = downloadUrl(url);
			jWeather = new JSONObject(data);
			forecast = jWeather.getJSONArray("hourly_forecast");
		} catch (Exception e) {
			Log.d("Background Task", e.toString());
		}

		int elapsedHours = (int) Math
				.round(Double.valueOf(SecondsElapsed) / 3600);

		JSONObject weatherHour;

		try {
			weatherHour = forecast
					.getJSONObject((elapsedHours - 1 > 0) ? elapsedHours - 1
							: 0);
			direction.Condition = weatherHour.getString("condition");
			lastCondition = weatherHour.getString("condition");
			direction.Temperature = weatherHour.getJSONObject("temp").getInt(
					"english");
			lastTemperature = weatherHour.getJSONObject("temp").getInt(
					"english");
			direction.WeatherIconURL = weatherHour.getString("icon_url");
			lastWeatherIconURL = weatherHour.getString("icon_url");
		} catch (Exception e) {
			Log.d("Background Task", e.toString());
		}

		return direction;
	}

	/** A method to download json data from url */
	private String downloadUrl(String strUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		} catch (Exception e) {
			Log.d("Exception while downloading url", e.toString());
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}
}
