package org.jempeg.manager.ui;

import java.awt.Component;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetListener;
import java.lang.reflect.Constructor;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import org.jempeg.ApplicationContext;
import org.jempeg.manager.event.ColumnWidthListener;
import org.jempeg.manager.event.PlaylistTableSelectionListener;
import org.jempeg.manager.event.SelectedContainerListener;
import org.jempeg.manager.event.TableDropTargetListener;
import org.jempeg.manager.event.TableKeyListener;
import org.jempeg.manager.event.TableModelCleanupListener;
import org.jempeg.manager.event.TableSortListener;

import com.inzyme.util.Debug;

/**
 * ComponentConfiguration provides convenience methods for setting
 * up standard Emplode components.
 * 
 * @author Mike Schrag
 */
public class ComponentConfiguration {
	/**
	 * Configures the given table.
	 * 
	 * @param _context the context to configure within
	 * @param _table the table to configure
	 */
	public static void configureTable(ApplicationContext _context, JTable _table) {
		_table.setColumnSelectionAllowed(false);
		_table.addKeyListener(new TableKeyListener());
		_table.setDefaultRenderer(Object.class, new PlaylistTableCellRenderer(_table.getDefaultRenderer(Object.class)));
		_table.setDefaultRenderer(Integer.class, new PlaylistTableCellRenderer(_table.getDefaultRenderer(Integer.class)));

		_table.getSelectionModel().addListSelectionListener(new PlaylistTableSelectionListener(_context, _table));

		JTableHeader tableHeader = _table.getTableHeader();
		try {
			// NTS: MSIEJVM
			TableCellRenderer oldTableHeaderRenderer = (TableCellRenderer)JTableHeader.class.getMethod("getDefaultRenderer", null).invoke(tableHeader, null);
			TableCellRenderer newTableHeaderRenderer = new SortedTableHeaderRenderer(oldTableHeaderRenderer);
			JTableHeader.class.getMethod("setDefaultRenderer", new Class[] { TableCellRenderer.class }).invoke(tableHeader, new Object[] { newTableHeaderRenderer });
		}
		catch (Throwable t) {
			// No sort arrows
		}
		tableHeader.setReorderingAllowed(false);
		tableHeader.addMouseListener(new TableSortListener());
		_table.addPropertyChangeListener(new ColumnWidthListener(_table));
		_table.addPropertyChangeListener(new TableModelCleanupListener(_context));
		_context.addContextListener(new SelectedContainerListener(_context, _table));

		try {
			// NTS: MSIEJVM
			Object tableDropTargetListener = TableDropTargetListener.class.getConstructor(new Class[] { ApplicationContext.class, JTable.class }).newInstance(new Object[] { _context, _table });
			Constructor dropTargetConstructor = DropTarget.class.getConstructor(new Class[] { Component.class, DropTargetListener.class });
			dropTargetConstructor.newInstance(new Object[] { _table, tableDropTargetListener });
			
			Component parent = _table.getParent();
			if (parent instanceof JViewport) {
				dropTargetConstructor.newInstance(new Object[] { parent, tableDropTargetListener });
				Component parentParent = parent.getParent();
				if (parentParent instanceof JScrollPane) {
					dropTargetConstructor.newInstance(new Object[] { parentParent, tableDropTargetListener });
				}
			}
			
			/*
			TableDropTargetListener tableDropListener = new TableDropTargetListener(_context, _table);
			new DropTarget(_table, tableDropListener);
			Component parent = _table.getParent();
			if (parent instanceof JViewport) {
				new DropTarget(parent, tableDropListener);
				Component parentParent = parent.getParent();
				if (parentParent instanceof JScrollPane) {
					new DropTarget(parentParent, tableDropListener);
				}
			}
			*/
		}
		catch (Throwable t) {
			Debug.println(Debug.WARNING, "Disabling Native Drag-And-Drop because you don't have a 1.2 or 1.3 virtual machine.");
		}
	}
}
