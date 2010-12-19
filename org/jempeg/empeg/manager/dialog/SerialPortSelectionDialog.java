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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.comm.CommPortIdentifier;
import javax.comm.NoSuchPortException;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;

import com.inzyme.ui.ConfirmationPanel;
import com.inzyme.ui.DialogUtils;

/**
* An dialog that provides an interface for
* selection among the various CommPorts on
* the system.
*
* @author Mike Schrag
*/
public class SerialPortSelectionDialog extends JDialog {
  public static final int APPROVE_OPTION = 1;
  public static final int CANCEL_OPTION  = 0;

  private JComboBox myCommPortList;
  private boolean myOkPressed;

  public SerialPortSelectionDialog(JFrame _frame) {
    super(_frame, "Select a Serial Port", true);
 
    Vector commPorts = new Vector();
    Enumeration portIdentifiers = CommPortIdentifier.getPortIdentifiers();
    while (portIdentifiers.hasMoreElements()) {
      CommPortIdentifier commPort = (CommPortIdentifier)portIdentifiers.nextElement();
      String name = commPort.getName();
      commPorts.addElement(name);
    }
    myCommPortList = new JComboBox(commPorts);
    ConfirmationPanel confPanel = new ConfirmationPanel(myCommPortList);
    confPanel.addOkListener(new OkListener());
    confPanel.addCancelListener(new CancelListener());
    getContentPane().add(confPanel);
    pack();
//      setSize(Math.max(420, getWidth()), Math.max(260, getHeight()));
    DialogUtils.centerWindow(SerialPortSelectionDialog.this);
  }

  public int showSerialPortSelectionDialog() {
    SerialPortSelectionDialog.this.setVisible(true);
    int results = (myOkPressed) ? APPROVE_OPTION : CANCEL_OPTION;
    return results;
  }

  public CommPortIdentifier getSelectedPort() throws NoSuchPortException {
    String commPortIdentifierStr = (String)myCommPortList.getSelectedItem();
    CommPortIdentifier commPort = CommPortIdentifier.getPortIdentifier(commPortIdentifierStr);
    return commPort;
  }

  protected class OkListener implements ActionListener {
    public void actionPerformed(ActionEvent _event) {
      myOkPressed = true;
      SerialPortSelectionDialog.this.setVisible(false);
      SerialPortSelectionDialog.this.dispose();
    }
  }

  protected class CancelListener implements ActionListener {
    public void actionPerformed(ActionEvent _event) {
      myOkPressed = false;
      SerialPortSelectionDialog.this.setVisible(false);
      SerialPortSelectionDialog.this.dispose();
    }
  }
}

