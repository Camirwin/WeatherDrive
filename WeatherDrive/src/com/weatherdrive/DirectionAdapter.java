package com.weatherdrive;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.weatherdrive.model.DirectionItem;

public class DirectionAdapter extends ArrayAdapter<DirectionItem> {
	private final Context context;
	private final ArrayList<DirectionItem> Directions;

	public DirectionAdapter(Context context, ArrayList<DirectionItem> directions) {
		super(context, R.layout.list_row);
		this.context = context;
		this.Directions = directions;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.list_row, parent, false);

		TextView instructions = (TextView) rowView
				.findViewById(R.id.tvInstructions);
		TextView time = (TextView) rowView.findViewById(R.id.tvTime);
		TextView distance = (TextView) rowView.findViewById(R.id.tvDistance);
		TextView temperature = (TextView) rowView
				.findViewById(R.id.tvTemperature);
		TextView condition = (TextView) rowView.findViewById(R.id.tvCondition);
		ImageView weatherIcon = (ImageView) rowView
				.findViewById(R.id.ivWeatherIcon);

		DirectionItem direction = Directions.get(position);

		instructions.setText(direction.Instruction);
		time.setText(direction.DurationDisplay);
		distance.setText(direction.Distance);
		temperature.setText(direction.Temperature);
		condition.setText(direction.Condition);

		try {
			InputStream is = (InputStream) new URL(direction.WeatherIconURL)
					.getContent();
			Drawable d = Drawable.createFromStream(is, direction.Condition);
			weatherIcon.setImageDrawable(d);
		} catch (Exception e) {
			Log.d("WeatherDrive", "WeatherIcon Failed");
		}

		return rowView;
	}
	
	public void update(ArrayList<DirectionItem> directions){
		this.Directions.clear();
		this.Directions.addAll(directions);
		this.notifyDataSetChanged();
	}
}
