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

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;

import org.jempeg.empeg.manager.event.DocumentActionAdapter;
import org.jempeg.nodestore.IDeviceSettings;

import com.inzyme.event.ActionSource;

/**
* AbstractChangeablePanel provides some convenience methods
* for tracking changes on various components (like 
* buttons, checkboxes, comboboxes, and textfields).  This
* is extended by things like the Empeg configuration dialog and
* properties dialogs that need to know when the user has
* modified various components in the dialog.
*
* @author Mike Schrag
* @author Ron Forrester
* @version $Revision: 1.2 $
*/
public abstract class AbstractChangeablePanel extends JPanel implements ActionListener, ChangeListener {
	private ActionSource myActionSource;
	private DocumentActionAdapter myDocumentActionAdapter;

	public AbstractChangeablePanel() {
		myActionSource = new ActionSource(this);
		myDocumentActionAdapter = new DocumentActionAdapter(myActionSource);
	}

	public void addActionListener(ActionListener _listener) {
		myActionSource.addActionListener(_listener);
	}
	
	public void removeActionListener(ActionListener _listener) {
		myActionSource.removeActionListener(_listener);
	}
	
	public void listenTo(JTextComponent _textComponent) {
		myDocumentActionAdapter.listenTo(_textComponent);
	}
	
	public void unlistenTo(JTextComponent _textComponent) {
		myDocumentActionAdapter.unlistenTo(_textComponent);
	}

	public void listenTo(AbstractButton _button) {
		_button.addActionListener(this);
	}
	
	public void unlistenTo(AbstractButton _button) {
		_button.removeActionListener(this);
	}
	
	public void listenTo(JComboBox _comboBox) {
		_comboBox.addActionListener(this);
	}
	
	public void unlistenTo(JComboBox _comboBox) {
		_comboBox.removeActionListener(this);
	}
	
  public void listenTo(JSlider _slider) {
    _slider.addChangeListener(this);
  }
  
  public void unlistenTo(JSlider _slider) {
    _slider.removeChangeListener(this);
  }

	public void read(IDeviceSettings _configFile) {
		myDocumentActionAdapter.setEnabled(false);
		read0(_configFile);
		myDocumentActionAdapter.setEnabled(true);
	}

	public void write(IDeviceSettings _configFile) {
		write0(_configFile);
	}

	protected abstract void read0(IDeviceSettings _configFile);

	protected abstract void write0(IDeviceSettings _configFile);
	
	public void actionPerformed(ActionEvent _event) {
		myActionSource.fireActionPerformed();
	}

  public void stateChanged(ChangeEvent _event) {
    myActionSource.fireActionPerformed();
  }
}
