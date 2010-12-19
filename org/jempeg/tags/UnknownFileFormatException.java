/**
* This file is licensed under the GPL.
*
* See the LICENSE0 file included in this release, or
* http://www.opensource.org/licenses/gpl-license.html
* for the details of the license.
*/
package org.jempeg.tags;

import java.io.IOException;

/**
* Thrown when a file format is unknown.
*
* @author Mike Schrag
* @version $Revision: 1.1 $
*/
public class UnknownFileFormatException extends IOException {
	public UnknownFileFormatException(String _message) {
		super(_message);
	}
}
