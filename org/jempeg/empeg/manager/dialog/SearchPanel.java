/* SearchPanel - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.dialog;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import com.inzyme.util.Debug;

import org.jempeg.ApplicationContext;
import org.jempeg.empeg.manager.action.SoupEditorAction;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.nodestore.model.FIDPlaylistTableModel;
import org.jempeg.nodestore.model.NodeFinder;
import org.jempeg.nodestore.model.SortedPlaylistTableModel;
import org.jempeg.nodestore.predicate.AndPredicate;
import org.jempeg.nodestore.predicate.IPredicate;
import org.jempeg.nodestore.predicate.LikePredicate;
import org.jempeg.nodestore.predicate.LiteralValue;
import org.jempeg.nodestore.predicate.PredicateParser;
import org.jempeg.nodestore.predicate.TagValue;
import org.jempeg.nodestore.soup.SearchSoupLayer;

public class SearchPanel extends JComponent
{
    private JTable myTable;
    private ApplicationContext myContext;
    private Vector myOptions;
    private JRadioButton myStandardSeach;
    private JRadioButton myAdvancedSearch;
    private JTextField myAdvancedTextField;
    private FIDPlaylist myLastSearchPlaylist;
    
    protected class SearchListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    try {
		performSearch();
	    } catch (ParseException e) {
		Debug.handleError(myContext.getFrame(), e, true);
	    }
	}
    }
    
    protected class SaveResultsListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    saveResults();
	}
    }
    
    protected class StandardSearchListener implements ChangeListener
    {
	public void stateChanged(ChangeEvent _event) {
	    boolean standardEnabled = myStandardSeach.isSelected();
	    Enumeration optionsEnum = myOptions.elements();
	    while (optionsEnum.hasMoreElements()) {
		TextOption option = (TextOption) optionsEnum.nextElement();
		option.setEnabled(standardEnabled);
	    }
	    myAdvancedTextField.setEnabled(!standardEnabled);
	}
    }
    
    protected class TextOption implements ActionListener
    {
	private JCheckBox myCheckBox;
	private JTextField myTextField;
	private String myTagName;
	
	public TextOption(String _optionTitle, String _tagName) {
	    myTagName = _tagName;
	    myCheckBox = new JCheckBox(_optionTitle);
	    myTextField = new JTextField();
	    myTextField.setEnabled(false);
	    myCheckBox.addActionListener(this);
	}
	
	public boolean isSelected() {
	    return myCheckBox.isSelected();
	}
	
	public void setEnabled(boolean _enabled) {
	    myCheckBox.setEnabled(_enabled);
	    myTextField.setEnabled(_enabled && myCheckBox.isSelected());
	}
	
	public void actionPerformed(ActionEvent _event) {
	    myTextField.setEnabled(myCheckBox.isSelected());
	}
	
	public IPredicate getPredicate() {
	    String value = myTextField.getText();
	    LikePredicate pred = new LikePredicate(new TagValue(myTagName),
						   new LiteralValue(value));
	    return pred;
	}
	
	public JCheckBox getCheckBox() {
	    return myCheckBox;
	}
	
	public JTextField getTextField() {
	    return myTextField;
	}
    }
    
    public SearchPanel(ApplicationContext _context) {
	this(_context, null);
    }
    
    public SearchPanel(ApplicationContext _context, JTable _table) {
	setLayout(new BorderLayout());
	myContext = _context;
	myTable = _table;
	myOptions = new Vector();
	JPanel optionSetPanel = new JPanel();
	optionSetPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10,
								 10));
	optionSetPanel.setLayout(new BoxLayout(optionSetPanel, 1));
	myStandardSeach = new JRadioButton("Standard Search");
	myStandardSeach.setAlignmentX(0.0F);
	myAdvancedSearch = new JRadioButton("Advanced Search");
	myAdvancedSearch.setAlignmentX(0.0F);
	ButtonGroup optionsGroup = new ButtonGroup();
	optionsGroup.add(myStandardSeach);
	optionsGroup.add(myAdvancedSearch);
	myStandardSeach.addChangeListener(new StandardSearchListener());
	JPanel optionsPanel = new JPanel();
	optionsPanel.setAlignmentX(0.0F);
	optionsPanel.setLayout(new GridLayout(0, 1));
	optionsPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
	addOption(optionsPanel, new TextOption("Title", "title"));
	addOption(optionsPanel, new TextOption("Artist", "artist"));
	addOption(optionsPanel, new TextOption("Source", "source"));
	addOption(optionsPanel, new TextOption("Genre", "genre"));
	addOption(optionsPanel, new TextOption("Year", "year"));
	JPanel advancedPanel = new JPanel();
	advancedPanel.setAlignmentX(0.0F);
	advancedPanel.setLayout(new GridLayout(0, 1));
	advancedPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
	myAdvancedTextField = new JTextField();
	advancedPanel.add(myAdvancedTextField);
	JButton searchButton = new JButton("Search");
	searchButton.addActionListener(new SearchListener());
	JButton saveResultsButton = new JButton("Save");
	saveResultsButton.addActionListener(new SaveResultsListener());
	JPanel buttonsPanel = new JPanel();
	buttonsPanel.setLayout(new FlowLayout());
	buttonsPanel.add(searchButton);
	buttonsPanel.add(saveResultsButton);
	buttonsPanel.setAlignmentX(0.0F);
	optionSetPanel.add(myStandardSeach);
	optionSetPanel.add(optionsPanel, "North");
	optionSetPanel.add(myAdvancedSearch);
	optionSetPanel.add(advancedPanel);
	if (_table != null) {
	    optionSetPanel.add(Box.createVerticalStrut(10));
	    optionSetPanel.add(buttonsPanel);
	}
	add(optionSetPanel, "North");
	myStandardSeach.setSelected(true);
    }
    
    public IPredicate getPredicate() {
	IPredicate predicate = null;
	if (myStandardSeach.isSelected()) {
	    Enumeration optionsEnum = myOptions.elements();
	    while (optionsEnum.hasMoreElements()) {
		TextOption option = (TextOption) optionsEnum.nextElement();
		if (option.isSelected()) {
		    IPredicate optionPredicate = option.getPredicate();
		    if (predicate == null)
			predicate = optionPredicate;
		    else
			predicate
			    = new AndPredicate(predicate, optionPredicate);
		}
	    }
	} else if (myAdvancedSearch.isSelected()) {
	    try {
		String searchString = myAdvancedTextField.getText();
		PredicateParser predParser = new PredicateParser();
		predicate = predParser.parse(searchString);
	    } catch (ParseException e) {
		String error = e.getMessage();
		Debug.handleError(myContext.getFrame(), error, true);
	    }
	}
	return predicate;
    }
    
    public void showLastSearchResults() {
	showSearchResults(myLastSearchPlaylist);
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
	IPredicate predicate = getPredicate();
	NodeFinder searcher = new NodeFinder(playerDatabase);
	FIDPlaylist searchPlaylist;
	if (predicate == null)
	    searchPlaylist = null;
	else
	    searchPlaylist
		= searcher.findMatches(predicate.toString(), predicate, true);
	showSearchResults(searchPlaylist);
	if (myLastSearchPlaylist != null
	    && myLastSearchPlaylist.getReferenceCount() == 0)
	    myLastSearchPlaylist.delete();
	myLastSearchPlaylist = searchPlaylist;
    }
    
    public void saveResults() {
	IPredicate predicate = getPredicate();
	if (predicate != null) {
	    SearchSoupLayer soupLayer = new SearchSoupLayer(predicate);
	    SoupEditorAction soupEditorAction
		= new SoupEditorAction(myContext);
	    soupEditorAction.editSoup(soupLayer);
	}
    }
    
    protected void addOption(JComponent _comp, TextOption _option) {
	myOptions.addElement(_option);
	_comp.add(_option.getCheckBox());
	_comp.add(_option.getTextField());
    }
}
