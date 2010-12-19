/* Debug - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
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

public class Debug
{
    public static final int COMPLETELY_RIDICULOUS = 64;
    public static final int PEDANTIC = 1;
    public static final int VERBOSE = 2;
    public static final int INFORMATIVE = 4;
    public static final int WARNING = 8;
    public static final int ERROR = 16;
    public static final int SILENCE = 32;
    private static int DEBUG_LEVEL = 16;
    private static boolean DEBUG_WINDOW = false;
    private static Window myTopLevelWindow;
    
    static {
	try {
	    DEBUG_LEVEL = Integer.getInteger("debuglevel", 24).intValue();
	    DEBUG_WINDOW = Boolean.getBoolean("debugWindow");
	} catch (Throwable t) {
	    DEBUG_LEVEL = 16;
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
	if ((_level & getDebugLevel()) != 0)
	    return true;
	return false;
    }
    
    public static int getDebugLevel() {
	return DEBUG_LEVEL;
    }
    
    public static void print(int _type, String _str) {
	if ((_type & DEBUG_LEVEL) != 0)
	    System.out.print(_str);
    }
    
    public static void println(int _type, String _str) {
	if ((_type & DEBUG_LEVEL) != 0)
	    System.out.println(_str);
    }
    
    public static void println(Throwable _t) {
	println(16, _t);
    }
    
    public static void println(int _type, Throwable _t) {
	if ((_type & DEBUG_LEVEL) != 0)
	    System.out.println(ExceptionUtils.getChainedStackTrace(_t));
    }
    
    public static void setTopLevelWindow(Window _topLevelWindow) {
	myTopLevelWindow = _topLevelWindow;
    }
    
    public static void handleError(String _errorMessage, boolean _modal) {
	handleError(null, _errorMessage, _modal);
    }
    
    public static void handleError(Window _parentFrame, String _errorMessage,
				   boolean _modal) {
	handleError(_parentFrame, _errorMessage, null, _modal);
    }
    
    public static void handleError(Throwable _error, boolean _modal) {
	handleError(null,
		    _error.getClass().getName() + ": " + _error.getMessage(),
		    _error, _modal);
    }
    
    public static void handleError(Window _parentFrame, Throwable _error,
				   boolean _modal) {
	handleError(_parentFrame,
		    _error.getClass().getName() + ": " + _error.getMessage(),
		    _error, _modal);
    }
    
    public static void handleError(String _errorMessage, Throwable _error,
				   boolean _modal) {
	handleError(null, _errorMessage, _error, _modal);
    }
    
    public static void handleError(Window _parentFrame, String _errorMessage,
				   Throwable _error, boolean _modal) {
	if (_error != null)
	    println(16, _error);
	if (_error == null)
	    ErrorDialog.showErrorDialog((_parentFrame == null
					 ? myTopLevelWindow : _parentFrame),
					_errorMessage, _modal);
	else
	    ErrorDialog.showErrorDialog((_parentFrame == null
					 ? myTopLevelWindow : _parentFrame),
					_error, _modal);
    }
}
