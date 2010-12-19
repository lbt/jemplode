/**
* Copyright (c) 2001, Mike Schrag & Daniel Zimmerman
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* Redistributions of source code must retain the above copyright notice,
* this list of conditions and the following disclaimer.
*
* Redistributions in binary form must reproduce the above copyright notice,
* this list of conditions and the following disclaimer in the documentation
* and/or other materials provided with the distribution.
*
* Neither the name of Mike Schrag, Daniel Zimmerman, nor the names of any
* other contributors may be used to endorse or promote products derived from
* this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
* "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
* TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
* PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
* LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
* SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
* CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
* ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGE.
*/
package org.jempeg.empeg.manager.event;

import java.awt.Point;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;

import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.jempeg.ApplicationContext;
import org.jempeg.manager.event.AbstractFileDropTargetListener;

import com.inzyme.container.IContainer;
import com.inzyme.util.Timer;

/**
* TreeDropTargetListener defines the specifics of
* dragging-and-dropping from the native filesystem
* onto the playlist tree.
*
* @author Mike Schrag
* @version $Revision: 1.3 $
*/
public class TreeDropTargetListener extends AbstractFileDropTargetListener {
	private JTree myTree;

  private Timer myExpandTimer;
  private int myRow;

	public TreeDropTargetListener(ApplicationContext _context, JTree _tree) {
		super(_context);
		myTree = _tree;
    myExpandTimer = new Timer(1000, this, "open");
    myRow = -1;
	}

  public void dragEnter(DropTargetDragEvent _event) {
    super.dragEnter(_event);
    myTree.requestFocus();
    myRow = -1;
  }

  public void dragOver(DropTargetDragEvent _event) {
    super.dragOver(_event);
    // Call getTreeNode so the node-opening-timer is started
    getTreeNode(_event);
  }

  public void dragExit(DropTargetDragEvent _event) {
    super.dragExit(_event);
    myExpandTimer.stop();
    myRow = -1;
  }

  protected boolean isValid(DropTargetEvent _event) {
    TreeNode treeNode = getTreeNode(_event);
    boolean valid = (treeNode != null);
    return valid;
  }

  protected TreeNode getTreeNode(DropTargetEvent _event) {
    TreeNode treeNode = null;
    Point loc = null;
    if (_event instanceof DropTargetDragEvent) {
      loc = ((DropTargetDragEvent)_event).getLocation();
    } else if (_event instanceof DropTargetDropEvent) {
      loc = ((DropTargetDropEvent)_event).getLocation();
    }
    
    int row = -1;
    if (loc != null) {
      row = myTree.getRowForLocation(loc.x, loc.y);
      if (row != myRow) {
        myExpandTimer.mark();
      }
      if (row != -1) {
//        int triggerDist = 50;
//        int scrollDist = 30;
//        int totalHeight = myTree.getHeight();
//        Rectangle visibleRect = myTree.getVisibleRect();
//        System.out.println("Loc y = " + loc.y + "; height = " + visibleRect + "; totalHeight = " + totalHeight);
//        if (loc.y < triggerDist) {
//          myTree.scrollRectToVisible(new Rectangle(0, Math.max(0, visibleRect.y + loc.y - scrollDist), 1, 1));
//        } else if (loc.y > visibleRect.height - triggerDist) {
//          myTree.scrollRectToVisible(new Rectangle(0, Math.min(totalHeight, visibleRect.y + loc.y + scrollDist), 1, 1));
//        }
//        myTree.repaint();
      }

      TreePath dropTargetPath = myTree.getPathForLocation((int)loc.getX(), (int)loc.getY());
      if (dropTargetPath != null) {
        treeNode = (TreeNode)dropTargetPath.getLastPathComponent();
      }
    }
    myRow = row;

    return treeNode;
  }

	protected IContainer getTargetContainer(DropTargetDropEvent _event) {
		TreeNode treeNode = getTreeNode(_event);
    IContainer container = (IContainer)treeNode;
    return container;
	}

  public void open() {
    if (myRow != -1) {
      myTree.expandRow(myRow);
    }
  }
}
