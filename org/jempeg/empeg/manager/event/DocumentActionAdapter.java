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
package org.jempeg.empeg.manager.event;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import com.inzyme.event.ActionSource;

/**
* Receives documentListener events and fires action performs
* from this.  This is used to signal some listener that some
* element on a panel changed (without the listener having to
* know that it was a document)
*
* @author Mike Schrag
* @version $Revision: 1.2 $
*/
public class DocumentActionAdapter implements DocumentListener {
	private ActionSource mySource;
	private boolean myEnabled;

	public DocumentActionAdapter(ActionSource _source) {
		mySource = _source;
		myEnabled = true;
	}

	public void listenTo(JTextComponent _textComponent) {
		listenTo(_textComponent.getDocument());
	}

	public void unlistenTo(JTextComponent _textComponent) {
		unlistenTo(_textComponent.getDocument());
	}
	
	public void listenTo(Document _document) {
		_document.addDocumentListener(this);
	}
	
	public void unlistenTo(Document _document) {
		_document.removeDocumentListener(this);
	}
	
	public void setEnabled(boolean _enabled) {
		myEnabled = _enabled;
	}
	
	public void changedUpdate(DocumentEvent _event) {
		if (myEnabled) {
			mySource.fireActionPerformed();
		}
	}

	public void insertUpdate(DocumentEvent _event) {
		if (myEnabled) {
			mySource.fireActionPerformed();
		}
	}

	public void removeUpdate(DocumentEvent _event) {
		if (myEnabled) {
			mySource.fireActionPerformed();
		}
	}
}
