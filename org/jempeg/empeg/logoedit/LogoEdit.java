/* LogoEdit - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.logoedit;
import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;

import javax.comm.CommPortIdentifier;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import com.inzyme.io.MemorySeekableInputStream;
import com.inzyme.io.SeekableInputStream;
import com.inzyme.properties.PropertiesManager;
import com.inzyme.ui.DialogUtils;
import com.inzyme.util.ApplicationUtils;
import com.inzyme.util.Debug;

import org.jempeg.JEmplodeProperties;
import org.jempeg.empeg.manager.dialog.SerialPortSelectionDialog;
import org.jempeg.empeg.protocol.SerialConnectionFactory;
import org.jempeg.empeg.protocol.Upgrader;
import org.jempeg.empeg.util.HijackUtils;
import org.jempeg.manager.util.FileChooserUtils;
import org.jempeg.protocol.IConnection;

public class LogoEdit extends JPanel
{
    private IConnection myConn;
    private JComboBox myTypeCB;
    private LogoEditPanel myHomeLogoEditPanel;
    private LogoEditPanel myCarLogoEditPanel;
    
    private class UndoListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    if (myHomeLogoEditPanel.getModificationTime()
		> myCarLogoEditPanel.getModificationTime())
		myHomeLogoEditPanel.undo();
	    else
		myCarLogoEditPanel.undo();
	}
    }
    
    private class LoadListener implements ActionListener
    {
	private boolean mySetHome;
	private boolean mySetCar;
	
	public LoadListener(boolean _setHome, boolean _setCar) {
	    mySetHome = _setHome;
	    mySetCar = _setCar;
	}
	
	public void actionPerformed(ActionEvent _event) {
	    try {
		Object image = ImageUtils.loadImage(LogoEdit.this);
		if (image != null)
		    LogoEdit.this.setLogo(image, mySetHome, mySetCar);
	    } catch (Throwable e) {
		Debug.handleError((JFrame) LogoEdit.this.getTopLevelAncestor(),
				  e, true);
	    }
	}
    }
    
    private class SaveListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    try {
		JFileChooser jfc = new JFileChooser();
		jfc.setMultiSelectionEnabled(false);
		FileChooserUtils.setToLastDirectory(jfc);
		int results = jfc.showSaveDialog(LogoEdit.this);
		if (results == 0) {
		    FileChooserUtils.saveLastDirectory(jfc);
		    File logoFile = jfc.getSelectedFile();
		    FileOutputStream fos = new FileOutputStream(logoFile);
		    Logo logo = getLogo();
		    LogoFormatUtils.fromLogo(fos, logo,
					     LogoFormatUtils.DEFAULT_CUTOFFS);
		    fos.close();
		}
	    } catch (IOException e) {
		Debug.println(e);
	    }
	}
    }
    
    private class UploadListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    try {
		com.inzyme.model.Reason shouldUseHijack
		    = HijackUtils.shouldUseHijack(myConn);
		if (shouldUseHijack == null)
		    uploadWithHijack();
		else
		    uploadWithSerial();
		JOptionPane.showMessageDialog(LogoEdit.this,
					      "Logo Upload Complete");
	    } catch (Throwable t) {
		t.printStackTrace();
		JOptionPane.showMessageDialog(LogoEdit.this,
					      "Logo Upload Failed");
	    }
	}
    }
    
    private class DownloadListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    try {
		com.inzyme.model.Reason shouldUseHijack
		    = HijackUtils.shouldUseHijack(myConn);
		if (shouldUseHijack == null)
		    downloadWithHijack();
		else
		    throw new Exception("Hijack required to download logos.");
		JOptionPane.showMessageDialog(LogoEdit.this,
					      "Logo Download Complete");
	    } catch (Throwable t) {
		t.printStackTrace();
		JOptionPane.showMessageDialog(LogoEdit.this,
					      "Logo Download Failed");
	    }
	}
    }
    
    public LogoEdit() {
	this((IConnection) null);
    }
    
    public LogoEdit(IConnection _conn) {
	myConn = _conn;
	setLayout(new BorderLayout());
	myHomeLogoEditPanel = new LogoEditPanel(128, 32);
	myCarLogoEditPanel = new LogoEditPanel(128, 32);
	LogoEditToolBar toolBar = new LogoEditToolBar();
	toolBar.addToolChangeListener(myHomeLogoEditPanel
					  .createToolChangeListener());
	toolBar.addToolChangeListener(myCarLogoEditPanel
					  .createToolChangeListener());
	toolBar.addScaleListener(myHomeLogoEditPanel.createScaleListener());
	toolBar.addScaleListener(myCarLogoEditPanel.createScaleListener());
	toolBar.addForegroundColorListener
	    (myHomeLogoEditPanel.createForegroundColorListener());
	toolBar.addBackgroundColorListener
	    (myHomeLogoEditPanel.createBackgroundColorListener());
	toolBar.addForegroundColorListener
	    (myCarLogoEditPanel.createForegroundColorListener());
	toolBar.addBackgroundColorListener
	    (myCarLogoEditPanel.createBackgroundColorListener());
	toolBar.initialize();
	myTypeCB = new JComboBox(new String[] { "Empeg", "RioCar" });
	toolBar.addSeparator();
	toolBar.add(new JLabel("Type: "));
	toolBar.add(myTypeCB);
	add(toolBar, "North");
	JSplitPane jsp = new JSplitPane();
	jsp.setOrientation(0);
	jsp.setOneTouchExpandable(true);
	jsp.setResizeWeight(0.5);
	jsp.setTopComponent(new JScrollPane(myHomeLogoEditPanel));
	jsp.setBottomComponent(new JScrollPane(myCarLogoEditPanel));
	add(jsp, "Center");
    }
    
    public Logo getLogo() {
	Image homeImage = myHomeLogoEditPanel.getImage();
	Image carImage = myCarLogoEditPanel.getImage();
	String type = myTypeCB.getSelectedIndex() == 0 ? "empg" : "rioc";
	Logo logo = new Logo(type, carImage, homeImage);
	return logo;
    }
    
    public SeekableInputStream getLogoInputStream() throws IOException {
	Logo logo = getLogo();
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	LogoFormatUtils.fromLogo(baos, logo, LogoFormatUtils.DEFAULT_CUTOFFS);
	MemorySeekableInputStream msis
	    = new MemorySeekableInputStream(baos.toByteArray());
	return msis;
    }
    
    public void uploadWithSerial() throws Throwable {
	SerialConnectionFactory connFactory = null;
	try {
	    Upgrader u = new Upgrader();
	    SerialPortSelectionDialog portDialog
		= new SerialPortSelectionDialog((JFrame)
						getTopLevelAncestor());
	    int results = portDialog.showSerialPortSelectionDialog();
	    boolean uploaded = false;
	    if (results == 1) {
		CommPortIdentifier commPort = portDialog.getSelectedPort();
		connFactory = new SerialConnectionFactory(commPort, 115200);
		int address = Integer.parseInt("a000", 16);
		u.upload(connFactory.createConnection(), getLogoInputStream(),
			 address);
	    }
	    if (!uploaded)
		throw new Exception("Serial logo upload failed.");
	} catch (Throwable t) {
	    Debug.println(t);
	    throw t;
	}
    }
    
    public void uploadWithHijack() throws Throwable {
	InetAddress inetAddress = HijackUtils.getAddress(myConn);
	HijackUtils.upload(inetAddress, "/proc/empeg_bootlogos",
			   getLogoInputStream(), 0, null);
    }
    
    public void downloadWithHijack() throws Throwable {
	InetAddress inetAddress = HijackUtils.getAddress(myConn);
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	HijackUtils.download(inetAddress, "/proc/empeg_bootlogos", baos, null);
	ByteArrayInputStream bais
	    = new ByteArrayInputStream(baos.toByteArray());
	Logo logo
	    = LogoFormatUtils.toLogo(myCarLogoEditPanel, bais,
				     LogoFormatUtils.DEFAULT_GRAY_VALUES);
	setLogo(logo, true, true);
    }
    
    private void setLogo(Object _image, boolean _setHome, boolean _setCar) {
	Image homeImage = null;
	Image carImage = null;
	if (_image instanceof Logo) {
	    Logo logo = (Logo) _image;
	    homeImage = logo.getHomeImage();
	    carImage = logo.getCarImage();
	} else {
	    homeImage = (Image) _image;
	    carImage = homeImage;
	}
	if (_setHome)
	    myHomeLogoEditPanel.setImage(homeImage, true);
	if (_setCar)
	    myCarLogoEditPanel.setImage(carImage, true);
    }
    
    private ActionListener createLoadListener(boolean _homeImage,
					      boolean _carImage) {
	return new LoadListener(_homeImage, _carImage);
    }
    
    private ActionListener createSaveListener() {
	return new SaveListener();
    }
    
    private ActionListener createUploadListener() {
	return new UploadListener();
    }
    
    private ActionListener createDownloadListener() {
	return new DownloadListener();
    }
    
    private ActionListener createUndoListener() {
	return new UndoListener();
    }
    
    public static JFrame createFrame(ActionListener _exitListener) {
	return createFrame(_exitListener, null);
    }
    
    public static JFrame createFrame(ActionListener _exitListener,
				     IConnection _conn) {
	JFrame jf = new JFrame("JLogoEdit");
	LogoEdit logoEdit = new LogoEdit(_conn);
	jf.getContentPane().add(logoEdit);
	JMenuBar menuBar = new JMenuBar();
	JMenu fileMenu = new JMenu("File");
	JMenuItem openHome = new JMenuItem("Open Home Image...");
	openHome.addActionListener(logoEdit.createLoadListener(true, false));
	JMenuItem openCar = new JMenuItem("Open Car Image...");
	openCar.addActionListener(logoEdit.createLoadListener(false, true));
	JMenuItem openBoth = new JMenuItem("Open Both...");
	openBoth.addActionListener(logoEdit.createLoadListener(true, true));
	JMenuItem save = new JMenuItem("Save As...");
	save.addActionListener(logoEdit.createSaveListener());
	JMenuItem upload = new JMenuItem("Upload To Empeg...");
	upload.addActionListener(logoEdit.createUploadListener());
	JMenuItem download = new JMenuItem("Download From Empeg...");
	download.addActionListener(logoEdit.createDownloadListener());
	final JMenuItem exit = new JMenuItem("Exit");
	exit.addActionListener(_exitListener);
	fileMenu.add(openHome);
	fileMenu.add(openCar);
	fileMenu.add(openBoth);
	fileMenu.addSeparator();
	fileMenu.add(save);
	fileMenu.addSeparator();
	fileMenu.add(upload);
	fileMenu.add(download);
	fileMenu.addSeparator();
	fileMenu.add(exit);
	JMenu editMenu = new JMenu("Edit");
	JMenuItem undo = new JMenuItem("Undo/Redo");
	undo.addActionListener(logoEdit.createUndoListener());
	editMenu.add(undo);
	menuBar.add(fileMenu);
	menuBar.add(editMenu);
	jf.setJMenuBar(menuBar);
	jf.addWindowListener(new WindowAdapter() {
	    public void windowClosing(WindowEvent e) {
		exit.doClick();
	    }
	});
	jf.pack();
	return jf;
    }
    
    public static void main(String[] _args) {
	PropertiesManager.initializeInstance("jEmplode Properties",
					     new File(ApplicationUtils
							  .getSettingsFolder(),
						      "jempegrc"));
	JEmplodeProperties.initializeDefaults();
	JFrame jf = createFrame(new ActionListener() {
	    public void actionPerformed(ActionEvent _event) {
		System.exit(0);
	    }
	});
	DialogUtils.centerWindow(jf);
	jf.setVisible(true);
    }
}
