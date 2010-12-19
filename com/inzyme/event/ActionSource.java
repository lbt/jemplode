package com.inzyme.event;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

/**
* ActionSource is can handle firing actionPerformed events to
* ActionListeners.  This is used by any class that wants to
* fire these events so that the action firing code doesn't have
* be rewritten in each class.
*
* @author Mike Schrag
* @version $Revision: 1.1 $
*/
public class ActionSource {
		/**
		* The source of the events (the parent class)
		*/
	private Object mySource;
	
		/**
		* The vector of action listeners
		*/
	private Vector myListeners;
	
		/**
		* Constructs an ActionSource with the given original source.
		*
		* @param _source the parent class (the real source of the events)
		*/
	public ActionSource(Object _source) {
		mySource = _source;
		myListeners = new Vector();
	}

		/**
		* Adds an action listener to receive events.
		*
		* @param _listener the listener to be added
		*/
	public void addActionListener(ActionListener _listener) {
		myListeners.addElement(_listener);
	}

		/**
		* Removes an action listener from receiving events.
		*
		* @param _listener the listener to be removed
		*/
	public void removeActionListener(ActionListener _listener) {
		myListeners.removeElement(_listener);
	}

		/**
		* Fires an actionPerformed event with a null command
		*/
	public void fireActionPerformed() {
		fireActionPerformed(null);
	}
	
		/**
		* Fires an actionPerformed event with the given command.
		*
		* @param _command the command to put in the ActionEvent
		*/
	public void fireActionPerformed(String _command) {
		ActionListener[] listeners;
		synchronized (myListeners) {
			listeners = new ActionListener[myListeners.size()];
			myListeners.copyInto(listeners);
		}
		ActionEvent event = new ActionEvent(mySource, ActionEvent.ACTION_PERFORMED, _command);
		for (int i = 0; i < listeners.length; i ++) {
			listeners[i].actionPerformed(event);
		}
	}
}
