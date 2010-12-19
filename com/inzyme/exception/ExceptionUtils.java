package com.inzyme.exception;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Handy-dandy utilities for handling exceptions.
 * 
 * @author Mike Schrag
 */
public class ExceptionUtils {
	/**
	 * Returns the stack trace for this chained throwable as a string.
	 * 
	 * @param _throwable the chained throwable to print
	 * @return String the stack trace for this throwable
	 */
	public static String getChainedStackTrace(Throwable _throwable) {
		StringWriter sw = new StringWriter();
		printChainedStackTrace(_throwable, sw);
		String str = sw.toString();
		return str;
	}

	/**
	 * Prints the contents of this chained throwable to System.out
	 * 
	 * @param _throwable the throwable to write
	 */
	public static void printChainedStackTrace(Throwable _throwable) {
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(System.out));
		printChainedStackTrace(_throwable, pw);
		pw.flush();
	}
	
	/**
	 * Prints the contents of this chained throwable to the given writer.
	 * 
	 * @param _throwable the throwable to write
	 * @param _writer the writer to output to
	 */
	public static void printChainedStackTrace(Throwable _throwable, Writer _writer) {
		PrintWriter pw = new PrintWriter(_writer);
		Throwable currentThrowable = _throwable;
		while (currentThrowable != null) {
			Throwable parentThrowable = ExceptionUtils.getParentThrowable(currentThrowable);
			if (parentThrowable != null) {
				String message = getMessage(currentThrowable);
				pw.print(message);
				pw.println(" caused by:");
			} else {
				currentThrowable.printStackTrace(pw);
			}
			currentThrowable = parentThrowable;
		}
		pw.flush();
	}

	/**
	 * Returns the paragraph form for this chained throwable as a string.
	 * 
	 * @param _throwable the chained throwable to print
	 * @return String the paragraph form for this throwable
	 */
	public static String getParagraph(Throwable _throwable) {
		StringWriter sw = new StringWriter();
		printParagraph(_throwable, sw);
		String str = sw.toString();
		return str;
	}

	
	/**
	 * Prints the paragraph form of this chained throwable to the given writer.
	 * 
	 * @param _throwable the throwable to write
	 * @param _writer the writer to output to
	 */
	public static void printParagraph(Throwable _throwable, Writer _writer) {
		PrintWriter pw = new PrintWriter(_writer);
		Throwable currentThrowable = _throwable;
		while (currentThrowable != null) {
			Throwable parentThrowable = ExceptionUtils.getParentThrowable(currentThrowable);
			String message = getMessage(currentThrowable);
			pw.print(message);
			if (parentThrowable != null) {
				pw.print("  ");
			}
			currentThrowable = parentThrowable;
		}
		pw.flush();
	}
	
	/**
	 * Returns a good message for this exception.
	 * 
	 * @param _throwable the Throwable to get a message for
	 * @return String the message for this exception
	 */
	public static String getMessage(Throwable _throwable) {
		String message = _throwable.getMessage();
		if (message == null || message.length() == 0) {
			message = _throwable.getClass().getName();
		}
		return message;
	}
	
	/**
	 * Returns the parent throwable in a chain of exceptions.
	 * 
	 * @param _throwable the throwable to get a parent for
	 * @return Throwable the parent throwable
	 */
	public static Throwable getParentThrowable(Throwable _throwable) {
		Throwable parentThrowable;
		if (_throwable instanceof IChainedThrowable) {
			parentThrowable = ((IChainedThrowable)_throwable).getParent();
		}
		else {
			parentThrowable = null;
		}
		return parentThrowable;
	}
}
