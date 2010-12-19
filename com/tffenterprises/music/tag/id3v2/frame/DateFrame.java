/* DateFrame - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.music.tag.id3v2.frame;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

public interface DateFrame
{
    public void clearDate();
    
    public boolean isSet();
    
    public void addDate(Calendar calendar);
    
    public void addDate(Date date);
    
    public void addDateRange(Calendar calendar, Calendar calendar_0_);
    
    public void setDate(Calendar calendar);
    
    public void setDate(Date date);
    
    public void setDateRange(Calendar calendar, Calendar calendar_1_);
    
    public Calendar getDate();
    
    public Enumeration getDates();
}
