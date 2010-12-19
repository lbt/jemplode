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
package org.jempeg.empeg.logoedit;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
* The interface for editor tools.
*
* @author Mike Schrag
*/
public class SelectTool implements ToolIfc {
  private Image myClipboardImage;
  private Image myDragImage;
  private Rectangle myHoleRect;
  private Rectangle myDragRect;
  private int myClickX;
  private int myClickY;
  private boolean mySelecting;
  
  public SelectTool() {
  }
  
  public String getName() {
    return "Select";
  }

  public void start(LogoEditPanel _editPanel, Graphics _g) {
    clear();
  }
  
  public void stop(LogoEditPanel _editPanel, Graphics _g) {
    clear();
  }
  
  public void click(LogoEditPanel _editPanel, Graphics _g, int _x, int _y, int _modifiers) {
    if (myDragRect == null || !myDragRect.contains(_x, _y)) {
      mySelecting = true;
      myClickX = _x;
      myClickY = _y;
      myDragImage = null;
      myDragRect = new Rectangle(_x, _y, 0, 0);
    } else {
      myClickX = _x - myDragRect.x;
      myClickY = _y - myDragRect.y;
//      dragSelectionTo(_editPanel, _x, _y);
    }
  }

  public void drag(LogoEditPanel _editPanel, Graphics _g, int _x, int _y, int _modifiers) {
    if (myDragRect != null && myDragImage == null && !mySelecting) {
      if ((_modifiers & InputEvent.SHIFT_MASK) != 0) {
        cutHole();
      }
      grabSelection(_editPanel);
    }
    dragSelectionTo(_editPanel, _x, _y);
  }

  public void release(LogoEditPanel _editPanel, Graphics _g, int _x, int _y, int _modifiers) {
    mySelecting = false;
    _editPanel.repaint();
  }

  public void type(LogoEditPanel _editPanel, Graphics _g, KeyEvent _event) {
    char ch = _event.getKeyChar();
    if (ch == KeyEvent.VK_ENTER) {
      commitDrag(_editPanel, _g);
    } else if (ch == KeyEvent.VK_ESCAPE) {
      rollbackDrag(_editPanel, _g);
    } else if (_event.isControlDown() && _event.getKeyChar() == ('c' - 'a' + 1)) {
      copySelection(_editPanel, _g);
    } else if (_event.isControlDown() && _event.getKeyChar() == ('x' - 'a' + 1)) {
      cutSelection(_editPanel, _g);
    } else if (_event.isControlDown() && _event.getKeyChar() == ('v' - 'a' + 1)) {
      pasteSelection(_editPanel, _g);
    }
  }

  public void paintOverlay(LogoEditPanel _editPanel, Graphics _g) {
    int scale = _editPanel.getScale();
    if (myHoleRect != null) {
      _g.setColor(_editPanel.getBackgroundColor());
      _g.fillRect(myHoleRect.x * scale, myHoleRect.y * scale, myHoleRect.width * scale, myHoleRect.height * scale);
    }

    if (myDragImage != null) {
      _g.drawImage(myDragImage, myDragRect.x * scale, myDragRect.y * scale, myDragRect.width * scale, myDragRect.height * scale, _editPanel);
    }

    if (myDragRect != null) {
      if (isValidSelection()) {
        _g.setColor(Color.red);
        _g.drawRect(myDragRect.x * scale, myDragRect.y * scale, myDragRect.width * scale, myDragRect.height * scale);
      }
    }
  }

  protected boolean isValidSelection() {
    boolean validSelection = (myDragRect.width >= 1 && myDragRect.height >= 1);
    return validSelection;
  }

  protected void grabSelection(LogoEditPanel _editPanel) {
    if (isValidSelection()) {
      myDragImage = _editPanel.createImage(myDragRect.width, myDragRect.height);
      Graphics dragImageGraphics = myDragImage.getGraphics();
      dragImageGraphics.drawImage(_editPanel.getInternalImage(), -myDragRect.x, -myDragRect.y, _editPanel);
    } else {
      clear();
    }
  }

  protected void cutHole() {
    myHoleRect = (Rectangle)myDragRect.clone();
  }

  protected void fillHole(Graphics _g) {
    _g.fillRect(myHoleRect.x, myHoleRect.y, myHoleRect.width, myHoleRect.height);
  }

  protected synchronized void pasteSelection(LogoEditPanel _editPanel, Graphics _g) {
    if (myClipboardImage != null && myDragImage == null) {
      myDragImage = myClipboardImage;
      myDragRect = new Rectangle(0, 0, myDragImage.getWidth(_editPanel), myDragImage.getHeight(_editPanel));
      myClipboardImage = null;
      myHoleRect = null;
      mySelecting = false;
      _editPanel.repaint();
    }
  }

  protected synchronized void copySelection(LogoEditPanel _editPanel, Graphics _g) {
    if (isValidSelection() && myDragImage == null) {
      grabSelection(_editPanel);
      myClipboardImage = myDragImage;
      clear();
      _editPanel.repaint();
    }
  }

  protected synchronized void cutSelection(LogoEditPanel _editPanel, Graphics _g) {
    if (isValidSelection() && myDragImage == null) {
      cutHole();
      grabSelection(_editPanel);
      myClipboardImage = myDragImage;
      _editPanel.saveUndo();
      _editPanel.setChanged(true);
      fillHole(_g);
      clear();
      _editPanel.repaint();
    }
  }

  protected void dragSelectionTo(LogoEditPanel _editPanel, int _x, int _y) {
    if (myDragImage == null) {
      if (_x < myClickX) {
        myDragRect.x = _x;
        myDragRect.width = (myClickX - _x);
      } else {
        myDragRect.x = myClickX;
        myDragRect.width  = (_x - myDragRect.x);
      }
      if (_y < myClickY) {
        myDragRect.y = _y;
        myDragRect.height = (myClickY - _y);
      } else {
        myDragRect.y = myClickY;
        myDragRect.height  = (_y - myDragRect.y);
      }
    } else {
      myDragRect.x = _x - myClickX;
      myDragRect.y = _y - myClickY;
    }
    _editPanel.repaint();
  }

  protected void commitDrag(LogoEditPanel _editPanel, Graphics _g) {
    if (myDragImage != null) {
      _editPanel.saveUndo();
      _editPanel.setChanged(true);
      
      if (myHoleRect != null) {
        fillHole(_g);
      }

      _g.drawImage(myDragImage, myDragRect.x, myDragRect.y, _editPanel);
    }
    clear();
    _editPanel.repaint();
  }

  protected void rollbackDrag(LogoEditPanel _editPanel, Graphics _g) {
    clear();
    _editPanel.repaint();
  }

  protected void clear() {
    myDragRect = null;
    myDragImage = null;
    myHoleRect = null;
  }
    
  public String toString() {
    return getName();
  }
}
