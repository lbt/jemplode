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

import java.awt.Color;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.jempeg.nodestore.DatabaseTags;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.NodeTags;
import org.jempeg.nodestore.model.NodeTag;
import org.jempeg.tags.Genre;

import com.inzyme.ui.DialogUtils;
import com.inzyme.ui.SelectAllTextField;

/**
 * A panel that allows the editing of the various
 * common NodeTags (title, artist, source, genre, etc.)
 *
 * @author Mike Schrag
 * @version $Revision: 1.6 $
 */
public class FIDNodeEditorPanel extends AbstractFIDNodePanel {
  private Color myTextFieldColor;
  private Color myComboBoxColor;

  private JTextField myTitle;
  private JTextField myArtist;
  private JTextField mySource;
  private JComboBox myGenre;
  private JTextField myYear;
  private JTextField myTrackNr;
  private JTextField myPIN;
  private JTextField myComment;

  private boolean myTitleMixed;
  private boolean myArtistMixed;
  private boolean mySourceMixed;
  private boolean myGenreMixed;
  private boolean myYearMixed;
  private boolean myTrackNrMixed;
  private boolean myPINMixed;
  private boolean myCommentMixed;

  public FIDNodeEditorPanel() {
    super();

    myTitle = new SelectAllTextField();
    myArtist = new SelectAllTextField();
    mySource = new SelectAllTextField();
    myYear = new SelectAllTextField();
    myTrackNr = new SelectAllTextField();
    myPIN = new SelectAllTextField();
    myComment = new SelectAllTextField();

    myGenre = new JComboBox(Genre.getSortedGenres());
    myGenre.setMaximumRowCount(5);
    myGenre.setEditable(true);

    myTextFieldColor = myTitle.getBackground();
    myComboBoxColor = myGenre.getBackground();

    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    DialogUtils.addRow(new JLabel(NodeTag.TITLE_TAG.getDescription()), myTitle, gridBagLayout, this);
    DialogUtils.addRow(new JLabel(NodeTag.ARTIST_TAG.getDescription()), myArtist, gridBagLayout, this);
    DialogUtils.addRow(new JLabel(NodeTag.SOURCE_TAG.getDescription()), mySource, gridBagLayout, this);
    DialogUtils.addRow(new JLabel(NodeTag.GENRE_TAG.getDescription()), myGenre, gridBagLayout, this);
    DialogUtils.addRow(new JLabel(NodeTag.YEAR_TAG.getDescription()), myYear, gridBagLayout, this);
    DialogUtils.addRow(new JLabel(NodeTag.TRACKNR_TAG.getDescription()), myTrackNr, gridBagLayout, this);
    DialogUtils.addRow(new JLabel(NodeTag.getNodeTag(DatabaseTags.PIN_TAG).getDescription()), myPIN, gridBagLayout, this);
    DialogUtils.addRow(new JLabel(NodeTag.getNodeTag(DatabaseTags.COMMENT_TAG).getDescription()), myComment, gridBagLayout, this);

    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
  }

  public FIDNodeEditorPanel(IFIDNode[] _nodes) {
    this();
    setNodes(_nodes);
  }

  public JComponent getFirstComponent() {
    return myTitle;
  }

  public void setNodes(IFIDNode[] _nodes) {
    super.setNodes(_nodes);

    myTitleMixed = false;
    myArtistMixed = false;
    mySourceMixed = false;
    myGenreMixed = false;
    myYearMixed = false;
    myTrackNrMixed = false;
    myPINMixed = false;
    myCommentMixed = false;

    for (int i = 0; i < _nodes.length; i ++ ) {
      IFIDNode node = _nodes[i];
      fillInFields(node, i, false);
    }
  }

  private void fillInFields(IFIDNode _node, int _index, boolean _recursive) {
    NodeTags tags = _node.getTags();
    String title = _node.getTitle();
    String artist = tags.getValue(DatabaseTags.ARTIST_TAG);
    String source = tags.getValue(DatabaseTags.SOURCE_TAG);
    String year = tags.getValue(DatabaseTags.YEAR_TAG);
    String tracknr = tags.getValue(DatabaseTags.TRACKNR_TAG);
    //		String pin = tags.getValue(DatabaseTags.PIN_TAG);
    String genre = Genre.getGenre(tags.getValue(DatabaseTags.GENRE_TAG));
    boolean found = false;
    for (int i = 0; !found && i < myGenre.getItemCount(); i ++ ) {
      Object genreItem = myGenre.getItemAt(i);
      found = genreItem.equals(genre);
    }
    if (!found) {
      myGenre.addItem(genre);
    }
    String comment = tags.getValue(DatabaseTags.COMMENT_TAG);

    if (_index != 0 || _recursive) {
      if (!_recursive) {
        myTitleMixed = myTitleMixed || !title.equals(myTitle.getText());
        myTrackNrMixed = myTrackNrMixed || !tracknr.equals(myTrackNr.getText());
        myCommentMixed = myCommentMixed || !comment.equals(myComment.getText());
      }
      myArtistMixed = myArtistMixed || !artist.equals(myArtist.getText());
      mySourceMixed = mySourceMixed || !source.equals(mySource.getText());
      myYearMixed = myYearMixed || !year.equals(myYear.getText());
      myGenreMixed = myGenreMixed || !genre.equals(myGenre.getSelectedItem());
    }

    if (!_recursive) {
      setValue(myTitle, myTextFieldColor, title, myTitleMixed);
      setValue(myTrackNr, myTextFieldColor, tracknr, myTrackNrMixed);
      setValue(myComment, myTextFieldColor, comment, myCommentMixed);
    }

    setValue(myArtist, myTextFieldColor, artist, myArtistMixed);
    setValue(mySource, myTextFieldColor, source, mySourceMixed);
    setValue(myYear, myTextFieldColor, year, myYearMixed);
    setValue(myGenre, myComboBoxColor, genre, myGenreMixed);

    if (_node instanceof FIDPlaylist) {
      FIDPlaylist playlist = (FIDPlaylist) _node;
      int size = playlist.getSize();
      for (int i = 0; i < size; i ++ ) {
        fillInFields(playlist.getNodeAt(i), i, true);
      }
    }
  }

  public void ok(boolean _recursive) {
    saveValue(DatabaseTags.TITLE_TAG, myTitle, myTitleMixed, false, false);
    saveValue(DatabaseTags.ARTIST_TAG, myArtist, myArtistMixed, _recursive, _recursive);
    saveValue(DatabaseTags.SOURCE_TAG, mySource, mySourceMixed, _recursive, _recursive);
    saveValue(DatabaseTags.YEAR_TAG, myYear, myYearMixed, _recursive, _recursive);
    saveValue(DatabaseTags.TRACKNR_TAG, myTrackNr, myTrackNrMixed, false, false);
    saveValue(DatabaseTags.PIN_TAG, myPIN, myPINMixed, false, false);
    saveValue(DatabaseTags.GENRE_TAG, myGenre, myGenreMixed, _recursive, _recursive);
    saveValue(DatabaseTags.COMMENT_TAG, myComment, myCommentMixed, false, false);
  }
}