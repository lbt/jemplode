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
package com.inzyme.text;

import java.text.CharacterIterator;
import java.text.ParseException;
import java.text.StringCharacterIterator;
import java.util.Enumeration;
import java.util.NoSuchElementException;

import com.inzyme.util.Debug;


/**
* CommandLineTokenizer is a simple parser for commandline-syntax
* arguments (that may have quotes in them for grouping
* arguments that have spaces).
*
* @author Mike Schrag
* @version $Revision: 1.2 $
*/
public class CommandLineTokenizer implements Enumeration {
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
		return (myIterator.current() != CharacterIterator.DONE);
	}
	
	public Object nextElement() {
		String token;
		try {
			token = nextToken();
		}
			catch (ParseException e) {
				Debug.println(e);
				token = null;
			}
		return token;
	}
	
	public String nextToken() throws ParseException {
		boolean escapeNext = false;
		// 1 = Whitespace, 2 = Text, 3 = Quoted;
		StringBuffer token = new StringBuffer();
		char c = myIterator.current();
		boolean done = false;
		while (!done && c != CharacterIterator.DONE) {
			if (escapeNext) {
				switch (c) {
					case '\n':
						throw new ParseException("Unexception escape '\\' at end of string.", myIterator.getIndex());
						
					default:
						token.append(c);
						c = myIterator.next();
						break;
				}
				escapeNext = false;
			} else {
				switch (myState) {
					case 1:
						switch (c) {
							case '\n':
							case ' ':
							case '\t':
								c = myIterator.next();
								break;
								
							case '\"':
								myState = 3;
								c = myIterator.next();
								if (token.length() > 0) {
									done = true;
								}
								break;
							
							case '\\':
								escapeNext = true;
								c = myIterator.next();
								break;
							
							default:
								myState = 2;
								if (token.length() > 0) {
									done = true;
								}
								break;
						}
						break;
					
					case 2:
						switch (c) {
							case ' ':
							case '\t':
							case '\n':
								myState = 1;
								break;
							
//							case '\"':
//								throw new ParseException("Unexpected quote '\"' in string.", myIterator.getIndex());
								
							case '\\':
								escapeNext = true;
								c = myIterator.next();
								break;
							
							default:
								token.append(c);
								c = myIterator.next();
								break;
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
								break;
						}
						break;
				}
			}
		}
		
		if (token.length() <= 0) {
      throw new NoSuchElementException("There are no more tokens on this line.");
    }
    
		return token.toString();
	}
}
