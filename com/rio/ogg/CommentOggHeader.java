/* CommentOggHeader - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.ogg;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import com.inzyme.text.StringUtils;
import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.util.Assertions;
import com.inzyme.util.ReflectionUtils;

public class CommentOggHeader extends AbstractOggHeader
{
    private String myVendorString;
    private String[] myComments;
    
    public CommentOggHeader(OggHeaderID _headerID) {
	super(_headerID);
    }
    
    public void read(LittleEndianInputStream _is) throws IOException {
	long vendorLength = _is.readUnsigned32();
	byte[] vendorBytes = new byte[(int) vendorLength];
	_is.read(vendorBytes);
	Vector commentsVec = new Vector();
	long userCommentListLength = _is.readUnsigned32();
	for (int i = 0; (long) i < userCommentListLength; i++) {
	    long userCommentLength = _is.readUnsigned32();
	    byte[] userCommentBytes = new byte[(int) userCommentLength];
	    _is.read(userCommentBytes);
	    String userComment = new String(userCommentBytes, "UTF-8");
	    userComment = StringUtils.trim(userComment);
	    commentsVec.addElement(userComment);
	}
	myComments = new String[commentsVec.size()];
	commentsVec.copyInto(myComments);
	int framingFlag = _is.readUnsigned8();
	Assertions.assertNotEquals("framing bit", (long) framingFlag, 0L);
    }
    
    public String getVendorString() {
	return myVendorString;
    }
    
    public String[] getComments() {
	return myComments;
    }
    
    public int getSize() {
	return myComments == null ? 0 : myComments.length;
    }
    
    public String getComment(int _index) {
	return myComments[_index];
    }
    
    public Properties toProperties() {
	try {
	    String lineSeparator = System.getProperty("line.separator");
	    StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < myComments.length; i++) {
		sb.append(myComments[i]);
		sb.append(lineSeparator);
	    }
	    Properties originalProps = new Properties();
	    String propsStr
		= StringUtils.escapeUnicodeString(sb.toString(), false);
	    originalProps.load(new ByteArrayInputStream(propsStr.getBytes()));
	    Properties upperCaseProps = new Properties();
	    Enumeration keysEnum = originalProps.keys();
	    while (keysEnum.hasMoreElements()) {
		String key = (String) keysEnum.nextElement();
		upperCaseProps.put(key.toUpperCase(), originalProps.get(key));
	    }
	    return upperCaseProps;
	} catch (IOException e) {
	    throw new RuntimeException("This shouldn't ever happen: "
				       + e.getMessage());
	}
    }
    
    public String toString() {
	return ReflectionUtils.toString(this);
    }
}
