/* FIDNodeEditorPanel - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.dialog;
import java.awt.Color;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.inzyme.ui.DialogUtils;
import com.inzyme.ui.SelectAllTextField;

import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.NodeTags;
import org.jempeg.nodestore.model.NodeTag;
import org.jempeg.tags.Genre;

public class FIDNodeEditorPanel extends AbstractFIDNodePanel
{
    private Color myTextFieldColor;
    private Color myComboBoxColor;
    private JTextField myTitle = new SelectAllTextField();
    private JTextField myArtist = new SelectAllTextField();
    private JTextField mySource = new SelectAllTextField();
    private JComboBox myGenre;
    private JTextField myYear = new SelectAllTextField();
    private JTextField myTrackNr = new SelectAllTextField();
    private JTextField myPIN = new SelectAllTextField();
    private JTextField myComment = new SelectAllTextField();
    private boolean myTitleMixed;
    private boolean myArtistMixed;
    private boolean mySourceMixed;
    private boolean myGenreMixed;
    private boolean myYearMixed;
    private boolean myTrackNrMixed;
    private boolean myPINMixed;
    private boolean myCommentMixed;
    
    public FIDNodeEditorPanel() {
	myGenre = new JComboBox(Genre.getSortedGenres());
	myGenre.setMaximumRowCount(5);
	myGenre.setEditable(true);
	myTextFieldColor = myTitle.getBackground();
	myComboBoxColor = myGenre.getBackground();
	GridBagLayout gridBagLayout = new GridBagLayout();
	setLayout(gridBagLayout);
	DialogUtils.addRow(new JLabel(NodeTag.TITLE_TAG.getDescription()),
			   myTitle, gridBagLayout, this);
	DialogUtils.addRow(new JLabel(NodeTag.ARTIST_TAG.getDescription()),
			   myArtist, gridBagLayout, this);
	DialogUtils.addRow(new JLabel(NodeTag.SOURCE_TAG.getDescription()),
			   mySource, gridBagLayout, this);
	DialogUtils.addRow(new JLabel(NodeTag.GENRE_TAG.getDescription()),
			   myGenre, gridBagLayout, this);
	DialogUtils.addRow(new JLabel(NodeTag.YEAR_TAG.getDescription()),
			   myYear, gridBagLayout, this);
	DialogUtils.addRow(new JLabel(NodeTag.TRACKNR_TAG.getDescription()),
			   myTrackNr, gridBagLayout, this);
	DialogUtils.addRow(new JLabel(NodeTag.getNodeTag("pin")
					  .getDescription()),
			   myPIN, gridBagLayout, this);
	DialogUtils.addRow(new JLabel(NodeTag.getNodeTag("comment")
					  .getDescription()),
			   myComment, gridBagLayout, this);
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
	for (int i = 0; i < _nodes.length; i++) {
	    IFIDNode node = _nodes[i];
	    fillInFields(node, i, false);
	}
    }
    
    private void fillInFields(IFIDNode _node, int _index, boolean _recursive) {
	NodeTags tags = _node.getTags();
	String title = _node.getTitle();
	String artist = tags.getValue("artist");
	String source = tags.getValue("source");
	String year = tags.getValue("year");
	String tracknr = tags.getValue("tracknr");
	String genre = Genre.getGenre(tags.getValue("genre"));
	boolean found = false;
	for (int i = 0; !found && i < myGenre.getItemCount(); i++) {
	    Object genreItem = myGenre.getItemAt(i);
	    found = genreItem.equals(genre);
	}
	if (!found)
	    myGenre.addItem(genre);
	String comment = tags.getValue("comment");
	if (_index != 0 || _recursive) {
	    if (!_recursive) {
		myTitleMixed
		    = myTitleMixed || !title.equals(myTitle.getText());
		myTrackNrMixed
		    = myTrackNrMixed || !tracknr.equals(myTrackNr.getText());
		myCommentMixed
		    = myCommentMixed || !comment.equals(myComment.getText());
	    }
	    myArtistMixed
		= myArtistMixed || !artist.equals(myArtist.getText());
	    mySourceMixed
		= mySourceMixed || !source.equals(mySource.getText());
	    myYearMixed = myYearMixed || !year.equals(myYear.getText());
	    myGenreMixed
		= myGenreMixed || !genre.equals(myGenre.getSelectedItem());
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
	    for (int i = 0; i < size; i++)
		fillInFields(playlist.getNodeAt(i), i, true);
	}
    }
    
    public void ok(boolean _recursive) {
	saveValue("title", myTitle, myTitleMixed, false, false);
	saveValue("artist", myArtist, myArtistMixed, _recursive, _recursive);
	saveValue("source", mySource, mySourceMixed, _recursive, _recursive);
	saveValue("year", myYear, myYearMixed, _recursive, _recursive);
	saveValue("tracknr", myTrackNr, myTrackNrMixed, false, false);
	saveValue("pin", myPIN, myPINMixed, false, false);
	saveValue("genre", myGenre, myGenreMixed, _recursive, _recursive);
	saveValue("comment", myComment, myCommentMixed, false, false);
    }
}
