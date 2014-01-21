/**
 * 
 */
package com.weatherdrive.model;

/**
 * @author Derek
 * 
 */
public class Step {

	int StepNumber;
	double Latitude, Longitude; // Coordinates
	String Instruction; // Step
	String DurationDisplay;
	String Distance;
	int Duration;

	public Step(int stepNumber, double latitude, double longitude,
			String instruction, String distance, int duration,
			String durationDisplay) {
		StepNumber = stepNumber;
		Latitude = latitude;
		Longitude = longitude;
		Instruction = instruction;
		DurationDisplay = durationDisplay;
		Distance = distance;
		Duration = duration;
	}

}
