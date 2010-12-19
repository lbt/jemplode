/* UpgraderDialog - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.dialog;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.net.InetAddress;
import java.util.Enumeration;
import java.util.Vector;

import javax.comm.CommPortIdentifier;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import com.inzyme.io.FileSeekableInputStream;
import com.inzyme.ui.DialogUtils;
import com.inzyme.util.Debug;

import org.jempeg.empeg.protocol.SerialConnectionFactory;
import org.jempeg.empeg.protocol.Upgrader;
import org.jempeg.empeg.protocol.discovery.SerialEmpegDiscoverer;
import org.jempeg.empeg.util.HijackUtils;
import org.jempeg.manager.dialog.ProgressBarProgressListener;
import org.jempeg.manager.event.FileChooserKeyListener;
import org.jempeg.manager.util.FileChooserUtils;
import org.jempeg.protocol.IConnection;
import org.jempeg.protocol.IConnectionFactory;
import org.jempeg.protocol.NoConnectionFactory;
import org.jempeg.protocol.discovery.BasicDevice;

public class UpgraderDialog extends JDialog
{
    private static final int PANEL_SELECT_FILE = 0;
    private static final int PANEL_SELECT_ADDRESS = 1;
    private static final int PANEL_SELECT_PORT = 2;
    private static final int PANEL_SELECT_UPGRADE = 3;
    private static final int EMPEG_UPGRADE = -1;
    private static final int OTHER = -2;
    private static final UploadLocation[] UPLOAD_LOCATIONS
	= { new UploadLocation("Empeg Upgrade", -1, null),
	    new UploadLocation("HiJack Kernel", 65536, "/proc/empeg_kernel"),
	    new UploadLocation("Logo", 40960, "/proc/empeg_bootlogos"),
	    new UploadLocation("Other", -2, null) };
    private IConnection myConn;
    private JPanel myUpgraderPanel;
    private JButton myNextButton;
    private JButton myPrevButton;
    private JButton myCancelButton;
    private JComponent myCurrentPanel;
    private JTextField myFileField;
    private JLabel myAddressLabel;
    private JComboBox myAddressesList;
    private JTextField myAddressField;
    private JComboBox myCommPortList;
    private ConnectionListModel myConnectionListModel;
    private JProgressBar myUpgradeProgressBar;
    private int myPanelNum;
    
    protected class DiscovererThread implements Runnable
    {
	public void run() {
	    SerialEmpegDiscoverer serialDiscoverer
		= new SerialEmpegDiscoverer(10000);
	    myConnectionListModel.removeAllElements();
	    serialDiscoverer.addDiscoveryListener(myConnectionListModel);
	    serialDiscoverer.startDiscovery();
	    if (myConnectionListModel.getSize() == 0)
		myConnectionListModel.addElement
		    (new BasicDevice("No Empegs Found",
				     new NoConnectionFactory()));
	}
    }
    
    protected class UpgraderThread implements Runnable
    {
	public void run() {
	    upgrade();
	}
    }
    
    protected class BrowseListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    JFileChooser fileChooser = new JFileChooser();
	    fileChooser
		.addKeyListener(new FileChooserKeyListener(fileChooser));
	    FileChooserUtils.setToLastDirectory(fileChooser);
	    int action = fileChooser.showOpenDialog(null);
	    if (action == 0) {
		FileChooserUtils.saveLastDirectory(fileChooser);
		File selectedFile = fileChooser.getSelectedFile();
		try {
		    String path = selectedFile.getCanonicalPath();
		    myFileField.setText(path);
		} catch (Throwable t) {
		    Debug.println(t);
		}
	    }
	}
    }
    
    protected class NextListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    next();
	}
    }
    
    protected class PrevListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    prev();
	}
    }
    
    protected class CancelListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    cancel();
	}
    }
    
    protected class AddressItemListener implements ItemListener
    {
	public void itemStateChanged(ItemEvent _event) {
	    UploadLocation location = (UploadLocation) _event.getItem();
	    boolean fieldEnabled = false;
	    if (location != null && location.getAddress() == -2)
		fieldEnabled = true;
	    myAddressLabel.setEnabled(fieldEnabled);
	    myAddressField.setEnabled(fieldEnabled);
	}
    }
    
    protected static class UploadLocation
    {
	private String myName;
	private int myAddress;
	private String myHijackLocation;
	
	public UploadLocation(String _name, int _address,
			      String _hijackLocation) {
	    myName = _name;
	    myAddress = _address;
	    myHijackLocation = _hijackLocation;
	}
	
	public int getAddress() {
	    return myAddress;
	}
	
	public String getHijackLocation() {
	    return myHijackLocation;
	}
	
	public String toString() {
	    return myName;
	}
    }
    
    public UpgraderDialog(JFrame _frame) {
	this(_frame, (IConnection) null);
    }
    
    public UpgraderDialog(JFrame _frame, IConnection _conn) {
	super(_frame, "Upgrader - Ugly, but Functional", true);
	myConn = _conn;
	myUpgraderPanel = new JPanel();
	myUpgraderPanel.setLayout(new BorderLayout());
	JPanel buttonPanel = new JPanel();
	buttonPanel.setLayout(new FlowLayout(2));
	myPrevButton = new JButton("< Prev");
	myPrevButton.addActionListener(new PrevListener());
	myNextButton = new JButton("Next >");
	myNextButton.addActionListener(new NextListener());
	myCancelButton = new JButton("Cancel");
	myCancelButton.addActionListener(new CancelListener());
	buttonPanel.add(myPrevButton);
	buttonPanel.add(myNextButton);
	buttonPanel.add(myCancelButton);
	myUpgraderPanel.add(buttonPanel, "South");
	myFileField = new JTextField();
	myFileField.setColumns(30);
	myAddressLabel = new JLabel("Address (In Decimal, Not Hex): ");
	myAddressField = new JTextField();
	myAddressField.setColumns(30);
	myAddressField.setEnabled(false);
	myAddressesList = new JComboBox(UPLOAD_LOCATIONS);
	myAddressesList.setSelectedIndex(-1);
	myAddressesList.addItemListener(new AddressItemListener());
	myUpgradeProgressBar = new JProgressBar();
	myUpgradeProgressBar.setStringPainted(true);
	Vector commPorts = new Vector();
	Enumeration portIdentifiers = CommPortIdentifier.getPortIdentifiers();
	while (portIdentifiers.hasMoreElements()) {
	    CommPortIdentifier commPort
		= (CommPortIdentifier) portIdentifiers.nextElement();
	    String name = commPort.getName();
	    commPorts.addElement(name);
	}
	myCommPortList = new JComboBox(commPorts);
	showPanel();
	getContentPane().add(myUpgraderPanel);
	pack();
	DialogUtils.centerWindow(this);
    }
    
    protected void showPanel() {
	JComponent panel;
	switch (myPanelNum) {
	case 0: {
	    panel = new JPanel();
	    panel.setLayout(new BoxLayout(panel, 0));
	    panel.add(myFileField);
	    JButton browseButton = new JButton("Browse");
	    browseButton.addActionListener(new BrowseListener());
	    panel.add(browseButton);
	    panel.setBorder(BorderFactory.createTitledBorder
			    ("Select Empeg, Hijack, or Logo Upgrade File"));
	    break;
	}
	case 1:
	    panel = new JPanel();
	    panel.setLayout(new FlowLayout(0));
	    panel.add(myAddressesList);
	    panel.add(Box.createHorizontalStrut(10));
	    panel.add(myAddressLabel);
	    panel.add(myAddressField);
	    panel.setBorder(BorderFactory.createTitledBorder
			    ("Select Address to Write To"));
	    break;
	case 2:
	    panel = new JPanel();
	    panel.setLayout(new BorderLayout());
	    panel.add(myCommPortList);
	    panel.setBorder
		(BorderFactory.createTitledBorder("Select Empeg to Upgrade"));
	    break;
	case 3:
	    panel = new JPanel();
	    panel.add(myUpgradeProgressBar);
	    panel.setBorder(BorderFactory.createTitledBorder("Upgrading"));
	    break;
	default:
	    panel = new JPanel();
	}
	if (myCurrentPanel != null)
	    myUpgraderPanel.remove(myCurrentPanel);
	myCurrentPanel = panel;
	myUpgraderPanel.add(panel, "Center");
	panel.revalidate();
	pack();
    }
    
    protected boolean canNext() {
	boolean canNext = false;
	switch (myPanelNum) {
	case 0:
	    canNext = getFile() != null;
	    break;
	case 1: {
	    UploadLocation location = getUploadLocation();
	    canNext = (location.getAddress() != -2
		       || location.getHijackLocation() != null);
	    break;
	}
	case 2:
	    canNext = getConnectionFactory() != null;
	    break;
	case 3:
	    canNext = false;
	    break;
	default:
	    canNext = false;
	}
	return canNext;
    }
    
    protected synchronized int getNextPanelNum() {
	int panelNum = myPanelNum;
	if (++panelNum == 2) {
	    UploadLocation location = getUploadLocation();
	    if (HijackUtils.shouldUseHijack(myConn) == null
		&& location.getHijackLocation() != null)
		panelNum++;
	}
	return panelNum;
    }
    
    protected synchronized void next() {
	if (canNext()) {
	    myPanelNum = getNextPanelNum();
	    showPanel();
	    switch (myPanelNum) {
	    case 0:
		myPrevButton.setEnabled(false);
		break;
	    case 1: {
		File file = getFile();
		if (file != null) {
		    String name = file.getName().toLowerCase();
		    if (name.endsWith(".upgrade"))
			myAddressesList.setSelectedIndex(0);
		    else if (name.indexOf("hijack") != -1)
			myAddressesList.setSelectedIndex(1);
		    else if (name.endsWith(".raw"))
			myAddressesList.setSelectedIndex(2);
		}
		myPrevButton.setEnabled(true);
		break;
	    }
	    case 2:
		myPrevButton.setEnabled(true);
		break;
	    case 3: {
		myPrevButton.setEnabled(false);
		myNextButton.setEnabled(false);
		myCancelButton.setEnabled(false);
		Thread upgraderThread = new Thread(new UpgraderThread());
		upgraderThread.start();
		break;
	    }
	    }
	} else
	    Toolkit.getDefaultToolkit().beep();
    }
    
    protected synchronized void prev() {
	if (myPanelNum > 0) {
	    myPanelNum--;
	    showPanel();
	} else
	    Toolkit.getDefaultToolkit().beep();
    }
    
    protected synchronized void cancel() {
	setVisible(false);
    }
    
    protected void upgrade() {
	try {
	    FileSeekableInputStream upgradeStream = null;
	    try {
		File upgradeFile = getFile();
		upgradeStream = new FileSeekableInputStream(upgradeFile);
		UploadLocation location = getUploadLocation();
		String hijackLocation = location.getHijackLocation();
		if (hijackLocation == null
		    || HijackUtils.shouldUseHijack(myConn) != null) {
		    Upgrader upgrader = new Upgrader();
		    upgrader.setListener
			(new ProgressBarUpgradeListener(myUpgradeProgressBar));
		    int address = location.getAddress();
		    if (address == -1) {
			upgrader.checkUpgrade(upgradeStream);
			IConnectionFactory connFactory
			    = getConnectionFactory();
			upgrader.doUpgrade(connFactory.createConnection());
		    } else {
			IConnectionFactory connFactory
			    = getConnectionFactory();
			upgrader.upload(connFactory.createConnection(),
					upgradeStream, address);
		    }
		} else {
		    InetAddress inetAddress = HijackUtils.getAddress(myConn);
		    ProgressBarProgressListener listener
			= new ProgressBarProgressListener(myUpgradeProgressBar,
							  null, true, true);
		    HijackUtils.upload(inetAddress, hijackLocation,
				       upgradeStream, 0, listener);
		}
	    } catch (Object object) {
		if (upgradeStream != null)
		    upgradeStream.close();
		throw object;
	    }
	    if (upgradeStream != null)
		upgradeStream.close();
	} catch (Throwable t) {
	    Debug.println(t);
	}
	setVisible(false);
    }
    
    protected File getFile() {
	File file = null;
	String fileName = myFileField.getText();
	if (fileName != null) {
	    file = new File(fileName);
	    if (!file.exists())
		file = null;
	}
	return file;
    }
    
    protected IConnectionFactory getConnectionFactory() {
	IConnectionFactory conn = null;
	try {
	    String commPortName = (String) myCommPortList.getSelectedItem();
	    CommPortIdentifier commPort
		= CommPortIdentifier.getPortIdentifier(commPortName);
	    conn = new SerialConnectionFactory(commPort, 115200);
	} catch (Throwable t) {
	    Debug.println(t);
	}
	return conn;
    }
    
    protected UploadLocation getUploadLocation() {
	UploadLocation location
	    = (UploadLocation) myAddressesList.getSelectedItem();
	if (location != null) {
	    int selectedAddress = location.getAddress();
	    if (selectedAddress == -2) {
		String addressText = myAddressField.getText();
		try {
		    int address = Integer.parseInt(addressText);
		    location = new UploadLocation("Other", address, null);
		} catch (NumberFormatException e) {
		    if (addressText.startsWith("/"))
			location
			    = new UploadLocation("Other", -2, addressText);
		    else
			location = new UploadLocation("Other", -2, null);
		}
	    }
	} else
	    location = new UploadLocation("Other", -2, null);
	return location;
    }
    
    public static void main(String[] _args) {
	UpgraderDialog ud = new UpgraderDialog((JFrame) null);
	ud.setVisible(true);
	System.exit(0);
    }
}
