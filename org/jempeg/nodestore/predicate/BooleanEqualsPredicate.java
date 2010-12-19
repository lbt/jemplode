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
package org.jempeg.nodestore.predicate;

import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.model.NodeTag;
import org.jempeg.nodestore.model.TagValueRetriever;

/**
 * BooleanEqualsPredicate is a predicate that doesn't 
 * need an explicit "right hand value".  So you can say
 * "marked" and it means "marked = true".
 * 
 * @author Mike Schrag
 * @version $Revision: 1.3 $
 */
public class BooleanEqualsPredicate implements IPredicate {
  private String myTagName;
  private NodeTag myNodeTag;
  
  public BooleanEqualsPredicate(String _tagName) {
    myTagName = _tagName;
  }
  
  public String getValue(IFIDNode _node) {
		return String.valueOf(evaluate(_node));
	}
  
  public boolean evaluate(IFIDNode _node) {
    String value = getLeftValue(_node);
    boolean equals = false;
    if (value != null && !value.equalsIgnoreCase("no") && !value.equalsIgnoreCase("false") && !value.equalsIgnoreCase("")) {
    	equals = true;
    }
    return equals;
  }
  
	public boolean isDependentOn(NodeTag _tag) {
		if (myNodeTag != null) {
			synchronized (this) {
				myNodeTag = NodeTag.getNodeTag(myTagName);
			}
		}
		return myNodeTag.isDerivedFrom(_tag);
	}

  protected String getLeftValue(IFIDNode _node) {
    return TagValueRetriever.getValue(_node, myTagName);
  }

  protected String getTagName() {
    return myTagName;
  }

  public String toString() {
    return "(" + getTagName() + ")";
  }
}
