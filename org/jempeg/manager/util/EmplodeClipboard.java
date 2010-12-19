/* EmplodeClipboard - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.util;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import com.inzyme.container.ContainerSelection;
import com.inzyme.container.IContainer;
import com.inzyme.progress.IConfirmationListener;
import com.inzyme.progress.IProgressListener;
import com.inzyme.util.Debug;

import org.jempeg.nodestore.model.ContainerModifierFactory;
import org.jempeg.nodestore.model.IContainerModifier;

public class EmplodeClipboard
{
    public static final String SELECTION_PROPERTY = "selection";
    private static EmplodeClipboard myInstance;
    private PropertyChangeSupport myPropertyChangeSupport
	= new PropertyChangeSupport(this);
    private ContainerSelection mySelection;
    private boolean myCut;
    
    protected EmplodeClipboard() {
	/* empty */
    }
    
    public void addPropertyChangeListener(PropertyChangeListener _listener) {
	myPropertyChangeSupport.addPropertyChangeListener(_listener);
    }
    
    public void removePropertyChangeListener
	(PropertyChangeListener _listener) {
	myPropertyChangeSupport.removePropertyChangeListener(_listener);
    }
    
    public static synchronized EmplodeClipboard getInstance() {
	if (myInstance == null)
	    myInstance = new EmplodeClipboard();
	return myInstance;
    }
    
    public synchronized boolean isCut() {
	return myCut;
    }
    
    public synchronized boolean isCut(Object _obj) {
	if (myCut && mySelection != null && mySelection.contains(_obj))
	    return true;
	return false;
    }
    
    public synchronized void cut(ContainerSelection _selection) {
	Debug.println(2, "Cutting " + _selection + " to clipboard...");
	clear();
	myCut = true;
	setSelection(_selection);
    }
    
    public synchronized void copy(ContainerSelection _selection) {
	Debug.println(2, "Copying " + _selection + " to clipboard...");
	clear();
	myCut = false;
	setSelection(_selection);
    }
    
    public synchronized int[] paste
	(IContainerModifier _containerModifier,
	 IConfirmationListener _confirmationListener, boolean _deepCopy,
	 IProgressListener _progressListener) {
	Debug.println(2, ("Pasting from clipboard into "
			  + _containerModifier.getTargetContainer() + "..."));
	int[] indexes;
	if (mySelection != null) {
	    if (myCut) {
		indexes = _containerModifier.moveTo(mySelection,
						    _confirmationListener,
						    _progressListener);
		clear();
	    } else
		indexes
		    = _containerModifier.copyTo(mySelection,
						_confirmationListener,
						_deepCopy, _progressListener);
	} else
	    indexes = new int[0];
	return indexes;
    }
    
    public synchronized int[] paste
	(IContainer _targetContainer,
	 IConfirmationListener _confirmationListener, boolean _deepCopy,
	 IProgressListener _progressListener) {
	IContainerModifier nodeModifier
	    = ContainerModifierFactory.getInstance(_targetContainer);
	int[] indexes = paste(nodeModifier, _confirmationListener, _deepCopy,
			      _progressListener);
	return indexes;
    }
    
    public synchronized void clear() {
	Debug.println(2, "Clearing clipboard");
	setSelection(null);
    }
    
    public ContainerSelection getSelection() {
	return mySelection;
    }
    
    protected void setSelection(ContainerSelection _selection) {
	ContainerSelection oldSelection = mySelection;
	mySelection = _selection;
	myPropertyChangeSupport.firePropertyChange("selection", oldSelection,
						   _selection);
    }
}
