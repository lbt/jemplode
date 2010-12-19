package org.jempeg.manager.ui;

import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.Autoscroll;

import javax.swing.JTree;

/**
* A JTree that supports AWT Drag-n-Drop autoscrolling.
*
* @author Mike Schrag
*/
public class ScrollableTree extends JTree implements Autoscroll {
  private static final int INSETS_DISTANCE = 30;
  private static final int SCROLL_DISTANCE = 50;

  /**
   * notify the <code>Component</code> to autoscroll
   * <P>
   * @param cursorLocn A <code>Point</code> indicating the
   * location of the cursor that triggered this operation.
   */
  public void autoscroll(Point cursorLocn) {
    Rectangle visibleRect = getVisibleRect();
    if (cursorLocn.y - visibleRect.y <= INSETS_DISTANCE) {
      visibleRect.y = Math.max(0, visibleRect.y - SCROLL_DISTANCE);
    } else if (visibleRect.y + visibleRect.height - cursorLocn.y <= INSETS_DISTANCE) {
      visibleRect.y = Math.min(getHeight(), visibleRect.y + SCROLL_DISTANCE);
    }
    if (cursorLocn.x - visibleRect.x <= INSETS_DISTANCE) {
      visibleRect.x = Math.max(0, visibleRect.x - SCROLL_DISTANCE);
    } else if (visibleRect.x + visibleRect.width - cursorLocn.x <= INSETS_DISTANCE) {
      visibleRect.x = Math.min(getHeight(), visibleRect.x + SCROLL_DISTANCE);
    }
    scrollRectToVisible(visibleRect);
  }

  /**
   * This method returns the <code>Insets</code> describing
   * the autoscrolling region or border relative
   * to the geometry of the implementing Component.
   * <P>
   * This value is read once by the <code>DropTarget</code>
   * upon entry of the drag <code>Cursor</code>
   * into the associated <code>Component</code>.
   * <P>
   * @return the Insets
   */
  public Insets getAutoscrollInsets() {
    Rectangle visibleRect = getVisibleRect();
    return new Insets(visibleRect.y + INSETS_DISTANCE * 2, visibleRect.x + INSETS_DISTANCE, getHeight() - visibleRect.y - visibleRect.height + INSETS_DISTANCE * 2, getWidth() - visibleRect.x - visibleRect.width + INSETS_DISTANCE);
  }
}
