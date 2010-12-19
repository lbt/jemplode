/* FIDNodeTagsPanel - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.dialog;
import java.awt.BorderLayout;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.NodeTags;

public class FIDNodeTagsPanel extends JComponent
{
    private IFIDNode[] myNodes;
    private HashtableEditorPanel myHashtableEditorPanel;
    
    public FIDNodeTagsPanel() {
	setLayout(new BorderLayout());
	myHashtableEditorPanel = new HashtableEditorPanel();
	add(myHashtableEditorPanel);
	setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
    
    public FIDNodeTagsPanel(IFIDNode[] _nodes) {
	this();
	setNodes(_nodes);
    }
    
    public void setNodes(IFIDNode[] _nodes) {
	myNodes = _nodes;
	Hashtable allNodeTagsHashtable = new Hashtable();
	for (int i = 0; i < _nodes.length; i++) {
	    IFIDNode node = _nodes[i];
	    NodeTags nodeTags = node.getTags();
	    Properties nodeTagsHashtable
		= (Properties) nodeTags.toProperties().clone();
	    Enumeration keysEnum = allNodeTagsHashtable.keys();
	    while (keysEnum.hasMoreElements()) {
		String key = (String) keysEnum.nextElement();
		String value = (String) nodeTagsHashtable.get(key);
		put(allNodeTagsHashtable, key, value, i);
		nodeTagsHashtable.remove(key);
	    }
	    keysEnum = nodeTagsHashtable.keys();
	    while (keysEnum.hasMoreElements()) {
		Object key = keysEnum.nextElement();
		Object value = nodeTagsHashtable.get(key);
		put(allNodeTagsHashtable, key, value, i);
	    }
	}
	myHashtableEditorPanel.setHashtable(allNodeTagsHashtable);
    }
    
    protected void put(Hashtable _allNodes, Object _key, Object _value,
		       int _index) {
	Object allValue = _allNodes.get(_key);
	boolean setValue = false;
	boolean mixedValue = false;
	if (allValue == null) {
	    if (_value != null) {
		if (_index > 0)
		    mixedValue = true;
		else
		    setValue = true;
	    }
	} else if (_value == null)
	    mixedValue = true;
	else if (!_value.equals(allValue))
	    mixedValue = true;
	if (mixedValue)
	    _allNodes.put(_key, "");
	else if (setValue)
	    _allNodes.put(_key, _value);
    }
    
    public IFIDNode[] getNodes() {
	return myNodes;
    }
    
    public void ok(boolean _recursive) {
	Enumeration modifiedKeys = myHashtableEditorPanel.getModifiedKeys();
	if (modifiedKeys.hasMoreElements()) {
	    Hashtable nodeTagsHashtable
		= myHashtableEditorPanel.getHashtable();
	    while (modifiedKeys.hasMoreElements()) {
		String name = (String) modifiedKeys.nextElement();
		String value;
		if (nodeTagsHashtable.containsKey(name))
		    value = (String) nodeTagsHashtable.get(name);
		else
		    value = null;
		for (int i = 0; i < myNodes.length; i++)
		    saveValue(myNodes[i], name, value, _recursive);
	    }
	}
    }
    
    private void saveValue(IFIDNode _node, String _tagName, String _value,
			   boolean _recursive) {
	_node.getTags().setValue(_tagName, _value);
	if (_recursive && _node instanceof FIDPlaylist) {
	    FIDPlaylist playlist = (FIDPlaylist) _node;
	    IFIDNode[] nodes = playlist.toArray();
	    for (int i = 0; i < nodes.length; i++)
		saveValue(nodes[i], _tagName, _value, true);
	}
    }
}
