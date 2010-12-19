/* FractionFrame - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.music.tag.id3v2.frame;
import java.io.Serializable;
import java.util.StringTokenizer;

public class FractionFrame extends TextFrame implements Serializable, Cloneable
{
    private short item = -1;
    private short total = -1;
    
    public FractionFrame() {
	/* empty */
    }
    
    public FractionFrame(FrameHeader header) {
	super(header);
    }
    
    public FractionFrame(FrameHeader header, short item, short total)
	throws IllegalArgumentException {
	this(header);
	this.item = item;
	this.total = total;
    }
    
    public synchronized Object clone() {
	FractionFrame newFrame = (FractionFrame) super.clone();
	if (newFrame != null) {
	    newFrame.item = item;
	    newFrame.total = total;
	}
	return newFrame;
    }
    
    public boolean equals(Object other) {
	if (!super.equals(other))
	    return false;
	FractionFrame otherFrame = (FractionFrame) other;
	if (otherFrame.item != item || otherFrame.total != total)
	    return false;
	return true;
    }
    
    public String toString() {
	StringBuffer sb = new StringBuffer(super.toString() + " (");
	if (item > 0) {
	    sb.append(getPartName() + " " + item);
	    if (total > 0)
		sb.append(" of " + total);
	} else
	    sb.append("unknown");
	return sb.append(')').toString();
    }
    
    public String getText() {
	if (item > 0) {
	    if (item <= total)
		return new String(item + "/" + total);
	    return new String("" + item);
	}
	return "";
    }
    
    public void setText(String frameText) {
	super.setText(frameText);
	StringTokenizer st = new StringTokenizer(frameText, "/");
	try {
	    if (st.hasMoreTokens())
		setItem(Short.parseShort(st.nextToken()));
	    if (st.hasMoreTokens())
		setTotal(Short.parseShort(st.nextToken()));
	} catch (NumberFormatException numberformatexception) {
	    /* empty */
	}
	sanitize();
    }
    
    public short getItem() {
	return item;
    }
    
    public void setItem(short i) {
	if (i != item) {
	    item = i;
	    sanitize();
	    setChanged(true);
	}
    }
    
    public short getTotal() {
	return total;
    }
    
    public void setTotal(short t) {
	if (t != total) {
	    total = t;
	    sanitize();
	    setChanged(true);
	}
    }
    
    private void sanitize() {
	if (item < 1) {
	    item = (short) -1;
	    total = (short) -1;
	} else if (total < item || total < 1)
	    total = (short) -1;
	StringBuffer sb = new StringBuffer();
	if (item >= 1) {
	    sb.append("" + item);
	    if (total >= 1)
		sb.append("/" + total);
	}
	super.setText(sb.toString());
    }
    
    public String getPartName() {
	return "item";
    }
}
