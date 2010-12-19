/* LogoEditPanel - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.logoedit;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JComponent;

public class LogoEditPanel extends JComponent
{
    private ToolIfc myTool;
    private Image myImage;
    private Graphics myImageGraphics;
    private Image myUndoImage;
    private Graphics myUndoImageGraphics;
    private int myWidth;
    private int myHeight;
    private int myScale;
    private Color myForeground;
    private Color myBackground;
    private boolean myChanged;
    private long myModificationTime;
    
    protected class MouseTracker extends MouseAdapter
    {
	public void mousePressed(MouseEvent _event) {
	    LogoEditPanel.this.requestFocus();
	    myModificationTime = System.currentTimeMillis();
	    if (LogoEditPanel.this.isEnabled() && myTool != null)
		myTool.click(LogoEditPanel.this, myImageGraphics,
			     _event.getX() / myScale, _event.getY() / myScale,
			     _event.getModifiers());
	}
	
	public void mouseReleased(MouseEvent _event) {
	    if (LogoEditPanel.this.isEnabled() && myTool != null)
		myTool.release(LogoEditPanel.this, myImageGraphics,
			       _event.getX() / myScale,
			       _event.getY() / myScale, _event.getModifiers());
	}
    }
    
    protected class MouseMotionTracker extends MouseMotionAdapter
    {
	public void mouseDragged(MouseEvent _event) {
	    if (LogoEditPanel.this.isEnabled() && myTool != null)
		myTool.drag(LogoEditPanel.this, myImageGraphics,
			    _event.getX() / myScale, _event.getY() / myScale,
			    _event.getModifiers());
	}
    }
    
    protected class KeyListener extends KeyAdapter
    {
	public void keyTyped(KeyEvent _event) {
	    if (LogoEditPanel.this.isEnabled() && myTool != null)
		myTool.type(LogoEditPanel.this, myImageGraphics, _event);
	}
    }
    
    protected class ToolChangeListener implements ToolChangeListenerIfc
    {
	public void toolChanged(ToolIfc _tool) {
	    setTool(_tool);
	}
    }
    
    protected class ScaleListener implements ItemListener
    {
	public void itemStateChanged(ItemEvent _event) {
	    if (_event.getStateChange() == 1) {
		Integer scale = (Integer) _event.getItem();
		setScale(scale.intValue());
	    }
	}
    }
    
    protected class ForegroundColorListener implements ItemListener
    {
	public void itemStateChanged(ItemEvent _event) {
	    if (_event.getStateChange() == 1) {
		Color color = (Color) _event.getItem();
		setForegroundColor(color);
	    }
	}
    }
    
    protected class BackgroundColorListener implements ItemListener
    {
	public void itemStateChanged(ItemEvent _event) {
	    if (_event.getStateChange() == 1) {
		Color color = (Color) _event.getItem();
		setBackgroundColor(color);
	    }
	}
    }
    
    public LogoEditPanel(int _width, int _height) {
	myWidth = _width;
	myHeight = _height;
	myScale = 7;
	myForeground = Color.blue;
	myBackground = Color.black;
	addMouseListener(new MouseTracker());
	addMouseMotionListener(new MouseMotionTracker());
	addKeyListener(new KeyListener());
    }
    
    protected void initializeImage() {
	myImage = ImageUtils.createImage(this, myWidth, myHeight);
	myImageGraphics = myImage.getGraphics();
	myUndoImage = createImage(myWidth, myHeight);
	myUndoImageGraphics = myUndoImage.getGraphics();
	drawImage(myImage, myUndoImageGraphics);
	try {
	    ((Graphics2D) myImageGraphics).setRenderingHint
		(RenderingHints.KEY_ANTIALIASING,
		 RenderingHints.VALUE_ANTIALIAS_OFF);
	    ((Graphics2D) myUndoImageGraphics).setRenderingHint
		(RenderingHints.KEY_ANTIALIASING,
		 RenderingHints.VALUE_ANTIALIAS_OFF);
	} catch (Throwable throwable) {
	    /* empty */
	}
    }
    
    public synchronized void setChanged(boolean _changed) {
	myChanged = _changed;
    }
    
    public synchronized boolean isChanged() {
	return myChanged;
    }
    
    public void paintComponent(Graphics _g) {
	if (myImage == null)
	    initializeImage();
	_g.drawImage(myImage, 0, 0, myScale * myWidth, myScale * myHeight,
		     this);
	if (myTool != null)
	    myTool.paintOverlay(this, _g);
    }
    
    public void setTool(ToolIfc _tool) {
	stopTool();
	myTool = _tool;
	startTool();
    }
    
    public void stopTool() {
	if (myTool != null)
	    myTool.stop(this, myImageGraphics);
    }
    
    public void startTool() {
	if (myTool != null)
	    myTool.start(this, myImageGraphics);
    }
    
    public ToolIfc getTool() {
	return myTool;
    }
    
    public synchronized void setScale(int _scale) {
	myScale = _scale;
	revalidate();
	repaint();
    }
    
    public int getScale() {
	return myScale;
    }
    
    public void setForegroundColor(Color _foreground) {
	myForeground = _foreground;
    }
    
    public Color getForegroundColor() {
	return myForeground;
    }
    
    public void setBackgroundColor(Color _background) {
	myBackground = _background;
    }
    
    public Color getBackgroundColor() {
	return myBackground;
    }
    
    public void getImage(Image _image) {
	Graphics copyImageGraphics = _image.getGraphics();
	drawImage(myImage, copyImageGraphics);
    }
    
    public Image getImage() {
	Image copyImage = createImage(myWidth, myHeight);
	getImage(copyImage);
	return copyImage;
    }
    
    Image getInternalImage() {
	return myImage;
    }
    
    public synchronized void setImage(Image _image, boolean _saveUndo) {
	if (_saveUndo)
	    saveUndo();
	drawImage(_image, myImageGraphics);
	if (!_saveUndo)
	    saveUndo();
	repaint();
	myChanged = false;
    }
    
    public int getImageWidth() {
	return myWidth;
    }
    
    public int getImageHeight() {
	return myHeight;
    }
    
    public Dimension getPreferredSize() {
	return new Dimension(myWidth * myScale, myHeight * myScale);
    }
    
    public void finalize() {
	if (myImageGraphics != null)
	    myImageGraphics.dispose();
    }
    
    public void saveUndo() {
	drawImage(myImage, myUndoImageGraphics);
    }
    
    public ToolChangeListenerIfc createToolChangeListener() {
	return new ToolChangeListener();
    }
    
    public ItemListener createScaleListener() {
	return new ScaleListener();
    }
    
    public ItemListener createForegroundColorListener() {
	return new ForegroundColorListener();
    }
    
    public ItemListener createBackgroundColorListener() {
	return new BackgroundColorListener();
    }
    
    public synchronized void undo() {
	Image redoImage = createImage(myWidth, myHeight);
	Graphics redoImageGraphics = redoImage.getGraphics();
	drawImage(myImage, redoImageGraphics);
	drawImage(myUndoImage, myImageGraphics);
	myUndoImageGraphics.dispose();
	myUndoImage = redoImage;
	myUndoImageGraphics = redoImageGraphics;
	repaint();
    }
    
    public long getModificationTime() {
	return myModificationTime;
    }
    
    protected boolean isErase(int _modifiers) {
	if ((_modifiers & 0x4) != 0)
	    return true;
	return false;
    }
    
    protected Color getColor(boolean _erase) {
	return _erase ? myBackground : myForeground;
    }
    
    protected void drawImage(Image _fromImage, Graphics _toGraphics) {
	_toGraphics.drawImage(_fromImage, 0, 0, myWidth, myHeight, null);
    }
}
