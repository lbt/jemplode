/* GenericContainer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.music.tag.id3v2.frame;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

import com.tffenterprises.music.tag.id3v2.Frame;

public class GenericContainer extends Container
    implements Serializable, Cloneable
{
    private Vector frameVector;
    
    public GenericContainer() {
	frameVector = new Vector();
    }
    
    public GenericContainer(String id) throws IllegalArgumentException {
	super(id);
	frameVector = new Vector();
    }
    
    public Frame addFrame(Frame frame) {
	frameVector.addElement(frame);
	return null;
    }
    
    public Frame removeFrame(Frame frame) {
	if (frameVector.removeElement(frame))
	    return frame;
	return null;
    }
    
    public Enumeration frames() {
	return frameVector.elements();
    }
    
    public boolean isEmpty() {
	return frameVector.isEmpty();
    }
    
    public int size() {
	return frameVector.size();
    }
    
    public boolean contains(Frame frame) {
	return frameVector.contains(frame);
    }
}
