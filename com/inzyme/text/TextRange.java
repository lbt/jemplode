/* TextRange - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.text;
import java.text.ParseException;
import java.util.StringTokenizer;
import java.util.Vector;

public class TextRange
{
    private char myStartChar;
    private char myEndChar;
    
    public TextRange(char _startChar, char _endChar) {
	myStartChar = _startChar;
	myEndChar = _endChar;
    }
    
    public TextRange(String _externalForm) throws ParseException {
	int length = _externalForm.length();
	if (length == 0)
	    throw new ParseException("You can't have an empty range.", 0);
	int hyphenIndex = _externalForm.indexOf('-');
	if (hyphenIndex == -1) {
	    if (length > 1)
		throw new ParseException
			  ("You can't have more than one character as a range.",
			   1);
	    myStartChar = _externalForm.charAt(0);
	    myEndChar = myStartChar;
	} else {
	    if (hyphenIndex == 0)
		throw new ParseException
			  ("You can't have a hyphen as the first character.",
			   hyphenIndex);
	    if (hyphenIndex == length - 1)
		throw new ParseException
			  ("You can't have a hyphen as the last character.",
			   hyphenIndex);
	    if (length != 3)
		throw new ParseException
			  ("You can't have more than three characters in a range (it must be of the format 'A-Z'.",
			   length);
	    myStartChar = _externalForm.charAt(0);
	    myEndChar = _externalForm.charAt(2);
	    if (myStartChar > myEndChar)
		throw new ParseException
			  ("You can't have an end character that is less than the start character.",
			   2);
	}
    }
    
    public static TextRange[] fromExternalForm(String _textRanges)
	throws ParseException {
	Vector textRangeVec = new Vector();
	StringTokenizer tokenizer = new StringTokenizer(_textRanges, ",");
	while (tokenizer.hasMoreElements()) {
	    String token = tokenizer.nextToken().trim();
	    TextRange textRange = new TextRange(token);
	    textRangeVec.addElement(textRange);
	}
	TextRange[] textRanges = new TextRange[textRangeVec.size()];
	textRangeVec.copyInto(textRanges);
	return textRanges;
    }
    
    public static String toExternalForm(TextRange[] _textRanges) {
	StringBuffer externalForm = new StringBuffer();
	for (int i = 0; i < _textRanges.length; i++) {
	    if (externalForm.length() > 0)
		externalForm.append(',');
	    externalForm.append(_textRanges[i]);
	}
	return externalForm.toString();
    }
    
    public char getStartChar() {
	return myStartChar;
    }
    
    public char getEndChar() {
	return myEndChar;
    }
    
    public boolean contains(char _ch) {
	boolean contains = _ch >= myStartChar && _ch <= myEndChar;
	return contains;
    }
    
    public boolean containsIgnoreCase(char _ch) {
	boolean contains
	    = (Character.toUpperCase(_ch) >= Character.toUpperCase(myStartChar)
	       && (Character.toUpperCase(_ch)
		   <= Character.toUpperCase(myEndChar)));
	return contains;
    }
    
    public String toString() {
	StringBuffer sb = new StringBuffer();
	sb.append(myStartChar);
	if (myStartChar != myEndChar) {
	    sb.append('-');
	    sb.append(myEndChar);
	}
	return sb.toString();
    }
}
