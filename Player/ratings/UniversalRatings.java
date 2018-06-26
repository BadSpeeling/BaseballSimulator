package ratings;

//holds average ratings.  these are not player ratings - these are the average percentages that all players are bounds by.
//how many sd's away from the mean of a rating determines how much better/worse a given player will be at this EventValue
public class UniversalRatings {
	
	public static EventValue swingAtStrikePercent = new EventValue (EventValueType.SWINGATSTRIKE, 55);
	public static EventValue contactPercent = new EventValue (EventValueType.CONTACTPERCENT, 75);
	public static EventValue swingAtBallPercent = new EventValue (EventValueType.SWINGATBALL, 15);

}
