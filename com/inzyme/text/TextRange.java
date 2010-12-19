package com.inzyme.text;

import java.text.ParseException;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * TextRange represents a range of characters.
 * 
 * @author Mike Schrag
 */
public class TextRange {
  private char myStartChar;
  private char myEndChar;

  /**
   * Constructor for TextRange.
   * 
   * @param _startChar the starting character of the range
   * @param _endChar the ending character of the range
   */
  public TextRange(char _startChar, char _endChar) {
    myStartChar = _startChar;
    myEndChar = _endChar;
  }

  /**
   * Constructs a TextRange from a String format. The format should be "A" or "A-Z". Any other format will fail with a ParseException.
   * 
   * @param _externalForm the String form of the range
   * @throws ParseException if the string isn't of the proper format
   */
  public TextRange(String _externalForm) throws ParseException {
    int length = _externalForm.length();
    if (length == 0) {
      throw new ParseException("You can't have an empty range.", 0);
    }

    int hyphenIndex = _externalForm.indexOf('-');
    if (hyphenIndex == -1) {
      if (length > 1) {
        throw new ParseException("You can't have more than one character as a range.", 1);
      }
      myStartChar = _externalForm.charAt(0);
      myEndChar = myStartChar;
    }
    else if (hyphenIndex == 0) {
      throw new ParseException("You can't have a hyphen as the first character.", hyphenIndex);
    }
    else if (hyphenIndex == length - 1) {
      throw new ParseException("You can't have a hyphen as the last character.", hyphenIndex);
    }
    else if (length != 3) {
      throw new ParseException("You can't have more than three characters in a range (it must be of the format 'A-Z'.", length);
    }
    else {
      myStartChar = _externalForm.charAt(0);
      myEndChar = _externalForm.charAt(2);
      if (myStartChar > myEndChar) {
        throw new ParseException("You can't have an end character that is less than the start character.", 2);
      }
    }
  }

  /**
   * Returns a set of TextRanges given a comma separated string.
   * 
   * @param _textRanges comma separated text ranges
   * @return a set of TextRanges
   * @throws ParseException if the string cannot be parsed
   */
  public static TextRange[] fromExternalForm(String _textRanges) throws ParseException {
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
    for (int i = 0; i < _textRanges.length; i ++ ) {
      if (externalForm.length() > 0) {
        externalForm.append(',');
      }
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
    boolean contains = (_ch >= myStartChar && _ch <= myEndChar);
    return contains;
  }

  public boolean containsIgnoreCase(char _ch) {
    boolean contains = (Character.toUpperCase(_ch) >= Character.toUpperCase(myStartChar) && Character.toUpperCase(_ch) <= Character.toUpperCase(myEndChar));
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