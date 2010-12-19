/* ComponentConfiguration - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.ui;
import java.lang.reflect.Constructor;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import com.inzyme.util.Debug;

import org.jempeg.ApplicationContext;
import org.jempeg.manager.event.ColumnWidthListener;
import org.jempeg.manager.event.PlaylistTableSelectionListener;
import org.jempeg.manager.event.SelectedContainerListener;
import org.jempeg.manager.event.TableKeyListener;
import org.jempeg.manager.event.TableModelCleanupListener;
import org.jempeg.manager.event.TableSortListener;

public class ComponentConfiguration
{
    /*synthetic*/ static Class class$0;
    /*synthetic*/ static Class class$1;
    /*synthetic*/ static Class class$2;
    /*synthetic*/ static Class class$3;
    /*synthetic*/ static Class class$4;
    /*synthetic*/ static Class class$5;
    /*synthetic*/ static Class class$6;
    /*synthetic*/ static Class class$7;
    /*synthetic*/ static Class class$8;
    /*synthetic*/ static Class class$9;
    
    public static void configureTable(ApplicationContext _context,
				      JTable _table) {
	_table.setColumnSelectionAllowed(false);
	_table.addKeyListener(new TableKeyListener());
	JTable jtable = _table;
	Class var_class = class$0;
	if (var_class == null) {
	    Class var_class_0_;
	    try {
		var_class_0_ = Class.forName("java.lang.Object");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class = class$0 = var_class_0_;
	}
	PlaylistTableCellRenderer playlisttablecellrenderer
	    = new PlaylistTableCellRenderer;
	JTable jtable_1_ = _table;
	Class var_class_2_ = class$0;
	if (var_class_2_ == null) {
	    Class var_class_3_;
	    try {
		var_class_3_ = Class.forName("java.lang.Object");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_2_ = class$0 = var_class_3_;
	}
	((UNCONSTRUCTED)playlisttablecellrenderer).PlaylistTableCellRenderer
	    (jtable_1_.getDefaultRenderer(var_class_2_));
	jtable.setDefaultRenderer(var_class, playlisttablecellrenderer);
	JTable jtable_4_ = _table;
	Class var_class_5_ = class$1;
	if (var_class_5_ == null) {
	    Class var_class_6_;
	    try {
		var_class_6_ = Class.forName("java.lang.Integer");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_5_ = class$1 = var_class_6_;
	}
	PlaylistTableCellRenderer playlisttablecellrenderer_7_
	    = new PlaylistTableCellRenderer;
	JTable jtable_8_ = _table;
	Class var_class_9_ = class$1;
	if (var_class_9_ == null) {
	    Class var_class_10_;
	    try {
		var_class_10_ = Class.forName("java.lang.Integer");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_9_ = class$1 = var_class_10_;
	}
	((UNCONSTRUCTED)playlisttablecellrenderer_7_).PlaylistTableCellRenderer
	    (jtable_8_.getDefaultRenderer(var_class_9_));
	jtable_4_.setDefaultRenderer(var_class_5_,
				     playlisttablecellrenderer_7_);
	_table.getSelectionModel().addListSelectionListener
	    (new PlaylistTableSelectionListener(_context, _table));
	JTableHeader tableHeader = _table.getTableHeader();
	try {
	    Class var_class_11_ = class$2;
	    if (var_class_11_ == null) {
		Class var_class_12_;
		try {
		    var_class_12_
			= Class.forName("javax.swing.table.JTableHeader");
		} catch (ClassNotFoundException classnotfoundexception) {
		    NoClassDefFoundError noclassdeffounderror
			= new NoClassDefFoundError;
		    ((UNCONSTRUCTED)noclassdeffounderror).NoClassDefFoundError
			(classnotfoundexception.getMessage());
		    throw noclassdeffounderror;
		}
		var_class_11_ = class$2 = var_class_12_;
	    }
	    TableCellRenderer oldTableHeaderRenderer
		= ((TableCellRenderer)
		   var_class_11_.getMethod("getDefaultRenderer", null)
		       .invoke(tableHeader, null));
	    TableCellRenderer newTableHeaderRenderer
		= new SortedTableHeaderRenderer(oldTableHeaderRenderer);
	    Class var_class_13_ = class$2;
	    if (var_class_13_ == null) {
		Class var_class_14_;
		try {
		    var_class_14_
			= Class.forName("javax.swing.table.JTableHeader");
		} catch (ClassNotFoundException classnotfoundexception) {
		    NoClassDefFoundError noclassdeffounderror
			= new NoClassDefFoundError;
		    ((UNCONSTRUCTED)noclassdeffounderror).NoClassDefFoundError
			(classnotfoundexception.getMessage());
		    throw noclassdeffounderror;
		}
		var_class_13_ = class$2 = var_class_14_;
	    }
	    String string = "setDefaultRenderer";
	    Class[] var_classes = new Class[1];
	    int i = 0;
	    Class var_class_15_ = class$3;
	    if (var_class_15_ == null) {
		Class var_class_16_;
		try {
		    var_class_16_
			= Class.forName("javax.swing.table.TableCellRenderer");
		} catch (ClassNotFoundException classnotfoundexception) {
		    NoClassDefFoundError noclassdeffounderror
			= new NoClassDefFoundError;
		    ((UNCONSTRUCTED)noclassdeffounderror).NoClassDefFoundError
			(classnotfoundexception.getMessage());
		    throw noclassdeffounderror;
		}
		var_class_15_ = class$3 = var_class_16_;
	    }
	    var_classes[i] = var_class_15_;
	    var_class_13_.getMethod(string, var_classes)
		.invoke(tableHeader, new Object[] { newTableHeaderRenderer });
	} catch (Throwable throwable) {
	    /* empty */
	}
	tableHeader.setReorderingAllowed(false);
	tableHeader.addMouseListener(new TableSortListener());
	_table.addPropertyChangeListener(new ColumnWidthListener(_table));
	_table.addPropertyChangeListener
	    (new TableModelCleanupListener(_context));
	_context.addContextListener(new SelectedContainerListener(_context,
								  _table));
	try {
	    Class var_class_17_ = class$4;
	    if (var_class_17_ == null) {
		Class var_class_18_;
		try {
		    var_class_18_
			= (Class.forName
			   ("org.jempeg.manager.event.TableDropTargetListener"));
		} catch (ClassNotFoundException classnotfoundexception) {
		    NoClassDefFoundError noclassdeffounderror
			= new NoClassDefFoundError;
		    ((UNCONSTRUCTED)noclassdeffounderror).NoClassDefFoundError
			(classnotfoundexception.getMessage());
		    throw noclassdeffounderror;
		}
		var_class_17_ = class$4 = var_class_18_;
	    }
	    Class[] var_classes = new Class[2];
	    int i = 0;
	    Class var_class_19_ = class$5;
	    if (var_class_19_ == null) {
		Class var_class_20_;
		try {
		    var_class_20_
			= Class.forName("org.jempeg.ApplicationContext");
		} catch (ClassNotFoundException classnotfoundexception) {
		    NoClassDefFoundError noclassdeffounderror
			= new NoClassDefFoundError;
		    ((UNCONSTRUCTED)noclassdeffounderror).NoClassDefFoundError
			(classnotfoundexception.getMessage());
		    throw noclassdeffounderror;
		}
		var_class_19_ = class$5 = var_class_20_;
	    }
	    var_classes[i] = var_class_19_;
	    int i_21_ = 1;
	    Class var_class_22_ = class$6;
	    if (var_class_22_ == null) {
		Class var_class_23_;
		try {
		    var_class_23_ = Class.forName("javax.swing.JTable");
		} catch (ClassNotFoundException classnotfoundexception) {
		    NoClassDefFoundError noclassdeffounderror
			= new NoClassDefFoundError;
		    ((UNCONSTRUCTED)noclassdeffounderror).NoClassDefFoundError
			(classnotfoundexception.getMessage());
		    throw noclassdeffounderror;
		}
		var_class_22_ = class$6 = var_class_23_;
	    }
	    var_classes[i_21_] = var_class_22_;
	    Object tableDropTargetListener
		= var_class_17_.getConstructor(var_classes)
		      .newInstance(new Object[] { _context, _table });
	    Class var_class_24_ = class$7;
	    if (var_class_24_ == null) {
		Class var_class_25_;
		try {
		    var_class_25_ = Class.forName("java.awt.dnd.DropTarget");
		} catch (ClassNotFoundException classnotfoundexception) {
		    NoClassDefFoundError noclassdeffounderror
			= new NoClassDefFoundError;
		    ((UNCONSTRUCTED)noclassdeffounderror).NoClassDefFoundError
			(classnotfoundexception.getMessage());
		    throw noclassdeffounderror;
		}
		var_class_24_ = class$7 = var_class_25_;
	    }
	    Class[] var_classes_26_ = new Class[2];
	    int i_27_ = 0;
	    Class var_class_28_ = class$8;
	    if (var_class_28_ == null) {
		Class var_class_29_;
		try {
		    var_class_29_ = Class.forName("java.awt.Component");
		} catch (ClassNotFoundException classnotfoundexception) {
		    NoClassDefFoundError noclassdeffounderror
			= new NoClassDefFoundError;
		    ((UNCONSTRUCTED)noclassdeffounderror).NoClassDefFoundError
			(classnotfoundexception.getMessage());
		    throw noclassdeffounderror;
		}
		var_class_28_ = class$8 = var_class_29_;
	    }
	    var_classes_26_[i_27_] = var_class_28_;
	    int i_30_ = 1;
	    Class var_class_31_ = class$9;
	    if (var_class_31_ == null) {
		Class var_class_32_;
		try {
		    var_class_32_
			= Class.forName("java.awt.dnd.DropTargetListener");
		} catch (ClassNotFoundException classnotfoundexception) {
		    NoClassDefFoundError noclassdeffounderror
			= new NoClassDefFoundError;
		    ((UNCONSTRUCTED)noclassdeffounderror).NoClassDefFoundError
			(classnotfoundexception.getMessage());
		    throw noclassdeffounderror;
		}
		var_class_31_ = class$9 = var_class_32_;
	    }
	    var_classes_26_[i_30_] = var_class_31_;
	    Constructor dropTargetConstructor
		= var_class_24_.getConstructor(var_classes_26_);
	    dropTargetConstructor
		.newInstance(new Object[] { _table, tableDropTargetListener });
	    java.awt.Component parent = _table.getParent();
	    if (parent instanceof JViewport) {
		dropTargetConstructor.newInstance(new Object[]
						  { parent,
						    tableDropTargetListener });
		java.awt.Component parentParent = parent.getParent();
		if (parentParent instanceof JScrollPane)
		    dropTargetConstructor.newInstance
			(new Object[] { parentParent,
					tableDropTargetListener });
	    }
	} catch (Throwable t) {
	    Debug.println
		(8,
		 "Disabling Native Drag-And-Drop because you don't have a 1.2 or 1.3 virtual machine.");
	}
    }
}
