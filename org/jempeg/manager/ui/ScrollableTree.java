/* ScrollableTree - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.ui;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.Autoscroll;

import javax.swing.JTree;

public class ScrollableTree extends JTree implements Autoscroll
{
    private static final int INSETS_DISTANCE = 30;
    private static final int SCROLL_DISTANCE = 50;
    
    public void autoscroll(Point cursorLocn) {
	Rectangle visibleRect = getVisibleRect();
	if (cursorLocn.y - visibleRect.y <= 30)
	    visibleRect.y = Math.max(0, visibleRect.y - 50);
	else if (visibleRect.y + visibleRect.height - cursorLocn.y <= 30)
	    visibleRect.y = Math.min(getHeight(), visibleRect.y + 50);
	if (cursorLocn.x - visibleRect.x <= 30)
	    visibleRect.x = Math.max(0, visibleRect.x - 50);
	else if (visibleRect.x + visibleRect.width - cursorLocn.x <= 30)
	    visibleRect.x = Math.min(getHeight(), visibleRect.x + 50);
	scrollRectToVisible(visibleRect);
    }
    
    public Insets getAutoscrollInsets() {
	Rectangle visibleRect = getVisibleRect();
	return new Insets(visibleRect.y + 60, visibleRect.x + 30,
			  (getHeight() - visibleRect.y - visibleRect.height
			   + 60),
			  getWidth() - visibleRect.x - visibleRect.width + 30);
    }
}
