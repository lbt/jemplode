/* ReasonsTableModel - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.table;
import javax.swing.table.AbstractTableModel;

import com.inzyme.model.Reason;

public class ReasonsTableModel extends AbstractTableModel
{
    private Reason[] myReasons;
    /*synthetic*/ static Class class$0;
    
    public ReasonsTableModel(Reason[] _reasons) {
	myReasons = _reasons;
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
	    name = "Failed/Skipped Filename";
	    break;
	case 1:
	    name = "Reason";
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
	return myReasons.length;
    }
    
    public Object getValueAt(int _rowIndex, int _columnIndex) {
	Reason reason = myReasons[_rowIndex];
	String value;
	switch (_columnIndex) {
	case 0:
	    value = reason.getFileName();
	    break;
	case 1:
	    value = reason.getReason();
	    break;
	default:
	    value = "";
	}
	return value;
    }
}
