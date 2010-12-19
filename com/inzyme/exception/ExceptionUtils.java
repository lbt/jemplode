/* ExceptionUtils - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.exception;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class ExceptionUtils
{
    public static String getChainedStackTrace(Throwable _throwable) {
	StringWriter sw = new StringWriter();
	printChainedStackTrace(_throwable, sw);
	String str = sw.toString();
	return str;
    }
    
    public static void printChainedStackTrace(Throwable _throwable) {
	PrintWriter pw = new PrintWriter(new OutputStreamWriter(System.out));
	printChainedStackTrace(_throwable, pw);
	pw.flush();
    }
    
    public static void printChainedStackTrace(Throwable _throwable,
					      Writer _writer) {
	PrintWriter pw = new PrintWriter(_writer);
	Throwable parentThrowable;
	for (Throwable currentThrowable = _throwable; currentThrowable != null;
	     currentThrowable = parentThrowable) {
	    parentThrowable = getParentThrowable(currentThrowable);
	    if (parentThrowable != null) {
		String message = getMessage(currentThrowable);
		pw.print(message);
		pw.println(" caused by:");
	    } else
		currentThrowable.printStackTrace(pw);
	}
	pw.flush();
    }
    
    public static String getParagraph(Throwable _throwable) {
	StringWriter sw = new StringWriter();
	printParagraph(_throwable, sw);
	String str = sw.toString();
	return str;
    }
    
    public static void printParagraph(Throwable _throwable, Writer _writer) {
	PrintWriter pw = new PrintWriter(_writer);
	Throwable parentThrowable;
	for (Throwable currentThrowable = _throwable; currentThrowable != null;
	     currentThrowable = parentThrowable) {
	    parentThrowable = getParentThrowable(currentThrowable);
	    String message = getMessage(currentThrowable);
	    pw.print(message);
	    if (parentThrowable != null)
		pw.print("  ");
	}
	pw.flush();
    }
    
    public static String getMessage(Throwable _throwable) {
	String message = _throwable.getMessage();
	if (message == null || message.length() == 0)
	    message = _throwable.getClass().getName();
	return message;
    }
    
    public static Throwable getParentThrowable(Throwable _throwable) {
	Throwable parentThrowable;
	if (_throwable instanceof IChainedThrowable)
	    parentThrowable = ((IChainedThrowable) _throwable).getParent();
	else
	    parentThrowable = null;
	return parentThrowable;
    }
}
