/* TimeFormat - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.format;

public class TimeFormat
{
    public static TimeFormat getInstance() {
	return new TimeFormat();
    }
    
    public String format(long _lengthInMillis) {
	long seconds = _lengthInMillis / 1000L % 60L;
	long minutes = _lengthInMillis / 60000L;
	StringBuffer sb = new StringBuffer();
	sb.append(minutes);
	sb.append(':');
	if (seconds < 10L)
	    sb.append('0');
	sb.append(seconds);
	return sb.toString();
    }
}
