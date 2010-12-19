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
import java.awt.event.KeyEvent;
import java.awt.image.PixelGrabber;

/**
* A flood fill tool.
*
* @author Mike Schrag
*/
public class FillTool implements ToolIfc {
  public FillTool() {
  }

  public String getName() {
    return "Fill";
  }

  public void start(LogoEditPanel _editPanel, Graphics _g) {
  }

  public void stop(LogoEditPanel _editPanel, Graphics _g) {
  }
  
  public void click(LogoEditPanel _editPanel, Graphics _g, int _x, int _y, int _modifiers) {
    _editPanel.saveUndo();
    if (_g != null) {
      _editPanel.setChanged(true);

      int width = _editPanel.getImageWidth();
      int height = _editPanel.getImageHeight();
      Image image = _editPanel.getInternalImage();

      int[] pixels = new int[width * height];
      PixelGrabber pg = new PixelGrabber(image, 0, 0, width, height, pixels, 0, width);
      try {
        pg.grabPixels();
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }

      Color color = _editPanel.getColor(_editPanel.isErase(_modifiers));
      _g.setColor(color);
      floodFill(_editPanel, _g, _x, _y, color.getRGB(), pixels, width, height);
      _editPanel.repaint();
    }
  }

  public void drag(LogoEditPanel _editPanel, Graphics _g, int _x, int _y, int _modifiers) {
  }

  public void release(LogoEditPanel _editPanel, Graphics _g, int _x, int _y, int _modifiers) {
  }

  public void type(LogoEditPanel _editPanel, Graphics _g, KeyEvent _event) {
  }

  public void paintOverlay(LogoEditPanel _editPanel, Graphics _g) {
  }

  protected void floodFill(LogoEditPanel _editPanel, Graphics _g, int _x, int _y, int _newColor, int[] _pixels, int _width, int _height) {
    PixelQueue queue = new PixelQueue();
    queue.enqueue(_x, _y);
    int oldColor = _pixels[_y * _width + _x];
    while (!queue.isEmpty()) {
      PixelQueue.Pixel item = queue.dequeue();
      int color = _pixels[item.y * _width + item.x];
      if (color == oldColor && color != _newColor) {
        _g.drawLine(item.x, item.y, item.x, item.y);
        _pixels[item.y * _width + item.x] = _newColor;
        if (item.x < _width - 1) {
          queue.enqueue(item.x + 1, item.y);
        }
        if (item.x > 0) {
          queue.enqueue(item.x - 1, item.y);
        }
        if (item.y < _height - 1 ) {
          queue.enqueue(item.x, item.y + 1);
        }
        if (item.y > 0) {
          queue.enqueue(item.x, item.y - 1);
        }
      }
    }
  }

  public String toString() {
    return getName();
  }
}
