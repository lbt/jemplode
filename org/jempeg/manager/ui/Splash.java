/* Splash - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.ui;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.File;
import java.net.URL;

public class Splash implements Runnable
{
    private Image myImage;
    private Window myWindow;
    
    public Splash(Image _image) {
	myImage = _image;
    }
    
    public Splash(File _imageFile) {
	myImage = Toolkit.getDefaultToolkit()
		      .getImage(_imageFile.getAbsolutePath());
    }
    
    public Splash(URL _url) {
	myImage = Toolkit.getDefaultToolkit().getImage(_url);
    }
    
    public void createWindow() {
	dispose();
	myWindow = new Window(new Frame(""));
	ImageCanvas image = new ImageCanvas(myImage);
	myWindow.add(image);
	myWindow.pack();
    }
    
    public void show() {
	if (myWindow == null)
	    createWindow();
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	Dimension windowSize = myWindow.getSize();
	Point location
	    = new Point((screenSize.width - windowSize.width) / 2,
			(screenSize.height - windowSize.height) / 2);
	myWindow.setLocation(location);
	myWindow.setVisible(true);
    }
    
    public void hide() {
	myWindow.setVisible(false);
    }
    
    public void dispose() {
	if (myWindow != null) {
	    myWindow.hide();
	    myWindow.dispose();
	}
    }
    
    public void start() {
	Thread t = new Thread(this);
	t.start();
    }
    
    public void run() {
	show();
	try {
	    Thread.sleep(8000L);
	} catch (Throwable throwable) {
	    /* empty */
	}
	dispose();
    }
}
