/* TextAreaOutputStream - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.io;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JTextArea;

public class TextAreaOutputStream extends OutputStream
{
    private JTextArea myTextArea;
    
    public TextAreaOutputStream(JTextArea _textArea) {
	myTextArea = _textArea;
    }
    
    public void write(int _b) throws IOException {
	myTextArea.append(new String(new char[] { (char) _b }));
    }
    
    public void write(byte[] b) throws IOException {
	myTextArea.append(new String(b));
    }
    
    public void write(byte[] b, int off, int len) throws IOException {
	myTextArea.append(new String(b, off, len));
    }
}
