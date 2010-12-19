/**
* This file is licensed under the GPL.
*
* See the LICENSE0 file included in this release, or
* http://www.opensource.org/licenses/gpl-license.html
* for the details of the license.
*/
package org.jempeg.empeg.protocol;

import java.io.IOException;

/**
* Thrown when a higher-level failure occurs in the Empeg protocol.
*
* @author Mike Schrag
* @version $Revision: 1.1 $
*/
public class EmpegProtocolException extends IOException {
	private boolean myErrorNumSet;
	private int myErrorNum;

	public EmpegProtocolException(String _message, int _errorNum) {
		super(_message);
		myErrorNum = _errorNum;
		myErrorNumSet = true;
	}

	public EmpegProtocolException(String _message) {
		super(_message);
	}

	public boolean isErrorNumSet() {
		return myErrorNumSet;
	}

	public int getErrorNum() {
		return myErrorNum;
	}
}
