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

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.jempeg.nodestore.IFIDNode;

import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.ui.ConfirmationPanel;
import com.inzyme.ui.DialogUtils;

/**
 * The playlist properties dialog.
 *
 * @author Mike Schrag
 * @author Daniel M. Zimmerman
 * @version $Revision: 1.7 $
 */
public class PlaylistPropertiesDialog extends AbstractPropertiesDialog {
  private FIDNodeEditorPanel myNodeEditorPanel;
  private FIDNodeInfoPanel myNodeInfoPanel;
  private FIDPlaylistPanel myPlaylistPanel;
  private FIDNodeStatisticsPanel myNodeStatsPanel;
  private FIDNodeWendyPanel myNodeWendyPanel;
  private FIDNodeTagsPanel myNodeTagsPanel;

  public PlaylistPropertiesDialog(JFrame _frame) {
    super(_frame, ResourceBundleUtils.getUIString("playlistProperties.frameTitle"));
    DialogUtils.setInitiallyFocusedComponent(this, myNodeEditorPanel.getFirstComponent());
  }

  protected ConfirmationPanel createConfirmationPanel(JTabbedPane _tabbedPane) {
    return createRecursiveConfirmationPanel(_tabbedPane);
  }

  public void setNodes(IFIDNode[] _nodes) {
    super.setNodes(_nodes);
    if (myNodeEditorPanel != null) {
      myNodeEditorPanel.setNodes(_nodes);
    }

    if (myPlaylistPanel != null) {
      myPlaylistPanel.setNodes(_nodes);
    }

    if (myNodeInfoPanel != null) {
      myNodeInfoPanel.setNodes(_nodes);
    }

    if (myNodeStatsPanel != null) {
      myNodeStatsPanel.setNodes(_nodes);
    }

    if (myNodeWendyPanel != null) {
      myNodeWendyPanel.setNodes(_nodes);
    }

    if (myNodeTagsPanel != null) {
      myNodeTagsPanel.setNodes(_nodes);
    }
  }

  public void ok(boolean _recursive) {
    if (myNodeEditorPanel != null) {
      myNodeEditorPanel.ok(_recursive);
    }

    if (myPlaylistPanel != null) {
      myPlaylistPanel.ok(_recursive);
    }

    if (myNodeInfoPanel != null) {
      myNodeInfoPanel.ok();
    }

    if (myNodeWendyPanel != null) {
      myNodeWendyPanel.ok();
    }

    if (myNodeTagsPanel != null) {
      myNodeTagsPanel.ok(_recursive);
    }
    setVisible(false);
  }

  public void cancel() {
    setVisible(false);
  }

  protected void addTabs(JTabbedPane _tabbedPane) {
    String tabs = ResourceBundleUtils.getUIString("playlistProperties.tabs");

    if (tabs.indexOf("details") != -1) {
      myNodeEditorPanel = new FIDNodeEditorPanel();
      _tabbedPane.addTab(ResourceBundleUtils.getUIString("properties.tab.details.text"), myNodeEditorPanel);
    }

    if (tabs.indexOf("playlist") != -1) {
      myPlaylistPanel = new FIDPlaylistPanel();
      _tabbedPane.addTab(ResourceBundleUtils.getUIString("properties.tab.playlist.text"), myPlaylistPanel);
    }

    if (tabs.indexOf("information") != -1) {
      myNodeInfoPanel = new FIDNodeInfoPanel();
      _tabbedPane.addTab(ResourceBundleUtils.getUIString("properties.tab.information.text"), myNodeInfoPanel);
    }

    if (tabs.indexOf("statistics") != -1) {
      myNodeStatsPanel = new FIDNodeStatisticsPanel();
      _tabbedPane.addTab(ResourceBundleUtils.getUIString("properties.tab.statistics.text"), myNodeStatsPanel);
    }

    if (tabs.indexOf("wendy") != -1) {
      myNodeWendyPanel = new FIDNodeWendyPanel();
      _tabbedPane.addTab(ResourceBundleUtils.getUIString("properties.tab.wendy.text"), myNodeWendyPanel);
    }

    if (tabs.indexOf("advanced") != -1) {
      myNodeTagsPanel = new FIDNodeTagsPanel();
      _tabbedPane.addTab(ResourceBundleUtils.getUIString("properties.tab.advanced.text"), myNodeTagsPanel);
    }
  }
}