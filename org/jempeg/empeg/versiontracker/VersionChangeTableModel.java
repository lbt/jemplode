/* VersionChangeTableModel - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.versiontracker;
import javax.swing.table.AbstractTableModel;

public class VersionChangeTableModel extends AbstractTableModel
{
    private VersionChange[] myChanges;
    /*synthetic*/ static Class class$0;
    
    public VersionChangeTableModel(VersionChange[] _changes) {
	myChanges = _changes;
    }
    
    public Class getColumnClass(int _columnIndex) {
	Class var_class = class$0;
	if (var_class == null) {
	    Class var_class_0_;
	    try {
		var_class_0_ = Class.forName("java.lang.String");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class = class$0 = var_class_0_;
	}
	return var_class;
    }
    
    public String getColumnName(int _columnIndex) {
	String name;
	switch (_columnIndex) {
	case 0:
	    name = "Version";
	    break;
	case 1:
	    name = "Feature";
	    break;
	default:
	    name = "";
	}
	return name;
    }
    
    public int getColumnCount() {
	return 2;
    }
    
    public int getRowCount() {
	return myChanges.length;
    }
    
    public Object getValueAt(int _rowIndex, int _columnIndex) {
	VersionChange change = myChanges[_rowIndex];
	String value;
	switch (_columnIndex) {
	case 0:
	    value = change.getVersion();
	    break;
	case 1:
	    value = change.getFeature();
	    break;
	default:
	    value = "";
	}
	return value;
    }
}
