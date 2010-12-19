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
package org.jempeg.nodestore.exporter;

import java.io.IOException;
import java.io.OutputStream;

import org.jempeg.nodestore.PlayerDatabase;

/**
* AbstractXMLExporter provides common methods for
* writing XML data.
*
* @author Mike Schrag
* @author Toby Speight
* @version $Revision: 1.1 $
*/
public abstract class AbstractXMLExporter implements IExporter {
	protected AbstractXMLExporter() {
	}

	public abstract void write(PlayerDatabase _db, OutputStream _outputStream) throws IOException;

	protected String encode(String _value) {
		int length = _value.length();
		StringBuffer encodedValue = new StringBuffer(length);

		for (int i = 0; i < length; i++) {
			char ch = _value.charAt(i);
			if (ch == '\0')
				break;
			encodeAndAppend(encodedValue, ch);
		}

		return encodedValue.toString();
	}

	private void encodeAndAppend(StringBuffer sb, char ch) {
		switch (ch) {
			default :
				if (ch < ' ') {
					// ASCII control character - use caret notation
					sb.append('^');
					ch += '@';
				}
				sb.append(ch);
				break;

			case '&' :
				sb.append("&amp;");
				break;
			case '<' :
				sb.append("&lt;");
				break;
			case '>' :
				sb.append("&gt;");
				break;

				// Windows 1252 characters:
				// <URL:http://www.unicode.org/Public/MAPPINGS/VENDORS/MICSFT/WINDOWS/CP1252.TXT>

			case 0x80 :
				sb.append("&#x20AC;");
				break; // EURO SIGN
			case 0x82 :
				sb.append("&#x201A;");
				break; // SINGLE LOW-9 QUOTATION MARK
			case 0x83 :
				sb.append("&#x0192;");
				break; // LATIN SMALL LETTER F WITH HOOK
			case 0x84 :
				sb.append("&#x201E;");
				break; // DOUBLE LOW-9 QUOTATION MARK
			case 0x85 :
				sb.append("&#x2026;");
				break; // HORIZONTAL ELLIPSIS
			case 0x86 :
				sb.append("&#x2020;");
				break; // DAGGER
			case 0x87 :
				sb.append("&#x2021;");
				break; // DOUBLE DAGGER
			case 0x88 :
				sb.append("&#x02C6;");
				break; // MODIFIER LETTER CIRCUMFLEX ACCENT
			case 0x89 :
				sb.append("&#x2030;");
				break; // PER MILLE SIGN
			case 0x8A :
				sb.append("&#x0160;");
				break; // LATIN CAPITAL LETTER S WITH CARON
			case 0x8B :
				sb.append("&#x2039;");
				break; // SINGLE LEFT-POINTING ANGLE QUOTATION MARK
			case 0x8C :
				sb.append("&#x0152;");
				break; // LATIN CAPITAL LIGATURE OE
			case 0x8E :
				sb.append("&#x017D;");
				break; // LATIN CAPITAL LETTER Z WITH CARON
			case 0x91 :
				sb.append("&#x2018;");
				break; // LEFT SINGLE QUOTATION MARK
			case 0x92 :
				sb.append("&#x2019;");
				break; // RIGHT SINGLE QUOTATION MARK
			case 0x93 :
				sb.append("&#x201C;");
				break; // LEFT DOUBLE QUOTATION MARK
			case 0x94 :
				sb.append("&#x201D;");
				break; // RIGHT DOUBLE QUOTATION MARK
			case 0x95 :
				sb.append("&#x2022;");
				break; // BULLET
			case 0x96 :
				sb.append("&#x2013;");
				break; // EN DASH
			case 0x97 :
				sb.append("&#x2014;");
				break; // EM DASH
			case 0x98 :
				sb.append("&#x02DC;");
				break; // SMALL TILDE
			case 0x99 :
				sb.append("&#x2122;");
				break; // TRADE MARK SIGN
			case 0x9A :
				sb.append("&#x0161;");
				break; // LATIN SMALL LETTER S WITH CARON
			case 0x9B :
				sb.append("&#x203A;");
				break; // SINGLE RIGHT-POINTING ANGLE QUOTATION MARK
			case 0x9C :
				sb.append("&#x0153;");
				break; // LATIN SMALL LIGATURE OE
			case 0x9E :
				sb.append("&#x017E;");
				break; // LATIN SMALL LETTER Z WITH CARON
			case 0x9F :
				sb.append("&#x0178;");
				break; // LATIN CAPITAL LETTER Y WITH DIAERESIS

			case 0x7F :
			case 0x81 :
			case 0x8D :
			case 0x8F :
			case 0x90 :
			case 0x9D :
				sb.append("?");
				break; // UNDEFINED
		}
	}

}
