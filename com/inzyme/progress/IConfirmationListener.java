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
package com.inzyme.progress;

/**
* IConfirmationListener is passed into actions
* that need to be confirmed before executing.
*
* @author Mike Schrag
* @version $Revision: 1.1 $
*/
public interface IConfirmationListener {
  public static final int YES         = 0;
  public static final int NO          = 1;
  public static final int CANCEL      = 3;
  public static final int YES_CHECKED = 4;

  /**
  * Confirm whether or not to perform the particular operation.
  *
  * @param _message the message to display
  * @param _target the object that is being operated on
  * @returns whether or not to perform the particular operation (YES, NO, CANCEL, or YES_CHECKED)
  */
  public int confirm(String _message, Object _target);

  /**
  * Confirm whether or not to perform the particular operation.
  *
  * @param _message the message to display
  * @param _checkboxMessage the message to display next to the checkbox
  * @param _target the object that is being operated on
  * @returns whether or not to perform the particular operation (YES, NO, CANCEL, or YES_CHECKED)
  */
  public int confirm(String _message, String _checkboxMessage, Object _target);
}
