package ratings;

import java.util.LinkedList;
import java.util.List;

import numbers.RandomNumber;

public class EventValue {
	
	private EventValueType type;
	private double mean; //the average value  - the higher the number the more likely this event happens
	private List <Modifier> modifiers = new LinkedList <Modifier> ();
	
	public EventValue (int id, double mean) {
		this.mean = mean;
		this.type = getType(id);
	}
	
	public EventValue (EventValueType type, double mean) {
		this.type = type;
		this.mean = mean;
	}
	
	
	//change by diff.  can be  negative
	public void changeMeanBy (double diff) {
		mean += diff;
	}
	
	public double get () {
		return mean;
	}
	
	public void addModifier (int id, double change) {
		modifiers.add(new Modifier(id,change));
	}
	
	//if we get less than the percent, we have a success. aka what this object stands for happened
	public boolean success () {
		int roll = RandomNumber.roll();
		return roll < mean;
	}
	
	private EventValueType getType (int num) {
		
		switch (num) {
			case 0:
				return EventValueType.SWINGATSTRIKE;
			case 1:
				return EventValueType.CONTACTPERCENT;
			case 2:
				return EventValueType.CONTACTQUALITY;
			case 3:
				return EventValueType.SWINGATBALL;
			case 4:
				return EventValueType.LOCATIONDIFFERENCE;
			default:
				return null;
		}
		
	}
	
}
