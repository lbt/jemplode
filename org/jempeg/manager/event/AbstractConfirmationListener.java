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
package org.jempeg.manager.event;

import java.text.MessageFormat;

import org.jempeg.nodestore.IFIDNode;

import com.inzyme.progress.IConfirmationListener;

/**
* AbstractConfirmationListener is a partial implementation
* of ConfirmationListener.  This provides impls of handling "Yes for All", "No
* for all", etc.
*
* @author Mike Schrag
* @version $Revision: 1.1 $
*/
public abstract class AbstractConfirmationListener implements IConfirmationListener {
	public static final int OPTION_YES        = 0;
	public static final int OPTION_NO         = 1;
	public static final int OPTION_YES_TO_ALL = 2;
	public static final int OPTION_NO_TO_ALL  = 3;
	public static final int OPTION_CANCEL     = 4;
	public static final String[] CONFIRM_VALUES = new String[] { "Yes", "No", "Yes to All", "No to All", "Cancel" };

	private int myDefaultValue;
	private int mySelectedValue = -1;

	public AbstractConfirmationListener() {
		this(AbstractConfirmationListener.OPTION_NO);
	}

	public AbstractConfirmationListener(int _defaultValue) {
		myDefaultValue = _defaultValue;
	}

	public int confirm(String _message, Object _target) {
		return confirm(_message, null, _target);
	}
	
	protected int getDefaultValue() {
		return myDefaultValue;
	}
	
	public int confirm(String _message, String _checkboxMessage, Object _target) {
		int confirmation;
		if (mySelectedValue == OPTION_YES_TO_ALL) {
			if (_checkboxMessage != null && isCheckboxSelected()) {
				confirmation = IConfirmationListener.YES_CHECKED;
			} else {
				confirmation = IConfirmationListener.YES;
			}
		} else if (mySelectedValue == OPTION_NO_TO_ALL) {
			confirmation = IConfirmationListener.NO;
		} else if (mySelectedValue == OPTION_CANCEL) {
			confirmation = IConfirmationListener.CANCEL;
		} else {
			String name;
			if (_target == null) {
				name = "";
			} else if (_target instanceof IFIDNode) {
				name = ((IFIDNode)_target).getTitle();
			} else {
				name = _target.toString();
			}

			String message = MessageFormat.format(_message, new Object[] { name });
			
			mySelectedValue = inputConfirmation(message, _checkboxMessage);
			
			if (mySelectedValue == OPTION_YES || mySelectedValue == OPTION_YES_TO_ALL) {
				if (_checkboxMessage != null && isCheckboxSelected()) {
					confirmation = IConfirmationListener.YES_CHECKED;
				} else {
					confirmation = IConfirmationListener.YES;
				}
			} else if (mySelectedValue == OPTION_NO || mySelectedValue == OPTION_NO_TO_ALL) {
				confirmation = IConfirmationListener.NO;
			} else {
				confirmation = IConfirmationListener.CANCEL;
			}
		}
		return confirmation;
	}

	protected abstract boolean isCheckboxSelected();

	protected abstract int inputConfirmation(String _message, String _checkboxMessage);
}
