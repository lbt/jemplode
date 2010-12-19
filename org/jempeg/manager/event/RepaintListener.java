/* RepaintListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.event;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import javax.swing.JComponent;

import com.inzyme.util.Timer;

import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.nodestore.event.IDatabaseListener;
import org.jempeg.nodestore.event.INodeTagListener;

public class RepaintListener
    implements PropertyChangeListener, IDatabaseListener, INodeTagListener
{
    private static final int FAST_REPAINT_TIME = 200;
    private static final int SLOW_REPAINT_TIME = 5000;
    private Vector myComponents = new Vector();
    private Timer myRepaintTimer = new Timer(200, this, "repaint");
    private Timer mySlowDownTimer
	= new Timer(5000, this, "activityCalmedDown");
    private int myImports;
    
    public void addComponent(JComponent _component) {
	myComponents.addElement(_component);
    }
    
    public void removeComponent(JComponent _component) {
	myComponents.removeElement(_component);
    }
    
    public void repaint() {
	Vector vector;
	MONITORENTER (vector = myComponents);
	JComponent[] components;
	MISSING MONITORENTER
	synchronized (vector) {
	    components = new JComponent[myComponents.size()];
	    myComponents.copyInto(components);
	}
	for (int i = 0; i < components.length; i++)
	    components[i].repaint();
    }
    
    public synchronized void repaintMark() {
	if (!myRepaintTimer.isSleeping())
	    repaint();
	myRepaintTimer.mark();
    }
    
    public void propertyChange(PropertyChangeEvent _event) {
	repaintMark();
    }
    
    public void activityCalmedDown() {
	myImports = 0;
	myRepaintTimer.setSleepTime(200);
    }
    
    public void freeSpaceChanged(PlayerDatabase _playerDatabase,
				 long _totalSpace, long _freeSpace) {
	/* empty */
    }
    
    public void databaseCleared(PlayerDatabase _playerDatabase) {
	/* empty */
    }
    
    public void nodeAdded(IFIDNode _node) {
	myImports++;
	mySlowDownTimer.mark();
	if (myImports >= 5)
	    myRepaintTimer.setSleepTime(5000);
    }
    
    public void nodeIdentified(IFIDNode _node) {
	/* empty */
    }
    
    public void nodeRemoved(IFIDNode _node) {
	/* empty */
    }
    
    public void beforeNodeTagModified(IFIDNode _node, String _tag,
				      String _oldvalue, String _newValue) {
	/* empty */
    }
    
    public void afterNodeTagModified(IFIDNode _node, String _tag,
				     String _oldvalue, String _newValue) {
	repaintMark();
    }
}
