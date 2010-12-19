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
package org.jempeg.manager.ui;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import com.inzyme.container.IMutableTypeContainer;
import com.inzyme.text.ResourceBundleUtils;

/**
* Playlist tree cell renderer implements the colorizing of dirty nodes
*
* @author Mike Schrag
* @version $Revision: 1.1 $
*/
public class PlaylistTreeCellRenderer implements TreeCellRenderer { //extends DefaultTreeCellRenderer {
  private TreeCellRenderer myOriginalRenderer;
	
  public static Icon[] ICONS = new Icon[] {
    new ImageIcon(PlaylistTreeCellRenderer.class.getResource(ResourceBundleUtils.getUIString("fidNodeType.root.icon"))),
		new ImageIcon(PlaylistTreeCellRenderer.class.getResource(ResourceBundleUtils.getUIString("fidNodeType.folder.icon"))),
		new ImageIcon(PlaylistTreeCellRenderer.class.getResource(ResourceBundleUtils.getUIString("fidNodeType.playlist.icon"))),
		new ImageIcon(PlaylistTreeCellRenderer.class.getResource(ResourceBundleUtils.getUIString("fidNodeType.tune.icon"))),
		new ImageIcon(PlaylistTreeCellRenderer.class.getResource(ResourceBundleUtils.getUIString("fidNodeType.tunes.icon"))),
		new ImageIcon(PlaylistTreeCellRenderer.class.getResource(ResourceBundleUtils.getUIString("fidNodeType.genre.icon"))),
		new ImageIcon(PlaylistTreeCellRenderer.class.getResource(ResourceBundleUtils.getUIString("fidNodeType.genres.icon"))),
		new ImageIcon(PlaylistTreeCellRenderer.class.getResource(ResourceBundleUtils.getUIString("fidNodeType.year.icon"))),
		new ImageIcon(PlaylistTreeCellRenderer.class.getResource(ResourceBundleUtils.getUIString("fidNodeType.years.icon"))),
		new ImageIcon(PlaylistTreeCellRenderer.class.getResource(ResourceBundleUtils.getUIString("fidNodeType.album.icon"))),
		new ImageIcon(PlaylistTreeCellRenderer.class.getResource(ResourceBundleUtils.getUIString("fidNodeType.albums.icon"))),
		new ImageIcon(PlaylistTreeCellRenderer.class.getResource(ResourceBundleUtils.getUIString("fidNodeType.artist.icon"))),
		new ImageIcon(PlaylistTreeCellRenderer.class.getResource(ResourceBundleUtils.getUIString("fidNodeType.artists.icon"))),
		new ImageIcon(PlaylistTreeCellRenderer.class.getResource(ResourceBundleUtils.getUIString("fidNodeType.playlist.icon")))
  };

	public PlaylistTreeCellRenderer(TreeCellRenderer _originalRenderer) {
    myOriginalRenderer = _originalRenderer;
	}

	public Component getTreeCellRendererComponent(JTree _tree, Object _value, boolean _sel, boolean _expanded, boolean _leaf, int _row, boolean _hasFocus) {
    Component comp = myOriginalRenderer.getTreeCellRendererComponent(_tree, _value, _sel, _expanded, _leaf, _row, _hasFocus);
    if (comp instanceof JLabel && _value instanceof IMutableTypeContainer) {
      IMutableTypeContainer playlistTreeNode = (IMutableTypeContainer)_value;
      int type = playlistTreeNode.getType();
      JLabel label = (JLabel)comp;
      label.setIcon(ICONS[type]);
    }
		
		NodeColorizer.colorize(comp, _value, comp.getForeground(), _sel);
		return comp;
	}
}
 
	
