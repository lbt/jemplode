/* FIDNodeInfoPanel - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.dialog;
import java.awt.Color;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.inzyme.format.TimeFormat;
import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.ui.DialogUtils;

import org.jempeg.manager.ui.JTriStateButton;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.NodeTags;

public class FIDNodeInfoPanel extends AbstractFIDNodePanel
{
    private JTextField myFileSize = new JTextField();
    private JTextField myDuration;
    private JTextField myCodec;
    private JTextField myFormat;
    private JTextField myReferenceCount;
    private JTriStateButton myMarked;
    private boolean myMarkedMixed;
    private Color myMarkedColor;
    
    public FIDNodeInfoPanel() {
	myFileSize.setEditable(false);
	myDuration = new JTextField();
	myDuration.setEditable(false);
	myCodec = new JTextField();
	myCodec.setEditable(false);
	myFormat = new JTextField();
	myFormat.setEditable(false);
	myReferenceCount = new JTextField();
	myReferenceCount.setEditable(false);
	myMarked = new JTriStateButton(ResourceBundleUtils.getUIString
				       ("properties.nodeInfo.marked.text"));
	myMarkedColor = myMarked.getBackground();
	GridBagLayout gridBagLayout = new GridBagLayout();
	setLayout(gridBagLayout);
	DialogUtils.addRow(new JLabel(ResourceBundleUtils.getUIString
				      ("properties.nodeInfo.fileSize.text")),
			   myFileSize, gridBagLayout, this);
	DialogUtils.addRow(new JLabel(ResourceBundleUtils.getUIString
				      ("properties.nodeInfo.duration.text")),
			   myDuration, gridBagLayout, this);
	DialogUtils.addRow(new JLabel(ResourceBundleUtils.getUIString
				      ("properties.nodeInfo.codec.text")),
			   myCodec, gridBagLayout, this);
	DialogUtils.addRow(new JLabel(ResourceBundleUtils.getUIString
				      ("properties.nodeInfo.format.text")),
			   myFormat, gridBagLayout, this);
	DialogUtils.addRow(new JLabel(ResourceBundleUtils.getUIString
				      ("properties.nodeInfo.refCount.text")),
			   myReferenceCount, gridBagLayout, this);
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
	    myFileSize.setText(tags.getValue("length"));
	    myDuration.setText(TimeFormat.getInstance()
				   .format(tags.getLongValue("duration", 0L)));
	    myCodec.setText(tags.getValue("codec"));
	    myFormat.setText(tags.getValue("bitrate") + ", "
			     + tags.getValue("samplerate"));
	    myReferenceCount.setText(String.valueOf(node.getReferenceCount()));
	    setValue(myMarked, myMarkedColor, tags.getBooleanValue("marked"),
		     false);
	} else {
	    myFileSize.setText("");
	    myDuration.setText("");
	    myCodec.setText("");
	    myFormat.setText("");
	    myReferenceCount.setText("");
	    myMarkedMixed = false;
	    int allMarked = 0;
	    for (int i = 0; i < _nodes.length; i++) {
		NodeTags tags = _nodes[i].getTags();
		int marked = tags.getBooleanValue("marked") ? 1 : 2;
		if (allMarked == 0)
		    allMarked = marked;
		else if (allMarked != marked)
		    myMarkedMixed = true;
		setValue(myMarked, myMarkedColor, allMarked == 1,
			 myMarkedMixed);
	    }
	}
    }
    
    public void ok() {
	saveValue("marked", myMarked, "1", "0", myMarkedMixed, false, false);
    }
}
