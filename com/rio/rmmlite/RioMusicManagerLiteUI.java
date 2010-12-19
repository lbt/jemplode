/* RioMusicManagerLiteUI - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.rmmlite;
import java.awt.Toolkit;
import java.text.ParseException;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.inzyme.container.IContainer;
import com.inzyme.progress.IProgressListener;
import com.inzyme.properties.PropertiesManager;
import com.inzyme.text.ResourceBundleKey;
import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.ui.UIUtils;
import com.rio.rmmlite.action.AboutAction;
import com.rio.rmmlite.action.PlaylistTabSelector;

import org.jempeg.ApplicationContext;
import org.jempeg.manager.action.AddToPlaylistAction;
import org.jempeg.manager.action.ClearAction;
import org.jempeg.manager.action.ColumnEditorAction;
import org.jempeg.manager.action.CopyAction;
import org.jempeg.manager.action.CutAction;
import org.jempeg.manager.action.DeleteAction;
import org.jempeg.manager.action.DownloadAction;
import org.jempeg.manager.action.InvertSelectionAction;
import org.jempeg.manager.action.NewRootLevelPlaylistAction;
import org.jempeg.manager.action.NewSearchSoupAction;
import org.jempeg.manager.action.PasteAction;
import org.jempeg.manager.action.PropertiesAction;
import org.jempeg.manager.action.SelectAllAction;
import org.jempeg.manager.event.DefaultPopupListener;
import org.jempeg.manager.event.DoubleClickPlaylistComboBoxOpener;
import org.jempeg.manager.event.TablePopupListener;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.nodestore.model.AbstractFIDPlaylistModel;
import org.jempeg.nodestore.model.FIDPlaylistTreeComboBoxModel;
import org.jempeg.nodestore.model.FIDPlaylistTreeNode;
import org.jempeg.nodestore.model.PlaylistTableUtils;
import org.jempeg.nodestore.soup.SoupLayerFactory;
import org.jempeg.nodestore.soup.SoupUtils;

public class RioMusicManagerLiteUI extends AbstractRioManagerUI
{
    public static final int PLAYLIST_TAB_INDEX = 4;
    private static final String LAST_SELETED_TAB_INDEX
	= "lastSelectedTabIndex";
    private JTabbedPane myTabbedPane;
    private ComboBoxTablePanel myAlbumsPanel;
    private ComboBoxTablePanel myArtistsPanel;
    private ComboBoxTablePanel myGenresPanel;
    private ComboBoxTablePanel mySongsPanel;
    private ComboBoxTablePanel myPlaylistsPanel;
    private PearlSearchPanel mySearchPanel;
    private PlaylistTabSelector myPlaylistSelector;
    private JMenuItem myCut;
    private JMenuItem myCopy;
    private JMenuItem myPaste;
    private JMenuItem myDelete;
    private JMenuItem mySelectAll;
    private JMenuItem myInvertSelection;
    private JMenuItem myAddToPlaylist;
    private JMenuItem myNewPlaylist;
    private JMenuItem myNewSearchSoup;
    private JMenuItem myProperties;
    private JMenuItem myColumns;
    private JMenuItem myPopupOpen;
    private JMenuItem myPopupCut;
    private JMenuItem myPopupCopy;
    private JMenuItem myPopupPaste;
    private JMenuItem myPopupDelete;
    private JMenuItem myPopupNewTune;
    private JMenuItem myPopupNewTuneDirectory;
    private JMenuItem myPopupProperties;
    private JMenuItem myPopupAddToPlaylist;
    private JMenuItem myPopupDownload;
    
    public RioMusicManagerLiteUI(ApplicationContext _context,
				 boolean _applet) {
	super(_context, "rmml", _applet);
	PlaylistTableUtils.setPlaylistColumnsKey("rmml.playlistColumns");
	PlaylistTableUtils.setColumnTagNames(9,
					     new String[] { "source", "artist",
							    "tracks" });
	PlaylistTableUtils.setColumnTagNames(10,
					     new String[] { "source", "artist",
							    "tracks" });
	PlaylistTableUtils.setColumnTagNames(11, new String[] { "artist",
								"tracks" });
	PlaylistTableUtils.setColumnTagNames(12, new String[] { "artist",
								"tracks" });
	PlaylistTableUtils.setColumnTagNames(5, new String[] { "genre",
							       "tracks" });
	PlaylistTableUtils.setColumnTagNames(6, new String[] { "genre",
							       "tracks" });
	PlaylistTableUtils
	    .setColumnTagNames(4, PlaylistTableUtils.SIMPLE_COLUMNS);
	PlaylistTableUtils
	    .setColumnTagNames(3, PlaylistTableUtils.SIMPLE_COLUMNS);
	PlaylistTableUtils
	    .setColumnTagNames(2, PlaylistTableUtils.SIMPLE_COLUMNS);
	PlaylistTableUtils.setColumnTagNames(13, new String[] { "title" });
    }
    
    protected JComponent createContentPane() {
	ApplicationContext context = getContext();
	myAlbumsPanel = new ComboBoxTablePanel(context);
	myArtistsPanel = new ComboBoxTablePanel(context);
	myGenresPanel = new ComboBoxTablePanel(context);
	myPlaylistsPanel = new ComboBoxTablePanel(context);
	mySongsPanel = new ComboBoxTablePanel(context);
	mySearchPanel = new PearlSearchPanel(getContext());
	myTabbedPane = new JTabbedPane();
	myTabbedPane.add(new ResourceBundleKey("ui", "tab.albums.text")
			     .getString(),
			 myAlbumsPanel);
	myTabbedPane.setIconAt(0, new ImageIcon(this.getClass().getResource
						(new ResourceBundleKey
						     ("ui", "tab.albums.icon")
						     .getString())));
	myTabbedPane.add(new ResourceBundleKey("ui", "tab.artists.text")
			     .getString(),
			 myArtistsPanel);
	myTabbedPane.setIconAt(1, new ImageIcon(this.getClass().getResource
						(new ResourceBundleKey
						     ("ui", "tab.artists.icon")
						     .getString())));
	myTabbedPane.add(new ResourceBundleKey("ui", "tab.genres.text")
			     .getString(),
			 myGenresPanel);
	myTabbedPane.setIconAt(2, new ImageIcon(this.getClass().getResource
						(new ResourceBundleKey
						     ("ui", "tab.genres.icon")
						     .getString())));
	myTabbedPane.add(new ResourceBundleKey("ui", "tab.songs.text")
			     .getString(),
			 mySongsPanel);
	myTabbedPane.setIconAt(3, new ImageIcon(this.getClass().getResource
						(new ResourceBundleKey
						     ("ui", "tab.songs.icon")
						     .getString())));
	myTabbedPane.add(new ResourceBundleKey("ui", "tab.playlists.text")
			     .getString(),
			 myPlaylistsPanel);
	myTabbedPane.setIconAt(4,
			       new ImageIcon(this.getClass().getResource
					     (new ResourceBundleKey
						  ("ui", "tab.playlists.icon")
						  .getString())));
	myTabbedPane.add(new ResourceBundleKey("ui", "tab.search.text")
			     .getString(),
			 mySearchPanel);
	myTabbedPane.setIconAt(5, new ImageIcon(this.getClass().getResource
						(new ResourceBundleKey
						     ("ui", "tab.search.icon")
						     .getString())));
	int lastSelectedTabIndex
	    = PropertiesManager.getInstance()
		  .getIntProperty("lastSelectedTabIndex", 4);
	if (lastSelectedTabIndex < 0
	    || lastSelectedTabIndex >= myTabbedPane.getTabCount())
	    lastSelectedTabIndex = 4;
	myTabbedPane.setSelectedIndex(lastSelectedTabIndex);
	myTabbedPane.addChangeListener(new ChangeListener() {
	    public void stateChanged(ChangeEvent _event) {
		PropertiesManager.getInstance().setIntProperty
		    ("lastSelectedTabIndex", myTabbedPane.getSelectedIndex());
		initializeCurrentTab();
	    }
	});
	return myTabbedPane;
    }
    
    public void databaseChanged(PlayerDatabase _oldDatabase,
				PlayerDatabase _newDatabase) {
	super.databaseChanged(_oldDatabase, _newDatabase);
	if (myPlaylistSelector != null && _oldDatabase != null)
	    _oldDatabase.removeDatabaseListener(myPlaylistSelector);
    }
    
    public void downloadCompleted(PlayerDatabase _playerDatabase) {
	if (myPlaylistSelector == null) {
	    myPlaylistSelector = new PlaylistTabSelector(myTabbedPane, 4);
	    _playerDatabase.addDatabaseListener(myPlaylistSelector);
	}
	initializeCurrentTab();
	try {
	    DefaultTreeModel treeModel
		= new DefaultTreeModel(new DefaultMutableTreeNode());
	    IProgressListener progressListener
		= getContext().getDownloadProgressListener();
	    progressListener.taskStarted
		(ResourceBundleUtils.getUIString("download.attachingAlbums"));
	    FIDPlaylist albumsPlaylist
		= (SoupUtils.createTransientSoupPlaylist
		   (_playerDatabase,
		    new ResourceBundleKey("ui", "tab.albums.default")
			.getString(),
		    SoupLayerFactory
			.fromExternalForm("search:type=tune~tag:source"),
		    false, false, false,
		    new ComboBoxSoupListener(myAlbumsPanel.getComboBox())));
	    albumsPlaylist.setType(10);
	    albumsPlaylist.setContainedType(9);
	    myAlbumsPanel.setComboBoxModel
		(new FIDPlaylistTreeComboBoxModel
		 (new FIDPlaylistTreeNode(treeModel, albumsPlaylist, 0),
		  true));
	    progressListener.taskStarted
		(ResourceBundleUtils.getUIString("download.attachingArtists"));
	    FIDPlaylist artistsPlaylist
		= (SoupUtils.createTransientSoupPlaylist
		   (_playerDatabase,
		    new ResourceBundleKey("ui", "tab.artists.default")
			.getString(),
		    (SoupLayerFactory.fromExternalForm
		     ("search:type=tune~tag:artist~tag:source")),
		    false, false, false,
		    new ComboBoxSoupListener(myArtistsPanel.getComboBox())));
	    artistsPlaylist.setType(12);
	    artistsPlaylist.setContainedType(11);
	    myArtistsPanel.setComboBoxModel
		(new FIDPlaylistTreeComboBoxModel
		 (new FIDPlaylistTreeNode(treeModel, artistsPlaylist, 0),
		  true));
	    progressListener.taskStarted
		(ResourceBundleUtils.getUIString("download.attachingGenres"));
	    FIDPlaylist genresPlaylist
		= (SoupUtils.createTransientSoupPlaylist
		   (_playerDatabase,
		    new ResourceBundleKey("ui", "tab.genres.default")
			.getString(),
		    (SoupLayerFactory.fromExternalForm
		     ("search:type=tune~tag:genre~tag:artist")),
		    false, false, false,
		    new ComboBoxSoupListener(myGenresPanel.getComboBox())));
	    genresPlaylist.setType(6);
	    genresPlaylist.setContainedType(5);
	    myGenresPanel.setComboBoxModel
		(new FIDPlaylistTreeComboBoxModel
		 (new FIDPlaylistTreeNode(treeModel, genresPlaylist, 0),
		  true));
	    progressListener.taskStarted
		(ResourceBundleUtils.getUIString("download.attachingSongs"));
	    FIDPlaylist songsPlaylist
		= (SoupUtils.createTransientSoupPlaylist
		   (_playerDatabase,
		    new ResourceBundleKey("ui", "tab.songs.default")
			.getString(),
		    SoupLayerFactory.fromExternalForm("search:type=tune"),
		    false, false, false, null));
	    mySongsPanel.setComboBoxModel
		(new FIDPlaylistTreeComboBoxModel
		 (new FIDPlaylistTreeNode(treeModel, songsPlaylist, 0), true));
	    progressListener.taskStarted(ResourceBundleUtils.getUIString
					 ("download.attachingPlaylists"));
	    FIDPlaylist playlistsPlaylist
		= (SoupUtils.createTransientSoupPlaylist
		   (_playerDatabase,
		    new ResourceBundleKey("ui", "tab.playlists.default")
			.getString(),
		    (SoupLayerFactory.fromExternalForm
		     ("search:type=playlist and fid != 256")),
		    false, false, false,
		    new ComboBoxSoupListener(myPlaylistsPanel.getComboBox())));
	    playlistsPlaylist.setType(13);
	    playlistsPlaylist.setContainedType(13);
	    myPlaylistsPanel.setComboBoxModel
		(new FIDPlaylistTreeComboBoxModel
		 (new FIDPlaylistTreeNode(treeModel, playlistsPlaylist, 0),
		  true));
	} catch (ParseException e) {
	    e.printStackTrace();
	}
	super.downloadCompleted(_playerDatabase);
    }
    
    protected void initializeCurrentTab() {
	java.awt.Component selectedComponent
	    = myTabbedPane.getSelectedComponent();
	if (selectedComponent instanceof JScrollPane)
	    selectedComponent
		= ((JScrollPane) selectedComponent).getViewport().getView();
	JTable playlistTable = null;
	IContainer selectedContainer = null;
	if (selectedComponent instanceof ComboBoxTablePanel) {
	    ComboBoxTablePanel comboBoxTablePanel
		= (ComboBoxTablePanel) selectedComponent;
	    playlistTable = comboBoxTablePanel.getTable();
	    javax.swing.ComboBoxModel comboBoxModel
		= comboBoxTablePanel.getComboBoxModel();
	    if (comboBoxModel != null) {
		AbstractFIDPlaylistModel selectedPlaylistModel
		    = ((AbstractFIDPlaylistModel)
		       comboBoxModel.getSelectedItem());
		if (selectedPlaylistModel != null)
		    selectedContainer = selectedPlaylistModel.getPlaylist();
		else
		    selectedContainer = null;
	    }
	} else {
	    if (selectedComponent instanceof JTable)
		playlistTable = (JTable) selectedComponent;
	    else if (selectedComponent instanceof PearlSearchPanel)
		playlistTable
		    = ((PearlSearchPanel) selectedComponent).getTable();
	    if (playlistTable != null) {
		Object tableModel = playlistTable.getModel();
		if (tableModel instanceof IContainer)
		    selectedContainer = (IContainer) tableModel;
	    }
	}
	ApplicationContext context = getContext();
	context.setTable(playlistTable);
	context.setSelectedContainer(selectedContainer);
	context.clearSelection(playlistTable);
    }
    
    protected void createMenus(JMenuBar _menuBar) {
	super.createMenus(_menuBar);
	ApplicationContext context = getContext();
	int menuShortcutKeyMask
	    = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
	JMenu editMenu = UIUtils.createMenu("menu.edit");
	myCut = UIUtils.createMenuItem("menu.edit.cut");
	CutAction cutAction = new CutAction(context, false);
	myCut.addActionListener(cutAction);
	KeyStroke cutKeyStroke = myCut.getAccelerator();
	KeyStroke winCutKeyStroke
	    = KeyStroke.getKeyStroke(127, menuShortcutKeyMask);
	myCopy = UIUtils.createMenuItem("menu.edit.copy");
	CopyAction copyAction = new CopyAction(context, false);
	myCopy.addActionListener(copyAction);
	KeyStroke copyKeyStroke = myCopy.getAccelerator();
	KeyStroke winCopyKeyStroke
	    = KeyStroke.getKeyStroke(155, menuShortcutKeyMask);
	myPaste = UIUtils.createMenuItem("menu.edit.paste");
	PasteAction pasteAction = new PasteAction(context, false);
	myPaste.addActionListener(pasteAction);
	KeyStroke pasteKeyStroke = myPaste.getAccelerator();
	KeyStroke winPasteKeyStroke = KeyStroke.getKeyStroke(155, 1);
	ClearAction clearAction = new ClearAction();
	KeyStroke clearKeyStroke = KeyStroke.getKeyStroke(27, 0);
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
	myProperties = UIUtils.createMenuItem("menu.edit.properties");
	PropertiesAction propertiesAction = new PropertiesAction(context);
	myProperties.addActionListener(propertiesAction);
	editMenu.add(myCut);
	editMenu.add(myCopy);
	editMenu.add(myPaste);
	editMenu.add(myDelete);
	editMenu.add(new JSeparator());
	editMenu.add(mySelectAll);
	editMenu.add(myInvertSelection);
	editMenu.add(new JSeparator());
	editMenu.add(myProperties);
	JMenu viewMenu = UIUtils.createMenu("menu.view");
	myColumns = UIUtils.createMenuItem("menu.view.columns");
	ColumnEditorAction columnEditorAction
	    = new ColumnEditorAction(context, null);
	myColumns.addActionListener(columnEditorAction);
	viewMenu.add(myColumns);
	JMenu playlistMenu = UIUtils.createMenu("menu.playlist");
	myNewPlaylist = UIUtils.createMenuItem("menu.playlist.newPlaylist");
	NewRootLevelPlaylistAction newPlaylistAction
	    = new NewRootLevelPlaylistAction(context);
	myNewPlaylist.addActionListener(newPlaylistAction);
	myAddToPlaylist
	    = UIUtils.createMenuItem("menu.playlist.addToPlaylist");
	AddToPlaylistAction addToPlaylistAction
	    = new AddToPlaylistAction(context, false);
	myAddToPlaylist.addActionListener(addToPlaylistAction);
	myNewSearchSoup
	    = UIUtils.createMenuItem("menu.playlist.newSearchSoup");
	NewSearchSoupAction newSearchSoupAction
	    = new NewSearchSoupAction(context, false);
	myNewSearchSoup.addActionListener(newSearchSoupAction);
	playlistMenu.add(myNewPlaylist);
	playlistMenu.add(myAddToPlaylist);
	JMenu helpMenu = UIUtils.createMenu("menu.help");
	JMenuItem about = UIUtils.createMenuItem("menu.help.about");
	AboutAction aboutAction = new AboutAction(context);
	about.addActionListener(aboutAction);
	helpMenu.add(about);
	_menuBar.add(editMenu);
	_menuBar.add(viewMenu);
	_menuBar.add(playlistMenu);
	_menuBar.add(helpMenu);
	JPopupMenu headerPopupMenu = new JPopupMenu();
	JMenuItem popupColumnEditor
	    = UIUtils.createMenuItem("menu.view.columns");
	popupColumnEditor.addActionListener(columnEditorAction);
	headerPopupMenu.add(popupColumnEditor);
	JPopupMenu tablePopupMenu = new JPopupMenu();
	myPopupOpen = UIUtils.createMenuItem("menu.edit.open");
	myPopupOpen.addActionListener(propertiesAction);
	myPopupCut = UIUtils.createMenuItem("menu.edit.cut");
	myPopupCut.addActionListener(cutAction);
	myPopupCopy = UIUtils.createMenuItem("menu.edit.copy");
	myPopupCopy.addActionListener(copyAction);
	myPopupPaste = UIUtils.createMenuItem("menu.edit.paste");
	myPopupPaste.addActionListener(pasteAction);
	myPopupDelete = UIUtils.createMenuItem("menu.edit.delete");
	myPopupDelete.addActionListener(deleteAction);
	myPopupNewTune = UIUtils.createMenuItem("menu.file.rmml.newTune");
	myPopupNewTune.addActionListener(getNewTuneAction());
	myPopupNewTuneDirectory
	    = UIUtils.createMenuItem("menu.file.rmml.newTuneDirectory");
	myPopupNewTuneDirectory.addActionListener(getNewTuneDirectoryAction());
	myPopupProperties = UIUtils.createMenuItem("menu.edit.properties");
	myPopupProperties.addActionListener(propertiesAction);
	myPopupAddToPlaylist
	    = UIUtils.createMenuItem("menu.playlist.addToPlaylist");
	myPopupAddToPlaylist.addActionListener(addToPlaylistAction);
	myPopupDownload = UIUtils.createMenuItem("menu.file.rmml.download");
	DownloadAction downloadAction = new DownloadAction(context);
	myPopupDownload.addActionListener(downloadAction);
	tablePopupMenu.add(myPopupOpen);
	tablePopupMenu.add(new JSeparator());
	tablePopupMenu.add(myPopupCut);
	tablePopupMenu.add(myPopupCopy);
	tablePopupMenu.add(myPopupPaste);
	tablePopupMenu.add(myPopupDelete);
	tablePopupMenu.add(new JSeparator());
	tablePopupMenu.add(myPopupNewTune);
	tablePopupMenu.add(myPopupNewTuneDirectory);
	tablePopupMenu.add(myPopupAddToPlaylist);
	tablePopupMenu.add(new JSeparator());
	tablePopupMenu.add(myPopupProperties);
	tablePopupMenu.add(new JSeparator());
	tablePopupMenu.add(myPopupDownload);
	TablePopupListener tablePopupListener
	    = new TablePopupListener(tablePopupMenu);
	JTable[] tables
	    = { myPlaylistsPanel.getTable(), myArtistsPanel.getTable(),
		myAlbumsPanel.getTable(), myGenresPanel.getTable(),
		mySongsPanel.getTable(), mySearchPanel.getTable() };
	for (int i = 0; i < tables.length; i++) {
	    tables[i].addPropertyChangeListener(this);
	    getRepaintListener().addComponent(tables[i]);
	    tables[i].addMouseListener(propertiesAction);
	    tables[i].getTableHeader()
		.addMouseListener(new DefaultPopupListener(headerPopupMenu));
	    tables[i].addMouseListener(tablePopupListener);
	    tables[i].registerKeyboardAction(cutAction, "", cutKeyStroke, 0);
	    tables[i].registerKeyboardAction(copyAction, "", copyKeyStroke, 0);
	    tables[i].registerKeyboardAction(pasteAction, "", pasteKeyStroke,
					     0);
	    tables[i].registerKeyboardAction(cutAction, "", winCutKeyStroke,
					     0);
	    tables[i].registerKeyboardAction(copyAction, "", winCopyKeyStroke,
					     0);
	    tables[i].registerKeyboardAction(pasteAction, "",
					     winPasteKeyStroke, 0);
	    tables[i].registerKeyboardAction(clearAction, "", clearKeyStroke,
					     0);
	}
	myPlaylistsPanel.getTable().addMouseListener
	    (new DoubleClickPlaylistComboBoxOpener(context,
						   myPlaylistsPanel
						       .getComboBox()));
	myArtistsPanel.getTable().addMouseListener
	    (new DoubleClickPlaylistComboBoxOpener(context,
						   myArtistsPanel
						       .getComboBox()));
	myAlbumsPanel.getTable().addMouseListener
	    (new DoubleClickPlaylistComboBoxOpener(context,
						   myAlbumsPanel
						       .getComboBox()));
	myGenresPanel.getTable().addMouseListener
	    (new DoubleClickPlaylistComboBoxOpener(context,
						   myGenresPanel
						       .getComboBox()));
	mySongsPanel.getTable().addMouseListener
	    (new DoubleClickPlaylistComboBoxOpener(context,
						   mySongsPanel
						       .getComboBox()));
    }
    
    protected void setSelectionMenuOptionsEnabled
	(boolean _enabled, int _selectedRowCount, boolean _transientSelected) {
	super.setSelectionMenuOptionsEnabled(_enabled, _selectedRowCount,
					     _transientSelected);
	boolean rowSelected = _selectedRowCount > 0;
	myDelete.setEnabled(rowSelected);
	myCut.setEnabled(rowSelected);
	myCopy.setEnabled(rowSelected);
	myPaste.setEnabled(_enabled);
	mySelectAll.setEnabled(_enabled);
	myInvertSelection.setEnabled(_enabled);
	myNewPlaylist.setEnabled(_enabled);
	myNewSearchSoup.setEnabled(_enabled);
	myAddToPlaylist.setEnabled(rowSelected);
	myProperties.setEnabled(!_transientSelected && rowSelected);
	myColumns.setEnabled(_enabled);
	myPopupOpen.setEnabled(rowSelected);
	myPopupCut.setEnabled(rowSelected);
	myPopupCopy.setEnabled(rowSelected);
	myPopupPaste.setEnabled(_enabled);
	myPopupDelete.setEnabled(rowSelected);
	myPopupNewTune.setEnabled(_enabled);
	myPopupNewTuneDirectory.setEnabled(_enabled);
	myPopupProperties.setEnabled(!_transientSelected && rowSelected);
	myPopupAddToPlaylist.setEnabled(rowSelected);
	myPopupDownload.setEnabled(rowSelected);
	updateTableOptions();
    }
    
    protected void setTableOptionsEnabled(boolean _enabled) {
	updateTableOptions();
    }
    
    protected void updateTableOptions() {
	/* empty */
    }
}
