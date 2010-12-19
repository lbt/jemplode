package com.inzyme.format;


/**
 * TimeFormat is responsible for turning a time (in milliseconds) into a human
 * readable format.  For instance: 1:20, etc.
 *
 * @author Mike Schrag
 */
public class TimeFormat {
	/**
	 * Constructor for TimeFormat.
	 */
	public TimeFormat() {
		super();
	}

	/**
	 * Returns an instance of a TimeFormat
	 */
	public static TimeFormat getInstance() {
		return new TimeFormat();
	}

	public String format(long _lengthInMillis) {
		long seconds  = (_lengthInMillis / 1000) % 60;
		long minutes  = _lengthInMillis / 60000;
		StringBuffer sb = new StringBuffer();
		sb.append(minutes);
		sb.append(':');
		if (seconds < 10) {
			sb.append('0');
		}
		sb.append(seconds);
		return sb.toString();
	}
}
