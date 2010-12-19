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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.AbstractAction;
import javax.swing.JComponent;

import org.jempeg.empeg.protocol.EmpegProtocolClient;
import org.jempeg.protocol.IProtocolClient;
import org.jempeg.protocol.ProtocolException;

import com.inzyme.util.Debug;

/**
* An EmpegScreen is a renderer for the GrabScreenPacket.  This basically grabs
* the bytes off of the Empeg's screen and displays them in a JComponent of
* configurable size.
*
* @author Mike Schrag
* @version $Revision: 1.1 $
*/
public class EmpegScreen extends JComponent {
	public static Color[] DEFAULT_COLORS = new Color[16];

	static {
		// I stepped the color faster up the grayscale, because it was _REALLY_ dark on my screen with the correct stepping
		int step = 50;
		for (int i = 0; i < DEFAULT_COLORS.length; i++) {
			DEFAULT_COLORS[i] = new Color(Math.min(i * step, 255), Math.min(i * step, 255), Math.min(i * step, 255));
		}
	}

	private IProtocolClient myClient;
	private Object myLock;
	private byte[] myScreen;
	private int myPixelSize;
	private Color[] myColors;
	private boolean myGrabbing;
	private boolean myStopGrabbing;

	/**
	* Creates an EmpegScreen without grabbing, a pixel size of 2, and the default colorset (grayscale)
	*/
	public EmpegScreen(IProtocolClient _client) throws ProtocolException {
		this(_client, DEFAULT_COLORS, 2, -1);
	}

	/**
	* Creates an EmpegScreen without grabbing and with the default colorset (grayscale)
	*/
	public EmpegScreen(IProtocolClient _client, int _pixelSize) throws ProtocolException {
		this(_client, DEFAULT_COLORS, _pixelSize, -1);
	}

	/**
	* Creates an EmpegScreen with grabbing, but with the default colorset (grayscale)
	*/
	public EmpegScreen(IProtocolClient _client, int _pixelSize, int _millisBetweenGrabs) throws ProtocolException {
		this(_client, DEFAULT_COLORS, _pixelSize, _millisBetweenGrabs);
	}

	/**
	* Creates an EmpegScreen without timed grabbing
	*/
	public EmpegScreen(IProtocolClient _client, Color[] _colors, int _pixelSize) throws ProtocolException {
		this(_client, _colors, _pixelSize, -1);
	}

	/**
	* Creates an EmpegScreen.  If _millisBetweenGrabs is greater than -1, then startGrabbing is automatically called.
	*
	* @param _client							The ProtocolClient that this can use to grab the Empeg screen
	* @param _colors							A color array going from dark to light of length 16
	* @param _pixelSize						The number of pixels that each Empeg pxiel should be represented as onscreen
	* @param _millisBetweenGrabs	The number of milliseconds between each screen grab (-1 will turn off timed grabbing)
	*/
	public EmpegScreen(IProtocolClient _client, Color[] _colors, int _pixelSize, int _millisBetweenGrabs) throws ProtocolException {
		this(_colors, _pixelSize);
		myLock = new Object();
		myClient = _client;
		if (_millisBetweenGrabs == -1) {
			grabScreen();
		}
		else {
			startGrabbing(_millisBetweenGrabs);
		}
	}

	/**
	* Creates an EmpegScreen with a prebuilt byte array.
	*
	* @array _screen		The bytes of the screen (2048 bytes long)
	* @array _colors		A color array going from dark to light of length 16
	* @array _pixelSize	The number of pixels that each Empeg pxiel should be represented as onscreen
	*/
	public EmpegScreen(byte[] _screen, Color[] _colors, int _pixelSize) {
		this(_colors, _pixelSize);
		myScreen = _screen;
	}

	protected EmpegScreen(Color[] _colors, int _pixelSize) {
		if (_colors.length != 16) {
			throw new RuntimeException("Color array must be exactly 16 colors long.");
		}
		myLock = new Object();
		myColors = _colors;
		myPixelSize = _pixelSize;
		setBackground(Color.black);
	}

	/**
	* Stops grabbing screens from the ProtocolClient
	*/
	public void stopGrabbing() {
		myStopGrabbing = true;
	}

	/**
	* Starts grabbing screens from the ProtocolClient.
	*/
	public void startGrabbing(int _millisBetweenGrabs) {
		myGrabbing = true;
		myStopGrabbing = false;
		ScreenUpdater su = new ScreenUpdater(_millisBetweenGrabs);
		Thread t = new Thread(su);
		t.start();
	}

	/**
	* Grabs a screen from the ProtocolClient.
	*/
	public void grabScreen() throws ProtocolException {
		synchronized (myLock) {
			myScreen = ((EmpegProtocolClient) myClient).grabScreen();
			repaint();
		}
	}

	public void paintComponent(Graphics _g) {
		synchronized (myLock) {
			_g.setColor(getBackground());
			Rectangle r = getBounds();
			_g.fillRect(0, 0, r.width, r.height);
			if (myScreen != null) {
				for (int y = 0; y < 32; y++) {
					for (int x = 0; x < 128; x++) {
						int value = LogoFormatUtils.from4bpp(myScreen, x, y);
						r.setBounds((x * myPixelSize) + x + 1, (y * myPixelSize) + y + 1, myPixelSize, myPixelSize);

						_g.setColor(myColors[value]);
						_g.fillRect(r.x, r.y, r.width, r.height);
					}
				}
			}
		}
	}

	public Dimension getPreferredSize() {
		return new Dimension(myPixelSize * 128 + 129, myPixelSize * 32 + 33);
	}

	protected class ScreenUpdater implements Runnable {
		private int myMillisBetweenGrabs;

		public ScreenUpdater(int _millisBetweenGrabs) {
			myMillisBetweenGrabs = _millisBetweenGrabs;
		}

		public void run() {
			while (!myStopGrabbing) {
				try {
					Thread.sleep(myMillisBetweenGrabs);
				}
				catch (Exception e) {
				}

				try {
					EmpegScreen.this.grabScreen();
				}
				catch (ProtocolException e) {
					Debug.println(e);
				}
			}
			myGrabbing = false;
		}
	}

	/**
	* Creates an AbstractAction that will regrab this EmpegScreen.  Use this to easily hook
	* the screen updating up to a JButton (for instance)
	*/
	public AbstractAction createGrabAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent _event) {
				try {
					EmpegScreen.this.grabScreen();
				}
				catch (ProtocolException e) {
					Debug.println(e);
				}
			}
		};
	}

	/**
	* Creates an AbstractAction that will regrab this EmpegScreen.  Use this to easily hook
	* the screen updating up to a JButton (for instance)
	*/
	public AbstractAction createRepeatingGrabAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent _event) {
				if (!EmpegScreen.this.myGrabbing) {
					EmpegScreen.this.startGrabbing(500);
				}
				else {
					EmpegScreen.this.stopGrabbing();
				}
			}
		};
	}

	/**
	* Creates a WindowListener that will stop grabbing on shutdown.
	*/
	public WindowListener createStopGrabbingWindowListener() {
		return new WindowAdapter() {
			public void windowClosed(WindowEvent _event) {
				EmpegScreen.this.stopGrabbing();
			}
		};
	}
}
