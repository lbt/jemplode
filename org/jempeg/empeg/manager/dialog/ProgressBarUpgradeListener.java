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
package org.jempeg.empeg.manager.dialog;

import javax.swing.JProgressBar;

import org.jempeg.empeg.protocol.UpgradeListenerIfc;
import org.jempeg.empeg.protocol.UpgraderConstants;

/**
* An implementation of UpgradeListener that updates a 
* progress bar with status.
*
* @author Mike Schrag
* @version $Revision: 1.1 $
**/
public class ProgressBarUpgradeListener implements UpgradeListenerIfc {
  private JProgressBar myProgressBar;
  
  public ProgressBarUpgradeListener(JProgressBar _progressBar) {
    myProgressBar = _progressBar;
  }
  
  public void textLoaded(String _info, String _what, String _release, String _version) {
  }

  public void showStatus(int _section, int _operation, int _current, int _maximum) {
    String operationStr;

    switch (_operation) {
      case UpgraderConstants.UPGRADE_CHECKFILE:
        operationStr = "Checking file...";
        break;

      case UpgraderConstants.UPGRADE_CHECKEDFILE:
        operationStr = "Checked file...";
        break;
    
      case UpgraderConstants.UPGRADE_FINDUNIT:
        operationStr = "Finding unit...";
        break;
      
      case UpgraderConstants.UPGRADE_CHECKID:
        operationStr = "Checking ID...";
        break;
      
      case UpgraderConstants.UPGRADE_ERASE:
        operationStr = "Erasing...";
        break;
      
      case UpgraderConstants.UPGRADE_PROGRAM:
        operationStr = "Programming...";
        break;
      
      case UpgraderConstants.UPGRADE_RESTART:
        operationStr = "Restarting...";
        break;
    
      case UpgraderConstants.UPGRADE_FINDPUMP:
        operationStr = "Finding Pump...";
        break;
      
      case UpgraderConstants.UPGRADE_PUMPWAIT:
        operationStr = "Pump Wait...";
        break;
      
      case UpgraderConstants.UPGRADE_PUMPSELECT:
        operationStr = "Pump Select...";
        break;
      
      case UpgraderConstants.UPGRADE_PUMP:
        operationStr = "Pumping...";
        break;
      
      case UpgraderConstants.UPGRADE_PUMPDONE:
        operationStr = "Pumping Done.";
        break;
    
      case UpgraderConstants.UPGRADE_DONE:
        operationStr = "Done.";
        break;
      
      case UpgraderConstants.UPGRADE_ERRORS:
        operationStr = "Errors.";
        break;
      
      default:
        operationStr = "?";
        break;
    }

    myProgressBar.setString(operationStr);
    myProgressBar.setMaximum(_maximum);
    myProgressBar.setValue(_current);
  }

  public void showError(int _section, int _error) {
  }
}

