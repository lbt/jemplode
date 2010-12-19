/* FIDNodeStatisticsPanel - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.dialog;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.ui.DialogUtils;

import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.NodeTags;
import org.jempeg.nodestore.model.NodeTag;

public class FIDNodeStatisticsPanel extends JComponent
{
    private IFIDNode[] myNodes;
    private JTextField myTimesPlayed = new JTextField();
    private JTextField myLastPlayed;
    private JTextField myTimesSkipped;
    
    public FIDNodeStatisticsPanel() {
	myTimesPlayed.setEditable(false);
	myLastPlayed = new JTextField();
	myLastPlayed.setEditable(false);
	myTimesSkipped = new JTextField();
	myTimesSkipped.setEditable(false);
	GridBagLayout gridBagLayout = new GridBagLayout();
	setLayout(gridBagLayout);
	DialogUtils.addRow(new JLabel(ResourceBundleUtils.getUIString
				      ("properties.statistics.timesPlayed")),
			   myTimesPlayed, gridBagLayout, this);
	DialogUtils.addRow(new JLabel(ResourceBundleUtils.getUIString
				      ("properties.statistics.lastPlayed")),
			   myLastPlayed, gridBagLayout, this);
	DialogUtils.addRow(new JLabel(ResourceBundleUtils.getUIString
				      ("properties.statistics.timesSkipped")),
			   myTimesSkipped, gridBagLayout, this);
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
	    myTimesPlayed.setText(tags.getValue("play_count"));
	    int lastPlayed = tags.getIntValue("play_last", 0);
	    if (lastPlayed > 0) {
		NodeTag nodeTag = NodeTag.getNodeTag("play_last");
		String value = nodeTag.getDisplayValue(node).toString();
		myLastPlayed.setText(value);
	    } else
		myLastPlayed.setText("");
	    myTimesSkipped.setText(tags.getValue("skip_count"));
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
