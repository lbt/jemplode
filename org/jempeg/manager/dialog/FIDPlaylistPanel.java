/* FIDPlaylistPanel - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.dialog;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.inzyme.text.ResourceBundleUtils;

import org.jempeg.manager.ui.JTriStateButton;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.NodeTags;
import org.jempeg.nodestore.model.NodeTag;

public class FIDPlaylistPanel extends AbstractFIDNodePanel
{
    private Color myCheckBoxColor;
    private Color myRadioButtonColor;
    private Color myTextFieldColor;
    private JTriStateButton myAlwaysRandomize;
    private JTriStateButton myAutoRepeat;
    private JTriStateButton myIgnoreChild;
    private JRadioButton myEntirePlaylistRB;
    private JRadioButton myNumRandomRB;
    private JRadioButton myPercentRandomRB;
    private JRadioButton myInvisibleRB;
    private JTextField myNumRandom;
    private JTextField myPercentRandom;
    private JComboBox mySortTagCB;
    private boolean myAlwaysRandomizeMixed;
    private boolean myAutoRepeatMixed;
    private boolean myIgnoreChildMixed;
    private boolean myEntirePlaylistMixed;
    private boolean myNumRandomMixed;
    private boolean myPercentRandomMixed;
    
    public FIDPlaylistPanel() {
	NodeTag[] nodeTags = NodeTag.getNodeTags();
	String[] nodeTagStrs = new String[nodeTags.length + 1];
	nodeTagStrs[0] = "";
	for (int i = 0; i < nodeTags.length; i++)
	    nodeTagStrs[i + 1] = nodeTags[i].getName();
	mySortTagCB = new JComboBox(nodeTagStrs);
	myAlwaysRandomize
	    = (new JTriStateButton
	       (ResourceBundleUtils.getUIString
		("playlistProperties.playlist.alwaysRandomize.text")));
	myAutoRepeat
	    = (new JTriStateButton
	       (ResourceBundleUtils.getUIString
		("playlistProperties.playlist.automaticallyRepeat.text")));
	myIgnoreChild = (new JTriStateButton
			 (ResourceBundleUtils.getUIString
			  ("playlistProperties.playlist.ignoreAsChild.text")));
	myEntirePlaylistRB
	    = (new JRadioButton
	       (ResourceBundleUtils.getUIString
		("playlistProperties.playlist.entirePlaylist.text")));
	myNumRandomRB
	    = (new JRadioButton
	       (ResourceBundleUtils.getUIString
		("playlistProperties.playlist.tracksAtRandom.text")));
	myPercentRandomRB
	    = (new JRadioButton
	       (ResourceBundleUtils.getUIString
		("playlistProperties.playlist.percentOfTracksAtRandom.text")));
	myNumRandom = new JTextField();
	myNumRandom.setColumns(3);
	myPercentRandom = new JTextField();
	myPercentRandom.setColumns(3);
	ButtonGroup bg = new ButtonGroup();
	bg.add(myInvisibleRB = new JRadioButton());
	bg.add(myEntirePlaylistRB);
	bg.add(myNumRandomRB);
	bg.add(myPercentRandomRB);
	myCheckBoxColor = myAlwaysRandomize.getForeground();
	myRadioButtonColor = myEntirePlaylistRB.getForeground();
	myTextFieldColor = myNumRandom.getBackground();
	setLayout(new BorderLayout());
	JPanel layoutPanel = new JPanel();
	GridLayout gridLayout = new GridLayout(1, 2);
	layoutPanel.setLayout(gridLayout);
	JPanel checkboxPanel = new JPanel();
	GridLayout checkboxLayout = new GridLayout(0, 1);
	checkboxPanel.setLayout(checkboxLayout);
	checkboxPanel.add(myAlwaysRandomize);
	checkboxPanel.add(myAutoRepeat);
	checkboxPanel.add(myIgnoreChild);
	JPanel radiobuttonPanel = new JPanel();
	radiobuttonPanel.setBorder
	    (BorderFactory.createCompoundBorder
	     ((BorderFactory.createTitledBorder
	       (ResourceBundleUtils
		    .getUIString("playlistProperties.playlist.panelTitle"))),
	      BorderFactory.createEmptyBorder(10, 10, 10, 10)));
	GridLayout radiobuttonLayout = new GridLayout(0, 1);
	radiobuttonPanel.setLayout(radiobuttonLayout);
	radiobuttonPanel.add(myEntirePlaylistRB);
	JPanel numRandomPanel = new JPanel();
	FlowLayout fl1 = new FlowLayout(0, 0, 0);
	numRandomPanel.setLayout(fl1);
	numRandomPanel.add(myNumRandomRB);
	numRandomPanel.add(myNumRandom);
	radiobuttonPanel.add(numRandomPanel);
	JPanel percentRandomPanel = new JPanel();
	FlowLayout fl2 = new FlowLayout(0, 0, 0);
	percentRandomPanel.setLayout(fl2);
	percentRandomPanel.add(myPercentRandomRB);
	percentRandomPanel.add(myPercentRandom);
	radiobuttonPanel.add(percentRandomPanel);
	layoutPanel.add(checkboxPanel);
	layoutPanel.add(radiobuttonPanel);
	JPanel sortTagLabelPanel = new JPanel();
	sortTagLabelPanel.setLayout(new BorderLayout());
	sortTagLabelPanel.add
	    (new JLabel(ResourceBundleUtils.getUIString
			("playlistProperties.playlist.sortTag.text")),
	     "West");
	sortTagLabelPanel.add(mySortTagCB, "Center");
	add(layoutPanel, "Center");
	add(sortTagLabelPanel, "South");
	setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
    
    public FIDPlaylistPanel(IFIDNode[] _nodes) {
	this();
	setNodes(_nodes);
    }
    
    public void setNodes(IFIDNode[] _nodes) {
	super.setNodes(_nodes);
	myAlwaysRandomizeMixed = false;
	myAutoRepeatMixed = false;
	myIgnoreChildMixed = false;
	myEntirePlaylistMixed = false;
	myNumRandomMixed = false;
	myPercentRandomMixed = false;
	boolean randomMixed = false;
	for (int i = 0; i < _nodes.length; i++) {
	    IFIDNode node = _nodes[i];
	    boolean alwaysRandomize = node.hasOption(8);
	    boolean autoRepeat = node.hasOption(16);
	    boolean ignoreChild = node.hasOption(32);
	    NodeTags tags = node.getTags();
	    int numRandom = tags.getIntValue("pickn", 0);
	    int percentRandom = tags.getIntValue("pickpercent", 0);
	    boolean entirePlaylist = numRandom == 0 && percentRandom == 0;
	    boolean numRandomSelected = numRandom > 0;
	    boolean percentRandomSelected = percentRandom > 0;
	    if (i != 0) {
		myAlwaysRandomizeMixed
		    = (myAlwaysRandomizeMixed
		       || alwaysRandomize != myAlwaysRandomize.isSelected());
		myAutoRepeatMixed
		    = (myAutoRepeatMixed
		       || autoRepeat != myAutoRepeat.isSelected());
		myIgnoreChildMixed
		    = (myIgnoreChildMixed
		       || ignoreChild != myIgnoreChild.isSelected());
		myEntirePlaylistMixed
		    = (myEntirePlaylistMixed
		       || entirePlaylist != myEntirePlaylistRB.isSelected());
		myNumRandomMixed
		    = myNumRandomMixed || !String.valueOf(numRandom)
					       .equals(myNumRandom.getText());
		myPercentRandomMixed
		    = (myPercentRandomMixed
		       || !String.valueOf(percentRandom)
			       .equals(myPercentRandom.getText()));
	    }
	    randomMixed = (myEntirePlaylistMixed || myNumRandomMixed
			   || myPercentRandomMixed);
	    setValue(myAlwaysRandomize, myCheckBoxColor, alwaysRandomize,
		     myAlwaysRandomizeMixed);
	    setValue(myAutoRepeat, myCheckBoxColor, autoRepeat,
		     myAutoRepeatMixed);
	    setValue(myIgnoreChild, myCheckBoxColor, ignoreChild,
		     myIgnoreChildMixed);
	    setValue(myEntirePlaylistRB, myRadioButtonColor,
		     !randomMixed && entirePlaylist, randomMixed);
	    setValue(myNumRandomRB, myRadioButtonColor,
		     !randomMixed && numRandomSelected, randomMixed);
	    setValue(myNumRandom, myTextFieldColor, String.valueOf(numRandom),
		     myNumRandomMixed);
	    setValue(myPercentRandomRB, myRadioButtonColor,
		     !randomMixed && percentRandomSelected, randomMixed);
	    setValue(myPercentRandom, myTextFieldColor,
		     String.valueOf(percentRandom), myPercentRandomMixed);
	    setValue(mySortTagCB, myTextFieldColor, tags.getValue("sort"),
		     false);
	}
	if (randomMixed)
	    myInvisibleRB.setSelected(true);
    }
    
    public void ok(boolean _recursive) {
	saveValue(8, myAlwaysRandomize, myAlwaysRandomizeMixed);
	saveValue(16, myAutoRepeat, myAutoRepeatMixed);
	saveValue(32, myIgnoreChild, myIgnoreChildMixed);
	saveValue("sort", mySortTagCB, false, false, _recursive);
	if (!myInvisibleRB.isSelected()) {
	    int numRandom = 0;
	    int percentRandom = 0;
	    do {
		if (myNumRandomRB.isSelected()) {
		    try {
			numRandom = Integer.parseInt(myNumRandom.getText());
			break;
		    } catch (NumberFormatException numberformatexception) {
			break;
		    }
		}
		if (myPercentRandomRB.isSelected()) {
		    try {
			percentRandom
			    = Integer.parseInt(myPercentRandom.getText());
		    } catch (NumberFormatException numberformatexception) {
			/* empty */
		    }
		}
	    } while (false);
	    saveValue("pickn", String.valueOf(numRandom),
		      myNumRandomMixed || myPercentRandomMixed, false,
		      _recursive);
	    saveValue("pickpercent", String.valueOf(percentRandom),
		      myNumRandomMixed || myPercentRandomMixed, false,
		      _recursive);
	}
    }
}
