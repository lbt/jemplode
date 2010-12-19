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

import java.awt.BorderLayout;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.NodeTags;

/**
 * A panel that shows a generic editor for all
 * the tags for a node.
 *
 * @author Mike Schrag
 * @version $Revision: 1.2 $
 */
public class FIDNodeTagsPanel extends JComponent {
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

    for (int i = 0; i < _nodes.length; i ++ ) {
      IFIDNode node = _nodes[i];
      NodeTags nodeTags = node.getTags();

      // check for changes
      Properties nodeTagsHashtable = (Properties) nodeTags.toProperties().clone();
      Enumeration keysEnum = allNodeTagsHashtable.keys();
      while (keysEnum.hasMoreElements()) {
        String key = (String) keysEnum.nextElement();
        String value = (String) nodeTagsHashtable.get(key);
        put(allNodeTagsHashtable, key, value, i);

        // remove each one that is in the "all" set so we're left with new nodes
        nodeTagsHashtable.remove(key);
      }

      // any leftovers means that there are new tags in this node
      keysEnum = nodeTagsHashtable.keys();
      while (keysEnum.hasMoreElements()) {
        Object key = keysEnum.nextElement();
        Object value = nodeTagsHashtable.get(key);
        put(allNodeTagsHashtable, key, value, i);
      }
    }

    myHashtableEditorPanel.setHashtable(allNodeTagsHashtable);
  }

  protected void put(Hashtable _allNodes, Object _key, Object _value, int _index) {
    Object allValue = _allNodes.get(_key);

    boolean setValue = false;
    boolean mixedValue = false;
    if (allValue == null) {
      if (_value == null) {
      }
      else if (_index > 0) {
        mixedValue = true;
      }
      else {
        setValue = true;
      }
    }
    else {
      if (_value == null) {
        mixedValue = true;
      }
      else if (!_value.equals(allValue)) {
        mixedValue = true;
      }
    }

    if (mixedValue) {
      _allNodes.put(_key, "");
    }
    else if (setValue) {
      _allNodes.put(_key, _value);
    }
  }

  public IFIDNode[] getNodes() {
    return myNodes;
  }

  public void ok(boolean _recursive) {
    Enumeration modifiedKeys = myHashtableEditorPanel.getModifiedKeys();
    if (modifiedKeys.hasMoreElements()) {
      Hashtable nodeTagsHashtable = myHashtableEditorPanel.getHashtable();
      while (modifiedKeys.hasMoreElements()) {
        String name = (String) modifiedKeys.nextElement();
        String value;
        if (nodeTagsHashtable.containsKey(name)) {
          value = (String) nodeTagsHashtable.get(name);
        }
        else {
          value = null;
        }
        for (int i = 0; i < myNodes.length; i ++ ) {
          saveValue(myNodes[i], name, value, _recursive);
        }
      }
    }
  }

  private void saveValue(IFIDNode _node, String _tagName, String _value, boolean _recursive) {
    _node.getTags().setValue(_tagName, _value);
    if (_recursive && _node instanceof FIDPlaylist) {
      FIDPlaylist playlist = (FIDPlaylist) _node;
      IFIDNode[] nodes = playlist.toArray();
      for (int i = 0; i < nodes.length; i ++ ) {
        saveValue(nodes[i], _tagName, _value, true);
      }
    }
  }
}