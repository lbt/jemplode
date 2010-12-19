/* Login2RequestPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2.packet;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.inzyme.typeconv.CharArray;
import com.inzyme.typeconv.LittleEndianOutputStream;

public class Login2RequestPacket extends AbstractRequestPacket
{
    private CharArray myMD5Sum;
    
    public Login2RequestPacket(CharArray _salt, String _password)
	throws NoSuchAlgorithmException {
	super(new PacketHeader(4));
	byte[] saltBytes = _salt.getValue();
	byte[] passwordBytes;
	if (_password == null)
	    passwordBytes = new byte[0];
	else
	    passwordBytes = _password.getBytes();
	byte[] saltedPassword
	    = new byte[saltBytes.length + passwordBytes.length];
	System.arraycopy(saltBytes, 0, saltedPassword, 0, saltBytes.length);
	System.arraycopy(passwordBytes, 0, saltedPassword, saltBytes.length,
			 passwordBytes.length);
	byte[] md5Sum
	    = MessageDigest.getInstance("MD5").digest(saltedPassword);
	myMD5Sum = new CharArray(md5Sum);
    }
    
    public Login2RequestPacket(CharArray _md5Sum)
	throws NoSuchAlgorithmException {
	super(new PacketHeader(4));
	myMD5Sum = _md5Sum;
    }
    
    protected void writePayload(LittleEndianOutputStream _os)
	throws IOException {
	myMD5Sum.write(_os);
    }
}
