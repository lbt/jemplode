/* ConnectionSelectionDialog - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.dialog;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.inzyme.ui.DialogUtils;
import com.inzyme.util.Debug;
import com.rio.protocol2.PearlSynchronizeClient;

import org.jempeg.ApplicationContext;
import org.jempeg.empeg.manager.DiscoveryManager;
import org.jempeg.empeg.protocol.EmpegSynchronizeClient;
import org.jempeg.manager.SynchronizeUI;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.protocol.IConnectionFactory;
import org.jempeg.protocol.NoConnectionFactory;
import org.jempeg.protocol.ProtocolException;
import org.jempeg.protocol.discovery.BasicDevice;
import org.jempeg.protocol.discovery.IDevice;
import org.jempeg.protocol.discovery.IDiscoverer;
import org.jempeg.protocol.discovery.IDiscoveryListener;
import org.jempeg.protocol.discovery.SSDPDevice;

public class ConnectionSelectionDialog extends JDialog
{
    private JList myConnectionList;
    private JButton myOKButton;
    private JButton myRefreshButton;
    private boolean mySelectionAllowed;
    private boolean myConnectionSelected;
    private DiscoveryManager myDiscoveryManager;
    
    protected class AllowSelectionListener implements IDiscoveryListener
    {
	private ConnectionListModel myListModel;
	
	public AllowSelectionListener(ConnectionListModel _listModel) {
	    myListModel = _listModel;
	}
	
	public void deviceDiscovered(IDiscoverer _discoverer,
				     IDevice _device) {
	    setSelectionAllowed(true);
	    myListModel.deviceDiscovered(_discoverer, _device);
	}
    }
    
    protected class ListSelectionHandler implements ListSelectionListener
    {
	public void valueChanged(ListSelectionEvent _event) {
	    myOKButton.setEnabled(isSelectionAllowed());
	}
    }
    
    protected class RefreshRunnable implements Runnable
    {
	public void run() {
	    refresh();
	}
    }
    
    protected class DoubleClickAction extends MouseAdapter
    {
	public void mousePressed(MouseEvent _event) {
	    if (isSelectionAllowed() && _event.getClickCount() == 2) {
		selectConnection();
		ConnectionSelectionDialog.this.setVisible(false);
	    }
	}
    }
    
    protected class OKAction extends AbstractAction
    {
	public void actionPerformed(ActionEvent _event) {
	    if (isSelectionAllowed()) {
		selectConnection();
		ConnectionSelectionDialog.this.setVisible(false);
	    }
	}
    }
    
    protected class CancelAction extends AbstractAction
    {
	public void actionPerformed(ActionEvent _event) {
	    cancel();
	    ConnectionSelectionDialog.this.setVisible(false);
	}
    }
    
    protected class RefreshAction extends AbstractAction
    {
	public void actionPerformed(ActionEvent _eent) {
	    refreshInBackground();
	}
    }
    
    protected class OptionsAction extends AbstractAction
    {
	public void actionPerformed(ActionEvent _event) {
	    options();
	}
    }
    
    protected class WindowCloseListener extends WindowAdapter
    {
	public void windowClosing(WindowEvent _event) {
	    cancel();
	    ConnectionSelectionDialog.this.setVisible(false);
	}
    }
    
    public ConnectionSelectionDialog(JFrame _frame) throws ProtocolException {
	super(_frame, "Select Connection", true);
	((JComponent) getContentPane())
	    .setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	myDiscoveryManager = new DiscoveryManager();
	setDefaultCloseOperation(0);
	myConnectionList = new JList();
	myConnectionList.setSelectionMode(1);
	myConnectionList.addListSelectionListener(new ListSelectionHandler());
	myConnectionList.addMouseListener(new DoubleClickAction());
	JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 5, 0));
	myOKButton = new JButton("OK");
	setSelectionAllowed(false);
	myOKButton.addActionListener(new OKAction());
	JButton cancelButton = new JButton("Cancel");
	cancelButton.addActionListener(new CancelAction());
	myRefreshButton = new JButton("Refresh");
	myRefreshButton.addActionListener(new RefreshAction());
	JButton optionsButton = new JButton("Options...");
	optionsButton.addActionListener(new OptionsAction());
	buttonPanel.add("OK", myOKButton);
	buttonPanel.add("Cancel", cancelButton);
	buttonPanel.add("Refresh", myRefreshButton);
	buttonPanel.add("Options...", optionsButton);
	Box vbox = Box.createVerticalBox();
	vbox.add(new JScrollPane(myConnectionList));
	vbox.add(Box.createVerticalStrut(15));
	vbox.add(buttonPanel);
	getContentPane().add(vbox);
	getGlassPane().setCursor(Cursor.getPredefinedCursor(3));
	pack();
	DialogUtils.centerWindow(this);
	addWindowListener(new WindowCloseListener());
    }
    
    public static void showConnectionSelectionDialog
	(ApplicationContext _context) throws ProtocolException {
	ConnectionSelectionDialog csd
	    = new ConnectionSelectionDialog(_context.getFrame());
	csd.refreshInBackground();
	csd.setVisible(true);
	boolean isConnectionSelected = csd.isConnectionSelected();
	csd.cancel();
	if (isConnectionSelected) {
	    IConnectionFactory connFactory = csd.getConnectionFactory();
	    if (connFactory != null) {
		PlayerDatabase playerDatabase = new PlayerDatabase();
		IDevice device = csd.getDevice();
		org.jempeg.protocol.ISynchronizeClient synchronizeClient;
		if (device instanceof SSDPDevice) {
		    synchronizeClient
			= new PearlSynchronizeClient(connFactory);
		    playerDatabase.setCreateUnattachedItems(false);
		} else {
		    synchronizeClient
			= new EmpegSynchronizeClient(connFactory);
		    playerDatabase.setCreateUnattachedItems(true);
		}
		_context.setSynchronizeClient(playerDatabase,
					      synchronizeClient);
		new SynchronizeUI
		    (playerDatabase, synchronizeClient, _context.getFrame())
		    .downloadInBackground
		    (_context.getDownloadProgressListener());
	    }
	}
    }
    
    protected void setSelectionAllowed(boolean _selectionAllowed) {
	if (!_selectionAllowed)
	    myOKButton.setEnabled(false);
	mySelectionAllowed = _selectionAllowed;
    }
    
    protected boolean isSelectionAllowed() {
	if (mySelectionAllowed && !myConnectionList.isSelectionEmpty())
	    return true;
	return false;
    }
    
    public boolean isConnectionSelected() {
	return myConnectionSelected;
    }
    
    public IConnectionFactory getConnectionFactory() {
	return getDevice().getConnectionFactory();
    }
    
    public IDevice getDevice() {
	int selectedIndex = myConnectionList.getSelectedIndex();
	ConnectionListModel listModel
	    = (ConnectionListModel) myConnectionList.getModel();
	IDevice device = listModel.getDeviceAt(selectedIndex);
	return device;
    }
    
    protected void selectConnection() {
	myConnectionSelected = true;
    }
    
    protected void cancel() {
	myConnectionSelected = false;
	myDiscoveryManager.cancel();
    }
    
    protected void options() {
	ConnectionOptionsDialog optionsDialog
	    = new ConnectionOptionsDialog((JFrame) getParent(),
					  myDiscoveryManager);
	optionsDialog.setVisible(true);
	if (optionsDialog.isApproved())
	    refreshInBackground();
    }
    
    protected void refresh() {
	getGlassPane().setVisible(true);
	setTitle("Looking for Devices...");
	try {
	    try {
		if (myDiscoveryManager == null)
		    myDiscoveryManager = new DiscoveryManager();
		ConnectionListModel listModel = new ConnectionListModel();
		myConnectionList.setModel(listModel);
		myDiscoveryManager
		    .refresh(new AllowSelectionListener(listModel));
		if (!myDiscoveryManager.isCancelled() && isVisible()) {
		    int empegsFound = myConnectionList.getModel().getSize();
		    if (empegsFound == 0) {
			listModel.addElement
			    (new BasicDevice("No Devices Found",
					     new NoConnectionFactory()));
			setTitle("No Devices Found");
			setSelectionAllowed(false);
		    } else {
			setTitle("Select a Device");
			if (empegsFound == 1
			    && myDiscoveryManager.isAutoSelectEnabled()) {
			    myConnectionList.setSelectedIndex(0);
			    myOKButton.doClick();
			}
		    }
		}
	    } catch (Exception e) {
		Debug.println(e);
	    }
	} catch (Object object) {
	    getGlassPane().setVisible(false);
	    throw object;
	}
	getGlassPane().setVisible(false);
    }
    
    public void refreshInBackground() {
	Thread t = new Thread(new RefreshRunnable(),
			      "jEmplode: Refresh Connections");
	t.start();
    }
}
