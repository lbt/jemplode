/* AbstractXMLExporter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.exporter;
import java.io.IOException;
import java.io.OutputStream;

import org.jempeg.nodestore.PlayerDatabase;

public abstract class AbstractXMLExporter implements IExporter
{
    protected AbstractXMLExporter() {
	/* empty */
    }
    
    public abstract void write(PlayerDatabase playerdatabase,
			       OutputStream outputstream) throws IOException;
    
    protected String encode(String _value) {
	int length = _value.length();
	StringBuffer encodedValue = new StringBuffer(length);
	for (int i = 0; i < length; i++) {
	    char ch = _value.charAt(i);
	    if (ch == 0)
		break;
	    encodeAndAppend(encodedValue, ch);
	}
	return encodedValue.toString();
    }
    
    private void encodeAndAppend(StringBuffer sb, char ch) {
	switch (ch) {
	default:
	    if (ch < ' ') {
		sb.append('^');
		ch += '@';
	    }
	    sb.append(ch);
	    break;
	case '&':
	    sb.append("&amp;");
	    break;
	case '<':
	    sb.append("&lt;");
	    break;
	case '>':
	    sb.append("&gt;");
	    break;
	case '\u0080':
	    sb.append("&#x20AC;");
	    break;
	case '\u0082':
	    sb.append("&#x201A;");
	    break;
	case '\u0083':
	    sb.append("&#x0192;");
	    break;
	case '\u0084':
	    sb.append("&#x201E;");
	    break;
	case '\u0085':
	    sb.append("&#x2026;");
	    break;
	case '\u0086':
	    sb.append("&#x2020;");
	    break;
	case '\u0087':
	    sb.append("&#x2021;");
	    break;
	case '\u0088':
	    sb.append("&#x02C6;");
	    break;
	case '\u0089':
	    sb.append("&#x2030;");
	    break;
	case '\u008a':
	    sb.append("&#x0160;");
	    break;
	case '\u008b':
	    sb.append("&#x2039;");
	    break;
	case '\u008c':
	    sb.append("&#x0152;");
	    break;
	case '\u008e':
	    sb.append("&#x017D;");
	    break;
	case '\u0091':
	    sb.append("&#x2018;");
	    break;
	case '\u0092':
	    sb.append("&#x2019;");
	    break;
	case '\u0093':
	    sb.append("&#x201C;");
	    break;
	case '\u0094':
	    sb.append("&#x201D;");
	    break;
	case '\u0095':
	    sb.append("&#x2022;");
	    break;
	case '\u0096':
	    sb.append("&#x2013;");
	    break;
	case '\u0097':
	    sb.append("&#x2014;");
	    break;
	case '\u0098':
	    sb.append("&#x02DC;");
	    break;
	case '\u0099':
	    sb.append("&#x2122;");
	    break;
	case '\u009a':
	    sb.append("&#x0161;");
	    break;
	case '\u009b':
	    sb.append("&#x203A;");
	    break;
	case '\u009c':
	    sb.append("&#x0153;");
	    break;
	case '\u009e':
	    sb.append("&#x017E;");
	    break;
	case '\u009f':
	    sb.append("&#x0178;");
	    break;
	case '\u007f':
	case '\u0081':
	case '\u008d':
	case '\u008f':
	case '\u0090':
	case '\u009d':
	    sb.append("?");
	}
    }
}
