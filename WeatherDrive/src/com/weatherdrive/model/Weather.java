package com.weatherdrive.model;

public class Weather {

	int StepNumber;
	int Temperature; // Temperature at specific step
	String Condition; // Condition at specific step

	public Weather(int stepNumber, int temperature, String condition) {
		StepNumber = stepNumber;
		Temperature = temperature;
		Condition = condition;
	}

}
