/* KeyedContainer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.music.tag.id3v2.frame;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

import com.tffenterprises.music.tag.id3v2.Frame;

public class KeyedContainer extends Container
    implements Serializable, Cloneable
{
    private Hashtable frameTable;
    /*synthetic*/ static Class class$0;
    
    public KeyedContainer() {
	frameTable = new Hashtable();
    }
    
    public KeyedContainer(String id) throws IllegalArgumentException {
	super(id);
	frameTable = new Hashtable();
	Class frameClass = Frame.GetFrameClassForID(id);
	Class var_class = class$0;
	if (var_class == null) {
	    Class var_class_0_;
	    try {
		var_class_0_
		    = (Class.forName
		       ("com.tffenterprises.music.tag.id3v2.frame.KeyedFrame"));
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class = class$0 = var_class_0_;
	}
	if (!var_class.isAssignableFrom(frameClass))
	    throw new IllegalArgumentException
		      ("This ID (" + id + ") cannot be "
		       + "handled by a KeyedFrame class.");
    }
    
    public Frame addFrame(Frame frame) throws IllegalArgumentException {
	if (frame.getFrameID().equals(getFrameID()))
	    return ((Frame)
		    frameTable.put(((KeyedFrame) frame).getDescription(),
				   frame));
	throw new IllegalArgumentException("Attempted to add frame of type "
					   + frame.getFrameID()
					   + " to Container " + "of type "
					   + getFrameID());
    }
    
    public Frame getFrame(String description) {
	return (Frame) frameTable.get(description);
    }
    
    public Frame removeFrame(String description) {
	return (Frame) frameTable.remove(description);
    }
    
    public Frame removeFrame(Frame frame) throws IllegalArgumentException {
	if (contains(frame))
	    return ((Frame)
		    frameTable.remove(((KeyedFrame) frame).getDescription()));
	return null;
    }
    
    public Enumeration frames() {
	return frameTable.elements();
    }
    
    public Enumeration descriptions() {
	return frameTable.keys();
    }
    
    public boolean isEmpty() {
	return frameTable.isEmpty();
    }
    
    public int size() {
	return frameTable.size();
    }
    
    public boolean contains(Frame frame) {
	if (frame.getFrameID().equals(getFrameID())) {
	    Frame f = ((Frame)
		       frameTable.get(((KeyedFrame) frame).getDescription()));
	    if (f != null)
		return f.equals(frame);
	}
	return false;
    }
    
    public boolean containsDescription(String description) {
	return frameTable.containsKey(description);
    }
}
