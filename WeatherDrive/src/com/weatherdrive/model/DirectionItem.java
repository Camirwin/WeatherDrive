package com.weatherdrive.model;

import android.text.Html;

public class DirectionItem {

	public int StepNumber;
	public String Instruction;
	public String DurationDisplay;
	public String Distance;
	public String Condition;
	public int Temperature;
	public String WeatherIconURL;

	public DirectionItem(Step step, Weather weather) {
		StepNumber = step.StepNumber;
		Instruction = step.Instruction;
		DurationDisplay = step.DurationDisplay;
		Distance = step.Distance;
		Condition = weather.Condition;
		Temperature = weather.Temperature;
	}

	public DirectionItem(String instruction, String distance,
			String durationDisplay) {
		Instruction = instruction;
		Distance = distance;
		DurationDisplay = durationDisplay;
	}

	public String toString() {

		return "Instruction: " + Html.fromHtml(Instruction) + "\nDistance: " + Distance
				+ "\nDuration: " + DurationDisplay + "\nTemperature: " + Temperature + "\nCondition: " + Condition + "\n\n";
	}

}
