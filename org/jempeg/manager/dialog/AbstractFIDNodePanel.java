/* AbstractFIDNodePanel - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.dialog;
import java.awt.Color;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.jempeg.manager.ui.JTriStateButton;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;

public abstract class AbstractFIDNodePanel extends JComponent
{
    public static final Color MIXED_COLOR = new Color(230, 230, 200);
    private IFIDNode[] myNodes;
    
    public AbstractFIDNodePanel() {
	/* empty */
    }
    
    public AbstractFIDNodePanel(IFIDNode[] _nodes) {
	this();
	setNodes(_nodes);
    }
    
    public void setNodes(IFIDNode[] _nodes) {
	myNodes = _nodes;
    }
    
    public IFIDNode[] getNodes() {
	return myNodes;
    }
    
    protected void saveValue(String _tagName, JTextField _comp, boolean _mixed,
			     boolean _recursiveTunes,
			     boolean _recursivePlaylists) {
	String value = _comp.getText();
	saveValue(_tagName, value, _mixed, _recursiveTunes,
		  _recursivePlaylists);
    }
    
    protected void saveValue(String _tagName, JComboBox _comp, boolean _mixed,
			     boolean _recursiveTunes,
			     boolean _recursivePlaylists) {
	String selectedItem = (String) _comp.getSelectedItem();
	saveValue(_tagName, selectedItem, _mixed, _recursiveTunes,
		  _recursivePlaylists);
    }
    
    protected void saveValue(String _tagName, JTriStateButton _comp,
			     String _yesValue, String _noValue, boolean _mixed,
			     boolean _recursiveTunes,
			     boolean _recursivePlaylists) {
	int triStateValue = _comp.getTriState();
	if (triStateValue == 1)
	    saveValue(_tagName, _yesValue, _mixed, _recursiveTunes,
		      _recursivePlaylists);
	else if (triStateValue == 2)
	    saveValue(_tagName, _noValue, _mixed, _recursiveTunes,
		      _recursivePlaylists);
    }
    
    protected void saveValue(String _tagName, String _value, boolean _mixed,
			     boolean _recursiveTunes,
			     boolean _recursivePlaylists) {
	if (!_mixed || !_value.equals("")) {
	    for (int i = 0; i < myNodes.length; i++)
		saveValue(myNodes[i], _tagName, _value, _mixed,
			  _recursiveTunes, _recursivePlaylists);
	}
    }
    
    protected void saveValue(IFIDNode _node, String _tagName, String _value,
			     boolean _mixed, boolean _recursiveTunes,
			     boolean _recursivePlaylists) {
	if (!_mixed || !_value.equals("")) {
	    if ((_recursivePlaylists && _node instanceof FIDPlaylist
		 || _recursiveTunes && !(_node instanceof FIDPlaylist)
		 || !_recursiveTunes && !_recursivePlaylists)
		&& (!"sort".equals(_tagName) || !(_node instanceof FIDPlaylist)
		    || !((FIDPlaylist) _node).isSoup()))
		_node.getTags().setValue(_tagName, _value);
	    if ((_recursiveTunes || _recursivePlaylists)
		&& _node instanceof FIDPlaylist) {
		FIDPlaylist playlist = (FIDPlaylist) _node;
		IFIDNode[] nodes = playlist.toArray();
		for (int i = 0; i < nodes.length; i++)
		    saveValue(nodes[i], _tagName, _value, _mixed,
			      _recursiveTunes, _recursivePlaylists);
	    }
	}
    }
    
    protected void saveValue(int _optionNum, JTriStateButton _comp,
			     boolean _mixed) {
	if (!_mixed || _comp.getTriState() != 0) {
	    for (int i = 0; i < myNodes.length; i++)
		myNodes[i].setOption(_optionNum, _comp.getTriState() == 1);
	}
    }
    
    protected void setValue(JTextField _comp, Color _originalColor,
			    String _value, boolean _mixed) {
	if (_mixed) {
	    _comp.setBackground(MIXED_COLOR);
	    _comp.setText("");
	} else {
	    _comp.setBackground(_originalColor);
	    _comp.setText(_value);
	}
    }
    
    protected void setValue(JComboBox _comp, Color _originalColor,
			    String _value, boolean _mixed) {
	if (_mixed) {
	    _comp.setBackground(MIXED_COLOR);
	    _comp.setSelectedItem("");
	} else {
	    _comp.setBackground(_originalColor);
	    _comp.setSelectedItem(_value);
	}
    }
    
    protected void setValue(JTriStateButton _comp, Color _originalColor,
			    boolean _selected, boolean _mixed) {
	if (_mixed) {
	    _comp.setForeground(MIXED_COLOR);
	    _comp.setActLikeCheckBox(false);
	    _comp.setTriState(0);
	} else {
	    _comp.setForeground(_originalColor);
	    _comp.setActLikeCheckBox(true);
	    _comp.setTriState(_selected ? 1 : 2);
	}
    }
    
    protected void setValue(JRadioButton _comp, Color _originalColor,
			    boolean _selected, boolean _mixed) {
	if (_mixed) {
	    _comp.setForeground(MIXED_COLOR);
	    _comp.setSelected(false);
	} else {
	    _comp.setForeground(_originalColor);
	    _comp.setSelected(_selected);
	}
    }
}
