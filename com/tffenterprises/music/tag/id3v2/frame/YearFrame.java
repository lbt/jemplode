/* YearFrame - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.music.tag.id3v2.frame;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;

public class YearFrame extends TextFrame
    implements DateFrame, Serializable, Cloneable
{
    public static final String MY_ID = "TYER";
    private int year = -1;
    
    YearFrame() {
	super.getHeader().setFrameID("TYER");
    }
    
    public YearFrame(FrameHeader header) {
	super(header);
	header.setFrameID("TYER");
    }
    
    public static int SanitizeYear(int year) {
	if (year < 0)
	    throw new IllegalArgumentException
		      ("Negative dates do not exist for the purpose of keeping track of recording dates");
	if (year < 25)
	    return (short) (2000 + year);
	if (year < 100)
	    return (short) (1900 + year);
	return year;
    }
    
    public synchronized Object clone() {
	YearFrame newFrame = (YearFrame) super.clone();
	if (newFrame != null)
	    newFrame.year = year;
	return newFrame;
    }
    
    public boolean equals(Object other) {
	if (!super.equals(other))
	    return false;
	YearFrame otherFrame = (YearFrame) other;
	if (otherFrame.year != year)
	    return false;
	return true;
    }
    
    public String getText() {
	return Integer.toString(year);
    }
    
    public void setText(String frameText) {
	super.setText(frameText.trim());
	try {
	    year = SanitizeYear(Integer.parseInt(frameText.trim()));
	} catch (NumberFormatException nfe) {
	    super.setText("");
	    year = -1;
	    throw new IllegalArgumentException
		      ("The string \"" + frameText + "\" "
		       + "which was passed to YearFrame."
		       + "setText() could not be parsed " + "for an integer.");
	}
    }
    
    public void setYear(int y) {
	if (y != year) {
	    year = SanitizeYear(y);
	    super.setText(Integer.toString(year));
	}
    }
    
    public void clearDate() {
	setYear(-1);
    }
    
    public boolean isSet() {
	if (year != -1)
	    return true;
	return false;
    }
    
    public void setDate(Calendar c) {
	if (c.isSet(1))
	    setYear(c.get(1));
	else
	    throw new IllegalArgumentException
		      ("The year wasn't set in the Calendar argument of setDate().");
    }
    
    public void setDate(Date d) {
	Calendar c = new GregorianCalendar();
	c.setTime(d);
	setDate(c);
    }
    
    public void setDateRange(Calendar begin, Calendar end) {
	setDate(begin);
    }
    
    public void addDate(Calendar c) {
	setDate(c);
    }
    
    public void addDate(Date d) {
	setDate(d);
    }
    
    public void addDateRange(Calendar begin, Calendar end) {
	setDate(begin);
    }
    
    public Calendar getDate() {
	Calendar c = new GregorianCalendar();
	c.clear();
	if (year != -1)
	    c.set(1, year);
	return c;
    }
    
    public Enumeration getDates() {
	class DateEnumeration implements Enumeration
	{
	    boolean done = !isSet();
	    
	    public boolean hasMoreElements() {
		return !done;
	    }
	    
	    public Object nextElement() {
		if (done)
		    return null;
		done = true;
		return getDate();
	    }
	};
	return new DateEnumeration();
    }
}
