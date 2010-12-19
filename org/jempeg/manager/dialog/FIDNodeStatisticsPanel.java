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

import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.jempeg.nodestore.DatabaseTags;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.NodeTags;
import org.jempeg.nodestore.model.NodeTag;

import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.ui.DialogUtils;

/**
* A panel that shows statistics about a a given
* FID. (play count, skip count, etc)
*
* @author Mike Schrag
* @version $Revision: 1.1 $
*/
public class FIDNodeStatisticsPanel extends JComponent {
	private IFIDNode[] myNodes;
	private JTextField myTimesPlayed;
	private JTextField myLastPlayed;
	private JTextField myTimesSkipped;
	
	public FIDNodeStatisticsPanel() {
		myTimesPlayed = new JTextField();
		myTimesPlayed.setEditable(false);
		
		myLastPlayed = new JTextField();
		myLastPlayed.setEditable(false);
		
		myTimesSkipped = new JTextField();
		myTimesSkipped.setEditable(false);
		
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    DialogUtils.addRow(new JLabel(ResourceBundleUtils.getUIString("properties.statistics.timesPlayed")), myTimesPlayed, gridBagLayout, this);
    DialogUtils.addRow(new JLabel(ResourceBundleUtils.getUIString("properties.statistics.lastPlayed")), myLastPlayed, gridBagLayout, this);
    DialogUtils.addRow(new JLabel(ResourceBundleUtils.getUIString("properties.statistics.timesSkipped")), myTimesSkipped, gridBagLayout, this);

    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}
	
	public FIDNodeStatisticsPanel(IFIDNode[] _nodes) {
		this();
		setNodes(_nodes);
	}
	
	public void setNodes(IFIDNode[] _nodes) {
		myNodes = _nodes;
    if (myNodes.length == 1) {
      IFIDNode node = _nodes[0];
      NodeTags tags = node.getTags();
  		myTimesPlayed.setText(tags.getValue(DatabaseTags.PLAY_COUNT_TAG));
      int lastPlayed = tags.getIntValue(DatabaseTags.PLAY_LAST_TAG, 0);
      if (lastPlayed > 0) {
        NodeTag nodeTag = NodeTag.getNodeTag(DatabaseTags.PLAY_LAST_TAG);
        String value = nodeTag.getDisplayValue(node).toString();
  		  myLastPlayed.setText(value);
      } else {
        myLastPlayed.setText("");
      }
  		myTimesSkipped.setText(tags.getValue(DatabaseTags.SKIP_COUNT_TAG));
    } else {
      myTimesPlayed.setText("");
      myLastPlayed.setText("");
      myTimesSkipped.setText("");
    }
	}
	
	public IFIDNode[] getNodes() {
		return myNodes;
	}
}
