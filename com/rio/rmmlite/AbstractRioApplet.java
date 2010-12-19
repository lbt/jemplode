/* AbstractRioApplet - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.rmmlite;
import java.applet.Applet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.UIManager;

import com.inzyme.util.Debug;

public abstract class AbstractRioApplet extends Applet
{
    private boolean myLoading;
    private int myLastX = 0;
    private int myLastY = 50;
    private int myXDirection = 5;
    private int myYDirection = 5;
    
    public void init() {
	setLoading(true);
	Thread loadingThread = new Thread(new Runnable() {
	    public void run() {
		while (myLoading) {
		    try {
			Thread.sleep(100L);
		    } catch (Throwable throwable) {
			/* empty */
		    }
		    AbstractRioApplet.this.repaint();
		}
	    }
	}, "Loading..");
	loadingThread.start();
	AppletUtils.requestFullPermissions();
	try {
	    UIManager
		.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	} catch (Throwable t) {
	    t.printStackTrace();
	}
	String hostAddress = getParameter("host");
	if (hostAddress == null)
	    hostAddress = getCodeBase().getHost();
	final String finalHostAddress = hostAddress;
	Thread t = new Thread(new Runnable() {
	    public void run() {
		try {
		    try {
			AbstractMain main = createMain();
			main.init(finalHostAddress, true);
		    } catch (Throwable e) {
			Debug.println(e);
		    }
		} catch (Object object) {
		    setLoading(false);
		    throw object;
		}
		setLoading(false);
	    }
	}, "Rio Initialize");
	t.start();
    }
    
    public void stop() {
	super.stop();
	myLoading = false;
    }
    
    protected abstract AbstractMain createMain() throws Throwable;
    
    protected void setLoading(boolean _loading) {
	myLoading = _loading;
	repaint();
    }
    
    public void paint(Graphics _g) {
	Dimension size = getSize();
	_g.setColor(Color.white);
	_g.fillRect(0, 0, size.width, size.height);
	if (myLoading) {
	    String str = "Rio";
	    int strWidth = _g.getFontMetrics().stringWidth(str);
	    int strHeight = _g.getFontMetrics().getHeight();
	    if (myLastY + myYDirection > size.height
		|| myLastY + myYDirection <= strHeight)
		myYDirection *= -1;
	    if (myLastX + myXDirection <= 0
		|| size.width - (myLastX + myXDirection) <= strWidth)
		myXDirection *= -1;
	    myLastX += myXDirection;
	    myLastY += myYDirection;
	    _g.setColor(Color.black);
	    _g.drawString(str, myLastX, myLastY);
	}
    }
    
    public void update(Graphics _g) {
	paint(_g);
    }
}
