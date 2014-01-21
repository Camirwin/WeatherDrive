package com.weatherdrive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.weatherdrive.model.DirectionItem;

public class DirectionsActivity extends ListActivity {

	Button btnReturn;
	String FromLocation, ToLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_directions);

		Intent intent = getIntent();

		btnReturn = (Button) findViewById(R.id.btnReturn);
		btnReturn.setText("<<");

		FromLocation = intent.getStringExtra("FROM_LOCATION");
		ToLocation = intent.getStringExtra("TO_LOCATION");

		TextView tvFrom = (TextView) findViewById(R.id.FromLocation);
		tvFrom.setText(FromLocation);
		
		TextView tvTo = (TextView) findViewById(R.id.ToLocation);
		tvTo.setText(ToLocation);
		
		FromLocation = FromLocation.replace(" ", "");
		ToLocation = ToLocation.replace(" ", "");
		
		ArrayAdapter<DirectionItem> adapter = new ArrayAdapter<DirectionItem>(
				this, android.R.layout.simple_list_item_1);
		setListAdapter(adapter);

		PlacesTask loadData = new PlacesTask(adapter);
		loadData.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.directions, menu);
		return true;
	}

	public void btnReturn_Click(View view) {
		Intent main = new Intent(this, MainActivity.class);
		main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(main);
	}

	private class PlacesTask extends
			AsyncTask<Void, Void, ArrayList<DirectionItem>> {

		private final ArrayAdapter<DirectionItem> mAdapter;
		ArrayList<DirectionItem> directions;

		public PlacesTask(ArrayAdapter<DirectionItem> adapter) {
			mAdapter = adapter;
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

		@Override
		protected ArrayList<DirectionItem> doInBackground(Void... place) {
			// http://maps.googleapis.com/maps/api/directions/json?origin=Toronto&destination=Montreal&sensor=false

			// For storing data from web service
			String data = "";

			// place type to be searched
			String origin = "origin=" + FromLocation;

			// place type to be searched
			String destination = "destination=" + ToLocation;

			// Sensor enabled
			String sensor = "sensor=true";

			// American Units
			String units = "units=imperial";

			// Building the parameters to the web service
			String parameters = origin + "&" + destination + "&" + units + "&"
					+ sensor;

			// Output format
			String output = "json";

			// Building the url to the web service
			String url = "http://maps.googleapis.com/maps/api/directions/"
					+ output + "?" + parameters;

			try {
				// Fetching the data from we service
				data = downloadUrl(url);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}

			DirectionsJSONParser directionsParser = new DirectionsJSONParser();

			try {
				JSONObject jData = new JSONObject(data);
				directions = directionsParser.parse(jData);
			} catch (Exception e) {
				Log.d("Exception", e.toString());
			}

			return directions;
		}

		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(DirectionsActivity.this, "",
					"Loading. Please wait...", true);
		}

		@Override
		protected void onPostExecute(ArrayList<DirectionItem> result) {
			mAdapter.addAll(result);
			mAdapter.notifyDataSetChanged();
			dialog.dismiss();
		}
	}

	public void toWunderground(View view) {
		Intent i = new Intent(Intent.ACTION_VIEW,
				Uri.parse("http://www.wunderground.com"));
		startActivity(i);
	}

}
