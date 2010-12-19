/* FIDPlaylistListModel - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.model;
import javax.swing.ListModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import com.inzyme.model.Range;

import org.jempeg.nodestore.FIDPlaylist;

public class FIDPlaylistListModel extends AbstractFIDPlaylistModel
    implements ListModel
{
    private boolean myShowSelf;
    private EventListenerList myEventListenerList = new EventListenerList();
    /*synthetic*/ static Class class$0;
    
    public FIDPlaylistListModel(FIDPlaylist _playlist, int _playlistIndex,
				boolean _showSelf) {
	super(_playlist, _playlistIndex);
	myShowSelf = _showSelf;
    }
    
    protected void notifyContentsChanged(int _startIndex, int _endIndex) {
	ListDataEvent event = null;
	Object[] listeners = myEventListenerList.getListenerList();
	for (int i = listeners.length - 2; i >= 0; i -= 2) {
	    Object object = listeners[i];
	    Class var_class = class$0;
	    if (var_class == null) {
		Class var_class_0_;
		try {
		    var_class_0_
			= Class.forName("javax.swing.event.ListDataListener");
		} catch (ClassNotFoundException classnotfoundexception) {
		    NoClassDefFoundError noclassdeffounderror
			= new NoClassDefFoundError;
		    ((UNCONSTRUCTED)noclassdeffounderror).NoClassDefFoundError
			(classnotfoundexception.getMessage());
		    throw noclassdeffounderror;
		}
		var_class = class$0 = var_class_0_;
	    }
	    if (object == var_class) {
		if (event == null)
		    event = new ListDataEvent(this, 0, _startIndex, _endIndex);
		((ListDataListener) listeners[i + 1]).contentsChanged(event);
	    }
	}
    }
    
    protected void notifyStructureChanged() {
	notifyContentsChanged(0, getSize());
    }
    
    protected void notifyChildrenWereInserted(int[] _indexes) {
	ListDataEvent event = null;
	Object[] listeners = myEventListenerList.getListenerList();
	for (int i = listeners.length - 2; i >= 0; i -= 2) {
	    Object object = listeners[i];
	    Class var_class = class$0;
	    if (var_class == null) {
		Class var_class_1_;
		try {
		    var_class_1_
			= Class.forName("javax.swing.event.ListDataListener");
		} catch (ClassNotFoundException classnotfoundexception) {
		    NoClassDefFoundError noclassdeffounderror
			= new NoClassDefFoundError;
		    ((UNCONSTRUCTED)noclassdeffounderror).NoClassDefFoundError
			(classnotfoundexception.getMessage());
		    throw noclassdeffounderror;
		}
		var_class = class$0 = var_class_1_;
	    }
	    if (object == var_class) {
		if (event == null) {
		    Range range = new Range(_indexes);
		    event = new ListDataEvent(this, 1, range.getStart(),
					      range.getEnd());
		}
		((ListDataListener) listeners[i + 1]).intervalAdded(event);
	    }
	}
    }
    
    protected void notifyChildrenWereRemoved(int[] _indexes,
					     Object[] _childModels) {
	ListDataEvent event = null;
	Object[] listeners = myEventListenerList.getListenerList();
	for (int i = listeners.length - 2; i >= 0; i -= 2) {
	    Object object = listeners[i];
	    Class var_class = class$0;
	    if (var_class == null) {
		Class var_class_2_;
		try {
		    var_class_2_
			= Class.forName("javax.swing.event.ListDataListener");
		} catch (ClassNotFoundException classnotfoundexception) {
		    NoClassDefFoundError noclassdeffounderror
			= new NoClassDefFoundError;
		    ((UNCONSTRUCTED)noclassdeffounderror).NoClassDefFoundError
			(classnotfoundexception.getMessage());
		    throw noclassdeffounderror;
		}
		var_class = class$0 = var_class_2_;
	    }
	    if (object == var_class) {
		if (event == null) {
		    Range range = new Range(_indexes);
		    event = new ListDataEvent(this, 2, range.getStart(),
					      range.getEnd());
		}
		((ListDataListener) listeners[i + 1]).intervalRemoved(event);
	    }
	}
    }
    
    protected AbstractFIDPlaylistModel createChildModel(FIDPlaylist _playlist,
							int _index) {
	FIDPlaylistListModel childListModel
	    = new FIDPlaylistListModel(_playlist, _index, false);
	return childListModel;
    }
    
    public int getSize() {
	return super.getSize() + (myShowSelf ? 1 : 0);
    }
    
    public Object getElementAt(int _index) {
	Object obj;
	if (!myShowSelf)
	    obj = getValueAt(_index);
	else if (_index == 0)
	    obj = this;
	else
	    obj = getValueAt(_index - 1);
	return obj;
    }
    
    public void addListDataListener(ListDataListener _listener) {
	EventListenerList eventlistenerlist = myEventListenerList;
	Class var_class = class$0;
	if (var_class == null) {
	    Class var_class_3_;
	    try {
		var_class_3_
		    = Class.forName("javax.swing.event.ListDataListener");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class = class$0 = var_class_3_;
	}
	eventlistenerlist.add(var_class, _listener);
    }
    
    public void removeListDataListener(ListDataListener _listener) {
	EventListenerList eventlistenerlist = myEventListenerList;
	Class var_class = class$0;
	if (var_class == null) {
	    Class var_class_4_;
	    try {
		var_class_4_
		    = Class.forName("javax.swing.event.ListDataListener");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class = class$0 = var_class_4_;
	}
	eventlistenerlist.remove(var_class, _listener);
    }
}
