/* AnimationApplet - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.logoedit;
import java.applet.Applet;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.inzyme.typeconv.LittleEndianInputStream;

public class AnimationApplet extends Applet implements MouseListener, Runnable
{
    private Animation myAnimation;
    private int mySequenceNum;
    private boolean myStopped;
    
    public void init() {
	try {
	    myStopped = true;
	    addMouseListener(this);
	    myAnimation = new Animation(this, 128, 32);
	    String src = getParameter("src");
	    URL codeBase = getCodeBase();
	    URL animationUrl = new URL(codeBase, src);
	    InputStream is = animationUrl.openStream();
	    LittleEndianInputStream eis = new LittleEndianInputStream(is);
	    myAnimation.load(eis, -1L);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
    
    public void start() {
	/* empty */
    }
    
    public void stop() {
	stopAnimation();
    }
    
    public synchronized void toggleAnimation() {
	if (myStopped)
	    startAnimation();
	else
	    stopAnimation();
    }
    
    public synchronized void startAnimation() {
	if (myStopped) {
	    myStopped = false;
	    Thread t = new Thread(this);
	    t.start();
	}
    }
    
    public synchronized void stopAnimation() {
	if (!myStopped)
	    myStopped = true;
    }
    
    public void paint(Graphics _g) {
	update(_g);
    }
    
    public void update(Graphics _g) {
	Animation animation;
	MONITORENTER (animation = myAnimation);
	MISSING MONITORENTER
	synchronized (animation) {
	    Image img = myAnimation.getSequenceAt(mySequenceNum);
	    Dimension size = getSize();
	    _g.drawImage(img, 0, 0, size.width, size.height, this);
	}
    }
    
    public void run() {
	while (!myStopped) {
	    Animation animation;
	    MONITORENTER (animation = myAnimation);
	    MISSING MONITORENTER
	    synchronized (animation) {
		mySequenceNum++;
		if (mySequenceNum >= myAnimation.getSequenceCount())
		    mySequenceNum = 0;
		try {
		    Thread.sleep(83L);
		} catch (Throwable throwable) {
		    /* empty */
		}
	    }
	    repaint();
	}
    }
    
    public void mouseClicked(MouseEvent _event) {
	/* empty */
    }
    
    public void mousePressed(MouseEvent _event) {
	toggleAnimation();
    }
    
    public void mouseReleased(MouseEvent _event) {
	/* empty */
    }
    
    public void mouseEntered(MouseEvent _event) {
	/* empty */
    }
    
    public void mouseExited(MouseEvent _event) {
	/* empty */
    }
}
