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

import org.jempeg.manager.ui.JTriStateButton;
import org.jempeg.nodestore.DatabaseTags;
import org.jempeg.nodestore.FIDConstants;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.NodeTags;
import org.jempeg.nodestore.model.NodeTag;

import com.inzyme.text.ResourceBundleUtils;

/**
 * A panel that allows the editing of the various
 * common NodeTags (title, artist, source, genre, etc.)
 *
 * This has got to be the worst code I've written -- It's
 * just plain bizarre .... But I wrote it fast, so
 * bear with me.
 * 
 * @author Mike Schrag
 * @version $Revision: 1.3 $
 */
public class FIDPlaylistPanel extends AbstractFIDNodePanel {
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
    super();

    NodeTag[] nodeTags = NodeTag.getNodeTags();
    String[] nodeTagStrs = new String[nodeTags.length + 1];
    nodeTagStrs[0] = "";
    for (int i = 0; i < nodeTags.length; i ++ ) {
      nodeTagStrs[i + 1] = nodeTags[i].getName();
    }

    mySortTagCB = new JComboBox(nodeTagStrs);
    myAlwaysRandomize = new JTriStateButton(ResourceBundleUtils.getUIString("playlistProperties.playlist.alwaysRandomize.text"));
    myAutoRepeat = new JTriStateButton(ResourceBundleUtils.getUIString("playlistProperties.playlist.automaticallyRepeat.text"));
    myIgnoreChild = new JTriStateButton(ResourceBundleUtils.getUIString("playlistProperties.playlist.ignoreAsChild.text"));

    myEntirePlaylistRB = new JRadioButton(ResourceBundleUtils.getUIString("playlistProperties.playlist.entirePlaylist.text"));
    myNumRandomRB = new JRadioButton(ResourceBundleUtils.getUIString("playlistProperties.playlist.tracksAtRandom.text"));
    myPercentRandomRB = new JRadioButton(ResourceBundleUtils.getUIString("playlistProperties.playlist.percentOfTracksAtRandom.text"));

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
    radiobuttonPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(ResourceBundleUtils.getUIString("playlistProperties.playlist.panelTitle")), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
    GridLayout radiobuttonLayout = new GridLayout(0, 1);
    radiobuttonPanel.setLayout(radiobuttonLayout);

    radiobuttonPanel.add(myEntirePlaylistRB);

    JPanel numRandomPanel = new JPanel();
    FlowLayout fl1 = new FlowLayout(FlowLayout.LEFT, 0, 0);
    numRandomPanel.setLayout(fl1);
    numRandomPanel.add(myNumRandomRB);
    numRandomPanel.add(myNumRandom);
    radiobuttonPanel.add(numRandomPanel);

    JPanel percentRandomPanel = new JPanel();
    FlowLayout fl2 = new FlowLayout(FlowLayout.LEFT, 0, 0);
    percentRandomPanel.setLayout(fl2);
    percentRandomPanel.add(myPercentRandomRB);
    percentRandomPanel.add(myPercentRandom);
    radiobuttonPanel.add(percentRandomPanel);

    layoutPanel.add(checkboxPanel);
    layoutPanel.add(radiobuttonPanel);

    JPanel sortTagLabelPanel = new JPanel();
    sortTagLabelPanel.setLayout(new BorderLayout());
    sortTagLabelPanel.add(new JLabel(ResourceBundleUtils.getUIString("playlistProperties.playlist.sortTag.text")), BorderLayout.WEST);
    sortTagLabelPanel.add(mySortTagCB, BorderLayout.CENTER);

    add(layoutPanel, BorderLayout.CENTER);
    add(sortTagLabelPanel, BorderLayout.SOUTH);

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
    for (int i = 0; i < _nodes.length; i ++ ) {
      IFIDNode node = _nodes[i];

      boolean alwaysRandomize = node.hasOption(FIDConstants.PLAYLIST_OPTION_RANDOMISE);
      boolean autoRepeat = node.hasOption(FIDConstants.PLAYLIST_OPTION_LOOP);
      boolean ignoreChild = node.hasOption(FIDConstants.PLAYLIST_OPTION_IGNOREASCHILD);

      NodeTags tags = node.getTags();
      int numRandom = tags.getIntValue(DatabaseTags.PICKN_TAG, 0);
      int percentRandom = tags.getIntValue(DatabaseTags.PICKPERCENT_TAG, 0);

      boolean entirePlaylist = (numRandom == 0 && percentRandom == 0);
      boolean numRandomSelected = numRandom > 0;
      boolean percentRandomSelected = percentRandom > 0;

      if (i != 0) {
        myAlwaysRandomizeMixed = myAlwaysRandomizeMixed || alwaysRandomize != myAlwaysRandomize.isSelected();
        myAutoRepeatMixed = myAutoRepeatMixed || autoRepeat != myAutoRepeat.isSelected();
        myIgnoreChildMixed = myIgnoreChildMixed || ignoreChild != myIgnoreChild.isSelected();

        myEntirePlaylistMixed = myEntirePlaylistMixed || entirePlaylist != myEntirePlaylistRB.isSelected();
        myNumRandomMixed = myNumRandomMixed || !String.valueOf(numRandom).equals(myNumRandom.getText());
        myPercentRandomMixed = myPercentRandomMixed || !String.valueOf(percentRandom).equals(myPercentRandom.getText());
      }

      randomMixed = myEntirePlaylistMixed || myNumRandomMixed || myPercentRandomMixed;

      setValue(myAlwaysRandomize, myCheckBoxColor, alwaysRandomize, myAlwaysRandomizeMixed);
      setValue(myAutoRepeat, myCheckBoxColor, autoRepeat, myAutoRepeatMixed);
      setValue(myIgnoreChild, myCheckBoxColor, ignoreChild, myIgnoreChildMixed);
      setValue(myEntirePlaylistRB, myRadioButtonColor, !randomMixed && entirePlaylist, randomMixed);
      setValue(myNumRandomRB, myRadioButtonColor, !randomMixed && numRandomSelected, randomMixed);
      setValue(myNumRandom, myTextFieldColor, String.valueOf(numRandom), myNumRandomMixed);
      setValue(myPercentRandomRB, myRadioButtonColor, !randomMixed && percentRandomSelected, randomMixed);
      setValue(myPercentRandom, myTextFieldColor, String.valueOf(percentRandom), myPercentRandomMixed);
      setValue(mySortTagCB, myTextFieldColor, tags.getValue(DatabaseTags.SORT_TAG), false);
    }

    if (randomMixed) {
      myInvisibleRB.setSelected(true);
    }
  }

  public void ok(boolean _recursive) {
    saveValue(FIDConstants.PLAYLIST_OPTION_RANDOMISE, myAlwaysRandomize, myAlwaysRandomizeMixed);
    saveValue(FIDConstants.PLAYLIST_OPTION_LOOP, myAutoRepeat, myAutoRepeatMixed);
    saveValue(FIDConstants.PLAYLIST_OPTION_IGNOREASCHILD, myIgnoreChild, myIgnoreChildMixed);
    saveValue(DatabaseTags.SORT_TAG, mySortTagCB, false, false, _recursive);

    if (myInvisibleRB.isSelected()) {
      // nothing changed
    }
    else {
      int numRandom = 0;
      int percentRandom = 0;
      if (myNumRandomRB.isSelected()) {
        try {
          numRandom = Integer.parseInt(myNumRandom.getText());
        }
        catch (NumberFormatException e) {
        }
      }
      else if (myPercentRandomRB.isSelected()) {
        try {
          percentRandom = Integer.parseInt(myPercentRandom.getText());
        }
        catch (NumberFormatException e) {
        }
      }
      saveValue(DatabaseTags.PICKN_TAG, String.valueOf(numRandom), myNumRandomMixed || myPercentRandomMixed, false, _recursive);
      saveValue(DatabaseTags.PICKPERCENT_TAG, String.valueOf(percentRandom), myNumRandomMixed || myPercentRandomMixed, false, _recursive);
    }
  }
}