/* AbstractRioManagerUI - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.rmmlite;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.BoxLayout;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicProgressBarUI;
import javax.swing.table.TableModel;

import com.inzyme.container.ContainerSelection;
import com.inzyme.container.IContainer;
import com.inzyme.format.SizeFormat;
import com.inzyme.progress.SilentProgressListener;
import com.inzyme.properties.PropertiesManager;
import com.inzyme.table.SortedTableModel;
import com.inzyme.text.ResourceBundleKey;
import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.ui.DialogUtils;
import com.inzyme.ui.UIUtils;
import com.inzyme.util.Debug;
import com.rio.rmmlite.action.ShowHideSyncDetailsAction;

import org.jempeg.ApplicationContext;
import org.jempeg.manager.IManagerUI;
import org.jempeg.manager.action.DownloadAction;
import org.jempeg.manager.action.ExitAction;
import org.jempeg.manager.action.NewTuneAction;
import org.jempeg.manager.action.SynchronizeAction;
import org.jempeg.manager.dialog.ProgressDialog;
import org.jempeg.manager.dialog.SynchronizeQueueDialog;
import org.jempeg.manager.event.RepaintListener;
import org.jempeg.manager.util.EmplodeClipboard;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IDatabaseChange;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.nodestore.event.IContextListener;
import org.jempeg.nodestore.event.IDatabaseListener;
import org.jempeg.nodestore.event.ISynchronizeClientListener;
import org.jempeg.nodestore.soup.PersistentSoupAttacher;
import org.jempeg.nodestore.soup.TransientSoupAttacher;
import org.jempeg.protocol.DefaultAuthenticator;
import org.jempeg.protocol.DefaultHostRequestor;
import org.jempeg.protocol.ISynchronizeClient;

public abstract class AbstractRioManagerUI
    implements PropertyChangeListener, ISynchronizeClientListener, IManagerUI,
	       IContextListener, IDatabaseListener
{
    private ApplicationContext myContext;
    private JFrame myFrame;
    private JLabel myTrackCountLabel;
    private JLabel myUsedSpaceLabel;
    private JLabel myFreeSpaceLabel;
    private JLabel myStatusLabel;
    private SynchronizeQueueDialog mySynchronizeQueueDialog;
    private BoundedRangeModel myFreeSpaceModel;
    private JProgressBar myFreeSpaceProgressBar;
    private TransientSoupAttacher myTransientSoupAttacher;
    private PersistentSoupAttacher myPersistentSoupAttacher;
    private String myProductName;
    private boolean myApplet;
    private JMenuItem myNewTune;
    private JMenuItem myNewTuneDirectory;
    private JMenuItem myDownload;
    private JMenuItem mySynchronize;
    private JMenuItem mySyncDetails;
    private JMenuItem myExit;
    private DownloadAction myDownloadAction;
    private NewTuneAction myNewTuneAction;
    private NewTuneAction myNewTuneDirectoryAction;
    private boolean myMenuOptionsEnabled;
    private RepaintListener myRepaintListener;
    
    public AbstractRioManagerUI(ApplicationContext _context,
				String _productName, boolean _applet) {
	UIUtils.initializeOSX(ResourceBundleUtils.getUIString
			      (_productName + ".frameTitleDefault"));
	myApplet = _applet;
	myContext = _context;
	myProductName = _productName;
	String lookAndFeelClassName
	    = (PropertiesManager.getInstance().getProperty
	       ("lnfClassName", UIManager.getSystemLookAndFeelClassName()));
	try {
	    UIManager.setLookAndFeel(lookAndFeelClassName);
	} catch (Exception e) {
	    Debug.println(8, e);
	}
	myFrame = new JFrame();
	DefaultAuthenticator.setTopLevelWindow(myFrame);
	DefaultHostRequestor.setTopLevelWindow(myFrame);
	Debug.setTopLevelWindow(myFrame);
	mySynchronizeQueueDialog
	    = new SynchronizeQueueDialog(myFrame, _context, false, true);
	myFreeSpaceModel = new DefaultBoundedRangeModel();
	myFreeSpaceModel.setMinimum(0);
	myFreeSpaceModel.setMaximum(100);
	myFreeSpaceProgressBar = new JProgressBar(myFreeSpaceModel);
	myFreeSpaceProgressBar.setUI(new BasicProgressBarUI());
	myStatusLabel = new JLabel(" ");
	myStatusLabel.addMouseListener
	    (new ShowHideSyncDetailsAction(mySynchronizeQueueDialog));
	myTrackCountLabel = new JLabel(" ");
	myUsedSpaceLabel = new JLabel(" ");
	Font labelFont = myUsedSpaceLabel.getFont();
	myUsedSpaceLabel.setFont(new Font(labelFont.getName(),
					  labelFont.getStyle(),
					  labelFont.getSize() - 1));
	myFreeSpaceLabel = new JLabel(" ");
	myFreeSpaceLabel.setFont(new Font(labelFont.getName(),
					  labelFont.getStyle(),
					  labelFont.getSize() - 1));
	_context.setFrame(myFrame);
	_context.setDownloadProgressListener(new ProgressDialog(myFrame,
								false));
	_context.setSynchronizeProgressListener(new SilentProgressListener());
	_context.setImportFilesProgressListener(new ProgressDialog(myFrame,
								   true));
	_context.setDownloadFilesProgressListener(new ProgressDialog(myFrame,
								     true));
	_context.setDefaultProgressListener(new ProgressDialog(myFrame,
							       false));
	_context.addContextListener(this);
	try {
	    myFrame.setIconImage(Toolkit.getDefaultToolkit().getImage
				 (this.getClass().getResource
				  ("/com/rio/rmmlite/icons/frameIcon.gif")));
	} catch (Throwable throwable) {
	    /* empty */
	}
	setFrameTitle(null);
	JPanel layoutPanel = new JPanel();
	layoutPanel.setLayout(new BorderLayout());
	PropertiesManager.getInstance().setIntProperty("jempeg.soup.num", 0);
	myTransientSoupAttacher = new TransientSoupAttacher(true);
	myPersistentSoupAttacher = new PersistentSoupAttacher(true);
	myRepaintListener = new RepaintListener();
	EmplodeClipboard.getInstance()
	    .addPropertyChangeListener(myRepaintListener);
	JPanel spaceLabelsPanel = new JPanel();
	spaceLabelsPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0,
								   0));
	spaceLabelsPanel.setLayout(new BorderLayout());
	myFreeSpaceLabel.setHorizontalAlignment(4);
	spaceLabelsPanel.add(myFreeSpaceProgressBar, "North");
	spaceLabelsPanel.add(myUsedSpaceLabel, "West");
	spaceLabelsPanel.add(myFreeSpaceLabel, "East");
	layoutPanel.add(createContentPane(), "Center");
	layoutPanel.add(spaceLabelsPanel, "South");
	JPanel statusBar = new JPanel();
	statusBar.setBorder(BorderFactory.createEmptyBorder(2, 2, 0, 2));
	statusBar.setLayout(new BorderLayout());
	myTrackCountLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0,
								    3));
	JPanel selectionCountPanel = new JPanel();
	selectionCountPanel.setLayout(new BoxLayout(selectionCountPanel, 0));
	selectionCountPanel.add(myTrackCountLabel);
	statusBar.add(myStatusLabel, "West");
	statusBar.add(selectionCountPanel, "East");
	layoutPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
	JMenuBar menuBar = new JMenuBar();
	createMenus(menuBar);
	setMenuOptionsEnabled(false);
	myFrame.setJMenuBar(menuBar);
	((JComponent) myFrame.getContentPane())
	    .setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	myFrame.getContentPane().add(layoutPanel, "Center");
	myFrame.getContentPane().add(statusBar, "South");
	myFrame.getGlassPane().addMouseListener(new MouseAdapter() {
	});
	myFrame.getGlassPane().addKeyListener(new KeyAdapter() {
	});
	myFrame.getGlassPane().setCursor(Cursor.getPredefinedCursor(3));
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	myFrame.setSize((int) Math.min(700.0,
				       (double) screenSize.width * 0.75),
			(int) Math.min(400.0,
				       (double) screenSize.height * 0.75));
	DialogUtils.centerWindow(myFrame);
    }
    
    protected ApplicationContext getContext() {
	return myContext;
    }
    
    protected RepaintListener getRepaintListener() {
	return myRepaintListener;
    }
    
    protected abstract JComponent createContentPane();
    
    public void downloadStarted(PlayerDatabase _playerDatabase) {
	/* empty */
    }
    
    public void freeSpaceChanged(PlayerDatabase _playerDatabase,
				 long _totalSpace, long _freeSpace) {
	if (!_playerDatabase.getSynchronizeQueue().isSynchronizingNow())
	    myFreeSpaceModel.setValue((int) (100.0
					     * ((double) (_totalSpace
							  - _freeSpace)
						/ (double) _totalSpace)));
	myFreeSpaceLabel.setText(new ResourceBundleKey
				     ("ui", "freeSpace.text",
				      (new Object[]
				       { SizeFormat.getInstance()
					     .format((double) _freeSpace) }))
				     .getString());
	myUsedSpaceLabel.setText
	    (new ResourceBundleKey
		 ("ui", "usedSpace.text",
		  (new Object[]
		   { SizeFormat.getInstance()
			 .format((double) (_totalSpace - _freeSpace)) }))
		 .getString());
    }
    
    public void databaseCleared(PlayerDatabase _playerDatabase) {
	/* empty */
    }
    
    public void nodeAdded(IFIDNode _node) {
	/* empty */
    }
    
    public void nodeRemoved(IFIDNode _node) {
	/* empty */
    }
    
    public void synchronizeCompleted(IDatabaseChange _databaseChange,
				     boolean _successfully) {
	String completed = "";
	myStatusLabel.setText(completed);
    }
    
    public void synchronizeCompleted(PlayerDatabase _playerDatabase,
				     boolean _succesfully) {
	String completed = "";
	myStatusLabel.setText(completed);
	setFrameTitle(null);
	myFreeSpaceProgressBar.setModel(myFreeSpaceModel);
	if (!mySynchronizeQueueDialog.isVisible())
	    mySynchronizeQueueDialog.getSynchronizeQueuePanel()
		.getSynchronizeQueueTableModel
		().clearDequeuedChanges();
	freeSpaceChanged(_playerDatabase, _playerDatabase.getTotalSpace(),
			 _playerDatabase.getFreeSpace());
    }
    
    public void synchronizeInProgress(IDatabaseChange _databaseChange,
				      long _current, long _total) {
	/* empty */
    }
    
    public void synchronizeStarted(IDatabaseChange _databaseChange) {
	String transferring
	    = ResourceBundleUtils.getUIString("statusLabel.transferring",
					      (new Object[]
					       { _databaseChange.getName() }));
	myStatusLabel.setText(transferring);
	setFrameTitle(transferring);
    }
    
    public void synchronizeStarted(PlayerDatabase _playerDatabase) {
	myFreeSpaceProgressBar.setModel(mySynchronizeQueueDialog
					    .getSynchronizeQueuePanel
					    ().getSynchronizeQueueTableModel
					    ().getCompletionModel());
	String transferStarted
	    = ResourceBundleUtils.getUIString("statusLabel.transferStarted");
	myStatusLabel.setText(transferStarted);
	setFrameTitle(transferStarted);
    }
    
    public void selectedContainerChanged(IContainer _oldSelectedContainer,
					 IContainer _newSelectedContainer) {
	selectionChanged();
    }
    
    public void selectionChanged(Object _source,
				 ContainerSelection _oldSelection,
				 ContainerSelection _newSelection) {
	if (!(_source instanceof JTable)) {
	    if (_newSelection == null)
		addTableSelection(-1);
	    else {
		int[] indexes = _newSelection.getSelectedIndexes();
		for (int i = 0; i < indexes.length; i++)
		    addTableSelection(indexes[i]);
	    }
	}
	selectionChanged();
    }
    
    public void synchronizeClientChanged
	(ISynchronizeClient _oldSynchronizeClient,
	 ISynchronizeClient _newSynchronizeClient) {
	if (_oldSynchronizeClient != null) {
	    _oldSynchronizeClient.removeSynchronizeClientListener(this);
	    _oldSynchronizeClient
		.removeSynchronizeClientListener(myTransientSoupAttacher);
	    _oldSynchronizeClient
		.removeSynchronizeClientListener(myPersistentSoupAttacher);
	}
	if (_newSynchronizeClient != null) {
	    _newSynchronizeClient.addSynchronizeClientListener(this);
	    _newSynchronizeClient
		.addSynchronizeClientListener(myTransientSoupAttacher);
	    _newSynchronizeClient
		.addSynchronizeClientListener(myPersistentSoupAttacher);
	}
    }
    
    public void propertyChange(PropertyChangeEvent _event) {
	Object source = _event.getSource();
	String name = _event.getPropertyName();
	if (source instanceof JTable) {
	    if (name.equals("model")) {
		TableModel oldTableModel = (TableModel) _event.getOldValue();
		TableModel newTableModel = (TableModel) _event.getNewValue();
		tableModelChanged(oldTableModel, newTableModel);
	    }
	} else if (source instanceof SortedTableModel
		   && (name.equals("sortDirection")
		       || name.equals("sortColumn")))
	    myContext.getTable().getTableHeader().repaint();
    }
    
    protected void tableModelChanged(TableModel _oldTableModel,
				     TableModel _newTableModel) {
	if (_oldTableModel != null
	    && _oldTableModel instanceof SortedTableModel) {
	    SortedTableModel sortedTableModel
		= (SortedTableModel) _oldTableModel;
	    sortedTableModel.removePropertyChangeListener(this);
	}
	if (_newTableModel != null
	    && _newTableModel instanceof SortedTableModel) {
	    SortedTableModel sortedTableModel
		= (SortedTableModel) _newTableModel;
	    sortedTableModel.addPropertyChangeListener(this);
	}
	selectionChanged();
    }
    
    public void databaseChanged(PlayerDatabase _oldDatabase,
				PlayerDatabase _newDatabase) {
	if (_oldDatabase != null) {
	    _oldDatabase.removeDatabaseListener(myRepaintListener);
	    _oldDatabase.removeNodeTagListener(myRepaintListener);
	    _oldDatabase.removeDatabaseListener(this);
	}
	if (_newDatabase != null) {
	    _newDatabase.addDatabaseListener(myRepaintListener);
	    _newDatabase.addNodeTagListener(myRepaintListener);
	    _newDatabase.addDatabaseListener(this);
	}
    }
    
    public void downloadCompleted(PlayerDatabase _playerDatabase) {
	freeSpaceChanged(_playerDatabase, _playerDatabase.getTotalSpace(),
			 _playerDatabase.getFreeSpace());
	setFrameTitle(null);
	setMenuOptionsEnabled(true);
    }
    
    public JFrame getFrame() {
	return myFrame;
    }
    
    protected void addTableSelection(int _selectedIndex) {
	System.out.println
	    ("AbstractRioManagerUI.addTableSelection: adding selection "
	     + _selectedIndex);
	JTable table = myContext.getTable();
	if (_selectedIndex == -1)
	    table.clearSelection();
	else {
	    TableModel tableModel = table.getModel();
	    if (tableModel instanceof SortedTableModel) {
		SortedTableModel sortedTableModel
		    = (SortedTableModel) tableModel;
		int mappedRow = sortedTableModel.getRowFor(_selectedIndex);
		if (mappedRow != -1) {
		    table.addRowSelectionInterval(mappedRow, mappedRow);
		    table.scrollRectToVisible(table.getCellRect(mappedRow, 0,
								true));
		    table.grabFocus();
		}
	    } else
		table.addRowSelectionInterval(_selectedIndex, _selectedIndex);
	}
    }
    
    protected void selectionChanged() {
	IContainer selectedContainer = myContext.getSelectedContainer();
	String selectedItems;
	if (selectedContainer instanceof FIDPlaylist) {
	    FIDPlaylist selectedPlaylist = (FIDPlaylist) selectedContainer;
	    int type = selectedPlaylist.getContainedType();
	    selectedItems = myProductName + "." + type;
	} else
	    selectedItems = myProductName;
	int totalRowCount
	    = selectedContainer == null ? 0 : selectedContainer.getSize();
	String trackCountText
	    = new ResourceBundleKey
		  ("ui", selectedItems + ".totalCount.text")
		  .getString(new Object[] { new Integer(totalRowCount) });
	myTrackCountLabel.setText(trackCountText);
	boolean transientSelected = false;
	ContainerSelection selection = myContext.getSelection();
	int selectedRowCount = selection == null ? 0 : selection.getSize();
	for (int i = 0; !transientSelected && i < selectedRowCount; i++) {
	    Object obj = selection.getValueAt(i);
	    if (obj instanceof IFIDNode) {
		IFIDNode selectedNode = (IFIDNode) obj;
		transientSelected |= selectedNode.isTransient();
	    }
	}
	setSelectionMenuOptionsEnabled(myMenuOptionsEnabled, selectedRowCount,
				       transientSelected);
    }
    
    protected void setFrameTitle(String _status) {
	String title;
	if (_status == null)
	    title
		= new ResourceBundleKey
		      ("ui", myProductName + ".frameTitleDefault").getString();
	else
	    title = new ResourceBundleKey
			("ui", myProductName + ".frameTitleWorking",
			 new Object[] { _status })
			.getString();
	myFrame.setTitle(title);
    }
    
    protected void createMenus(JMenuBar _menuBar) {
	JMenu fileMenu = UIUtils.createMenu("menu.file");
	myNewTune = UIUtils.createMenuItem("menu.file." + myProductName
					   + ".newTune");
	myNewTuneAction = new NewTuneAction(myContext, 2);
	myNewTune.addActionListener(myNewTuneAction);
	myNewTuneDirectory
	    = UIUtils.createMenuItem("menu.file." + myProductName
				     + ".newTuneDirectory");
	myNewTuneDirectoryAction = new NewTuneAction(myContext, 1);
	myNewTuneDirectory.addActionListener(myNewTuneDirectoryAction);
	myDownload = UIUtils.createMenuItem("menu.file." + myProductName
					    + ".download");
	myDownloadAction = new DownloadAction(myContext);
	myDownload.addActionListener(myDownloadAction);
	mySynchronize = UIUtils.createMenuItem("menu.file.synchronize");
	SynchronizeAction synchronizeAction = new SynchronizeAction(myContext);
	mySynchronize.addActionListener(synchronizeAction);
	mySyncDetails = UIUtils.createMenuItem("menu.file.syncDetails");
	mySyncDetails.addActionListener
	    (new ShowHideSyncDetailsAction(mySynchronizeQueueDialog));
	myExit = UIUtils.createMenuItem("menu.file.exit");
	try {
	    myFrame.setDefaultCloseOperation(0);
	} catch (Throwable throwable) {
	    /* empty */
	}
	ExitAction exitAction = new ExitAction(myContext, !myApplet);
	myExit.addActionListener(exitAction);
	fileMenu.add(myNewTune);
	fileMenu.add(myNewTuneDirectory);
	fileMenu.add(myDownload);
	fileMenu.add(new JSeparator());
	fileMenu.add(mySynchronize);
	fileMenu.add(mySyncDetails);
	fileMenu.add(new JSeparator());
	fileMenu.add(myExit);
	_menuBar.add(fileMenu);
	myFrame.addWindowListener(exitAction);
    }
    
    protected void setMenuOptionsEnabled(boolean _enabled) {
	final boolean finalEnabled = _enabled;
	Runnable runnable = new Runnable() {
	    public void run() {
		myMenuOptionsEnabled = finalEnabled;
		myNewTune.setEnabled(finalEnabled);
		myNewTuneDirectory.setEnabled(finalEnabled);
		mySynchronize.setEnabled(finalEnabled);
		mySyncDetails.setEnabled(finalEnabled);
		setSelectionMenuOptionsEnabled(finalEnabled, 0, false);
	    }
	};
	SwingUtilities.invokeLater(runnable);
    }
    
    protected void setSelectionMenuOptionsEnabled
	(boolean _enabled, int _selectedRowCount, boolean _transientSelected) {
	boolean rowsSelected = _selectedRowCount > 0;
	myDownload.setEnabled(rowsSelected);
    }
    
    protected boolean isMenuOptionsEnabled() {
	return myMenuOptionsEnabled;
    }
    
    protected NewTuneAction getNewTuneAction() {
	return myNewTuneAction;
    }
    
    protected NewTuneAction getNewTuneDirectoryAction() {
	return myNewTuneDirectoryAction;
    }
    
    protected DownloadAction getDownloadAction() {
	return myDownloadAction;
    }
}
