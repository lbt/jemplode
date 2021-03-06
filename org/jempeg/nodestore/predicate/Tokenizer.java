package org.jempeg.nodestore.predicate;

import java.text.ParseException;
import java.util.Enumeration;
import java.util.Stack;
import java.util.Vector;

public class Tokenizer {
	private int myIndex;
	private int myQuoteIndex;
	private boolean myInOperator;
	private boolean myInNumber;
	private String myString;

	private Group myGroup;
	private StringBuffer myTokenBuf;

	public Tokenizer(String _string) {
		myString = _string;
	}

	public Tokenizer.Group tokenize() throws ParseException {
		Stack groupStack = new Stack();
		myGroup = new Tokenizer.Group();
		myTokenBuf = new StringBuffer();
		myQuoteIndex = -1;
		myInOperator = false;
		myInNumber = false;

		boolean escape = false;
		int length = myString.length();
		for (myIndex = 0; myIndex < length; myIndex ++) {
			char ch = myString.charAt(myIndex);
			if (myQuoteIndex != -1) {
				if (escape) {
					myTokenBuf.append(ch);
					escape = false;
				}
				else if (ch == '"') {
					handleToken();
					myQuoteIndex = -1;
				}
				else if (ch == '\\') {
					escape = true;
				}
				else {
					myTokenBuf.append(ch);
				}
			}
			else if (ch == '"') {
				if (myInOperator) {
					handleToken();
				}
				else if (myTokenBuf.length() > 0) {
					throw new ParseException("You started a string in the middle of a token.", myIndex);
				}
				myQuoteIndex = myIndex;
			}
			else if (ch == '(') {
				handleToken();
				groupStack.push(myGroup);
				myGroup = new Tokenizer.Group();
			}
			else if (ch == ')') {
				handleToken();
				Group previousGroup = (Group) groupStack.pop();
				if (previousGroup == null) {
					throw new ParseException("You closed a paren without a matching open paren.", myIndex);
				}
				previousGroup.addToken(myGroup);

				if (previousGroup.getParamCount() > 0) {
					previousGroup = (Group) groupStack.pop();
				}

				myGroup = previousGroup;
			}
			else if (ch == ',') {
				handleToken();

				Group functionParamsGroup;
				if (groupStack.size() == 0) {
					throw new ParseException("You used a comma outside the context of a set of function parameters.", myIndex);
				}
				Group previousGroup = (Group) groupStack.peek();
				if (previousGroup.getParamCount() == 0) {
					functionParamsGroup = new Tokenizer.Group();
					previousGroup.addToken(functionParamsGroup);
				}
				else {
					functionParamsGroup = previousGroup;
				}
				functionParamsGroup.addToken(myGroup);
				myGroup = new Tokenizer.Group();
			}
			else if (ch == ' ') {
				handleToken();
				// ignore everything after the first space
			}
			else {
				if (isOperator(ch)) {
					if (!myInOperator) {
						handleToken();
						myInOperator = true;
					}
				}
				else if (myInOperator) {
					handleToken();
					myInOperator = false;
				}
				if (Character.isDigit(ch) && myTokenBuf.length() == 0) {
					myInNumber = true;
				}
				myTokenBuf.append(ch);
			}
		}

		handleToken();
		
		if (myQuoteIndex != -1) {
			myQuoteIndex = -1;
			//throw new ParseException("You opened a quote without closing it.", myQuoteIndex);
		}

		if (groupStack.size() > 0) {
			throw new ParseException("You opened " + groupStack.size() + " parens without closing them.", 0);
		}

		return myGroup;
	}

	private boolean isOperator(char ch) {
		return (ch == '=' || ch == '!' || ch == '<' || ch == '>' || ch == '|' || ch == '&' || ch == '-' || ch == '+' || ch == '/' || ch == '*');
	}

	private void handleToken() throws ParseException {
		IToken token;
		if (myQuoteIndex != -1) {
			token = new Tokenizer.StringLiteral(myTokenBuf.toString());
		}
		else if (myInNumber) {
			token = new Tokenizer.NumericLiteral(myTokenBuf.toString());
			myInNumber = false;
		}
		else if (myTokenBuf.length() > 0) {
			token = new Tokenizer.KeywordToken(myTokenBuf.toString().toLowerCase());
		}
		else {
			token = null;
		}
		if (token != null) {
			myTokenBuf.setLength(0);
			myGroup.addToken(token);
		}
	}

	interface IToken {
	}

	public static class StringLiteral extends LiteralToken {
		public StringLiteral(String _value) {
			super(_value);
		}

		public String toString() {
			return "[StringLiteral: " + getValue() + "]";
		}
	}

	public static class NumericLiteral extends LiteralToken {
		public NumericLiteral(String _value) {
			super(_value);
		}

		public String toString() {
			return "[NumericLiteral: " + getValue() + "]";
		}
	}

	public static class KeywordToken implements IToken {
		private String myName;

		public KeywordToken(String _name) {
			myName = _name;
		}

		public String getName() {
			return myName;
		}

		public String toString() {
			return "[Keyword: " + myName + "]";
		}
	}

	public static abstract class LiteralToken implements IToken {
		private String myValue;

		public LiteralToken(String _value) {
			myValue = _value;
		}

		public String getValue() {
			return myValue;
		}
	}

	public static class Group implements IToken {
		private Vector myMembers;
		private int myParamCount;

		public Group() {
			myMembers = new Vector();
			myParamCount = 0;
		}

		public Enumeration tokens() {
			return myMembers.elements();
		}
		
		public int getSize() {
			return myMembers.size();
		}
		
		public IToken getTokenAt(int _index) {
			return (IToken)myMembers.elementAt(_index);
		}

		public void addToken(IToken _token) {
			myMembers.addElement(_token);
		}
		
		public int getParamCount() {
			return myParamCount;
		}

		public void incrementParamCount() {
			myParamCount ++;
		}

		public String toString() {
			return "[Group: members = " + myMembers + "]";
		}
	}
}
