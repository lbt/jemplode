/* EmpegScreen - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.logoedit;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.AbstractAction;
import javax.swing.JComponent;

import com.inzyme.util.Debug;

import org.jempeg.empeg.protocol.EmpegProtocolClient;
import org.jempeg.protocol.IProtocolClient;
import org.jempeg.protocol.ProtocolException;

public class EmpegScreen extends JComponent
{
    public static Color[] DEFAULT_COLORS = new Color[16];
    private IProtocolClient myClient;
    private Object myLock;
    private byte[] myScreen;
    private int myPixelSize;
    private Color[] myColors;
    private boolean myGrabbing;
    private boolean myStopGrabbing;
    
    protected class ScreenUpdater implements Runnable
    {
	private int myMillisBetweenGrabs;
	
	public ScreenUpdater(int _millisBetweenGrabs) {
	    myMillisBetweenGrabs = _millisBetweenGrabs;
	}
	
	public void run() {
	    while (!myStopGrabbing) {
		try {
		    Thread.sleep((long) myMillisBetweenGrabs);
		} catch (Exception exception) {
		    /* empty */
		}
		try {
		    grabScreen();
		} catch (ProtocolException e) {
		    Debug.println(e);
		}
	    }
	    myGrabbing = false;
	}
    }
    
    static {
	int step = 50;
	for (int i = 0; i < DEFAULT_COLORS.length; i++)
	    DEFAULT_COLORS[i]
		= new Color(Math.min(i * step, 255), Math.min(i * step, 255),
			    Math.min(i * step, 255));
    }
    
    public EmpegScreen(IProtocolClient _client) throws ProtocolException {
	this(_client, DEFAULT_COLORS, 2, -1);
    }
    
    public EmpegScreen(IProtocolClient _client, int _pixelSize)
	throws ProtocolException {
	this(_client, DEFAULT_COLORS, _pixelSize, -1);
    }
    
    public EmpegScreen(IProtocolClient _client, int _pixelSize,
		       int _millisBetweenGrabs) throws ProtocolException {
	this(_client, DEFAULT_COLORS, _pixelSize, _millisBetweenGrabs);
    }
    
    public EmpegScreen(IProtocolClient _client, Color[] _colors,
		       int _pixelSize) throws ProtocolException {
	this(_client, _colors, _pixelSize, -1);
    }
    
    public EmpegScreen(IProtocolClient _client, Color[] _colors,
		       int _pixelSize,
		       int _millisBetweenGrabs) throws ProtocolException {
	this(_colors, _pixelSize);
	myLock = new Object();
	myClient = _client;
	if (_millisBetweenGrabs == -1)
	    grabScreen();
	else
	    startGrabbing(_millisBetweenGrabs);
    }
    
    public EmpegScreen(byte[] _screen, Color[] _colors, int _pixelSize) {
	this(_colors, _pixelSize);
	myScreen = _screen;
    }
    
    protected EmpegScreen(Color[] _colors, int _pixelSize) {
	if (_colors.length != 16)
	    throw new RuntimeException
		      ("Color array must be exactly 16 colors long.");
	myLock = new Object();
	myColors = _colors;
	myPixelSize = _pixelSize;
	setBackground(Color.black);
    }
    
    public void stopGrabbing() {
	myStopGrabbing = true;
    }
    
    public void startGrabbing(int _millisBetweenGrabs) {
	myGrabbing = true;
	myStopGrabbing = false;
	ScreenUpdater su = new ScreenUpdater(_millisBetweenGrabs);
	Thread t = new Thread(su);
	t.start();
    }
    
    public void grabScreen() throws ProtocolException {
	Object object;
	MONITORENTER (object = myLock);
	MISSING MONITORENTER
	synchronized (object) {
	    myScreen = ((EmpegProtocolClient) myClient).grabScreen();
	    repaint();
	}
    }
    
    public void paintComponent(Graphics _g) {
	Object object;
	MONITORENTER (object = myLock);
	MISSING MONITORENTER
	synchronized (object) {
	    _g.setColor(getBackground());
	    Rectangle r = getBounds();
	    _g.fillRect(0, 0, r.width, r.height);
	    if (myScreen != null) {
		for (int y = 0; y < 32; y++) {
		    for (int x = 0; x < 128; x++) {
			int value = LogoFormatUtils.from4bpp(myScreen, x, y);
			r.setBounds(x * myPixelSize + x + 1,
				    y * myPixelSize + y + 1, myPixelSize,
				    myPixelSize);
			_g.setColor(myColors[value]);
			_g.fillRect(r.x, r.y, r.width, r.height);
		    }
		}
	    }
	}
    }
    
    public Dimension getPreferredSize() {
	return new Dimension(myPixelSize * 128 + 129, myPixelSize * 32 + 33);
    }
    
    public AbstractAction createGrabAction() {
	return new AbstractAction() {
	    public void actionPerformed(ActionEvent _event) {
		try {
		    grabScreen();
		} catch (ProtocolException e) {
		    Debug.println(e);
		}
	    }
	};
    }
    
    public AbstractAction createRepeatingGrabAction() {
	return new AbstractAction() {
	    public void actionPerformed(ActionEvent _event) {
		if (!myGrabbing)
		    startGrabbing(500);
		else
		    stopGrabbing();
	    }
	};
    }
    
    public WindowListener createStopGrabbingWindowListener() {
	return new WindowAdapter() {
	    public void windowClosed(WindowEvent _event) {
		stopGrabbing();
	    }
	};
    }
}
