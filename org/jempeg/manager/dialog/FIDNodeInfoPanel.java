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
package org.jempeg.manager.dialog;

import java.awt.Color;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.jempeg.manager.ui.JTriStateButton;
import org.jempeg.nodestore.DatabaseTags;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.NodeTags;

import com.inzyme.format.TimeFormat;
import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.ui.DialogUtils;

/**
* A panel that shows information about a a given
* FID. (length, codec, etc.)
*
* @author Mike Schrag
* @version $Revision: 1.3 $
*/
public class FIDNodeInfoPanel extends AbstractFIDNodePanel {
	private JTextField myFileSize;
	private JTextField myDuration;
	private JTextField myCodec;
	private JTextField myFormat;
	private JTextField myReferenceCount;
	
  private JTriStateButton myMarked;
  private boolean myMarkedMixed;
  private Color myMarkedColor;
	
	public FIDNodeInfoPanel() {
		myFileSize = new JTextField();
		myFileSize.setEditable(false);
		
		myDuration = new JTextField();
		myDuration.setEditable(false);
		
		myCodec = new JTextField();
		myCodec.setEditable(false);
		
		myFormat = new JTextField();
		myFormat.setEditable(false);
		
		myReferenceCount = new JTextField();
		myReferenceCount.setEditable(false);

    myMarked = new JTriStateButton(ResourceBundleUtils.getUIString("properties.nodeInfo.marked.text"));
    myMarkedColor = myMarked.getBackground();

    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    DialogUtils.addRow(new JLabel(ResourceBundleUtils.getUIString("properties.nodeInfo.fileSize.text")), myFileSize, gridBagLayout, this);
    DialogUtils.addRow(new JLabel(ResourceBundleUtils.getUIString("properties.nodeInfo.duration.text")), myDuration, gridBagLayout, this);
    DialogUtils.addRow(new JLabel(ResourceBundleUtils.getUIString("properties.nodeInfo.codec.text")), myCodec, gridBagLayout, this);
    DialogUtils.addRow(new JLabel(ResourceBundleUtils.getUIString("properties.nodeInfo.format.text")), myFormat, gridBagLayout, this);
    DialogUtils.addRow(new JLabel(ResourceBundleUtils.getUIString("properties.nodeInfo.refCount.text")), myReferenceCount, gridBagLayout, this);
    DialogUtils.addRow(new JLabel(""), myMarked, gridBagLayout, this);

    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}
	
	public FIDNodeInfoPanel(IFIDNode[] _nodes) {
		this();
		setNodes(_nodes);
	}
	
	public void setNodes(IFIDNode[] _nodes) {
		super.setNodes(_nodes);
    if (_nodes.length == 1) {
      IFIDNode node = _nodes[0];
      NodeTags tags = _nodes[0].getTags();
  		myFileSize.setText(tags.getValue(DatabaseTags.LENGTH_TAG));
  		myDuration.setText(TimeFormat.getInstance().format(tags.getLongValue(DatabaseTags.DURATION_TAG, 0)));
  		myCodec.setText(tags.getValue(DatabaseTags.CODEC_TAG));
  		myFormat.setText(tags.getValue(DatabaseTags.BITRATE_TAG) + ", " + tags.getValue(DatabaseTags.SAMPLERATE_TAG));
  		myReferenceCount.setText(String.valueOf(node.getReferenceCount()));
  		setValue(myMarked, myMarkedColor, tags.getBooleanValue(DatabaseTags.MARKED_TAG), false);
    } else {
      myFileSize.setText("");
      myDuration.setText("");
      myCodec.setText("");
      myFormat.setText("");
      myReferenceCount.setText("");
      
      myMarkedMixed = false;
      int allMarked = 0;
      for (int i = 0; i < _nodes.length; i ++) { 
	      NodeTags tags = _nodes[i].getTags();
      	int marked = tags.getBooleanValue(DatabaseTags.MARKED_TAG) ? 1 : 2;
      	if (allMarked == 0) {
      		allMarked = marked;
      	} else if (allMarked != marked) {
      		myMarkedMixed = true;
      	}
      	setValue(myMarked, myMarkedColor, (allMarked == 1), myMarkedMixed);
      }
    }
	}
	
  public void ok() {
  	saveValue(DatabaseTags.MARKED_TAG, myMarked, "1", "0", myMarkedMixed, false, false);
  }
}
