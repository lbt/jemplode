/**
* This file is licensed under the GPL.
*
* See the LICENSE0 file included in this release, or
* http://www.opensource.org/licenses/gpl-license.html
* for the details of the license.
*/
package org.jempeg.empeg.protocol;

/**
* The interface that is implemented by
* anything that wants to receive upgrade
* status events.
*
* @author Mike Schrag
* @version $Revision: 1.1 $
*/
public interface UpgradeListenerIfc {
  public void showStatus(int _section, int _operation, int _current, int _maximum);
  public void showError(int _section, int _error);
  public void textLoaded(String _info, String _what, String _release, String _version);
}

