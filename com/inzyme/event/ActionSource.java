/* ActionSource - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

public class ActionSource
{
    private Object mySource;
    private Vector myListeners;
    
    public ActionSource(Object _source) {
	mySource = _source;
	myListeners = new Vector();
    }
    
    public void addActionListener(ActionListener _listener) {
	myListeners.addElement(_listener);
    }
    
    public void removeActionListener(ActionListener _listener) {
	myListeners.removeElement(_listener);
    }
    
    public void fireActionPerformed() {
	fireActionPerformed(null);
    }
    
    public void fireActionPerformed(String _command) {
	Vector vector;
	MONITORENTER (vector = myListeners);
	ActionListener[] listeners;
	MISSING MONITORENTER
	synchronized (vector) {
	    listeners = new ActionListener[myListeners.size()];
	    myListeners.copyInto(listeners);
	}
	ActionEvent event = new ActionEvent(mySource, 1001, _command);
	for (int i = 0; i < listeners.length; i++)
	    listeners[i].actionPerformed(event);
    }
}
