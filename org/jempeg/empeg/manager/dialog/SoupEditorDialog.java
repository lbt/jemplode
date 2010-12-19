/* SoupEditorDialog - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.dialog;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.text.ParseException;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.inzyme.text.TextRange;
import com.inzyme.ui.CloseDialogListener;
import com.inzyme.ui.ConfirmationPanel;
import com.inzyme.ui.DialogUtils;
import com.inzyme.util.Debug;

import org.jempeg.ApplicationContext;
import org.jempeg.nodestore.model.NodeTag;
import org.jempeg.nodestore.predicate.EqualsPredicate;
import org.jempeg.nodestore.predicate.IPredicate;
import org.jempeg.nodestore.predicate.LiteralValue;
import org.jempeg.nodestore.predicate.TagValue;
import org.jempeg.nodestore.soup.ISoupLayer;
import org.jempeg.nodestore.soup.SearchSoupLayer;
import org.jempeg.nodestore.soup.SoupLayerFactory;

public class SoupEditorDialog extends JDialog
{
    private ApplicationContext myContext;
    private JList mySoupList;
    private JComboBox myTagsComboBox;
    private JComboBox mySortTagsComboBox;
    private JPanel mySortTagsPanel;
    private JTextField myNameTextField;
    private JRadioButton myTransientRB;
    private JRadioButton myPersistentRB;
    private ConfirmationPanel myConfirmationPanel;
    /*synthetic*/ static Class class$0;
    /*synthetic*/ static Class class$1;
    
    protected class AddTagListener implements ActionListener
    {
	private Class myTagEntryClass;
	/*synthetic*/ static Class class$0;
	
	public AddTagListener(Class _tagEntryClass) {
	    myTagEntryClass = _tagEntryClass;
	}
	
	public void actionPerformed(ActionEvent _event) {
	    JDialog tagDialog
		= new JDialog(SoupEditorDialog.this, "Tag", true);
	    try {
		try {
		    JPanel layoutPanel = new JPanel();
		    layoutPanel.setLayout(new BorderLayout());
		    layoutPanel.add(myTagsComboBox, "Center");
		    layoutPanel.add(mySortTagsPanel, "South");
		    ConfirmationPanel confirmationPanel
			= new ConfirmationPanel(layoutPanel);
		    confirmationPanel
			.addOkListener(new CloseDialogListener(tagDialog));
		    confirmationPanel
			.addCancelListener(new CloseDialogListener(tagDialog));
		    tagDialog.getContentPane().add(confirmationPanel);
		    tagDialog.pack();
		    DialogUtils.centerWindow(tagDialog);
		    tagDialog.setVisible(true);
		    int confirmation = confirmationPanel.getSelectedOption();
		    if (confirmation == 1) {
			NodeTag tag
			    = (NodeTag) myTagsComboBox.getSelectedItem();
			if (tag != null) {
			    Class var_class = myTagEntryClass;
			    Class[] var_classes = new Class[2];
			    int i = 0;
			    Class var_class_0_ = class$0;
			    if (var_class_0_ == null) {
				Class var_class_1_;
				try {
				    var_class_1_
					= (Class.forName
					   ("org.jempeg.nodestore.model.NodeTag"));
				} catch (ClassNotFoundException classnotfoundexception) {
				    NoClassDefFoundError noclassdeffounderror
					= new NoClassDefFoundError;
				    ((UNCONSTRUCTED)noclassdeffounderror)
					.NoClassDefFoundError
					(classnotfoundexception.getMessage());
				    throw noclassdeffounderror;
				}
				var_class_0_ = class$0 = var_class_1_;
			    }
			    var_classes[i] = var_class_0_;
			    int i_2_ = 1;
			    Class var_class_3_ = class$0;
			    if (var_class_3_ == null) {
				Class var_class_4_;
				try {
				    var_class_4_
					= (Class.forName
					   ("org.jempeg.nodestore.model.NodeTag"));
				} catch (ClassNotFoundException classnotfoundexception) {
				    NoClassDefFoundError noclassdeffounderror
					= new NoClassDefFoundError;
				    ((UNCONSTRUCTED)noclassdeffounderror)
					.NoClassDefFoundError
					(classnotfoundexception.getMessage());
				    throw noclassdeffounderror;
				}
				var_class_3_ = class$0 = var_class_4_;
			    }
			    var_classes[i_2_] = var_class_3_;
			    Constructor constructor
				= var_class.getConstructor(var_classes);
			    AbstractTagEntry entry
				= ((AbstractTagEntry)
				   (constructor.newInstance
				    (new Object[] { tag,
						    getSelectedSortTag() })));
			    DefaultListModel listModel
				= (DefaultListModel) mySoupList.getModel();
			    listModel.addElement(entry);
			}
		    }
		} catch (Throwable t) {
		    t.printStackTrace();
		    Debug.println(t);
		}
	    } catch (Object object) {
		tagDialog.dispose();
		throw object;
	    }
	    tagDialog.dispose();
	}
    }
    
    protected class AddRangeTagListener implements ActionListener
    {
	public AddRangeTagListener() {
	    /* empty */
	}
	
	public void actionPerformed(ActionEvent _event) {
	    JDialog tagDialog
		= new JDialog(SoupEditorDialog.this, "Tag Range", true);
	    try {
		try {
		    JTextField rangeField = new JTextField();
		    JPanel rangePanel = new JPanel();
		    rangePanel.setLayout(new BorderLayout());
		    rangePanel.add(myTagsComboBox, "North");
		    rangePanel.add
			((new JLabel
			  ("Range: (i.e. 'A-J,K-Z', '5-9,A,B-D,E-Z', etc.)")),
			 "Center");
		    rangePanel.add(rangeField, "South");
		    JPanel layoutPanel = new JPanel();
		    layoutPanel.setLayout(new BorderLayout());
		    layoutPanel.add(rangePanel, "Center");
		    layoutPanel.add(mySortTagsPanel, "South");
		    ConfirmationPanel confirmationPanel
			= new ConfirmationPanel(layoutPanel);
		    confirmationPanel
			.addOkListener(new CloseDialogListener(tagDialog));
		    confirmationPanel
			.addCancelListener(new CloseDialogListener(tagDialog));
		    tagDialog.getContentPane().add(confirmationPanel);
		    tagDialog.pack();
		    DialogUtils.centerWindow(tagDialog);
		    tagDialog.setVisible(true);
		    int confirmation = confirmationPanel.getSelectedOption();
		    if (confirmation == 1) {
			NodeTag tag
			    = (NodeTag) myTagsComboBox.getSelectedItem();
			if (tag != null) {
			    String textRange = rangeField.getText().trim();
			    RangeTagEntry entry
				= new RangeTagEntry(tag, textRange,
						    getSelectedSortTag());
			    DefaultListModel listModel
				= (DefaultListModel) mySoupList.getModel();
			    listModel.addElement(entry);
			}
		    }
		} catch (Throwable t) {
		    t.printStackTrace();
		    Debug.println(t);
		}
	    } catch (Object object) {
		tagDialog.dispose();
		throw object;
	    }
	    tagDialog.dispose();
	}
    }
    
    protected class AddSearchListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    JDialog searchDialog
		= new JDialog(SoupEditorDialog.this, "Search", true);
	    SearchPanel searchPanel = new SearchPanel(myContext);
	    JPanel layoutPanel = new JPanel();
	    layoutPanel.setLayout(new BorderLayout());
	    layoutPanel.add(searchPanel, "Center");
	    layoutPanel.add(mySortTagsPanel, "South");
	    ConfirmationPanel confirmationPanel
		= new ConfirmationPanel(layoutPanel);
	    confirmationPanel
		.addOkListener(new CloseDialogListener(searchDialog));
	    confirmationPanel
		.addCancelListener(new CloseDialogListener(searchDialog));
	    searchDialog.getContentPane().add(confirmationPanel);
	    searchDialog.pack();
	    DialogUtils.centerWindow(searchDialog);
	    searchDialog.setVisible(true);
	    int confirmation = confirmationPanel.getSelectedOption();
	    if (confirmation == 1) {
		IPredicate predicate = searchPanel.getPredicate();
		if (predicate != null) {
		    SearchEntry entry
			= new SearchEntry(predicate, getSelectedSortTag());
		    DefaultListModel listModel
			= (DefaultListModel) mySoupList.getModel();
		    listModel.addElement(entry);
		}
	    }
	    searchDialog.dispose();
	}
    }
    
    protected class AddAllListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    JDialog allDialog
		= new JDialog(SoupEditorDialog.this, "All", true);
	    JPanel layoutPanel = new JPanel();
	    layoutPanel.setLayout(new BorderLayout());
	    layoutPanel.add(mySortTagsPanel, "South");
	    ConfirmationPanel confirmationPanel
		= new ConfirmationPanel(layoutPanel);
	    confirmationPanel
		.addOkListener(new CloseDialogListener(allDialog));
	    confirmationPanel
		.addCancelListener(new CloseDialogListener(allDialog));
	    allDialog.getContentPane().add(confirmationPanel);
	    allDialog.pack();
	    DialogUtils.centerWindow(allDialog);
	    allDialog.setVisible(true);
	    int confirmation = confirmationPanel.getSelectedOption();
	    if (confirmation == 1) {
		AllEntry entry = new AllEntry(getSelectedSortTag());
		DefaultListModel listModel
		    = (DefaultListModel) mySoupList.getModel();
		listModel.addElement(entry);
	    }
	    allDialog.dispose();
	}
    }
    
    protected class AddSortListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    JDialog sortDialog
		= new JDialog(SoupEditorDialog.this, "Sort Tracks", true);
	    JPanel layoutPanel = new JPanel();
	    layoutPanel.setLayout(new BorderLayout());
	    layoutPanel.add(mySortTagsPanel, "South");
	    ConfirmationPanel confirmationPanel
		= new ConfirmationPanel(layoutPanel);
	    confirmationPanel
		.addOkListener(new CloseDialogListener(sortDialog));
	    confirmationPanel
		.addCancelListener(new CloseDialogListener(sortDialog));
	    sortDialog.getContentPane().add(confirmationPanel);
	    sortDialog.pack();
	    DialogUtils.centerWindow(sortDialog);
	    sortDialog.setVisible(true);
	    int confirmation = confirmationPanel.getSelectedOption();
	    if (confirmation == 1) {
		SortEntry entry = new SortEntry(getSelectedSortTag());
		DefaultListModel listModel
		    = (DefaultListModel) mySoupList.getModel();
		listModel.addElement(entry);
	    }
	    sortDialog.dispose();
	}
    }
    
    protected class DeleteListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    int selectedIndex = mySoupList.getSelectedIndex();
	    if (selectedIndex != -1) {
		DefaultListModel listModel
		    = (DefaultListModel) mySoupList.getModel();
		listModel.removeElementAt(selectedIndex);
	    }
	}
    }
    
    protected class ClearListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    DefaultListModel listModel
		= (DefaultListModel) mySoupList.getModel();
	    listModel.removeAllElements();
	}
    }
    
    protected abstract static class AbstractTagEntry
    {
	private NodeTag myTag;
	private NodeTag mySortTag;
	
	public AbstractTagEntry(NodeTag _tag, NodeTag _sortTag) {
	    myTag = _tag;
	    mySortTag = _sortTag;
	}
	
	public NodeTag getTag() {
	    return myTag;
	}
	
	public NodeTag getSortTag() {
	    return mySortTag;
	}
    }
    
    protected static class BasicTagEntry extends AbstractTagEntry
    {
	public BasicTagEntry(NodeTag _tag, NodeTag _sortTag) {
	    super(_tag, _sortTag);
	}
	
	public String toString() {
	    return ("By " + getTag().getDescription() + ", sorted by "
		    + getSortTag().getDescription());
	}
    }
    
    protected static class FirstLetterTagEntry extends AbstractTagEntry
    {
	public FirstLetterTagEntry(NodeTag _tag, NodeTag _sortTag) {
	    super(_tag, _sortTag);
	}
	
	public String toString() {
	    return ("First Letter of " + getTag().getDescription()
		    + ", sorted by " + getSortTag().getDescription());
	}
    }
    
    protected static class RangeTagEntry extends AbstractTagEntry
    {
	private TextRange[] myTextRanges;
	
	public RangeTagEntry(NodeTag _tag, String _textRanges,
			     NodeTag _sortTag) throws ParseException {
	    super(_tag, _sortTag);
	    myTextRanges = TextRange.fromExternalForm(_textRanges);
	}
	
	public TextRange[] getTextRanges() {
	    return myTextRanges;
	}
	
	public String toString() {
	    StringBuffer sb = new StringBuffer();
	    sb.append(TextRange.toExternalForm(myTextRanges));
	    sb.append(" of ");
	    sb.append(getTag().getDescription());
	    sb.append(", sorted by " + getSortTag().getDescription());
	    return sb.toString();
	}
    }
    
    protected static class SearchEntry
    {
	private IPredicate myPredicate;
	private NodeTag mySortTag;
	
	public SearchEntry(IPredicate _predicate, NodeTag _sortTag) {
	    myPredicate = _predicate;
	    mySortTag = _sortTag;
	}
	
	public IPredicate getPredicate() {
	    return myPredicate;
	}
	
	public NodeTag getSortTag() {
	    return mySortTag;
	}
	
	public String toString() {
	    return ("Results of " + myPredicate.toString() + ", sorted by "
		    + mySortTag.getDescription());
	}
    }
    
    protected static class AllEntry extends SearchEntry
    {
	public AllEntry(NodeTag _sortTag) {
	    super(new EqualsPredicate(new TagValue("type"),
				      new LiteralValue("tune")),
		  _sortTag);
	}
	
	public String toString() {
	    return "All, sorted by " + getSortTag().getDescription();
	}
    }
    
    protected static class SortEntry
    {
	private NodeTag mySortTag;
	
	public SortEntry(NodeTag _sortTag) {
	    mySortTag = _sortTag;
	}
	
	public NodeTag getSortTag() {
	    return mySortTag;
	}
	
	public String toString() {
	    return "Tracks, sorted by " + getSortTag().getDescription();
	}
    }
    
    public SoupEditorDialog(ApplicationContext _context, JFrame _frame) {
	this(_context, _frame, null);
    }
    
    public SoupEditorDialog(ApplicationContext _context, JFrame _frame,
			    ISoupLayer _soupLayer) {
	super(_frame, "Soup Editor", true);
	myContext = _context;
	NodeTag[] nodeTags = NodeTag.getNodeTags();
	myTagsComboBox = new JComboBox(nodeTags);
	mySortTagsPanel = new JPanel();
	mySortTagsPanel.setLayout(new BorderLayout());
	mySortTagsPanel.add(new JLabel("Sort By:"), "West");
	mySortTagsComboBox = new JComboBox(nodeTags);
	mySortTagsPanel.add(mySortTagsComboBox, "East");
	JPanel soupEditorPanel = new JPanel();
	soupEditorPanel.setLayout(new BorderLayout());
	JPanel namePanel = new JPanel();
	namePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
	namePanel.add(new JLabel("Soup Name: "));
	myNameTextField = new JTextField();
	myNameTextField.setColumns(30);
	namePanel.add(myNameTextField);
	DefaultListModel listModel = new DefaultListModel();
	mySoupList = new JList(listModel);
	mySoupList.setSelectionMode(0);
	JScrollPane soupListScrollPane = new JScrollPane(mySoupList);
	JPanel buttonLayoutPanel = new JPanel();
	buttonLayoutPanel.setLayout(new BorderLayout());
	JPanel buttonPanel = new JPanel();
	GridLayout gridLayout = new GridLayout(0, 1);
	gridLayout.setVgap(5);
	buttonPanel.setLayout(gridLayout);
	JButton addTagLayerButton = new JButton("Add Tag Layer");
	JButton jbutton = addTagLayerButton;
	AddTagListener addtaglistener = new AddTagListener;
	SoupEditorDialog soupeditordialog = this;
	Class var_class = class$0;
	if (var_class == null) {
	    Class var_class_5_;
	    try {
		var_class_5_
		    = (Class.forName
		       ("org.jempeg.empeg.manager.dialog.SoupEditorDialog$BasicTagEntry"));
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class = class$0 = var_class_5_;
	}
	soupeditordialog.((UNCONSTRUCTED)addtaglistener)
	    .AddTagListener(var_class);
	jbutton.addActionListener(addtaglistener);
	buttonPanel.add(addTagLayerButton);
	JButton addFirstLetterTagLayerButton
	    = new JButton("Add First Letter Layer");
	JButton jbutton_6_ = addFirstLetterTagLayerButton;
	AddTagListener addtaglistener_7_ = new AddTagListener;
	SoupEditorDialog soupeditordialog_8_ = this;
	Class var_class_9_ = class$1;
	if (var_class_9_ == null) {
	    Class var_class_10_;
	    try {
		var_class_10_
		    = (Class.forName
		       ("org.jempeg.empeg.manager.dialog.SoupEditorDialog$FirstLetterTagEntry"));
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_9_ = class$1 = var_class_10_;
	}
	soupeditordialog_8_.((UNCONSTRUCTED)addtaglistener_7_)
	    .AddTagListener(var_class_9_);
	jbutton_6_.addActionListener(addtaglistener_7_);
	buttonPanel.add(addFirstLetterTagLayerButton);
	JButton addRangeTagLayerButton = new JButton("Add Range Layer");
	addRangeTagLayerButton.addActionListener(new AddRangeTagListener());
	buttonPanel.add(addRangeTagLayerButton);
	JButton addSearchLayerButton = new JButton("Add Search Layer");
	addSearchLayerButton.addActionListener(new AddSearchListener());
	buttonPanel.add(addSearchLayerButton);
	JButton addAllLayerButton = new JButton("Add All Layer");
	addAllLayerButton.addActionListener(new AddAllListener());
	buttonPanel.add(addAllLayerButton);
	JButton addSortLayerButton = new JButton("Add Tracks Sort Layer");
	addSortLayerButton.addActionListener(new AddSortListener());
	buttonPanel.add(addSortLayerButton);
	JButton deleteButton = new JButton("Delete");
	deleteButton.addActionListener(new DeleteListener());
	buttonPanel.add(deleteButton);
	JButton clearButton = new JButton("Clear");
	clearButton.addActionListener(new ClearListener());
	buttonPanel.add(clearButton);
	buttonLayoutPanel.add(buttonPanel, "North");
	buttonLayoutPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0,
								    5));
	JPanel transientOrPersistentPanel = new JPanel();
	transientOrPersistentPanel
	    .setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
	myTransientRB = new JRadioButton("jEmplode Only");
	myPersistentRB = new JRadioButton("On the Empeg");
	ButtonGroup buttonGroup = new ButtonGroup();
	buttonGroup.add(myTransientRB);
	buttonGroup.add(myPersistentRB);
	myTransientRB.setSelected(true);
	transientOrPersistentPanel.add(myTransientRB);
	transientOrPersistentPanel.add(myPersistentRB);
	soupEditorPanel.add(namePanel, "North");
	soupEditorPanel.add(soupListScrollPane, "Center");
	soupEditorPanel.add(buttonLayoutPanel, "East");
	soupEditorPanel.add(transientOrPersistentPanel, "South");
	myConfirmationPanel = new ConfirmationPanel(soupEditorPanel);
	myConfirmationPanel.setOkText("Create Playlist");
	myConfirmationPanel.addOkListener(new CloseDialogListener(this));
	myConfirmationPanel.addCancelListener(new CloseDialogListener(this));
	getContentPane().add(myConfirmationPanel);
	pack();
	DialogUtils.centerWindow(this);
	if (_soupLayer != null && _soupLayer instanceof SearchSoupLayer) {
	    SearchSoupLayer searchSoupUpdater = (SearchSoupLayer) _soupLayer;
	    SearchEntry searchEntry
		= new SearchEntry(searchSoupUpdater.getPredicate(),
				  searchSoupUpdater.getSortTag());
	    listModel.addElement(searchEntry);
	}
    }
    
    public boolean shouldCreateSoupPlaylist() {
	boolean shouldCreate = myConfirmationPanel.getSelectedOption() == 1;
	return shouldCreate;
    }
    
    public String getPlaylistName() {
	String name = myNameTextField.getText();
	return name;
    }
    
    public ISoupLayer[] getSoupLayers() throws ParseException {
	StringBuffer soupExternalForm = new StringBuffer();
	DefaultListModel listModel = (DefaultListModel) mySoupList.getModel();
	int size = listModel.getSize();
	for (int i = 0; i < size; i++) {
	    Object obj = listModel.getElementAt(i);
	    if (obj instanceof BasicTagEntry) {
		BasicTagEntry tagEntry = (BasicTagEntry) obj;
		NodeTag nodeTag = tagEntry.getTag();
		SoupLayerFactory.appendExternalForm(soupExternalForm, "tag",
						    tagEntry.getSortTag(),
						    nodeTag.getName());
	    } else if (obj instanceof FirstLetterTagEntry) {
		FirstLetterTagEntry tagEntry = (FirstLetterTagEntry) obj;
		NodeTag nodeTag = tagEntry.getTag();
		SoupLayerFactory.appendExternalForm(soupExternalForm,
						    "tagletter",
						    tagEntry.getSortTag(),
						    nodeTag.getName());
	    } else if (obj instanceof RangeTagEntry) {
		RangeTagEntry tagEntry = (RangeTagEntry) obj;
		NodeTag nodeTag = tagEntry.getTag();
		TextRange[] textRanges = tagEntry.getTextRanges();
		SoupLayerFactory.appendExternalForm
		    (soupExternalForm, "tagrange", tagEntry.getSortTag(),
		     (nodeTag.getName() + ","
		      + TextRange.toExternalForm(textRanges)));
	    } else if (obj instanceof SortEntry) {
		SortEntry sortEntry = (SortEntry) obj;
		SoupLayerFactory.appendExternalForm(soupExternalForm, "sort",
						    sortEntry.getSortTag(),
						    "");
	    } else if (obj instanceof SearchEntry) {
		SearchEntry searchEntry = (SearchEntry) obj;
		IPredicate predicate = searchEntry.getPredicate();
		SoupLayerFactory.appendExternalForm(soupExternalForm, "search",
						    searchEntry.getSortTag(),
						    predicate.toString());
	    } else
		throw new IllegalArgumentException
			  ("There is no known handler for the entry: " + obj);
	}
	ISoupLayer[] soupLayers
	    = SoupLayerFactory.fromExternalForm(soupExternalForm.toString());
	return soupLayers;
    }
    
    public boolean isTransient() {
	boolean isTransient = myTransientRB.isSelected();
	return isTransient;
    }
    
    protected NodeTag getSelectedSortTag() {
	NodeTag sortTag = (NodeTag) mySortTagsComboBox.getSelectedItem();
	if (sortTag == null)
	    sortTag = NodeTag.TITLE_TAG;
	return sortTag;
    }
}
