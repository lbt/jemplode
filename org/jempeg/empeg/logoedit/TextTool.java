
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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

/**
* An implementation of a simple painting canvas
* that supports drawing, paintbrush, floodfill,
* color switching, brush sizes, text, and undo/redo.
*
* @author Mike Schrag
*/
public class TextTool implements ToolIfc {
  private StringBuffer myTextBuffer;
  private Font myFont;
  private Color myColor;
  private int myTextX;
  private int myTextY;

  public TextTool() {
    myTextBuffer = new StringBuffer();
  }
  
  public String getName() {
    return "Text";
  }

  public void setFont(Font _font) {
    myFont = _font;
  }

  public Font getFont() {
    return myFont;
  }

  public void start(LogoEditPanel _editPanel, Graphics _g) {
    myTextBuffer.setLength(0);
    myTextX = -1;
    myTextY = -1;
  }
  
  public void stop(LogoEditPanel _editPanel, Graphics _g) {
    myTextBuffer.setLength(0);
  }
  
  public void commitText(LogoEditPanel _editPanel, Graphics _g) {
    if (myTextBuffer.length() > 0) {
      _editPanel.saveUndo();
      _editPanel.setChanged(true);
      _g.setColor(myColor);
      paintText(_g, 1);
      _editPanel.repaint();
    }
  }

  public void click(LogoEditPanel _editPanel, Graphics _g, int _x, int _y, int _modifiers) {
    myColor = _editPanel.getColor(_editPanel.isErase(_modifiers));
    myTextX = _x;
    myTextY = _y;
    _editPanel.repaint();
  }

  public void drag(LogoEditPanel _editPanel, Graphics _g, int _x, int _y, int _modifiers) {
  }

  public void release(LogoEditPanel _editPanel, Graphics _g, int _x, int _y, int _modifiers) {
  }

  public void type(LogoEditPanel _editPanel, Graphics _g, KeyEvent _event) {
    char ch = _event.getKeyChar();
    if (ch == KeyEvent.VK_BACK_SPACE) {
      if (myTextBuffer.length() > 0) {
        myTextBuffer.setLength(myTextBuffer.length() - 1);
      }
    } else if (ch == KeyEvent.VK_ESCAPE) {
      myTextBuffer.setLength(0);
    } else if (ch == KeyEvent.VK_ENTER) {
      commitText(_editPanel, _g);
    } else {
      myTextBuffer.append(ch);
    }
    _editPanel.repaint();
  }

  public void paintOverlay(LogoEditPanel _editPanel, Graphics _g) {
    if (myTextX != -1) {
      _g.setColor(myColor);
      FontMetrics fm = _g.getFontMetrics();
      int ascent = fm.getAscent();
      int width = fm.stringWidth(myTextBuffer.toString());
      int scale = _editPanel.getScale();
      _g.drawLine((myTextX + width) * scale, myTextY * scale, (myTextX + width) * scale, (myTextY - ascent) * scale);
      paintText(_g, scale);
    }
  }
  
  protected void paintText(Graphics _g, int _scale) {
    _g.setFont(new Font(myFont.getName(), myFont.getStyle(), myFont.getSize() * _scale));
    _g.drawString(myTextBuffer.toString(), myTextX * _scale, myTextY * _scale);
  }

  public String toString() {
    return getName();
  }
}
