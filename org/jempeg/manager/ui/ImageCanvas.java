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
package org.jempeg.manager.ui;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;

/**
* ImageCanvas is an AWT Canvas that
* is able to draw an image on itself
* (i.e. for use in a Splash Screen).
*
* @author Mike Schrag
* @version $Revision: 1.1 $
*/
public class ImageCanvas extends Canvas {
  private Image myImage;
  private Dimension mySize;

  public ImageCanvas() {
    this(null);
  }

  public ImageCanvas(Image _image) {
    setImage(_image);
  }

  public void setImage(Image _image) {
    myImage = _image;
    if (myImage == null) {
      mySize = new Dimension(0, 0);
    } else {
      MediaTracker mt = new MediaTracker(this);
      mt.addImage(_image, 0);
      try {
        mt.waitForID(0);
      } catch (Exception e) {
        e.printStackTrace();
      }
      mySize = new Dimension(myImage.getWidth(this), myImage.getHeight(this));
    }
  }

  public Image getImage() {
    return myImage;
  }

  public Dimension getPreferredSize() {
    return mySize;
  }

  public Dimension getMinimumSize() {
    return mySize;
  }

  public Dimension getMaximumSize() {
    return mySize;
  }

  public void paint(Graphics _g) {
    if (myImage != null) {
      Rectangle bounds = getBounds();
      int x = (bounds.width - mySize.width) / 2;
      int y = (bounds.height - mySize.height) / 2;
      _g.drawImage(myImage, x, y, this);
    }
  }
}
