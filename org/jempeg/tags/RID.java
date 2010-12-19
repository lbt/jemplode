/* RID - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.tags;
import java.io.IOException;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.inzyme.io.ChainedIOException;
import com.inzyme.io.SeekableInputStream;
import com.inzyme.text.StringUtils;
import com.inzyme.typeconv.TypeConversionUtils;

public class RID
{
    private static byte[] MD5_INPUT_BUFFER = new byte[65536];
    
    public static synchronized String calculateRid
	(SeekableInputStream _is, long _startingOffset, long _endingOffset)
	throws IOException {
	try {
	    MessageDigest md5Digest = MessageDigest.getInstance("MD5");
	    byte[] digest = new byte[16];
	    _is.seek(_startingOffset);
	    int totalLength = (int) (_endingOffset - _startingOffset);
	    if (totalLength <= 65536) {
		_is.readFully(MD5_INPUT_BUFFER, 0, totalLength);
		md5Digest.update(MD5_INPUT_BUFFER, 0, totalLength);
		md5Digest.digest(digest, 0, 16);
	    } else {
		_is.readFully(MD5_INPUT_BUFFER);
		byte[] digest1 = md5Digest.digest(MD5_INPUT_BUFFER);
		_is.seek(_endingOffset - 65536L);
		_is.readFully(MD5_INPUT_BUFFER);
		byte[] digest2 = md5Digest.digest(MD5_INPUT_BUFFER);
		_is.seek((_startingOffset + _endingOffset - 65536L) / 2L);
		_is.readFully(MD5_INPUT_BUFFER);
		byte[] digest3 = md5Digest.digest(MD5_INPUT_BUFFER);
		int digestLength = digest1.length;
		for (int i = 0; i < digestLength; i++)
		    digest[i] = (byte) (digest1[i] ^ digest2[i] ^ digest3[i]);
	    }
	    StringBuffer sb = new StringBuffer(32);
	    for (int i = 0; i < digest.length; i++) {
		int value = TypeConversionUtils.toUnsigned8(digest[i]);
		if (value < 16)
		    sb.append('0');
		StringUtils.appendHexString(sb, value);
	    }
	    return sb.toString();
	} catch (DigestException e) {
	    throw new ChainedIOException("Unable to compute MD5 sum.", e);
	} catch (NoSuchAlgorithmException e) {
	    throw new ChainedIOException("Unable to load MD5 algorithm.", e);
	}
    }
}
