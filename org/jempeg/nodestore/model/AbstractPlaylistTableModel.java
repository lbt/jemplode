/* AbstractPlaylistTableModel - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.model;
import javax.swing.table.AbstractTableModel;

import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;

public abstract class AbstractPlaylistTableModel extends AbstractTableModel
    implements IPlaylistTableModel
{
    private String[] myColumnTagNames;
    /*synthetic*/ static Class class$0;
    /*synthetic*/ static Class class$1;
    /*synthetic*/ static Class class$2;
    
    public AbstractPlaylistTableModel(String[] _columnTagNames) {
	myColumnTagNames = _columnTagNames;
    }
    
    public String[] getColumnTagNames() {
	return myColumnTagNames;
    }
    
    public void setColumnTagNames(String[] _columnTagNames) {
	myColumnTagNames = _columnTagNames;
	fireTableStructureChanged();
    }
    
    public String getColumnName(int _column) {
	NodeTag nodeTag = getNodeTag(_column);
	String columnName = nodeTag.getDescription();
	return columnName;
    }
    
    public int getColumnCount() {
	int columnCount = myColumnTagNames.length;
	return columnCount;
    }
    
    public Object getValueAt(int _row, int _column) {
	Object value = null;
	try {
	    IFIDNode rowNode = getNodeAt(_row);
	    NodeTag nodeTag = getNodeTag(_column);
	    FIDPlaylist playlist = getPlaylist();
	    if (playlist == null)
		value = nodeTag.getDisplayValue(rowNode);
	    else
		value = nodeTag.getDisplayValue(playlist, _row);
	} catch (ArrayIndexOutOfBoundsException e) {
	    try {
		Class sortClass = getSortColumnClass(_column);
		Class var_class = sortClass;
		Class var_class_0_ = class$0;
		if (var_class_0_ == null) {
		    Class var_class_1_;
		    try {
			var_class_1_ = Class.forName("java.lang.String");
		    } catch (ClassNotFoundException classnotfoundexception) {
			NoClassDefFoundError noclassdeffounderror
			    = new NoClassDefFoundError;
			((UNCONSTRUCTED)noclassdeffounderror)
			    .NoClassDefFoundError
			    (classnotfoundexception.getMessage());
			throw noclassdeffounderror;
		    }
		    var_class_0_ = class$0 = var_class_1_;
		}
		if (var_class == var_class_0_)
		    value = "";
		else {
		    Class var_class_2_ = sortClass;
		    Class var_class_3_ = class$1;
		    if (var_class_3_ == null) {
			Class var_class_4_;
			try {
			    var_class_4_ = Class.forName("java.lang.Integer");
			} catch (ClassNotFoundException classnotfoundexception) {
			    NoClassDefFoundError noclassdeffounderror
				= new NoClassDefFoundError;
			    ((UNCONSTRUCTED)noclassdeffounderror)
				.NoClassDefFoundError
				(classnotfoundexception.getMessage());
			    throw noclassdeffounderror;
			}
			var_class_3_ = class$1 = var_class_4_;
		    }
		    if (var_class_2_ == var_class_3_)
			value = new Integer(0);
		    else {
			Class var_class_5_ = sortClass;
			Class var_class_6_ = class$2;
			if (var_class_6_ == null) {
			    Class var_class_7_;
			    try {
				var_class_7_ = Class.forName("java.lang.Long");
			    } catch (ClassNotFoundException classnotfoundexception) {
				NoClassDefFoundError noclassdeffounderror
				    = new NoClassDefFoundError;
				((UNCONSTRUCTED)noclassdeffounderror)
				    .NoClassDefFoundError
				    (classnotfoundexception.getMessage());
				throw noclassdeffounderror;
			    }
			    var_class_6_ = class$2 = var_class_7_;
			}
			if (var_class_5_ == var_class_6_)
			    value = new Long(0L);
		    }
		}
	    } catch (ArrayIndexOutOfBoundsException e2) {
		value = "?";
	    }
	}
	return value;
    }
    
    public Class getColumnClass(int _column) {
	NodeTag nodeTag = getNodeTag(_column);
	Class columnClass = nodeTag.getType();
	return columnClass;
    }
    
    public Class getSortColumnClass(int _column) {
	NodeTag nodeTag = getNodeTag(_column);
	Class columnClass = nodeTag.getSortType();
	return columnClass;
    }
    
    public Object getSortValueAt(int _row, int _column) {
	Object value = null;
	try {
	    IFIDNode rowNode = getNodeAt(_row);
	    NodeTag nodeTag = getNodeTag(_column);
	    FIDPlaylist playlist = getPlaylist();
	    if (playlist == null)
		value = nodeTag.getValue(rowNode);
	    else
		value = nodeTag.getValue(playlist, _row);
	    if (value == null)
		value = "";
	} catch (ArrayIndexOutOfBoundsException e) {
	    Class sortClass = getSortColumnClass(_column);
	    Class var_class = sortClass;
	    Class var_class_8_ = class$0;
	    if (var_class_8_ == null) {
		Class var_class_9_;
		try {
		    var_class_9_ = Class.forName("java.lang.String");
		} catch (ClassNotFoundException classnotfoundexception) {
		    NoClassDefFoundError noclassdeffounderror
			= new NoClassDefFoundError;
		    ((UNCONSTRUCTED)noclassdeffounderror).NoClassDefFoundError
			(classnotfoundexception.getMessage());
		    throw noclassdeffounderror;
		}
		var_class_8_ = class$0 = var_class_9_;
	    }
	    if (var_class == var_class_8_)
		value = "";
	    else {
		Class var_class_10_ = sortClass;
		Class var_class_11_ = class$1;
		if (var_class_11_ == null) {
		    Class var_class_12_;
		    try {
			var_class_12_ = Class.forName("java.lang.Integer");
		    } catch (ClassNotFoundException classnotfoundexception) {
			NoClassDefFoundError noclassdeffounderror
			    = new NoClassDefFoundError;
			((UNCONSTRUCTED)noclassdeffounderror)
			    .NoClassDefFoundError
			    (classnotfoundexception.getMessage());
			throw noclassdeffounderror;
		    }
		    var_class_11_ = class$1 = var_class_12_;
		}
		if (var_class_10_ == var_class_11_)
		    value = new Integer(0);
		else {
		    Class var_class_13_ = sortClass;
		    Class var_class_14_ = class$2;
		    if (var_class_14_ == null) {
			Class var_class_15_;
			try {
			    var_class_15_ = Class.forName("java.lang.Long");
			} catch (ClassNotFoundException classnotfoundexception) {
			    NoClassDefFoundError noclassdeffounderror
				= new NoClassDefFoundError;
			    ((UNCONSTRUCTED)noclassdeffounderror)
				.NoClassDefFoundError
				(classnotfoundexception.getMessage());
			    throw noclassdeffounderror;
			}
			var_class_14_ = class$2 = var_class_15_;
		    }
		    if (var_class_13_ == var_class_14_)
			value = new Long(0L);
		}
	    }
	}
	return value;
    }
    
    public NodeTag getNodeTag(int _columnNum) {
	NodeTag nodeTag = NodeTag.getNodeTag(myColumnTagNames[_columnNum]);
	return nodeTag;
    }
    
    public int getSize() {
	return getRowCount();
    }
    
    protected abstract FIDPlaylist getPlaylist();
}
