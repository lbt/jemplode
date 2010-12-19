/* PearlSearchPanel - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.rmmlite;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.inzyme.util.Debug;
import com.rio.rmmlite.easteregg.KarmaVsiPod;

import org.jempeg.ApplicationContext;
import org.jempeg.manager.ui.ComponentConfiguration;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.nodestore.model.FIDPlaylistTableModel;
import org.jempeg.nodestore.model.NodeFinder;
import org.jempeg.nodestore.model.SortedPlaylistTableModel;
import org.jempeg.nodestore.predicate.AndPredicate;
import org.jempeg.nodestore.predicate.EqualsPredicate;
import org.jempeg.nodestore.predicate.IPredicate;
import org.jempeg.nodestore.predicate.LikePredicate;
import org.jempeg.nodestore.predicate.LiteralValue;
import org.jempeg.nodestore.predicate.OrPredicate;
import org.jempeg.nodestore.predicate.PredicateParser;
import org.jempeg.nodestore.predicate.TagValue;

public class PearlSearchPanel extends JComponent
{
    private ApplicationContext myContext;
    private JTextField mySearchTextField;
    private JButton myFindNowButton;
    private JCheckBox myAdvancedCheckbox;
    private JTable myTable;
    private FIDPlaylist myLastSearchPlaylist;
    
    protected class FindNowListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    try {
		performSearch();
	    } catch (Throwable t) {
		Debug.handleError(myContext.getFrame(), t, true);
	    }
	}
    }
    
    public PearlSearchPanel(ApplicationContext _context) {
	myContext = _context;
	setLayout(new BorderLayout());
	mySearchTextField = new JTextField();
	myFindNowButton = new JButton("Find Now");
	myAdvancedCheckbox = new JCheckBox("Advanced");
	JPanel spacingPanel = new JPanel();
	spacingPanel.setLayout(new BoxLayout(spacingPanel, 0));
	spacingPanel.add(new JLabel("Look for: "));
	spacingPanel.add(mySearchTextField);
	spacingPanel.add(myFindNowButton);
	spacingPanel.add(myAdvancedCheckbox);
	spacingPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
	myTable = new JTable();
	JScrollPane tableScrollPane = new JScrollPane(myTable);
	ComponentConfiguration.configureTable(_context, myTable);
	tableScrollPane.setVerticalScrollBarPolicy(22);
	add(spacingPanel, "North");
	add(tableScrollPane, "Center");
	mySearchTextField.addActionListener(new FindNowListener());
	myFindNowButton.addActionListener(new FindNowListener());
    }
    
    public JTable getTable() {
	return myTable;
    }
    
    public String getSearchString() {
	return mySearchTextField.getText();
    }
    
    protected void showSearchResults(FIDPlaylist _playlist) {
	if (_playlist != null) {
	    FIDPlaylistTableModel searchResultsTableModel
		= new FIDPlaylistTableModel(_playlist);
	    SortedPlaylistTableModel sortedSearchResultsTableModel
		= new SortedPlaylistTableModel(searchResultsTableModel);
	    myTable.setModel(sortedSearchResultsTableModel);
	} else
	    myTable.setModel(new DefaultTableModel());
    }
    
    public synchronized void performSearch() throws ParseException {
	PlayerDatabase playerDatabase = myContext.getPlayerDatabase();
	String searchString = getSearchString().trim();
	if ("karmavsipod".equals(searchString))
	    KarmaVsiPod.launch(false);
	else if (!searchString.equals("")) {
	    IPredicate predicate;
	    if (myAdvancedCheckbox.isSelected())
		predicate = new PredicateParser().parse(searchString);
	    else
		predicate
		    = (new AndPredicate
		       (new EqualsPredicate(new TagValue("type"),
					    new LiteralValue("tune")),
			(new OrPredicate
			 (new LikePredicate(new TagValue("title"),
					    new LiteralValue(searchString)),
			  (new OrPredicate
			   (new LikePredicate(new TagValue("artist"),
					      new LiteralValue(searchString)),
			    (new LikePredicate
			     (new TagValue("source"),
			      new LiteralValue(searchString)))))))));
	    NodeFinder searcher = new NodeFinder(playerDatabase);
	    FIDPlaylist searchPlaylist;
	    if (predicate == null)
		searchPlaylist = null;
	    else
		searchPlaylist = searcher.findMatches(predicate.toString(),
						      predicate, true);
	    showSearchResults(searchPlaylist);
	    if (myLastSearchPlaylist != null
		&& myLastSearchPlaylist.getReferenceCount() == 0)
		myLastSearchPlaylist.delete();
	    myLastSearchPlaylist = searchPlaylist;
	}
    }
}
