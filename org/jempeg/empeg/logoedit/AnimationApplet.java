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

import java.applet.Applet;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.inzyme.typeconv.LittleEndianInputStream;

/**
* AnimationApplet is an applet player for Empeg animations.
*
* @author Mike Schrag
*/
public class AnimationApplet extends Applet implements MouseListener, Runnable {
  private Animation myAnimation;
  private int mySequenceNum;
  private boolean myStopped;

  public void init() {
    try {
      myStopped = true;
      addMouseListener(this);
      myAnimation = new Animation(this, LogoFormatUtils.DEFAULT_WIDTH, LogoFormatUtils.DEFAULT_HEIGHT);
      String src = getParameter("src");
      URL codeBase = getCodeBase();
      URL animationUrl = new URL(codeBase, src);
      InputStream is = animationUrl.openStream();
      LittleEndianInputStream eis = new LittleEndianInputStream(is);
      myAnimation.load(eis, -1);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void start() {
  }

  public void stop() {
    stopAnimation();
  }

  public synchronized void toggleAnimation() {
    if (myStopped) {
      startAnimation();
    } else {
      stopAnimation();
    }
  }

  public synchronized void startAnimation() {
    if (myStopped) {
      myStopped = false;
      Thread t = new Thread(this);
      t.start();
    }
  }

  public synchronized void stopAnimation() {
    if (!myStopped) {
      myStopped = true;
    }
  }

  public void paint(Graphics _g) {
    update(_g);
  }

  public void update(Graphics _g) {
    synchronized (myAnimation) {
      Image img = myAnimation.getSequenceAt(mySequenceNum);
      Dimension size = getSize();
      _g.drawImage(img, 0, 0, size.width, size.height, this);
    }
  }

  public void run() {
    while (!myStopped) {
      synchronized (myAnimation) {
        mySequenceNum ++;
        if (mySequenceNum >= myAnimation.getSequenceCount()) {
          mySequenceNum = 0;
        }

        try { Thread.sleep(83); } catch (Throwable t) { }
      }
      repaint();
    }
  }

  public void mouseClicked(MouseEvent _event) {
  }

  public void mousePressed(MouseEvent _event) {
    toggleAnimation();
  }

  public void mouseReleased(MouseEvent _event) {
  }

  public void mouseEntered(MouseEvent _event) {
  }

  public void mouseExited(MouseEvent _event) {
  }
}

