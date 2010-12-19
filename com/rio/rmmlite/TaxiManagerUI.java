/* TaxiManagerUI - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.rmmlite;
import java.text.ParseException;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;

import com.inzyme.ui.UIUtils;
import com.rio.rmmlite.action.AboutAction;

import org.jempeg.ApplicationContext;
import org.jempeg.manager.action.DeleteAction;
import org.jempeg.manager.action.InvertSelectionAction;
import org.jempeg.manager.action.SelectAllAction;
import org.jempeg.manager.event.DefaultPopupListener;
import org.jempeg.manager.event.TablePopupListener;
import org.jempeg.manager.ui.ComponentConfiguration;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.nodestore.model.FIDPlaylistTableModel;
import org.jempeg.nodestore.model.PlaylistTableUtils;
import org.jempeg.nodestore.soup.SoupLayerFactory;
import org.jempeg.nodestore.soup.SoupUtils;

public class TaxiManagerUI extends AbstractRioManagerUI
{
    private JTable myTaxiTable;
    private JMenuItem myDelete;
    private JMenuItem mySelectAll;
    private JMenuItem myInvertSelection;
    private JMenuItem myPopupDelete;
    private JMenuItem myPopupNewTune;
    private JMenuItem myPopupNewTuneDirectory;
    private JMenuItem myPopupDownload;
    
    public TaxiManagerUI(ApplicationContext _context, boolean _applet) {
	super(_context, "taxi", _applet);
	PlaylistTableUtils.setPlaylistColumnsKey("taxi.playlistColumns");
	PlaylistTableUtils.setColumnTagNames(new String[] { "title", "length",
							    "ctime" });
	PlaylistTableUtils
	    .setColumnTagNames(3, new String[] { "title", "length", "ctime" });
    }
    
    protected JComponent createContentPane() {
	myTaxiTable = new JTable();
	JScrollPane taxiScrollPane = new JScrollPane(myTaxiTable);
	return taxiScrollPane;
    }
    
    public void downloadCompleted(PlayerDatabase _playerDatabase) {
	super.downloadCompleted(_playerDatabase);
	ApplicationContext context = getContext();
	try {
	    FIDPlaylist taxiPlaylist
		= (SoupUtils.createTransientSoupPlaylist
		   (_playerDatabase, "Taxi",
		    SoupLayerFactory.fromExternalForm("search:type=taxi"),
		    false, false, false, null));
	    myTaxiTable.setModel(new FIDPlaylistTableModel(taxiPlaylist));
	    context.setTable(myTaxiTable);
	    context.setSelectedContainer(taxiPlaylist);
	    context.clearSelection(myTaxiTable);
	} catch (ParseException e) {
	    e.printStackTrace();
	}
    }
    
    protected void createMenus(JMenuBar _menuBar) {
	super.createMenus(_menuBar);
	ApplicationContext context = getContext();
	JMenu editMenu = UIUtils.createMenu("menu.edit");
	myDelete = UIUtils.createMenuItem("menu.edit.delete");
	DeleteAction deleteAction = new DeleteAction(context);
	myDelete.addActionListener(deleteAction);
	mySelectAll = UIUtils.createMenuItem("menu.edit.selectAll");
	SelectAllAction selectAllAction = new SelectAllAction(context, null);
	mySelectAll.addActionListener(selectAllAction);
	myInvertSelection
	    = UIUtils.createMenuItem("menu.edit.invertSelection");
	InvertSelectionAction invertSelectionAction
	    = new InvertSelectionAction(context, null);
	myInvertSelection.addActionListener(invertSelectionAction);
	editMenu.add(myDelete);
	editMenu.add(new JSeparator());
	editMenu.add(mySelectAll);
	editMenu.add(myInvertSelection);
	JMenu helpMenu = UIUtils.createMenu("menu.help");
	JMenuItem about = UIUtils.createMenuItem("menu.help.about");
	AboutAction aboutAction = new AboutAction(context);
	about.addActionListener(aboutAction);
	helpMenu.add(about);
	_menuBar.add(editMenu);
	_menuBar.add(helpMenu);
	JPopupMenu headerPopupMenu = new JPopupMenu();
	JPopupMenu tablePopupMenu = new JPopupMenu();
	myPopupDelete = UIUtils.createMenuItem("menu.edit.delete");
	myPopupDelete.addActionListener(deleteAction);
	myPopupNewTune = UIUtils.createMenuItem("menu.file.taxi.newTune");
	myPopupNewTune.addActionListener(getNewTuneAction());
	myPopupNewTuneDirectory
	    = UIUtils.createMenuItem("menu.file.taxi.newTuneDirectory");
	myPopupNewTuneDirectory.addActionListener(getNewTuneDirectoryAction());
	myPopupDownload = UIUtils.createMenuItem("menu.file.taxi.download");
	myPopupDownload.addActionListener(getDownloadAction());
	tablePopupMenu.add(myPopupDelete);
	tablePopupMenu.add(new JSeparator());
	tablePopupMenu.add(myPopupNewTune);
	tablePopupMenu.add(myPopupNewTuneDirectory);
	tablePopupMenu.add(new JSeparator());
	tablePopupMenu.add(myPopupDownload);
	TablePopupListener tablePopupListener
	    = new TablePopupListener(tablePopupMenu);
	myTaxiTable.addPropertyChangeListener(this);
	getRepaintListener().addComponent(myTaxiTable);
	myTaxiTable.getTableHeader()
	    .addMouseListener(new DefaultPopupListener(headerPopupMenu));
	myTaxiTable.addMouseListener(tablePopupListener);
	ComponentConfiguration.configureTable(context, myTaxiTable);
    }
    
    protected void setSelectionMenuOptionsEnabled
	(boolean _enabled, int _selectedRowCount, boolean _transientSelected) {
	super.setSelectionMenuOptionsEnabled(_enabled, _selectedRowCount,
					     _transientSelected);
	boolean rowSelected = _selectedRowCount > 0;
	myDelete.setEnabled(rowSelected);
	mySelectAll.setEnabled(_enabled);
	myInvertSelection.setEnabled(_enabled);
	myPopupDelete.setEnabled(rowSelected);
	myPopupNewTune.setEnabled(_enabled);
	myPopupNewTuneDirectory.setEnabled(_enabled);
	myPopupDownload.setEnabled(rowSelected);
    }
}
