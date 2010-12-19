/* CommandLineTokenizer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.text;
import java.text.ParseException;
import java.text.StringCharacterIterator;
import java.util.Enumeration;
import java.util.NoSuchElementException;

import com.inzyme.util.Debug;

public class CommandLineTokenizer implements Enumeration
{
    private StringCharacterIterator myIterator;
    private int myState;
    
    public CommandLineTokenizer(String _line) {
	myIterator = new StringCharacterIterator(_line);
	reset();
    }
    
    public void reset() {
	myState = 1;
	myIterator.first();
    }
    
    public boolean hasMoreElements() {
	return hasMoreTokens();
    }
    
    public boolean hasMoreTokens() {
	if (myIterator.current() != '\uffff')
	    return true;
	return false;
    }
    
    public Object nextElement() {
	String token;
	try {
	    token = nextToken();
	} catch (ParseException e) {
	    Debug.println(e);
	    token = null;
	}
	return token;
    }
    
    public String nextToken() throws ParseException {
	boolean escapeNext = false;
	StringBuffer token = new StringBuffer();
	char c = myIterator.current();
	boolean done = false;
	while (!done && c != '\uffff') {
	    if (escapeNext) {
		switch (c) {
		case '\n':
		    throw new ParseException
			      ("Unexception escape '\\' at end of string.",
			       myIterator.getIndex());
		default:
		    token.append(c);
		    c = myIterator.next();
		    escapeNext = false;
		    break;
		}
	    } else {
		switch (myState) {
		case 1:
		    switch (c) {
		    case '\t':
		    case '\n':
		    case ' ':
			c = myIterator.next();
			break;
		    case '\"':
			myState = 3;
			c = myIterator.next();
			if (token.length() > 0)
			    done = true;
			break;
		    case '\\':
			escapeNext = true;
			c = myIterator.next();
			break;
		    default:
			myState = 2;
			if (token.length() > 0)
			    done = true;
		    }
		    break;
		case 2:
		    switch (c) {
		    case '\t':
		    case '\n':
		    case ' ':
			myState = 1;
			break;
		    case '\\':
			escapeNext = true;
			c = myIterator.next();
			break;
		    default:
			token.append(c);
			c = myIterator.next();
		    }
		    break;
		case 3:
		    switch (c) {
		    case '\"':
			myState = 1;
			c = myIterator.next();
			break;
		    case '\\':
			escapeNext = true;
			c = myIterator.next();
			break;
		    default:
			token.append(c);
			c = myIterator.next();
		    }
		    break;
		}
	    }
	}
	if (token.length() <= 0)
	    throw new NoSuchElementException
		      ("There are no more tokens on this line.");
	return token.toString();
    }
}
