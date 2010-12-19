/* StringUtils - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.text;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

public class StringUtils
{
    public static final String DEFAULT_ENCODING = "default";
    public static final String ISO_8859_1 = "ISO-8859-1";
    public static final String UTF8 = "UTF8";
    public static final String UNICODE_LITTLE = "UTF-16LE";
    public static final String UNICODE_BIG = "UTF-16BE";
    public static final String UNICODE = "Unicode";
    private static final char[] digits
	= { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c',
	    'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
	    'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
    
    public static void repeat(StringBuffer _sb, String _stringToRepeat,
			      int _repeatCount) {
	for (int i = 0; i < _repeatCount; i++)
	    _sb.append(_stringToRepeat);
    }
    
    public static String trim(String _str) {
	int length = _str.length();
	boolean done = false;
	do {
	    if (length == 0)
		done = true;
	    else {
		char ch = _str.charAt(length - 1);
		if (ch != 0 && ch != '\ufffd' && ch != '\ufffc')
		    done = true;
		else
		    length--;
	    }
	} while (!done);
	return _str.substring(0, length);
    }
    
    public static int getNullTerminatedLength(byte[] _bytez,
					      int _offsetFromEnd) {
	int actualLength;
	for (actualLength = _bytez.length - _offsetFromEnd;
	     actualLength > 0 && _bytez[actualLength - 1] == 0;
	     actualLength--) {
	    /* empty */
	}
	return actualLength;
    }
    
    public static String getStringBetween(String _str, char _openChar,
					  char _closeChar) {
	int openIndex = _str.indexOf(_openChar);
	String betweenStr;
	if (openIndex != -1) {
	    int closeIndex = _str.indexOf(_closeChar, openIndex + 1);
	    betweenStr = _str.substring(openIndex + 1, closeIndex);
	} else
	    betweenStr = null;
	return betweenStr;
    }
    
    public static String uncapitalize(String _str) {
	StringBuffer newStr = new StringBuffer();
	int len = _str.length();
	boolean lowercaseLetterFound = false;
	int i;
	for (i = 0; !lowercaseLetterFound && i < len; i++) {
	    char ch = _str.charAt(i);
	    if (Character.isLowerCase(ch))
		lowercaseLetterFound = true;
	    else
		newStr.append(Character.toLowerCase(ch));
	}
	if (i > 0 && lowercaseLetterFound)
	    newStr.append(_str.substring(i - 1));
	return newStr.toString();
    }
    
    public static String[] split(String _source, String _delim) {
	String[] retval;
	if (_delim.length() > 0) {
	    int count = 0;
	    String src = _source;
	    int length1 = _source.length();
	    int length2 = _delim.length();
	    int[] indices = new int[length1];
	    for (int i = 0; i <= length1 - length2; i++) {
		if (_source.startsWith(_delim, i)) {
		    indices[count++] = i;
		    i += length2 - 1;
		}
	    }
	    int total = count + 1;
	    if (total == 1)
		retval = new String[] { src };
	    else {
		retval = new String[total];
		for (int presentToken = 0; presentToken < total;
		     presentToken++) {
		    if (presentToken == 0)
			retval[presentToken]
			    = src.substring(0, indices[presentToken]);
		    else if (presentToken == total - 1)
			retval[presentToken]
			    = src.substring(indices[presentToken - 1]
					    + length2);
		    else
			retval[presentToken]
			    = src.substring((indices[presentToken - 1]
					     + length2),
					    indices[presentToken]);
		}
	    }
	} else
	    retval = new String[] { _source };
	return retval;
    }
    
    public static String replace(String _orig, String _replace,
				 String _replaceWith) {
	StringBuffer retval = new StringBuffer();
	boolean hasTokens = false;
	String[] splits = split(_orig, _replace);
	for (int i = 0; i < splits.length; i++) {
	    hasTokens = true;
	    retval.append(splits[i]);
	    retval.append(_replaceWith);
	}
	if (hasTokens)
	    retval.setLength(retval.length() - _replaceWith.length());
	return new String(retval);
    }
    
    public static void toLowerCase(char[] _value, int _startIndex,
				   int _endIndex) {
	for (int i = _startIndex; i < _endIndex; i++) {
	    char ch = _value[i];
	    char lowercaseCh = Character.toLowerCase(ch);
	    if (lowercaseCh != ch)
		_value[i] = lowercaseCh;
	}
    }
    
    public static boolean startsWith(char[] _value, int _startIndex,
				     char[] _startsWithValue) {
	int length = _startsWithValue.length;
	if (_startIndex + length > _value.length)
	    return false;
	for (int i = 0; i < length; i++) {
	    if (_value[_startIndex + i] != _startsWithValue[i])
		return false;
	}
	return true;
    }
    
    public static void putSubstringOnEnd(char[] _value, int _startIndex,
					 int _endIndex, int _substringLength) {
	int articlePos = _endIndex - _startIndex - _substringLength;
	char[] article = new char[_substringLength - 1];
	System.arraycopy(_value, _startIndex, article, 0, article.length);
	System.arraycopy(_value, _startIndex + _substringLength, _value, 0,
			 articlePos);
	System.arraycopy(article, 0, _value, articlePos + 1, article.length);
	_value[articlePos] = ',';
    }
    
    public static String pad(long _value, int _size, int _radix) {
	StringBuffer sb = new StringBuffer();
	padAppend(sb, _value, _size, _radix);
	return sb.toString();
    }
    
    public static void padAppend(PrintStream _ps, long _value, int _size,
				 int _radix) {
	String strValue = Long.toString(_value, _radix).toUpperCase();
	int pad = _size - strValue.length();
	for (int i = 0; i < pad; i++)
	    _ps.write(48);
	_ps.print(strValue);
    }
    
    public static void padAppend(StringBuffer _sb, long _value, int _size,
				 int _radix) {
	String strValue = Long.toString(_value, _radix).toUpperCase();
	int pad = _size - strValue.length();
	for (int i = 0; i < pad; i++)
	    _sb.append("0");
	_sb.append(strValue);
    }
    
    public static String toCharacterString(char _ch) {
	return toCharacterString((int) _ch);
    }
    
    public static String toCharacterString(int _ch) {
	StringBuffer sb = new StringBuffer();
	sb.append(_ch);
	if (_ch == 0)
	    sb.append(" (char = NULL)");
	else if (_ch == 10)
	    sb.append(" (char = '\\n')");
	else if (_ch == 13)
	    sb.append(" (char = '\\r')");
	else if (_ch > 13 && _ch < 255)
	    sb.append(" (char = '" + (char) _ch + "')");
	return sb.toString();
    }
    
    public static String getValueWithDefault(String _value, String _default) {
	String value;
	if (_value == null || _value.length() == 0)
	    value = _default;
	else
	    value = _value;
	return value;
    }
    
    public static String parseString
	(byte[] _bytes, String _encoding, String _defaultEncoding)
	throws UnsupportedEncodingException {
	int offset;
	if (_encoding.equals("UTF-16LE")) {
	    if (_bytes.length >= 2 && _bytes[0] == -1 && _bytes[1] == -2)
		offset = 2;
	    else
		offset = 0;
	} else if (_encoding.equals("UTF-16BE")) {
	    if (_bytes.length >= 2 && _bytes[0] == -2 && _bytes[1] == -1)
		offset = 2;
	    else
		offset = 0;
	} else if (_encoding.equals("Unicode")) {
	    if (_bytes.length >= 2 && _bytes[0] == -1 && _bytes[1] == -2) {
		_encoding = "UTF-16LE";
		offset = 2;
	    } else if (_bytes.length >= 2 && _bytes[0] == -2
		       && _bytes[1] == -1) {
		_encoding = "UTF-16BE";
		offset = 2;
	    } else {
		_encoding = _defaultEncoding;
		offset = 0;
	    }
	} else
	    offset = 0;
	String str;
	if ("default".equals(_encoding))
	    str = new String(_bytes, offset, _bytes.length - offset);
	else
	    str = new String(_bytes, offset, _bytes.length - offset,
			     _encoding);
	str = trim(str);
	if (_encoding.equals("ISO-8859-1")) {
	    int length = str.length();
	    int newLength = 0;
	    for (newLength = 0;
		 newLength < length && str.charAt(newLength) != 0;
		 newLength++) {
		/* empty */
	    }
	    if (newLength != length)
		str = str.substring(0, newLength);
	}
	return str;
    }
    
    public static String escapeUnicodeString(String str, boolean escapeAscii) {
	StringBuffer sb = new StringBuffer();
	for (int i = 0; i < str.length(); i++) {
	    char ch = str.charAt(i);
	    if (!escapeAscii && ch >= '\002' && ch <= '~')
		sb.append(ch);
	    else {
		sb.append("\\u");
		String hex = Integer.toHexString(str.charAt(i) & '\uffff');
		if (hex.length() == 2)
		    sb.append("00");
		else if (hex.length() == 3)
		    sb.append("0");
		sb.append(hex.toUpperCase(Locale.ENGLISH));
	    }
	}
	return sb.toString();
    }
    
    public static String toHexString(byte[] _bytez) {
	StringBuffer sb = new StringBuffer();
	for (int i = 0; i < _bytez.length; i++)
	    padAppend(sb, (long) _bytez[i], 2, 16);
	return sb.toString();
    }
    
    public static String getDefaultEncoding() {
	return System.getProperty("file.encoding");
    }
    
    public static int parseIntWithoutException(String _str) {
	boolean numeric = _str != null;
	if (numeric) {
	    int length = _str.length();
	    for (int i = 0; numeric && i < length; i++)
		numeric = Character.isDigit(_str.charAt(i));
	}
	int value;
	do {
	    if (numeric) {
		try {
		    value = Integer.parseInt(_str);
		    break;
		} catch (NumberFormatException e) {
		    value = 0;
		    break;
		}
	    }
	    value = 0;
	} while (false);
	return value;
    }
    
    public static long parseLongWithoutException(String _str) {
	boolean numeric = _str != null;
	if (numeric) {
	    int length = _str.length();
	    for (int i = 0; numeric && i < length; i++)
		numeric = Character.isDigit(_str.charAt(i));
	}
	long value;
	do {
	    if (numeric) {
		try {
		    value = Long.parseLong(_str);
		    break;
		} catch (NumberFormatException e) {
		    value = 0L;
		    break;
		}
	    }
	    value = 0L;
	} while (false);
	return value;
    }
    
    public static void appendHexString(StringBuffer _sb, int i) {
	appendUnsignedString(_sb, i, 4);
    }
    
    private static void appendUnsignedString(StringBuffer _sb, int i,
					     int shift) {
	char[] buf = new char[32];
	int charPos = 32;
	int radix = 1 << shift;
	int mask = radix - 1;
	do {
	    buf[--charPos] = digits[i & mask];
	    i >>>= shift;
	} while (i != 0);
	_sb.append(buf, charPos, 32 - charPos);
    }
}
