package org.jempeg.tags;

import java.io.IOException;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.inzyme.io.ChainedIOException;
import com.inzyme.io.SeekableInputStream;
import com.inzyme.text.StringUtils;
import com.inzyme.typeconv.TypeConversionUtils;

/**
 * @author Mike Schrag
 */
public class RID {
	private static byte[] MD5_INPUT_BUFFER = new byte[65536];

	/**
	 * Computes a hash value on the given input stream.  This will skip
	 * forward _startingOffset bytes and read until the count gets
	 * to _endingOffset.
	 *
	 * @param _algorithm the algorithm to hash with
	 * @param _is the SeekableInputStream to read from
	 * @param _startingOffset the starting offset
	 * @param _endingOffset the ending offset
	 * @return the hash value that was computed
	 * @throws IOException if the digest cannot be computed
	 * @throws NoSuchAlgorithmException if the hash cannot be computed
	 * @throws DigestException if the computation of the digest fails
	 */
	public static synchronized String calculateRid(SeekableInputStream _is, long _startingOffset, long _endingOffset) throws IOException {
		try {
			MessageDigest md5Digest = MessageDigest.getInstance("MD5");

			byte[] digest = new byte[16];
			_is.seek(_startingOffset);
			int totalLength = (int) (_endingOffset - _startingOffset);
			if (totalLength <= 65536) {
				_is.readFully(MD5_INPUT_BUFFER, 0, totalLength);
				md5Digest.update(MD5_INPUT_BUFFER, 0, totalLength);
				md5Digest.digest(digest, 0, 16);
			}
			else {
				_is.readFully(MD5_INPUT_BUFFER);
				byte[] digest1 = md5Digest.digest(MD5_INPUT_BUFFER);

				_is.seek(_endingOffset - 65536);
				_is.readFully(MD5_INPUT_BUFFER);
				byte[] digest2 = md5Digest.digest(MD5_INPUT_BUFFER);

				_is.seek((_startingOffset + _endingOffset - 65536) / 2);
				_is.readFully(MD5_INPUT_BUFFER);
				byte[] digest3 = md5Digest.digest(MD5_INPUT_BUFFER);

				int digestLength = digest1.length;
				for (int i = 0; i < digestLength; i ++) {
					digest[i] = (byte) (digest1[i] ^ digest2[i] ^ digest3[i]);
				}
			}
			StringBuffer sb = new StringBuffer(32);
			for (int i = 0; i < digest.length; i ++) {
				int value = TypeConversionUtils.toUnsigned8(digest[i]);
				if (value < 16) {
					sb.append('0');
				}
				StringUtils.appendHexString(sb, value);
			}
			return sb.toString();
		}
		catch (DigestException e) {
			throw new ChainedIOException("Unable to compute MD5 sum.", e);
		}
		catch (NoSuchAlgorithmException e) {
			throw new ChainedIOException("Unable to load MD5 algorithm.", e);
		}
	}

}
