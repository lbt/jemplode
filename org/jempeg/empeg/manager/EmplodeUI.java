/**
 * Copyright (c) 2001, Mike Schrag & Daniel Zimmerman
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of Mike Schrag, Daniel Zimmerman, nor the names of any
 * other contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.jempeg.empeg.manager;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.dnd.DropTarget;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.jempeg.ApplicationContext;
import org.jempeg.JEmplodeProperties;
import org.jempeg.empeg.Version;
import org.jempeg.empeg.exporter.CSVExporter;
import org.jempeg.empeg.exporter.XMLExporter;
import org.jempeg.empeg.logoedit.action.AnimEditAction;
import org.jempeg.empeg.logoedit.action.GrabScreenAction;
import org.jempeg.empeg.logoedit.action.LogoEditAction;
import org.jempeg.empeg.manager.action.ConfigurePlayerAction;
import org.jempeg.empeg.manager.action.EditConfigIniAction;
import org.jempeg.empeg.manager.action.ExportAction;
import org.jempeg.empeg.manager.action.FsckAction;
import org.jempeg.empeg.manager.action.HijackDownloadAction;
import org.jempeg.empeg.manager.action.MoveDownAction;
import org.jempeg.empeg.manager.action.MoveUpAction;
import org.jempeg.empeg.manager.action.OpenAnotherDeviceAction;
import org.jempeg.empeg.manager.action.OpenDatabaseAction;
import org.jempeg.empeg.manager.action.OptionsAction;
import org.jempeg.empeg.manager.action.PlayAction;
import org.jempeg.empeg.manager.action.PlayPauseAction;
import org.jempeg.empeg.manager.action.RebuildAction;
import org.jempeg.empeg.manager.action.ResortAction;
import org.jempeg.empeg.manager.action.RevertAction;
import org.jempeg.empeg.manager.action.SetPlaylistOrderAction;
import org.jempeg.empeg.manager.action.SoupEditorAction;
import org.jempeg.empeg.manager.action.ToggleColoredAction;
import org.jempeg.empeg.manager.action.UpgradeAction;
import org.jempeg.empeg.manager.action.VersionUpdateAction;
import org.jempeg.empeg.manager.action.WendyAction;
import org.jempeg.empeg.manager.dialog.SearchPanel;
import org.jempeg.empeg.manager.event.DoubleClickPlaylistTreeOpener;
import org.jempeg.empeg.manager.event.PlaylistTreeSelectionListener;
import org.jempeg.empeg.manager.event.SearchTabListener;
import org.jempeg.empeg.manager.event.TransferDetailsTabListener;
import org.jempeg.empeg.manager.event.TreeDropTargetListener;
import org.jempeg.empeg.manager.event.TreeKeyListener;
import org.jempeg.empeg.manager.event.TreePopupListener;
import org.jempeg.empeg.manager.event.TreeSoupAttacher;
import org.jempeg.empeg.manager.plugins.JEmplodePlugin;
import org.jempeg.empeg.manager.plugins.PluginsManager;
import org.jempeg.empeg.protocol.EmpegRequest;
import org.jempeg.manager.action.AddToPlaylistAction;
import org.jempeg.manager.action.ClearAction;
import org.jempeg.manager.action.ColumnEditorAction;
import org.jempeg.manager.action.CopyAction;
import org.jempeg.manager.action.CutAction;
import org.jempeg.manager.action.DeleteAction;
import org.jempeg.manager.action.ExitAction;
import org.jempeg.manager.action.InvertSelectionAction;
import org.jempeg.manager.action.NewPlaylistAction;
import org.jempeg.manager.action.NewTuneAction;
import org.jempeg.manager.action.PasteAction;
import org.jempeg.manager.action.PropertiesAction;
import org.jempeg.manager.action.ReplaceTuneAction;
import org.jempeg.manager.action.SelectAllAction;
import org.jempeg.manager.action.SynchronizeAction;
import org.jempeg.manager.dialog.ProgressDialog;
import org.jempeg.manager.dialog.ProgressPanel;
import org.jempeg.manager.dialog.SynchronizeQueuePanel;
import org.jempeg.manager.event.DefaultPopupListener;
import org.jempeg.manager.event.PlaylistTableSelectionListener;
import org.jempeg.manager.event.RepaintListener;
import org.jempeg.manager.event.TablePopupListener;
import org.jempeg.manager.ui.ComponentConfiguration;
import org.jempeg.manager.ui.PlaylistTreeCellRenderer;
import org.jempeg.manager.ui.ScrollableTree;
import org.jempeg.manager.util.EmplodeClipboard;
import org.jempeg.nodestore.IDatabaseChange;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.nodestore.event.IContextListener;
import org.jempeg.nodestore.event.IDatabaseListener;
import org.jempeg.nodestore.event.ISynchronizeClientListener;
import org.jempeg.nodestore.exporter.TMSXMLExporter;
import org.jempeg.nodestore.model.FIDPlaylistTreeNode;
import org.jempeg.nodestore.soup.PersistentSoupAttacher;
import org.jempeg.nodestore.soup.TransientSoupAttacher;
import org.jempeg.protocol.ISynchronizeClient;

import com.inzyme.container.ContainerSelection;
import com.inzyme.container.IContainer;
import com.inzyme.format.SizeFormat;
import com.inzyme.progress.CompositeProgressListener;
import com.inzyme.properties.PropertiesManager;
import com.inzyme.table.SortedTableModel;
import com.inzyme.tree.BasicContainerTreeNode;
import com.inzyme.tree.IContainerTreeNode;
import com.inzyme.ui.DialogUtils;
import com.inzyme.ui.UIUtils;
import com.inzyme.util.Debug;

/**
 * Emplode is the main class for the Java Emplode clone.  Much like Emptool,
 * the various methods of Emplode implement the various functions that can
 * be executed by the user.  The binding of these methods to buttons and panels
 * is done through the .dialog and .action packages.
 *
 * @author Mike Schrag
 * @author Daniel M. Zimmerman
 * @version $Revision: 1.29 $
 */
public class EmplodeUI implements ISynchronizeClientListener, IContextListener, IDatabaseListener, PropertyChangeListener {
  private ApplicationContext myContext;

  private JFrame myFrame;
  private JTree myTree;
  private JTable myTable;
  private JLabel mySelectionLabel;
  private JLabel myFreeSpaceLabel;
  private JLabel mySortLabel;

  private JTabbedPane myTabbedPane;
  private SearchPanel mySearchPanel;

  private TransientSoupAttacher myTransientSoupAttacher;
  private PersistentSoupAttacher myPersistentSoupAttacher;
  private TreeSoupAttacher myTreeSoupAttacher;

  private boolean myMenuOptionsEnabled;
  private boolean myTableOptionsEnabled;

  public EmplodeUI(ApplicationContext _context) {
    myContext = _context;

    String lookAndFeel = PropertiesManager.getInstance().getProperty(JEmplodeProperties.LOOK_AND_FEEL_PROPERTY);
    try {
      if (lookAndFeel.equals(JEmplodeProperties.LOOK_AND_FEEL_SYSTEM)) {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }
      else {
        UIManager.setLookAndFeel(lookAndFeel);
      }
    }
    catch (Exception e) {
      Debug.println(Debug.WARNING, e);
    }

    myFrame = new JFrame();
    _context.setFrame(myFrame);
    _context.setDownloadProgressListener(new ProgressDialog(myFrame, false));
    _context.setSynchronizeProgressListener(new ProgressDialog(myFrame, true));
    _context.setImportFilesProgressListener(new ProgressDialog(myFrame, true));
    _context.setDownloadFilesProgressListener(new ProgressDialog(myFrame, true));
    _context.setDefaultProgressListener(new ProgressDialog(myFrame, false));
    _context.addContextListener(this);

    try {
      myFrame.setIconImage(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/org/jempeg/manager/icons/logo16.gif")));
    }
    catch (Throwable t) {
      // Oh well... no icon for you :)
    }
    resetFrameTitle(null);

    JSplitPane splitPane = new JSplitPane(javax.swing.JSplitPane.HORIZONTAL_SPLIT);
    try {
      myTree = new ScrollableTree();
    }
    catch (Throwable t) {
      // Autoscroll interface didn't exist till 1.2, so catch this _just in case_
      myTree = new JTree();
    }

    myTree.setModel(new DefaultTreeModel(new BasicContainerTreeNode("Loading...", IContainerTreeNode.TYPE_ROOT, IFIDNode.TYPE_PLAYLISTS)));
    myTree.setShowsRootHandles(true);
    myTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    myTree.addKeyListener(new TreeKeyListener());
    myTree.setCellRenderer(new PlaylistTreeCellRenderer(myTree.getCellRenderer()));
    myTree.addFocusListener(new PlaylistTreeSelectionListener(myContext, myTree));

    // These need to be in this order so that the persistent soups are handled
    // first and all the setSoup(true) happen before getting to transient soups
    myTransientSoupAttacher = new TransientSoupAttacher(true);
    myPersistentSoupAttacher = new PersistentSoupAttacher(true);
    myTreeSoupAttacher = new TreeSoupAttacher(myTree);

    myTable = new JTable();
    _context.setTable(myTable);
    JScrollPane tableScrollPane = new JScrollPane(myTable);
    tableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    ComponentConfiguration.configureTable(myContext, myTable);
    myTable.addFocusListener(new PlaylistTableSelectionListener(myContext, myTable));

    myRepaintListener = new RepaintListener();
    myRepaintListener.addComponent(myTree);
    myRepaintListener.addComponent(myTable);
    EmplodeClipboard.getInstance().addPropertyChangeListener(myRepaintListener);

    myTree.getSelectionModel().addTreeSelectionListener(new PlaylistTreeSelectionListener(myContext, myTree));
    myTable.addMouseListener(new DoubleClickPlaylistTreeOpener(myContext, myTree));

    //				TreeDragListener treeDragListener = new TreeDragListener(this, frame, tracker);
    //				TableDragListener tableDragListener = new TableDragListener(this, frame, tracker);

    SynchronizeQueuePanel synchronizeQueuePanel = new SynchronizeQueuePanel(_context, true);

    JScrollPane treeScrollPane = new JScrollPane(myTree);
    treeScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    ProgressPanel treeProgressPanel = new ProgressPanel(false, false);
    _context.setSynchronizeProgressListener(new CompositeProgressListener(treeProgressPanel, _context.getSynchronizeProgressListener()));
    JPanel treeWithProgressPanel = new JPanel();
    treeWithProgressPanel.setLayout(new BorderLayout());
    treeWithProgressPanel.add(treeScrollPane, BorderLayout.CENTER);
    treeWithProgressPanel.add(treeProgressPanel, BorderLayout.SOUTH);

    mySearchPanel = new SearchPanel(myContext, myTable);
    ProgressPanel searchProgressPanel = new ProgressPanel(false, false);
    _context.setSynchronizeProgressListener(new CompositeProgressListener(searchProgressPanel, _context.getSynchronizeProgressListener()));
    JPanel searchWithProgressPanel = new JPanel();
    searchWithProgressPanel.setLayout(new BorderLayout());
    searchWithProgressPanel.add(mySearchPanel, BorderLayout.CENTER);
    searchWithProgressPanel.add(searchProgressPanel, BorderLayout.SOUTH);

    myTabbedPane = new JTabbedPane();
    myTabbedPane.add("Playlists", treeWithProgressPanel);
    myTabbedPane.add("Search", searchWithProgressPanel);
    myTabbedPane.add("Transfer Details", synchronizeQueuePanel);
    myTabbedPane.addChangeListener(new SearchTabListener(myTree, mySearchPanel));
    myTabbedPane.addChangeListener(new TransferDetailsTabListener(synchronizeQueuePanel));
    splitPane.setLeftComponent(myTabbedPane);
    splitPane.setRightComponent(tableScrollPane);

    JPanel statusBar = new JPanel();

    statusBar.setLayout(new GridBagLayout());
    mySelectionLabel = new JLabel(" ");
    mySelectionLabel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(), BorderFactory.createEmptyBorder(0, 3, 0, 3)));
    myFreeSpaceLabel = new JLabel(" ");
    myFreeSpaceLabel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(), BorderFactory.createEmptyBorder(0, 3, 0, 3)));
    mySortLabel = new JLabel(" ");
    mySortLabel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(), BorderFactory.createEmptyBorder(0, 3, 0, 3)));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(2, 2, 2, 0);
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 1.0;

    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 1.0;
    statusBar.add(new JPanel(), gbc);

    gbc.insets = new Insets(2, 5, 2, 0);
    gbc.weightx = 1.0;
    statusBar.add(mySelectionLabel, gbc);
    statusBar.add(myFreeSpaceLabel, gbc);
    statusBar.add(mySortLabel, gbc);

    try {
      TreeDropTargetListener treeDropListener = new TreeDropTargetListener(myContext, myTree);
      new DropTarget(myTree, treeDropListener);
      new DropTarget(myTree.getParent(), treeDropListener);
      new DropTarget(treeScrollPane, treeDropListener);
    }
    catch (Throwable t) {
      Debug.println(Debug.WARNING, "Disabling Native Drag-And-Drop because you don't have a 1.2 or 1.3 virtual machine.");
    }

    JMenuBar menuBar = new JMenuBar();
    JToolBar toolBar = new JToolBar();

    try {
      if (UIManager.getLookAndFeel().getClass().getName().equals("com.sun.java.swing.plaf.windows.WindowsLookAndFeel")) {
        Method setUIMethod = toolBar.getClass().getMethod("setUI", new Class[] {
          Class.forName("javax.swing.plaf.ToolBarUI")
        });
        setUIMethod.invoke(toolBar, new Object[] {
          Class.forName("org.jempeg.manager.ui.ModifiedWindowsToolBarUI").newInstance()
        });
      }
    }
    catch (Throwable t) {
      t.printStackTrace();
      // 1.4 ... oh well
    }

    createMenus(menuBar, toolBar);
    setMenuOptionsEnabled(false);
    myFrame.setJMenuBar(menuBar);

    myFrame.getContentPane().add(toolBar, BorderLayout.NORTH);
    myFrame.getContentPane().add(splitPane, BorderLayout.CENTER);
    myFrame.getContentPane().add(statusBar, BorderLayout.SOUTH);

    // allow us to put up a wait cursor
    myFrame.getGlassPane().addMouseListener(new MouseAdapter() {
    });
    myFrame.getGlassPane().addKeyListener(new KeyAdapter() {
    });
    myFrame.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    myFrame.setSize((int) (screenSize.width * 0.75), (int) (screenSize.height * 0.75));
    splitPane.setDividerLocation(200);

    DialogUtils.centerWindow(myFrame);
    int offset = ExitAction.getWindowCount() * 20;
    if (offset > 0) {
      myFrame.setLocation(myFrame.getX() + offset, myFrame.getY() + offset);
    }
  }

  public void freeSpaceChanged(PlayerDatabase _playerDatabase, long _totalSpace, long _freeSpace) {
    myFreeSpaceLabel.setText("Est. Free Space: " + SizeFormat.getInstance().format(_freeSpace) + ", Capacity: " + SizeFormat.getInstance().format(_totalSpace));
  }

  public void databaseCleared(PlayerDatabase _playerDatabase) {
  }

  public void nodeAdded(IFIDNode _node) {
  }

  public void nodeRemoved(IFIDNode _node) {
  }

  public void databaseChanged(PlayerDatabase _oldPlayerDatabase, PlayerDatabase _newPlayerDatabase) {
    if (_oldPlayerDatabase != null) {
      _oldPlayerDatabase.removeDatabaseListener(myRepaintListener);
      _oldPlayerDatabase.removeNodeTagListener(myRepaintListener);
      _oldPlayerDatabase.removeDatabaseListener(this);
      _oldPlayerDatabase.removeNodeTagListener(myTreeSoupAttacher);
      _oldPlayerDatabase.removeDatabaseListener(myTreeSoupAttacher);
    }
    if (_newPlayerDatabase != null) {
      _newPlayerDatabase.addDatabaseListener(myRepaintListener);
      _newPlayerDatabase.addNodeTagListener(myRepaintListener);
      _newPlayerDatabase.addDatabaseListener(this);
    }
    resetFrameTitle(_newPlayerDatabase);
  }

  public void synchronizeClientChanged(ISynchronizeClient _oldSynchronizeClient, ISynchronizeClient _newSynchronizeClient) {
    if (_oldSynchronizeClient != null) {
      _oldSynchronizeClient.removeSynchronizeClientListener(this);
      _oldSynchronizeClient.removeSynchronizeClientListener(myTransientSoupAttacher);
      _oldSynchronizeClient.removeSynchronizeClientListener(myPersistentSoupAttacher);
    }
    if (_newSynchronizeClient != null) {
      _newSynchronizeClient.addSynchronizeClientListener(this);
      _newSynchronizeClient.addSynchronizeClientListener(myTransientSoupAttacher);
      _newSynchronizeClient.addSynchronizeClientListener(myPersistentSoupAttacher);
    }
  }

  public void selectionChanged(Object _source, ContainerSelection _oldSelection, ContainerSelection _newSelection) {
    selectionChanged();
  }

  public void selectedContainerChanged(IContainer _oldSelectedContainer, IContainer _newSelectedContainer) {
  }

  // MODIFIED
  //	else if (name.equals(EmplodeContext.TABLE_SELECTION_ADDED_PROPERTY)) {
  //		int selectedIndex = ((Integer) _event.getNewValue()).intValue();
  //		addTableSelection(selectedIndex);
  //	}

  public void propertyChange(PropertyChangeEvent _event) {
    Object source = _event.getSource();
    String name = _event.getPropertyName();
    if (source == myTable) {
      if (name.equals("model")) {
        TableModel oldTableModel = (TableModel) _event.getOldValue();
        if (oldTableModel != null && oldTableModel instanceof SortedTableModel) {
          SortedTableModel sortedTableModel = (SortedTableModel) oldTableModel;
          sortedTableModel.removePropertyChangeListener(this);
        }

        TableModel newTableModel = (TableModel) _event.getNewValue();
        if (newTableModel != null && newTableModel instanceof SortedTableModel) {
          SortedTableModel sortedTableModel = (SortedTableModel) newTableModel;
          sortedTableModel.addPropertyChangeListener(this);
          sortChanged(sortedTableModel);
        }
        else {
          sortChanged(null);
        }

        selectionChanged();
      }
    }
    else if (source instanceof SortedTableModel) {
      if (name.equals("sortDirection") || name.equals("sortColumn")) {
        sortChanged((SortedTableModel) source);
      }
    }
  }

  public void downloadStarted(PlayerDatabase _playerDatabase) {
  }

  public void downloadCompleted(PlayerDatabase _playerDatabase) {
    resetFrameTitle(_playerDatabase);
    setMenuOptionsEnabled(true);
    String rootName = _playerDatabase.getDeviceSettings().getName();

    BasicContainerTreeNode rootNode = new BasicContainerTreeNode(rootName, IContainerTreeNode.TYPE_ROOT, IFIDNode.TYPE_PLAYLISTS);
    DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
    FIDPlaylistTreeNode rootPlaylistNode = new FIDPlaylistTreeNode(treeModel, _playerDatabase.getRootPlaylist(), 0);
    rootNode.add(rootPlaylistNode);

    myTree.setLargeModel(true);
    myTree.setModel(treeModel);

    // Little dirty here, but this highlights the "Playlists" node (root playlist).  It happens
    // that it is always the first child of the root tree node
    myTree.setSelectionInterval(1, 1);

    myTreeSoupAttacher.initialize(_playerDatabase);
    _playerDatabase.addNodeTagListener(myTreeSoupAttacher);
    _playerDatabase.addDatabaseListener(myTreeSoupAttacher);
  }

  public void synchronizeInProgress(IDatabaseChange _databaseChange, long _current, long _total) {
  }

  public void synchronizeStarted(IDatabaseChange _databaseChange) {
  }

  public void synchronizeStarted(PlayerDatabase _playerDatabase) {
  }

  public void synchronizeCompleted(IDatabaseChange _databaseChange, boolean _successfully) {
  }

  public void synchronizeCompleted(PlayerDatabase _playerDatabase, boolean _succesfully) {
    myTree.repaint();
    myTable.repaint();
  }

  /**
   * Returns the primary frame.
   * 
   * @return the primary frame
   */
  public JFrame getFrame() {
    return myFrame;
  }

  /**
   * Returns the playlist table.
   *
   * @returns the playlist table
   */
  public JTable getTable() {
    return myTable;
  }

  /**
   * Returns the playlist tree.
   *
   * @returns the playlist tree
   */
  public JTree getTree() {
    return myTree;
  }

  /**
   * Returns whether or not the search tab is selected.
   *
   * @returns whether or not the search tab is selected
   */
  public boolean isSearchTabSelected() {
    boolean searchTabSelected = (myTabbedPane.getSelectedComponent() == mySearchPanel);
    return searchTabSelected;
  }

  /**
   * Sets the sort column and ascending status for the
   * sort label in the status bar.
   */
  protected void sortChanged(SortedTableModel _sortedTableModel) {
    if (_sortedTableModel == null) {
      mySortLabel.setText("Ascending by Position");
    }
    else {
      int sortColumn = _sortedTableModel.getSortingColumn();
      boolean ascending = _sortedTableModel.isAscending();
      String sortColumnName = _sortedTableModel.getColumnName(sortColumn);
      String ascendingStr = (ascending) ? "Ascending" : "Descending";
      mySortLabel.setText(ascendingStr + " by " + sortColumnName);
    }
    myTable.getTableHeader().repaint();
  }

  /**
   * Called when a selection is added to the table
   * 
   * @param _selectedIndex the index to add to the selection
   */
  protected void addTableSelection(int _selectedIndex) {
    if (_selectedIndex == -1) {
      myTable.clearSelection();
    }
    else {
      TableModel tableModel = myTable.getModel();
      if (tableModel instanceof SortedTableModel) {
        SortedTableModel sortedTableModel = (SortedTableModel) tableModel;
        int mappedRow = sortedTableModel.getRowFor(_selectedIndex);
        if (mappedRow != -1) {
          myTable.addRowSelectionInterval(mappedRow, mappedRow);
          myTable.scrollRectToVisible(myTable.getCellRect(mappedRow, 0, true));
          myTable.grabFocus();
        }
      }
      else {
        myTable.addRowSelectionInterval(_selectedIndex, _selectedIndex);
      }
    }
  }

  /**
   * Called when the table selection changes to update the label.
   */
  protected void selectionChanged() {
    int selectedRowCount = myTable.getSelectedRowCount();
    int rowCount = myTable.getRowCount();

    mySelectionLabel.setText(rowCount + " items listed, " + selectedRowCount + " items selected");
  }

  protected void resetFrameTitle(PlayerDatabase _db) {
    StringBuffer title = new StringBuffer();
    title.append("jEmplode v");
    title.append(Version.VERSION_STRING);

    if (_db != null) {
      title.append(" - ");
      title.append(_db.getDeviceSettings().getName());
      title.append(" (");
      title.append(myContext.getSynchronizeClient().getConnectionFactory().getLocationName());
      title.append(")");
    }

    myFrame.setTitle(title.toString());
  }

  protected void createMenus(JMenuBar _menuBar, JToolBar _toolBar) {
    int menuShortcutKeyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    KeyStroke cutKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_X, menuShortcutKeyMask);
    KeyStroke copyKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_C, menuShortcutKeyMask);
    KeyStroke pasteKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_V, menuShortcutKeyMask);
    KeyStroke winCutKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, menuShortcutKeyMask);
    KeyStroke winCopyKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, menuShortcutKeyMask);
    KeyStroke winPasteKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, KeyEvent.SHIFT_MASK);
    KeyStroke clearKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);

    JMenu fileMenu = new JMenu("File");
    fileMenu.setMnemonic('F');
    myOpenPlayerDatabase = new JMenuItem("Open Player Database...");
    myOpenPlayerDatabase.setMnemonic('P');
    OpenDatabaseAction openDatabaseAction = new OpenDatabaseAction(myContext);
    myOpenPlayerDatabase.addActionListener(openDatabaseAction);

    myOpenAnotherDevice = new JMenuItem("Open Another Device...");
    myOpenAnotherDevice.setMnemonic('D');
    OpenAnotherDeviceAction openAnotherDeviceAction = new OpenAnotherDeviceAction();
    myOpenAnotherDevice.addActionListener(openAnotherDeviceAction);

    myRevertChanges = new JMenuItem("Revert Changes");
    myRevertChanges.setMnemonic('R');
    RevertAction revertAction = new RevertAction(myContext);
    myRevertChanges.addActionListener(revertAction);

    mySynchronize = new JMenuItem("Synchronize");
    mySynchronize.setMnemonic('S');
    SynchronizeAction synchronizeAction = new SynchronizeAction(myContext);
    mySynchronize.addActionListener(synchronizeAction);

    myConfigurePlayer = new JMenuItem("Configure Player...");
    myConfigurePlayer.setMnemonic('o');
    ConfigurePlayerAction configurePlayerAction = new ConfigurePlayerAction(myContext);
    myConfigurePlayer.addActionListener(configurePlayerAction);

    myExit = new JMenuItem("Exit");
    myExit.setMnemonic('x');
    try {
      myFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }
    catch (Throwable t) {
    }
    ExitAction exitAction = new ExitAction(myContext, true);
    myExit.addActionListener(exitAction);
    fileMenu.add(myOpenPlayerDatabase);
    fileMenu.add(myOpenAnotherDevice);
    fileMenu.add(myRevertChanges);
    fileMenu.add(mySynchronize);
    fileMenu.add(myConfigurePlayer);
    fileMenu.add(new JSeparator());
    fileMenu.add(myExit);

    JMenu editMenu = new JMenu("Edit");
    editMenu.setMnemonic('E');
    myCut = new JMenuItem("Cut");
    myCut.setMnemonic('t');
    myCut.setAccelerator(cutKeyStroke);
    CutAction cutAction = new CutAction(myContext, true);
    myCut.addActionListener(cutAction);

    myCopy = new JMenuItem("Copy");
    myCopy.setMnemonic('C');
    myCopy.setAccelerator(copyKeyStroke);
    CopyAction copyAction = new CopyAction(myContext, true);
    myCopy.addActionListener(copyAction);

    myPaste = new JMenuItem("Paste");
    myPaste.setMnemonic('P');
    myPaste.setAccelerator(pasteKeyStroke);
    PasteAction pasteAction = new PasteAction(myContext, false);
    myPaste.addActionListener(pasteAction);

    myDeepPaste = new JMenuItem("Deep Paste");
    myDeepPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, menuShortcutKeyMask | Event.SHIFT_MASK));
    myDeepPaste.setToolTipText("Deep paste will make copies of playlists instead of references to playlists");
    PasteAction deepPasteAction = new PasteAction(myContext, true);
    myDeepPaste.addActionListener(deepPasteAction);

    ClearAction clearAction = new ClearAction();

    myTable.registerKeyboardAction(cutAction, "", cutKeyStroke, JComponent.WHEN_FOCUSED);
    myTable.registerKeyboardAction(copyAction, "", copyKeyStroke, JComponent.WHEN_FOCUSED);
    myTable.registerKeyboardAction(pasteAction, "", pasteKeyStroke, JComponent.WHEN_FOCUSED);

    myTable.registerKeyboardAction(cutAction, "", winCutKeyStroke, JComponent.WHEN_FOCUSED);
    myTable.registerKeyboardAction(copyAction, "", winCopyKeyStroke, JComponent.WHEN_FOCUSED);
    myTable.registerKeyboardAction(pasteAction, "", winPasteKeyStroke, JComponent.WHEN_FOCUSED);
    myTable.registerKeyboardAction(clearAction, "", clearKeyStroke, JComponent.WHEN_FOCUSED);

    myTree.registerKeyboardAction(cutAction, "", cutKeyStroke, JComponent.WHEN_FOCUSED);
    myTree.registerKeyboardAction(copyAction, "", copyKeyStroke, JComponent.WHEN_FOCUSED);
    myTree.registerKeyboardAction(pasteAction, "", pasteKeyStroke, JComponent.WHEN_FOCUSED);

    myTree.registerKeyboardAction(cutAction, "", winCutKeyStroke, JComponent.WHEN_FOCUSED);
    myTree.registerKeyboardAction(copyAction, "", winCopyKeyStroke, JComponent.WHEN_FOCUSED);
    myTree.registerKeyboardAction(pasteAction, "", winPasteKeyStroke, JComponent.WHEN_FOCUSED);
    myTree.registerKeyboardAction(clearAction, "", clearKeyStroke, JComponent.WHEN_FOCUSED);

    myDelete = new JMenuItem("Delete");
    myDelete.setMnemonic('D');
    myDelete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
    DeleteAction deleteAction = new DeleteAction(myContext);
    myDelete.addActionListener(deleteAction);

    mySelectAll = new JMenuItem("Select All");
    mySelectAll.setMnemonic('A');
    mySelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, menuShortcutKeyMask));
    SelectAllAction selectAllAction = new SelectAllAction(myContext, myTable);
    mySelectAll.addActionListener(selectAllAction);

    myInvertSelection = new JMenuItem("Invert Selection");
    myInvertSelection.setMnemonic('I');
    InvertSelectionAction invertSelectionAction = new InvertSelectionAction(myContext, myTable);
    myInvertSelection.addActionListener(invertSelectionAction);

    myNewTune = new JMenuItem("New Tune...");
    myNewTune.setMnemonic('u');
    myNewTune.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, menuShortcutKeyMask));
    NewTuneAction newTuneAction = new NewTuneAction(myContext, JFileChooser.FILES_ONLY);
    myNewTune.addActionListener(newTuneAction);

    myNewTuneDirectory = new JMenuItem("New Tune Directory...");
    myNewTuneDirectory.setMnemonic('y');
    myNewTuneDirectory.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, menuShortcutKeyMask));
    NewTuneAction newTuneDirectoryAction = new NewTuneAction(myContext, JFileChooser.DIRECTORIES_ONLY);
    myNewTuneDirectory.addActionListener(newTuneDirectoryAction);

    myNewPlaylist = new JMenuItem("New Playlist...");
    myNewPlaylist.setMnemonic('l');
    myNewPlaylist.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, menuShortcutKeyMask));
    NewPlaylistAction newPlaylistAction = new NewPlaylistAction(myContext);
    myNewPlaylist.addActionListener(newPlaylistAction);

    myAddToPlaylist = UIUtils.createMenuItem("menu.edit.addToPlaylist");
    AddToPlaylistAction addToPlaylistAction = new AddToPlaylistAction(myContext, true);
    myAddToPlaylist.addActionListener(addToPlaylistAction);

    mySoupEditor = new JMenuItem("Create Soup Playlist...");
    mySoupEditor.setMnemonic('S');
    SoupEditorAction soupEditorAction = new SoupEditorAction(myContext);
    mySoupEditor.addActionListener(soupEditorAction);

    myReplaceTune = new JMenuItem("Replace Tune...");
    myReplaceTune.setMnemonic('R');
    myReplaceTune.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, menuShortcutKeyMask));
    ReplaceTuneAction replaceTuneAction = new ReplaceTuneAction(myContext);
    myReplaceTune.addActionListener(replaceTuneAction);

    myMoveUp = new JMenuItem("Move Up");
    myMoveUp.setMnemonic('v');
    MoveUpAction moveUpAction = new MoveUpAction(myContext);
    myMoveUp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP, menuShortcutKeyMask));
    myMoveUp.addActionListener(moveUpAction);

    myMoveDown = new JMenuItem("Move Down");
    myMoveDown.setMnemonic('w');
    MoveDownAction moveDownAction = new MoveDownAction(myContext);
    myMoveDown.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, menuShortcutKeyMask));
    myMoveDown.addActionListener(moveDownAction);

    mySetPlaylistOrder = new JMenuItem("Set Playlist Order...");
    mySetPlaylistOrder.setMnemonic('S');
    SetPlaylistOrderAction setPlaylistOrderAction = new SetPlaylistOrderAction(myContext, myTable);
    mySetPlaylistOrder.addActionListener(setPlaylistOrderAction);

    myProperties = new JMenuItem("Properties");
    myProperties.setMnemonic('r');
    myProperties.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, Event.ALT_MASK));
    PropertiesAction propertiesAction = new PropertiesAction(myContext);
    myProperties.addActionListener(propertiesAction);
    myTable.addMouseListener(propertiesAction);

    myDownload = new JMenuItem("Download...");
    myDownload.setMnemonic('n');
    HijackDownloadAction downloadAction = new HijackDownloadAction(myContext);
    myDownload.addActionListener(downloadAction);

    myToggleColored = new JMenuItem("Toggle Colored");
    myToggleColored.setMnemonic('g');
    ToggleColoredAction toggleColoredAction = new ToggleColoredAction(myContext);
    myToggleColored.addActionListener(toggleColoredAction);

    myResort = new JMenuItem("Resort");
    myResort.setMnemonic('t');
    ResortAction resortAction = new ResortAction(myContext);
    myResort.addActionListener(resortAction);
    editMenu.add(myCut);
    editMenu.add(myCopy);
    editMenu.add(myPaste);
    editMenu.add(myDeepPaste);
    editMenu.add(myDelete);
    editMenu.add(new JSeparator());
    editMenu.add(mySelectAll);
    editMenu.add(myInvertSelection);
    editMenu.add(new JSeparator());
    editMenu.add(myNewTune);
    editMenu.add(myNewTuneDirectory);
    editMenu.add(myNewPlaylist);
    editMenu.add(myAddToPlaylist);
    editMenu.add(myReplaceTune);
    editMenu.add(mySoupEditor);
    editMenu.add(new JSeparator());
    editMenu.add(myMoveUp);
    editMenu.add(myMoveDown);
    editMenu.add(mySetPlaylistOrder);
    editMenu.add(myResort);
    editMenu.add(new JSeparator());
    editMenu.add(myProperties);
    editMenu.add(new JSeparator());
    editMenu.add(myDownload);
    editMenu.add(myToggleColored);

    JMenu viewMenu = new JMenu("View");
    viewMenu.setMnemonic('V');
    myColumns = new JMenuItem("Columns...");
    myColumns.setMnemonic('o');
    ColumnEditorAction columnEditorAction = new ColumnEditorAction(myContext, myTable);
    myColumns.addActionListener(columnEditorAction);
    viewMenu.add(myColumns);

    JMenu toolsMenu = new JMenu("Tools");
    toolsMenu.setMnemonic('T');
    myExportCSV = new JMenuItem("Export Database to CSV...");
    myExportCSV.setMnemonic('E');
    ExportAction exportCSVAction = new ExportAction(myContext, new CSVExporter());
    myExportCSV.addActionListener(exportCSVAction);

    myExportEmplodeXML = new JMenuItem("Export Database to Emplode XML...");
    ExportAction exportEmplodeXMLAction = new ExportAction(myContext, new XMLExporter());
    myExportEmplodeXML.addActionListener(exportEmplodeXMLAction);

    myExportXML = new JMenuItem("Export Database to XML...");
    myExportXML.setMnemonic('x');
    ExportAction exportXMLAction = new ExportAction(myContext, new TMSXMLExporter());
    myExportXML.addActionListener(exportXMLAction);

    myWendyFilters = new JMenuItem("Wendy Filters...");
    myWendyFilters.setMnemonic('w');
    WendyAction wendyAction = new WendyAction(myContext);
    myWendyFilters.addActionListener(wendyAction);

    myEditConfigIni = new JMenuItem("Edit Config.ini...");
    myEditConfigIni.setMnemonic('i');
    EditConfigIniAction editConfigIniAction = new EditConfigIniAction(myContext);
    myEditConfigIni.addActionListener(editConfigIniAction);

    myOptions = new JMenuItem("Options...");
    myOptions.setMnemonic('O');
    OptionsAction optionsAction = new OptionsAction(myContext, myContext.getFrame());
    myOptions.addActionListener(optionsAction);

    myFsck = new JMenuItem("Force Check Media");
    FsckAction fsckAction = new FsckAction(myContext);
    myFsck.addActionListener(fsckAction);

    myUpgrade = new JMenuItem("Upgrade Empeg");
    myUpgrade.setMnemonic('U');
    UpgradeAction upgradeAction = new UpgradeAction(myContext);
    myUpgrade.addActionListener(upgradeAction);
    myUpgrade.setEnabled(true);

    myUpdateHijack = new JMenuItem("Update Hijack + jEmplode");
    myUpdateHijack.setMnemonic('H');
    VersionUpdateAction hijackUpdateAction = new VersionUpdateAction(myContext);
    myUpdateHijack.addActionListener(hijackUpdateAction);

    myRebuild = new JMenuItem("Rebuild");
    myRebuild.setMnemonic('R');
    RebuildAction rebuildAction = new RebuildAction(myContext);
    myRebuild.addActionListener(rebuildAction);

    toolsMenu.add(myWendyFilters);
    toolsMenu.add(new JSeparator());
    toolsMenu.add(myExportCSV);
    toolsMenu.add(myExportEmplodeXML);
    toolsMenu.add(myExportXML);
    toolsMenu.add(new JSeparator());
    toolsMenu.add(myEditConfigIni);
    toolsMenu.add(myFsck);
    toolsMenu.add(myUpgrade);
    toolsMenu.add(myUpdateHijack);
    toolsMenu.add(myRebuild);
    toolsMenu.add(new JSeparator());
    toolsMenu.add(myOptions);

    JMenu appsMenu = new JMenu("Plugins");
    appsMenu.setMnemonic('P');

    LogoEditAction logoEditAction = new LogoEditAction(myContext);
    logoEditAction.putValue("Name", "LogoEdit");
    logoEditAction.putValue("Text", "Logo Editor");
    logoEditAction.putValue("Mnemonic", "typed L");
    logoEditAction.putValue("Accelerator", "");
    logoEditAction.putValue("Icon", "");

    AnimEditAction animEditAction = new AnimEditAction(myContext);
    animEditAction.putValue("Name", "AnimEdit");
    animEditAction.putValue("Text", "Animation Editor");
    animEditAction.putValue("Mnemonic", "typed A");
    animEditAction.putValue("Accelerator", "");
    animEditAction.putValue("Icon", "");

    GrabScreenAction grabScreenAction = new GrabScreenAction(myContext);
    grabScreenAction.putValue("Name", "GrabScreen");
    grabScreenAction.putValue("Text", "Grab Empeg Screen");
    grabScreenAction.putValue("Mnemonic", "typed G");
    grabScreenAction.putValue("Accelerator", "");
    grabScreenAction.putValue("Icon", "");

    JEmplodePlugin[] plugins = PluginsManager.getPluginActions(myContext);
    myPlugins = new JEmplodePlugin[plugins.length + 3];
    myPlugins[0] = logoEditAction;
    myPlugins[1] = animEditAction;
    myPlugins[2] = grabScreenAction;
    System.arraycopy(plugins, 0, myPlugins, 3, plugins.length);

    myPluginMenuItems = new JMenuItem[myPlugins.length];
    for (int i = 0; i < myPlugins.length; i ++ ) {
      myPluginMenuItems[i] = new JMenuItem();

      String nameWithMnemonic = (String) myPlugins[i].getValue("Text");
      UIUtils.NameAndMnemonic nam = UIUtils.parseNameWithMnemonic(nameWithMnemonic);
      myPluginMenuItems[i].setText(nam.getName());
      if (nam.hasMnemonic()) {
        myPluginMenuItems[i].setMnemonic(nam.getMnemonic());
      }
      else {
        String mnemonic = (String) myPlugins[i].getValue("Mnemonic");
        UIUtils.initializeMnemonic("", mnemonic, myPluginMenuItems[i]);
      }

      String iconLocation = (String) myPlugins[i].getValue("Icon");
      UIUtils.initializeIcon("", iconLocation, myPluginMenuItems[i]);

      String accelerator = (String) myPlugins[i].getValue("Accelerator");
      UIUtils.initializeAccelerator("", accelerator, myPluginMenuItems[i]);

      myPluginMenuItems[i].addActionListener(myPlugins[i]);
      appsMenu.add(myPluginMenuItems[i]);
    }

    JMenu helpMenu = new JMenu("Help");
    helpMenu.setMnemonic('H');
    myHelpTopics = new JMenuItem("Help Topics");
    myHelpTopics.setMnemonic('H');
    myHelpTopics.setEnabled(false);

    myAboutEmplode = new JMenuItem("About Emplode");
    myAboutEmplode.setMnemonic('A');
    myAboutEmplode.setEnabled(false);
    helpMenu.add(myHelpTopics);
    helpMenu.add(new JSeparator());
    helpMenu.add(myAboutEmplode);

    _menuBar.add(fileMenu);
    _menuBar.add(editMenu);
    _menuBar.add(viewMenu);
    _menuBar.add(toolsMenu);
    _menuBar.add(appsMenu);
    _menuBar.add(helpMenu);

    JPopupMenu headerPopupMenu = new JPopupMenu();
    JMenuItem popupColumnEditor = new JMenuItem("Columns...");
    popupColumnEditor.addActionListener(columnEditorAction);
    headerPopupMenu.add(popupColumnEditor);

    JTableHeader tableHeader = myTable.getTableHeader();
    tableHeader.addMouseListener(new DefaultPopupListener(headerPopupMenu));

    JPopupMenu tablePopupMenu = new JPopupMenu();
    myPopupOpen = new JMenuItem("Open");
    myPopupOpen.addActionListener(propertiesAction);

    myPopupCut = new JMenuItem("Cut");
    myPopupCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, menuShortcutKeyMask));
    myPopupCut.addActionListener(cutAction);

    myPopupCopy = new JMenuItem("Copy");
    myPopupCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, menuShortcutKeyMask));
    myPopupCopy.addActionListener(copyAction);

    myPopupPaste = new JMenuItem("Paste");
    myPopupPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, menuShortcutKeyMask));
    myPopupPaste.addActionListener(pasteAction);

    myPopupDeepPaste = new JMenuItem("Deep Paste");
    myPopupDeepPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, menuShortcutKeyMask | Event.SHIFT_MASK));
    myPopupDeepPaste.addActionListener(deepPasteAction);

    myPopupDelete = new JMenuItem("Delete");
    myPopupDelete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
    myPopupDelete.addActionListener(deleteAction);

    myPopupNewTune = new JMenuItem("New Tune...");
    myPopupNewTune.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, menuShortcutKeyMask));
    myPopupNewTune.addActionListener(newTuneAction);

    myPopupNewTuneDirectory = new JMenuItem("New Tune Directory...");
    myPopupNewTuneDirectory.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, menuShortcutKeyMask));
    myPopupNewTuneDirectory.addActionListener(newTuneDirectoryAction);

    myPopupNewPlaylist = new JMenuItem("New Playlist...");
    myPopupNewPlaylist.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, menuShortcutKeyMask));
    myPopupNewPlaylist.addActionListener(newPlaylistAction);

    myPopupAddToPlaylist = UIUtils.createMenuItem("menu.edit.addToPlaylist");
    myPopupAddToPlaylist.addActionListener(addToPlaylistAction);

    myPopupReplaceTune = new JMenuItem("Replace Tune...");
    myPopupReplaceTune.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, menuShortcutKeyMask));
    myPopupReplaceTune.addActionListener(replaceTuneAction);

    myPopupMoveUp = new JMenuItem("Move Up");
    myPopupMoveUp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP, menuShortcutKeyMask));
    myPopupMoveUp.addActionListener(moveUpAction);

    myPopupMoveDown = new JMenuItem("Move Down");
    myPopupMoveDown.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, menuShortcutKeyMask));
    myPopupMoveDown.addActionListener(moveDownAction);

    myPopupSetPlaylistOrder = new JMenuItem("Set Playlist Order...");
    myPopupSetPlaylistOrder.addActionListener(setPlaylistOrderAction);

    myPopupProperties = new JMenuItem("Properties");
    myPopupProperties.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, Event.ALT_MASK));
    myPopupProperties.addActionListener(propertiesAction);

    myPopupDownload = new JMenuItem("Download...");
    myPopupDownload.addActionListener(downloadAction);

    myPopupToggleColored = new JMenuItem("Toggle Colored");
    myPopupToggleColored.addActionListener(toggleColoredAction);

    myPopupAddSoupPlaylist = new JMenuItem("Create Soup Playlist...");
    myPopupAddSoupPlaylist.addActionListener(soupEditorAction);
    tablePopupMenu.add(myPopupOpen);
    tablePopupMenu.add(new JSeparator());
    tablePopupMenu.add(myPopupCut);
    tablePopupMenu.add(myPopupCopy);
    tablePopupMenu.add(myPopupPaste);
    tablePopupMenu.add(myPopupDeepPaste);
    tablePopupMenu.add(myPopupDelete);
    tablePopupMenu.add(new JSeparator());
    tablePopupMenu.add(myPopupNewTune);
    tablePopupMenu.add(myPopupNewTuneDirectory);
    tablePopupMenu.add(myPopupNewPlaylist);
    tablePopupMenu.add(myPopupAddToPlaylist);
    tablePopupMenu.add(myPopupReplaceTune);
    tablePopupMenu.add(myPopupAddSoupPlaylist);
    tablePopupMenu.add(new JSeparator());
    tablePopupMenu.add(myPopupMoveUp);
    tablePopupMenu.add(myPopupMoveDown);
    tablePopupMenu.add(myPopupSetPlaylistOrder);
    tablePopupMenu.add(new JSeparator());
    tablePopupMenu.add(myPopupProperties);
    tablePopupMenu.add(new JSeparator());
    tablePopupMenu.add(myPopupDownload);
    tablePopupMenu.add(myPopupToggleColored);

    myToolBarSynchronize = new JButton();
    myToolBarSynchronize.setToolTipText("Synchronize");
    myToolBarSynchronize.setIcon(new ImageIcon(getClass().getResource("/org/jempeg/manager/icons/Save24.gif")));
    myToolBarSynchronize.addActionListener(synchronizeAction);

    myToolBarConfigurePlayer = new JButton();
    myToolBarConfigurePlayer.setToolTipText("Player Configuration");
    myToolBarConfigurePlayer.setIcon(new ImageIcon(getClass().getResource("/org/jempeg/manager/icons/Preferences24.gif")));
    myToolBarConfigurePlayer.addActionListener(configurePlayerAction);

    myToolBarProperties = new JButton();
    myToolBarProperties.setToolTipText("Properties");
    myToolBarProperties.setIcon(new ImageIcon(getClass().getResource("/org/jempeg/manager/icons/Edit24.gif")));
    myToolBarProperties.addActionListener(propertiesAction);

    myToolBarNewTune = new JButton();
    myToolBarNewTune.setToolTipText("New Tune");
    myToolBarNewTune.setIcon(new ImageIcon(getClass().getResource("/org/jempeg/manager/icons/AddTune24.gif")));
    myToolBarNewTune.addActionListener(newTuneAction);

    myToolBarNewTuneDirectory = new JButton();
    myToolBarNewTuneDirectory.setToolTipText("New Tune Directory");
    myToolBarNewTuneDirectory.setIcon(new ImageIcon(getClass().getResource("/org/jempeg/manager/icons/AddTuneDirectory24.gif")));
    myToolBarNewTuneDirectory.addActionListener(newTuneDirectoryAction);

    myToolBarNewPlaylist = new JButton();
    myToolBarNewPlaylist.setToolTipText("New Playlist");
    myToolBarNewPlaylist.setIcon(new ImageIcon(getClass().getResource("/org/jempeg/manager/icons/AddPlaylist24.gif")));
    myToolBarNewPlaylist.addActionListener(newPlaylistAction);

    myToolBarCut = new JButton();
    myToolBarCut.setToolTipText("Cut");
    myToolBarCut.setIcon(new ImageIcon(getClass().getResource("/org/jempeg/manager/icons/Cut24.gif")));
    myToolBarCut.addActionListener(cutAction);

    myToolBarCopy = new JButton();
    myToolBarCopy.setToolTipText("Copy");
    myToolBarCopy.setIcon(new ImageIcon(getClass().getResource("/org/jempeg/manager/icons/Copy24.gif")));
    myToolBarCopy.addActionListener(copyAction);

    myToolBarPaste = new JButton();
    myToolBarPaste.setToolTipText("Paste");
    myToolBarPaste.setIcon(new ImageIcon(getClass().getResource("/org/jempeg/manager/icons/Paste24.gif")));
    myToolBarPaste.addActionListener(pasteAction);

    myToolBarDelete = new JButton();
    myToolBarDelete.setToolTipText("Delete");
    myToolBarDelete.setIcon(new ImageIcon(getClass().getResource("/org/jempeg/manager/icons/Delete24.gif")));
    myToolBarDelete.addActionListener(deleteAction);

    myToolBarMoveUp = new JButton();
    myToolBarMoveUp.setToolTipText("Move Up");
    myToolBarMoveUp.setIcon(new ImageIcon(getClass().getResource("/org/jempeg/manager/icons/Up24.gif")));
    myToolBarMoveUp.addActionListener(moveUpAction);

    myToolBarMoveDown = new JButton();
    myToolBarMoveDown.setToolTipText("Move Down");
    myToolBarMoveDown.setIcon(new ImageIcon(getClass().getResource("/org/jempeg/manager/icons/Down24.gif")));
    myToolBarMoveDown.addActionListener(moveDownAction);

    myToolBarSetPlaylistOrder = new JButton();
    myToolBarSetPlaylistOrder.setToolTipText("Set Playlist Order");
    myToolBarSetPlaylistOrder.setIcon(new ImageIcon(getClass().getResource("/org/jempeg/manager/icons/SetPlaylistOrder24.gif")));
    myToolBarSetPlaylistOrder.addActionListener(setPlaylistOrderAction);

    myToolBarPlayReplace = new JButton();
    myToolBarPlayReplace.setToolTipText("Play (replace)");
    myToolBarPlayReplace.setEnabled(false);
    myToolBarPlayReplace.setIcon(new ImageIcon(getClass().getResource("/org/jempeg/manager/icons/Play24.gif")));
    myToolBarPlayReplace.addActionListener(new PlayAction(myContext, EmpegRequest.PLAYMODE_REPLACE));

    myToolBarPlayInsert = new JButton();
    myToolBarPlayInsert.setToolTipText("Play (insert)");
    myToolBarPlayInsert.setEnabled(false);
    myToolBarPlayInsert.setIcon(new ImageIcon(getClass().getResource("/org/jempeg/manager/icons/PlayInsert24.gif")));
    myToolBarPlayInsert.addActionListener(new PlayAction(myContext, EmpegRequest.PLAYMODE_INSERT));

    myToolBarPlayAppend = new JButton();
    myToolBarPlayAppend.setToolTipText("Play (append)");
    myToolBarPlayAppend.setEnabled(false);
    myToolBarPlayAppend.setIcon(new ImageIcon(getClass().getResource("/org/jempeg/manager/icons/PlayAppend24.gif")));
    myToolBarPlayAppend.addActionListener(new PlayAction(myContext, EmpegRequest.PLAYMODE_APPEND));

    PlayPauseAction playPauseAction = new PlayPauseAction(myContext);
    myToolBarPause = new JButton();
    myToolBarPause.setToolTipText("Pause");
    myToolBarPause.setEnabled(false);
    myToolBarPause.setIcon(new ImageIcon(getClass().getResource("/org/jempeg/manager/icons/Pause24.gif")));
    myToolBarPause.addActionListener(playPauseAction);

    _toolBar.setFloatable(true);
    _toolBar.add(myToolBarSynchronize);
    _toolBar.add(myToolBarConfigurePlayer);
    _toolBar.addSeparator();
    _toolBar.add(myToolBarProperties);

    JSeparator separator1 = new JSeparator(JSeparator.VERTICAL);
    separator1.setMaximumSize(new Dimension(5, 100));
    _toolBar.add(separator1);

    _toolBar.add(myToolBarNewTune);
    _toolBar.add(myToolBarNewTuneDirectory);
    _toolBar.add(myToolBarNewPlaylist);

    JSeparator separator2 = new JSeparator(JSeparator.VERTICAL);
    separator2.setMaximumSize(new Dimension(5, 100));
    _toolBar.add(separator2);

    _toolBar.add(myToolBarCut);
    _toolBar.add(myToolBarCopy);
    _toolBar.add(myToolBarPaste);
    _toolBar.add(myToolBarDelete);

    JSeparator separator3 = new JSeparator(JSeparator.VERTICAL);
    separator3.setMaximumSize(new Dimension(5, 100));
    _toolBar.add(separator3);

    _toolBar.add(myToolBarMoveUp);
    _toolBar.add(myToolBarMoveDown);
    _toolBar.add(myToolBarSetPlaylistOrder);

    JSeparator separator4 = new JSeparator(JSeparator.VERTICAL);
    separator4.setMaximumSize(new Dimension(5, 100));
    _toolBar.add(separator4);

    _toolBar.add(myToolBarPlayReplace);
    _toolBar.add(myToolBarPlayInsert);
    _toolBar.add(myToolBarPlayAppend);
    _toolBar.add(myToolBarPause);

    myFrame.addWindowListener(exitAction);

    TreePopupListener treePopupListener = new TreePopupListener(tablePopupMenu);
    myTree.addMouseListener(treePopupListener);

    TablePopupListener tablePopupListener = new TablePopupListener(tablePopupMenu);
    myTable.addMouseListener(tablePopupListener);
  }

  /**
   * Enables or disables the menu options which are only useful when
   * a player is connected and its database loaded.
   *
   * @param _enabled true to enable the menu options, false to disable them
   */
  protected void setMenuOptionsEnabled(boolean _enabled) {
    final boolean finalEnabled = _enabled;

    Runnable runnable = new Runnable() {
      public void run() {
        myMenuOptionsEnabled = finalEnabled;
        myTableOptionsEnabled = finalEnabled;

        myRevertChanges.setEnabled(finalEnabled);
        mySynchronize.setEnabled(finalEnabled);
        myConfigurePlayer.setEnabled(finalEnabled);
        myDelete.setEnabled(finalEnabled);
        myCut.setEnabled(finalEnabled);
        myCopy.setEnabled(finalEnabled);
        myPaste.setEnabled(finalEnabled);
        myDeepPaste.setEnabled(finalEnabled);
        myDelete.setEnabled(finalEnabled);
        mySelectAll.setEnabled(finalEnabled);
        myInvertSelection.setEnabled(finalEnabled);
        myNewTune.setEnabled(finalEnabled);
        myNewTuneDirectory.setEnabled(finalEnabled);
        myNewPlaylist.setEnabled(finalEnabled);
        myAddToPlaylist.setEnabled(finalEnabled);
        myReplaceTune.setEnabled(finalEnabled);
        myProperties.setEnabled(finalEnabled);
        myDownload.setEnabled(finalEnabled);
        myColumns.setEnabled(finalEnabled);
        myExportCSV.setEnabled(finalEnabled);
        mySoupEditor.setEnabled(finalEnabled);
        myWendyFilters.setEnabled(finalEnabled);
        myExportXML.setEnabled(finalEnabled);
        myEditConfigIni.setEnabled(finalEnabled);
        myUpdateHijack.setEnabled(finalEnabled);
        myFsck.setEnabled(finalEnabled);

        for (int i = 0; i < myPluginMenuItems.length; i ++ ) {
          String requiresConnection = (String) myPlugins[i].getValue("RequiresConnection");
          if (requiresConnection == null || requiresConnection.equalsIgnoreCase("true")) {
            myPluginMenuItems[i].setEnabled(finalEnabled);
          }
        }

        myPopupOpen.setEnabled(finalEnabled);
        myPopupCut.setEnabled(finalEnabled);
        myPopupCopy.setEnabled(finalEnabled);
        myPopupPaste.setEnabled(finalEnabled);
        myPopupDeepPaste.setEnabled(finalEnabled);
        myPopupDelete.setEnabled(finalEnabled);
        myPopupNewTune.setEnabled(finalEnabled);
        myPopupNewTuneDirectory.setEnabled(finalEnabled);
        myPopupNewPlaylist.setEnabled(finalEnabled);
        myPopupAddToPlaylist.setEnabled(finalEnabled);
        myPopupReplaceTune.setEnabled(finalEnabled);
        myPopupProperties.setEnabled(finalEnabled);
        myPopupToggleColored.setEnabled(finalEnabled);
        myPopupAddSoupPlaylist.setEnabled(finalEnabled);

        myToolBarSynchronize.setEnabled(finalEnabled);
        myToolBarConfigurePlayer.setEnabled(finalEnabled);
        myToolBarProperties.setEnabled(finalEnabled);
        myToolBarNewTune.setEnabled(finalEnabled);
        myToolBarNewTuneDirectory.setEnabled(finalEnabled);
        myToolBarNewPlaylist.setEnabled(finalEnabled);
        myToolBarCut.setEnabled(finalEnabled);
        myToolBarCopy.setEnabled(finalEnabled);
        myToolBarPaste.setEnabled(finalEnabled);
        myToolBarDelete.setEnabled(finalEnabled);
        myToolBarPlayReplace.setEnabled(finalEnabled);
        myToolBarPlayInsert.setEnabled(finalEnabled);
        myToolBarPlayAppend.setEnabled(finalEnabled);
        myToolBarPause.setEnabled(finalEnabled);

        updateTableOptions();
      }
    };

    SwingUtilities.invokeLater(runnable);
  }

  /**
   * Enables or disables the menu options that are only available
   * when the table is focused.
   *
   * @param _enabled true to enable the menu options, false to disable them
   */
  protected void setTableOptionsEnabled(boolean _enabled) {
    myTableOptionsEnabled = _enabled;
    updateTableOptions();
  }

  /**
   * Updates the current enabled state of the menu options
   */
  protected void updateTableOptions() {
    Runnable runnable = new Runnable() {
      public void run() {
        myMoveUp.setEnabled(myTableOptionsEnabled && myMenuOptionsEnabled);
        myMoveDown.setEnabled(myTableOptionsEnabled && myMenuOptionsEnabled);
        mySetPlaylistOrder.setEnabled(myTableOptionsEnabled && myMenuOptionsEnabled);

        myPopupMoveUp.setEnabled(myTableOptionsEnabled && myMenuOptionsEnabled);
        myPopupMoveDown.setEnabled(myTableOptionsEnabled && myMenuOptionsEnabled);
        myPopupSetPlaylistOrder.setEnabled(myTableOptionsEnabled && myMenuOptionsEnabled);

        myToolBarMoveUp.setEnabled(myTableOptionsEnabled && myMenuOptionsEnabled);
        myToolBarMoveDown.setEnabled(myTableOptionsEnabled && myMenuOptionsEnabled);
        myToolBarSetPlaylistOrder.setEnabled(myTableOptionsEnabled && myMenuOptionsEnabled);
      }
    };

    SwingUtilities.invokeLater(runnable);
  }

  private JMenuItem[] myPluginMenuItems;
  private JEmplodePlugin[] myPlugins;

  private JMenuItem myOpenPlayerDatabase;
  private JMenuItem myOpenAnotherDevice;
  private JMenuItem myRevertChanges;
  private JMenuItem mySynchronize;
  private JMenuItem myConfigurePlayer;
  private JMenuItem myExit;

  private JMenuItem myCut;
  private JMenuItem myCopy;
  private JMenuItem myPaste;
  private JMenuItem myDeepPaste;
  private JMenuItem myDelete;
  private JMenuItem mySelectAll;
  private JMenuItem myInvertSelection;
  private JMenuItem myNewTune;
  private JMenuItem myNewTuneDirectory;
  private JMenuItem myNewPlaylist;
  private JMenuItem myAddToPlaylist;
  private JMenuItem myReplaceTune;
  private JMenuItem myMoveUp;
  private JMenuItem myMoveDown;
  private JMenuItem mySetPlaylistOrder;
  private JMenuItem myProperties;
  private JMenuItem myDownload;
  private JMenuItem myToggleColored;
  private JMenuItem myResort;
  private JMenuItem myColumns;

  private JMenuItem myExportCSV;
  private JMenuItem myExportEmplodeXML;
  private JMenuItem myExportXML;
  private JMenuItem mySoupEditor;
  private JMenuItem myWendyFilters;
  private JMenuItem myEditConfigIni;
  private JMenuItem myOptions;
  private JMenuItem myFsck;
  private JMenuItem myUpgrade;
  private JMenuItem myUpdateHijack;
  private JMenuItem myRebuild;

  private JMenuItem myHelpTopics;
  private JMenuItem myAboutEmplode;

  private JMenuItem myPopupOpen;
  private JMenuItem myPopupCut;
  private JMenuItem myPopupCopy;
  private JMenuItem myPopupPaste;
  private JMenuItem myPopupDeepPaste;
  private JMenuItem myPopupDelete;
  private JMenuItem myPopupNewTune;
  private JMenuItem myPopupNewTuneDirectory;
  private JMenuItem myPopupNewPlaylist;
  private JMenuItem myPopupAddToPlaylist;
  private JMenuItem myPopupReplaceTune;
  private JMenuItem myPopupMoveUp;
  private JMenuItem myPopupMoveDown;
  private JMenuItem myPopupSetPlaylistOrder;
  private JMenuItem myPopupProperties;
  private JMenuItem myPopupDownload;
  private JMenuItem myPopupToggleColored;
  private JMenuItem myPopupAddSoupPlaylist;

  private AbstractButton myToolBarSynchronize;
  private AbstractButton myToolBarConfigurePlayer;
  private AbstractButton myToolBarProperties;
  private AbstractButton myToolBarNewTune;
  private AbstractButton myToolBarNewTuneDirectory;
  private AbstractButton myToolBarNewPlaylist;
  private AbstractButton myToolBarCut;
  private AbstractButton myToolBarCopy;
  private AbstractButton myToolBarPaste;
  private AbstractButton myToolBarDelete;
  private AbstractButton myToolBarMoveUp;
  private AbstractButton myToolBarMoveDown;
  private AbstractButton myToolBarSetPlaylistOrder;
  private AbstractButton myToolBarPlayReplace;
  private AbstractButton myToolBarPlayInsert;
  private AbstractButton myToolBarPlayAppend;
  private AbstractButton myToolBarPause;

  private RepaintListener myRepaintListener;
}