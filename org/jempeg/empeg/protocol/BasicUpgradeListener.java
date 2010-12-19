/**
* This file is licensed under the GPL.
*
* See the LICENSE0 file included in this release, or
* http://www.opensource.org/licenses/gpl-license.html
* for the details of the license.
*/
package org.jempeg.empeg.protocol;

/**
* A basic implemention of UpgradeListenerIfc that
* just prints status to the console.
*
* @author Mike Schrag
* @version $Revision: 1.1 $
*/
public class BasicUpgradeListener implements UpgradeListenerIfc {
  public void showStatus(int _section, int _operation, int _current, int _maximum) {
    System.out.println("Status: Section = " + _section + "; Operation = " + _operation + "; Current = " + _current + "; Maximum = " + _maximum);
  }

  public void showError(int _section, int _error) {
    System.out.println("Error: Section = " + _section + "; Error = " + _error);
  }

  public void textLoaded(String _info, String _what, String _release, String _version) {
    System.out.println("TextLoaded: Info = " + _info + "; What = " + _what + "; Release = " + _release + "; Version = " + _version);
  }
}
