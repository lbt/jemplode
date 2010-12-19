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
package com.inzyme.util;

import java.awt.Window;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.inzyme.exception.ExceptionUtils;
import com.inzyme.io.TextAreaOutputStream;
import com.inzyme.ui.ErrorDialog;

/**
* Debug provides a toggle-able debug
* output interface.  Debug output
* defaults to off, but can be turned on
* by setting the system property "debug"
* to "true".
*
* @author Mike Schrag
* @version $Revision: 1.3 $
*/
public class Debug {
  public static final int COMPLETELY_RIDICULOUS = 64;
  public static final int PEDANTIC        = 1;
  public static final int VERBOSE         = 2;
  public static final int INFORMATIVE     = 4;
  public static final int WARNING         = 8;
  public static final int ERROR           = 16;
  public static final int SILENCE         = 32;
  
  private static int DEBUG_LEVEL = ERROR;
  private static boolean DEBUG_WINDOW = false;
  private static Window myTopLevelWindow;

  static {
  	try {
			DEBUG_LEVEL = Integer.getInteger("debuglevel", WARNING | ERROR).intValue();
			DEBUG_WINDOW = Boolean.getBoolean("debugWindow");
  	}
  	catch (Throwable t) {
  		DEBUG_LEVEL = ERROR;
  		DEBUG_WINDOW = false;
  	}
  	
  	if (DEBUG_WINDOW) {
  		JFrame debugFrame = new JFrame("debug");
  		JTextArea jta = new JTextArea();
  		JScrollPane jsp = new JScrollPane(jta);
  		debugFrame.getContentPane().add(jsp);
  		TextAreaOutputStream taos = new TextAreaOutputStream(jta);
  		System.setOut(new PrintStream(taos));
  		debugFrame.setSize(400, 600);
  		debugFrame.show();
  	}
	}
	
  public static void setDebugLevel(int _debugLevel) {
    DEBUG_LEVEL = _debugLevel;
  }
  
  public static boolean isDebugLevel(int _level) {
  	return ((_level & getDebugLevel()) != 0);
  }
     
  public static int getDebugLevel() {
  	return DEBUG_LEVEL;
  }
  
	public static void print(int _type, String _str) {
		if ((_type & DEBUG_LEVEL) != 0) {
			System.out.print(_str);
		}
	}

	public static void println(int _type, String _str) {
		if ((_type & DEBUG_LEVEL) != 0) {
			System.out.println(_str);
		}
	}
	
	public static void println(Throwable _t) {
    println(Debug.ERROR, _t);
  }

	public static void println(int _type, Throwable _t) {
		if ((_type & DEBUG_LEVEL) != 0) {
			System.out.println(ExceptionUtils.getChainedStackTrace(_t));
		}
	}
	
	public static void setTopLevelWindow(Window _topLevelWindow) {
		myTopLevelWindow = _topLevelWindow;
	}
	
	public static void handleError(String _errorMessage, boolean _modal) {
		handleError(null, _errorMessage, _modal);
	}
	
	public static void handleError(Window _parentFrame, String _errorMessage, boolean _modal) {
		handleError(_parentFrame, _errorMessage, null, _modal);
	}
	
	public static void handleError(Throwable _error, boolean _modal) {
		handleError(null, _error.getClass().getName() + ": " + _error.getMessage(), _error, _modal);
	}
		
	public static void handleError(Window _parentFrame, Throwable _error, boolean _modal) {
		handleError(_parentFrame, _error.getClass().getName() + ": " + _error.getMessage(), _error, _modal);
	}
	
	public static void handleError(String _errorMessage, Throwable _error, boolean _modal) {
		handleError(null, _errorMessage, _error, _modal);
	}
	
	public static void handleError(Window _parentFrame, String _errorMessage, Throwable _error, boolean _modal) {
		if (_error != null) {
      Debug.println(Debug.ERROR, _error);
		}
		if (_error == null) {
			ErrorDialog.showErrorDialog((_parentFrame == null) ? myTopLevelWindow : _parentFrame, _errorMessage, _modal);
		} else {
			ErrorDialog.showErrorDialog((_parentFrame == null) ? myTopLevelWindow : _parentFrame, _error, _modal);
		}
	}
}
