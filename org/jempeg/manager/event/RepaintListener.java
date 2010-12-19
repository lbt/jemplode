package org.jempeg.manager.event;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import javax.swing.JComponent;

import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.nodestore.event.IDatabaseListener;
import org.jempeg.nodestore.event.INodeTagListener;

import com.inzyme.util.Timer;

/**
 * RepaintListener is responsible for triggering a repaint of the 
 * tree and table when certain events occur (like a node becomes
 * dirty or a cut occurs, which needs to gray out the nodes).
 * 
 * @author Mike Schrag
 */
public class RepaintListener implements PropertyChangeListener, IDatabaseListener, INodeTagListener {
	private static final int FAST_REPAINT_TIME = 200;
	private static final int SLOW_REPAINT_TIME = 5000;
	
	private Vector myComponents;
	private Timer myRepaintTimer;
	private Timer mySlowDownTimer;
	private int myImports;
	
	/**
	 * Constructor for RepaintListener.
	 */
	public RepaintListener() {
		myComponents = new Vector();
		myRepaintTimer = new Timer(RepaintListener.FAST_REPAINT_TIME, this, "repaint");
		mySlowDownTimer = new Timer(5000, this, "activityCalmedDown");
	}
	
	public void addComponent(JComponent _component) {
		myComponents.addElement(_component);
	}
	
	public void removeComponent(JComponent _component) {
		myComponents.removeElement(_component);
	}
	
	public void repaint() {
		JComponent[] components;
		synchronized (myComponents) {
			components = new JComponent[myComponents.size()];
			myComponents.copyInto(components);
		}
		for (int i = 0; i < components.length; i ++) {
			components[i].repaint();
		}
	}
	
	public synchronized void repaintMark() {
		if (!myRepaintTimer.isSleeping()) {
			repaint();
		}
		myRepaintTimer.mark();
	}
	
	public void propertyChange(PropertyChangeEvent _event) {
		repaintMark();
	}
	
	public void activityCalmedDown() {
		myImports = 0;
		myRepaintTimer.setSleepTime(RepaintListener.FAST_REPAINT_TIME);
	}
	
	public void freeSpaceChanged(PlayerDatabase _playerDatabase, long _totalSpace, long _freeSpace) {
	}
  
  public void databaseCleared(PlayerDatabase _playerDatabase) {
  }
	
	public void nodeAdded(IFIDNode _node) {
		myImports ++;
		mySlowDownTimer.mark();
		
		// if we've added a lot of nodes since the slow down timer 
		// last woke up, that means we're probably importing, so we want
		// to chill out on the repaints
		if (myImports >= 5) {
			myRepaintTimer.setSleepTime(RepaintListener.SLOW_REPAINT_TIME);
		}
	}

	public void nodeIdentified(IFIDNode _node) {
	}

	public void nodeRemoved(IFIDNode _node) {
	}

	public void beforeNodeTagModified(IFIDNode _node, String _tag, String _oldvalue, String _newValue) {
	}

	public void afterNodeTagModified(IFIDNode _node, String _tag, String _oldvalue, String _newValue) {
		repaintMark();
	}
}
