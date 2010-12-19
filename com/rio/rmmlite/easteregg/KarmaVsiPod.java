/* KarmaVsiPod - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.rmmlite.easteregg;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;

import com.inzyme.ui.DialogUtils;

public class KarmaVsiPod extends JComponent
{
    private ImageIcon myKarmaIcon
	= new ImageIcon(this.getClass().getResource
			("/com/rio/rmmlite/easteregg/karma.gif"));
    private ImageIcon myiPodIcon
	= new ImageIcon(this.getClass().getResource
			("/com/rio/rmmlite/easteregg/ipod.gif"));
    private ImageIcon myiPodDeadIcon
	= new ImageIcon(this.getClass().getResource
			("/com/rio/rmmlite/easteregg/ipoddead.gif"));
    private int myiPodX = 0;
    private int myiPodY = 5;
    private int myiPodDirection = 0;
    private int myKarmaX = 0;
    private int myKarmaY = 0;
    private int myBulletX = -1;
    private int myBulletY = -1;
    private boolean myRunning;
    private int myiPodDead;
    private int myHitsLeft;
    private int myiPodHits;
    private int myiPodMisses;
    private int myLevel;
    
    private class iPodMover implements Runnable
    {
	public void run() {
	    while (myRunning) {
		if (myiPodDead > 0)
		    myiPodDead--;
		else {
		    int width = KarmaVsiPod.this.getWidth();
		    int iconWidth = myiPodIcon.getIconWidth();
		    myiPodX += myiPodDirection;
		    if (myiPodX <= 0) {
			myiPodX = 0;
			myiPodDirection *= -1;
		    } else if (myiPodX + iconWidth >= width) {
			myiPodX = KarmaVsiPod.this.getWidth() - iconWidth;
			myiPodDirection *= -1;
		    }
		}
		KarmaVsiPod.this.repaint();
		try {
		    Thread.sleep(50L);
		} catch (Throwable throwable) {
		    /* empty */
		}
	    }
	}
    }
    
    private class KarmaMover extends MouseMotionAdapter
    {
	public void mouseMoved(MouseEvent _event) {
	    myKarmaX = _event.getX() - myKarmaIcon.getIconWidth() / 2;
	    KarmaVsiPod.this.repaint();
	}
    }
    
    private class KarmaGun extends MouseAdapter implements Runnable
    {
	public synchronized void mousePressed(MouseEvent _event) {
	    if (myBulletX == -1 && myiPodDead == 0) {
		myBulletX = myKarmaX + myKarmaIcon.getIconWidth() / 2;
		myBulletY = myKarmaY;
		Thread t = new Thread(this);
		t.start();
	    }
	}
	
	public void run() {
	    while (myBulletY != -1) {
		if (myBulletY <= myiPodY + myiPodIcon.getIconHeight()) {
		    if (myBulletX >= myiPodX
			&& myBulletX <= myiPodX + myiPodIcon.getIconWidth())
			hit();
		    else
			miss();
		    myBulletX = -1;
		    myBulletY = -1;
		} else
		    myBulletY -= 25;
		try {
		    Thread.sleep(50L);
		} catch (Throwable throwable) {
		    /* empty */
		}
	    }
	}
    }
    
    public KarmaVsiPod() {
	checkLevel();
	addMouseMotionListener(new KarmaMover());
	addMouseListener(new KarmaGun());
    }
    
    public synchronized void start() {
	if (!myRunning) {
	    myRunning = true;
	    Thread ipodMover = new Thread(new iPodMover());
	    ipodMover.start();
	}
    }
    
    public synchronized void stop() {
	myRunning = false;
    }
    
    public void hit() {
	myiPodDead = 20;
	myiPodHits++;
	checkLevel();
    }
    
    public void miss() {
	myiPodMisses++;
	checkLevel();
    }
    
    public void checkLevel() {
	int hitsToWin = 3;
	int maxHits = 10;
	myHitsLeft = maxHits - myiPodHits - myiPodMisses;
	if (myiPodHits == hitsToWin) {
	    myLevel++;
	    myiPodHits = 0;
	    myiPodMisses = 0;
	    checkLevel();
	} else if (myHitsLeft == 0) {
	    myiPodHits = 0;
	    myiPodMisses = 0;
	    checkLevel();
	}
	myiPodDirection = (10 + myLevel * 2) * (myiPodDirection < 0 ? -1 : 1);
    }
    
    public void reshape(int x, int y, int w, int h) {
	super.reshape(x, y, w, h);
	myKarmaY = h - myKarmaIcon.getIconHeight() - 5;
    }
    
    protected void paintComponent(Graphics _g) {
	_g.setColor(Color.white);
	_g.fillRect(0, 0, getWidth(), getHeight());
	if (myiPodDead > 0)
	    _g.drawImage(myiPodDeadIcon.getImage(), myiPodX, myiPodY, this);
	else
	    _g.drawImage(myiPodIcon.getImage(), myiPodX, myiPodY, this);
	_g.drawImage(myKarmaIcon.getImage(), myKarmaX, myKarmaY, this);
	if (myBulletX != -1 && myBulletY != -1) {
	    String str = "rio";
	    int strWidth = _g.getFontMetrics().stringWidth(str);
	    _g.setFont(new Font("SansSerif", 1, 12));
	    _g.setColor(Color.red);
	    _g.drawString(str, myBulletX - strWidth / 2, myBulletY);
	}
	_g.setFont(new Font("SansSerif", 1, 20));
	int textY = myiPodIcon.getIconHeight() + myiPodY;
	int fontHeight = _g.getFontMetrics().getHeight();
	_g.setColor(new Color(0, 0, 255));
	_g.drawString("Level " + String.valueOf(myLevel + 1), 10,
		      textY + (fontHeight + 10) * 1);
	_g.setColor(new Color(0, 0, 100));
	_g.drawString(String.valueOf(myHitsLeft) + " Hits Left", 10,
		      textY + (fontHeight + 10) * 2);
	_g.drawString(String.valueOf(myiPodHits) + " Hits", 10,
		      textY + (fontHeight + 10) * 3);
	_g.drawString(String.valueOf(myiPodMisses) + " Misses", 10,
		      textY + (fontHeight + 10) * 4);
    }
    
    public static void launch(final boolean _exitOnClose) {
	final JFrame jf = new JFrame("Karma Vs iPod");
	final KarmaVsiPod egg = new KarmaVsiPod();
	jf.getContentPane().add(egg);
	jf.setSize(400, 400);
	DialogUtils.centerWindow(jf);
	jf.addWindowListener(new WindowAdapter() {
	    public void windowClosing(WindowEvent _event) {
		egg.stop();
		jf.dispose();
		if (_exitOnClose)
		    System.exit(0);
	    }
	});
	jf.show();
	egg.start();
    }
    
    public static void main(String[] args) {
	launch(true);
    }
}
