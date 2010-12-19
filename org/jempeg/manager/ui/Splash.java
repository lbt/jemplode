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

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.File;
import java.net.URL;

/**
* Splash is an AWT implementation of a splash screen.
*
* @author Mike Schrag
* @version $Revision: 1.1 $
*/
public class Splash implements Runnable {
	private Image myImage;
	private Window myWindow;
	
		/**
		* Creates a splash screen with the given _image
		*/
	public Splash(Image _image) {
		myImage = _image;
	}
	
		/**
		* Creates a splash screen with the image with
		* the given file name
		*/
	public Splash(File _imageFile) {
		myImage = Toolkit.getDefaultToolkit().getImage(_imageFile.getAbsolutePath());
	}
	
  /**
   * Creates a splash screen with the image from the specified URL.
   *
   * @param _url The URL.
   **/
  
  public Splash(URL _url) {
    myImage = Toolkit.getDefaultToolkit().getImage(_url);
  }
  
  
		/**
		* Configure this splash screen to use a window (versus a titled frame)
		*/
	public void createWindow() {
		dispose();
		
		myWindow = new Window(new Frame(""));
		ImageCanvas image = new ImageCanvas(myImage);
		myWindow.add(image);
		myWindow.pack();
	}
	
		/**
		* Shows this splash screen.  If you have not explicitly called createWindow()
		* or createFrame(), then the default will be a window
		*/
	public void show() {
		if (myWindow == null) {
			createWindow();
		}
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension windowSize = myWindow.getSize();
		Point location = new Point((screenSize.width - windowSize.width) / 2, (screenSize.height - windowSize.height) / 2);
		myWindow.setLocation(location);
		myWindow.setVisible(true);
	}
	
		/**
		* Hides the current splash window or frame
		*/
	public void hide() {
		myWindow.setVisible(false);
	}
	
		/**
		* Disposes the current splash window or frame
		*/
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
			Thread.sleep(8000);
		}
			catch (Throwable t) {
			}
		dispose();
	}
}
