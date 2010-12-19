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
import javax.swing.ListSelectionModel;

import org.jempeg.ApplicationContext;
import org.jempeg.nodestore.DatabaseTags;
import org.jempeg.nodestore.model.NodeTag;
import org.jempeg.nodestore.predicate.EqualsPredicate;
import org.jempeg.nodestore.predicate.IPredicate;
import org.jempeg.nodestore.predicate.LiteralValue;
import org.jempeg.nodestore.predicate.TagValue;
import org.jempeg.nodestore.soup.ISoupLayer;
import org.jempeg.nodestore.soup.SearchSoupLayer;
import org.jempeg.nodestore.soup.SoupLayerFactory;

import com.inzyme.text.TextRange;
import com.inzyme.ui.CloseDialogListener;
import com.inzyme.ui.ConfirmationPanel;
import com.inzyme.ui.DialogUtils;
import com.inzyme.util.Debug;

/**
 * @author mschrag
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class SoupEditorDialog extends JDialog {
	private ApplicationContext myContext;
	private JList mySoupList;
	private JComboBox myTagsComboBox;
  private JComboBox mySortTagsComboBox;
  private JPanel mySortTagsPanel;
	private JTextField myNameTextField;
	private JRadioButton myTransientRB;
	private JRadioButton myPersistentRB;
	private ConfirmationPanel myConfirmationPanel;

	/**
	 * Constructor for CreateSoupPanel.
	 */
	public SoupEditorDialog(ApplicationContext _context, JFrame _frame) {
		this(_context, _frame, null);
	}

	/**
	 * Constructor for CreateSoupPanel.
	 */
	public SoupEditorDialog(ApplicationContext _context, JFrame _frame, ISoupLayer _soupLayer) {
		super(_frame, "Soup Editor", true);
		myContext = _context;
		NodeTag[] nodeTags = NodeTag.getNodeTags();
		myTagsComboBox = new JComboBox(nodeTags);
    
    mySortTagsPanel = new JPanel();
    mySortTagsPanel.setLayout(new BorderLayout());
    mySortTagsPanel.add(new JLabel("Sort By:"), BorderLayout.WEST);
    mySortTagsComboBox = new JComboBox(nodeTags);
    mySortTagsPanel.add(mySortTagsComboBox, BorderLayout.EAST);

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
		mySoupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane soupListScrollPane = new JScrollPane(mySoupList);

		JPanel buttonLayoutPanel = new JPanel();
		buttonLayoutPanel.setLayout(new BorderLayout());
		JPanel buttonPanel = new JPanel();
		GridLayout gridLayout = new GridLayout(0, 1);
		gridLayout.setVgap(5);
		buttonPanel.setLayout(gridLayout);

		JButton addTagLayerButton = new JButton("Add Tag Layer");
		addTagLayerButton.addActionListener(new AddTagListener(BasicTagEntry.class));
		buttonPanel.add(addTagLayerButton);

		JButton addFirstLetterTagLayerButton = new JButton("Add First Letter Layer");
		addFirstLetterTagLayerButton.addActionListener(new AddTagListener(FirstLetterTagEntry.class));
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
		buttonLayoutPanel.add(buttonPanel, BorderLayout.NORTH);
		buttonLayoutPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 5));

		JPanel transientOrPersistentPanel = new JPanel();
		transientOrPersistentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
		myTransientRB = new JRadioButton("jEmplode Only");
		myPersistentRB = new JRadioButton("On the Empeg");
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(myTransientRB);
		buttonGroup.add(myPersistentRB);
		myTransientRB.setSelected(true);
		transientOrPersistentPanel.add(myTransientRB);
		transientOrPersistentPanel.add(myPersistentRB);

		soupEditorPanel.add(namePanel, BorderLayout.NORTH);
		soupEditorPanel.add(soupListScrollPane, BorderLayout.CENTER);
		soupEditorPanel.add(buttonLayoutPanel, BorderLayout.EAST);
		soupEditorPanel.add(transientOrPersistentPanel, BorderLayout.SOUTH);

		myConfirmationPanel = new ConfirmationPanel(soupEditorPanel);
		myConfirmationPanel.setOkText("Create Playlist");
		myConfirmationPanel.addOkListener(new CloseDialogListener(this));
		myConfirmationPanel.addCancelListener(new CloseDialogListener(this));

		getContentPane().add(myConfirmationPanel);
		pack();

		DialogUtils.centerWindow(this);

		if (_soupLayer != null && _soupLayer instanceof SearchSoupLayer) {
			SearchSoupLayer searchSoupUpdater = (SearchSoupLayer) _soupLayer;
			SearchEntry searchEntry = new SearchEntry(searchSoupUpdater.getPredicate(), searchSoupUpdater.getSortTag());
			listModel.addElement(searchEntry);
		}
	}

	/**
	 * Returns whether or not the soup playlist should be created (user hit OK)
	 * 
	 * @return whether or not the soup playlist should be created (user hit OK)
	 */
	public boolean shouldCreateSoupPlaylist() {
		boolean shouldCreate = (myConfirmationPanel.getSelectedOption() == ConfirmationPanel.OK_OPTION);
		return shouldCreate;
	}

	/**
	 * Returns the name of the soup playlist.
	 * 
	 * @return the name of the soup playlist
	 */
	public String getPlaylistName() {
		String name = myNameTextField.getText();
		return name;
	}

	/**
	 * Returns the SoupLayers that were designed
	 * by the user.
	 * 
	 * @return the created SoupLayers
	 */
	public ISoupLayer[] getSoupLayers() throws ParseException {
		StringBuffer soupExternalForm = new StringBuffer();
		DefaultListModel listModel = (DefaultListModel) mySoupList.getModel();
		int size = listModel.getSize();
		for (int i = 0; i < size; i ++) {
			Object obj = listModel.getElementAt(i);
			if (obj instanceof BasicTagEntry) {
				BasicTagEntry tagEntry = (BasicTagEntry) obj;
				NodeTag nodeTag = tagEntry.getTag();
				SoupLayerFactory.appendExternalForm(soupExternalForm, SoupLayerFactory.TAG_NAME, tagEntry.getSortTag(), nodeTag.getName());
			}
			else if (obj instanceof FirstLetterTagEntry) {
				FirstLetterTagEntry tagEntry = (FirstLetterTagEntry) obj;
				NodeTag nodeTag = tagEntry.getTag();
				SoupLayerFactory.appendExternalForm(soupExternalForm, SoupLayerFactory.TAGLETTER_NAME, tagEntry.getSortTag(), nodeTag.getName());
			}
			else if (obj instanceof RangeTagEntry) {
				RangeTagEntry tagEntry = (RangeTagEntry) obj;
				NodeTag nodeTag = tagEntry.getTag();
				TextRange[] textRanges = tagEntry.getTextRanges();
				SoupLayerFactory.appendExternalForm(soupExternalForm, SoupLayerFactory.TAGRANGE_NAME, tagEntry.getSortTag(), nodeTag.getName() + "," + TextRange.toExternalForm(textRanges));
			}
      else if (obj instanceof SortEntry) {
        SortEntry sortEntry = (SortEntry) obj;
        SoupLayerFactory.appendExternalForm(soupExternalForm, SoupLayerFactory.SORT_NAME, sortEntry.getSortTag(), "");
      }
			else if (obj instanceof SearchEntry) {
				SearchEntry searchEntry = (SearchEntry) obj;
				IPredicate predicate = searchEntry.getPredicate();
				SoupLayerFactory.appendExternalForm(soupExternalForm, SoupLayerFactory.SEARCH_NAME, searchEntry.getSortTag(), predicate.toString());
			}
      else {
        throw new IllegalArgumentException("There is no known handler for the entry: " + obj);
      }
		}
		ISoupLayer[] soupLayers = SoupLayerFactory.fromExternalForm(soupExternalForm.toString());
		return soupLayers;
	}

	/**
	 * Returns whether or not this playlist should be transient.
	 * 
	 * @return whether or not this playlist should be transient
	 */
	public boolean isTransient() {
		boolean isTransient = myTransientRB.isSelected();
		return isTransient;
	}
  
  protected NodeTag getSelectedSortTag() {
    NodeTag sortTag = (NodeTag) mySortTagsComboBox.getSelectedItem();
    if (sortTag == null) {
      sortTag = NodeTag.TITLE_TAG;
    }
    return sortTag;
  }

	protected class AddTagListener implements ActionListener {
		private Class myTagEntryClass;

		public AddTagListener(Class _tagEntryClass) {
			myTagEntryClass = _tagEntryClass;
		}

		public void actionPerformed(ActionEvent _event) {
			JDialog tagDialog = new JDialog(SoupEditorDialog.this, "Tag", true);
			try {
        JPanel layoutPanel = new JPanel();
        layoutPanel.setLayout(new BorderLayout());
        layoutPanel.add(myTagsComboBox, BorderLayout.CENTER);
        layoutPanel.add(mySortTagsPanel, BorderLayout.SOUTH);

        ConfirmationPanel confirmationPanel = new ConfirmationPanel(layoutPanel);
				confirmationPanel.addOkListener(new CloseDialogListener(tagDialog));
				confirmationPanel.addCancelListener(new CloseDialogListener(tagDialog));
				tagDialog.getContentPane().add(confirmationPanel);
				tagDialog.pack();
				DialogUtils.centerWindow(tagDialog);
				tagDialog.setVisible(true);
				int confirmation = confirmationPanel.getSelectedOption();
				if (confirmation == ConfirmationPanel.OK_OPTION) {
					NodeTag tag = (NodeTag) myTagsComboBox.getSelectedItem();
					if (tag != null) {
						Constructor constructor = myTagEntryClass.getConstructor(new Class[] { NodeTag.class, NodeTag.class });
						AbstractTagEntry entry = (AbstractTagEntry) constructor.newInstance(new Object[] { tag, getSelectedSortTag() });
						DefaultListModel listModel = (DefaultListModel) mySoupList.getModel();
						listModel.addElement(entry);
					}
				}
			}
			catch (Throwable t) {
				t.printStackTrace();
				Debug.println(t);
			}
			finally {
				tagDialog.dispose();
			}
		}
	}

	protected class AddRangeTagListener implements ActionListener {
		public AddRangeTagListener() {
		}

		public void actionPerformed(ActionEvent _event) {
			JDialog tagDialog = new JDialog(SoupEditorDialog.this, "Tag Range", true);
			try {
				JTextField rangeField = new JTextField();
				JPanel rangePanel = new JPanel();
				rangePanel.setLayout(new BorderLayout());
				rangePanel.add(myTagsComboBox, BorderLayout.NORTH);
				rangePanel.add(new JLabel("Range: (i.e. 'A-J,K-Z', '5-9,A,B-D,E-Z', etc.)"), BorderLayout.CENTER);
				rangePanel.add(rangeField, BorderLayout.SOUTH);
        
        JPanel layoutPanel = new JPanel();
        layoutPanel.setLayout(new BorderLayout());
        layoutPanel.add(rangePanel, BorderLayout.CENTER);
        layoutPanel.add(mySortTagsPanel, BorderLayout.SOUTH);
        
				ConfirmationPanel confirmationPanel = new ConfirmationPanel(layoutPanel);
				confirmationPanel.addOkListener(new CloseDialogListener(tagDialog));
				confirmationPanel.addCancelListener(new CloseDialogListener(tagDialog));
				tagDialog.getContentPane().add(confirmationPanel);
				tagDialog.pack();
				DialogUtils.centerWindow(tagDialog);
				tagDialog.setVisible(true);
				int confirmation = confirmationPanel.getSelectedOption();
				if (confirmation == ConfirmationPanel.OK_OPTION) {
					NodeTag tag = (NodeTag) myTagsComboBox.getSelectedItem();
					if (tag != null) {
						String textRange = rangeField.getText().trim();
						RangeTagEntry entry = new RangeTagEntry(tag, textRange, getSelectedSortTag());
						DefaultListModel listModel = (DefaultListModel) mySoupList.getModel();
						listModel.addElement(entry);
					}
				}
			}
			catch (Throwable t) {
				t.printStackTrace();
				Debug.println(t);
			}
			finally {
				tagDialog.dispose();
			}
		}
	}

	protected class AddSearchListener implements ActionListener {
		public void actionPerformed(ActionEvent _event) {
			JDialog searchDialog = new JDialog(SoupEditorDialog.this, "Search", true);
      
      SearchPanel searchPanel = new SearchPanel(myContext);
      JPanel layoutPanel = new JPanel();
      layoutPanel.setLayout(new BorderLayout());
      layoutPanel.add(searchPanel, BorderLayout.CENTER);
      layoutPanel.add(mySortTagsPanel, BorderLayout.SOUTH);

			ConfirmationPanel confirmationPanel = new ConfirmationPanel(layoutPanel);
			confirmationPanel.addOkListener(new CloseDialogListener(searchDialog));
			confirmationPanel.addCancelListener(new CloseDialogListener(searchDialog));
			searchDialog.getContentPane().add(confirmationPanel);
			searchDialog.pack();
			DialogUtils.centerWindow(searchDialog);
			searchDialog.setVisible(true);
			int confirmation = confirmationPanel.getSelectedOption();
			if (confirmation == ConfirmationPanel.OK_OPTION) {
				IPredicate predicate = searchPanel.getPredicate();
				if (predicate != null) {
					SearchEntry entry = new SearchEntry(predicate, getSelectedSortTag());
					DefaultListModel listModel = (DefaultListModel) mySoupList.getModel();
					listModel.addElement(entry);
				}
			}
			searchDialog.dispose();
		}
	}

	protected class AddAllListener implements ActionListener {
		public void actionPerformed(ActionEvent _event) {
      JDialog allDialog = new JDialog(SoupEditorDialog.this, "All", true);
      
      JPanel layoutPanel = new JPanel();
      layoutPanel.setLayout(new BorderLayout());
      layoutPanel.add(mySortTagsPanel, BorderLayout.SOUTH);

      ConfirmationPanel confirmationPanel = new ConfirmationPanel(layoutPanel);
      confirmationPanel.addOkListener(new CloseDialogListener(allDialog));
      confirmationPanel.addCancelListener(new CloseDialogListener(allDialog));
      allDialog.getContentPane().add(confirmationPanel);
      allDialog.pack();
      DialogUtils.centerWindow(allDialog);
      allDialog.setVisible(true);
      int confirmation = confirmationPanel.getSelectedOption();
      if (confirmation == ConfirmationPanel.OK_OPTION) {
  			AllEntry entry = new AllEntry(getSelectedSortTag());
  			DefaultListModel listModel = (DefaultListModel) mySoupList.getModel();
  			listModel.addElement(entry);
      }
      allDialog.dispose();
		}
	}

  protected class AddSortListener implements ActionListener {
    public void actionPerformed(ActionEvent _event) {
      JDialog sortDialog = new JDialog(SoupEditorDialog.this, "Sort Tracks", true);
      
      JPanel layoutPanel = new JPanel();
      layoutPanel.setLayout(new BorderLayout());
      layoutPanel.add(mySortTagsPanel, BorderLayout.SOUTH);

      ConfirmationPanel confirmationPanel = new ConfirmationPanel(layoutPanel);
      confirmationPanel.addOkListener(new CloseDialogListener(sortDialog));
      confirmationPanel.addCancelListener(new CloseDialogListener(sortDialog));
      sortDialog.getContentPane().add(confirmationPanel);
      sortDialog.pack();
      DialogUtils.centerWindow(sortDialog);
      sortDialog.setVisible(true);
      int confirmation = confirmationPanel.getSelectedOption();
      if (confirmation == ConfirmationPanel.OK_OPTION) {
        SortEntry entry = new SortEntry(getSelectedSortTag());
        DefaultListModel listModel = (DefaultListModel) mySoupList.getModel();
        listModel.addElement(entry);
      }
      sortDialog.dispose();
    }
  }

	protected class DeleteListener implements ActionListener {
		public void actionPerformed(ActionEvent _event) {
			int selectedIndex = mySoupList.getSelectedIndex();
			if (selectedIndex != -1) {
				DefaultListModel listModel = (DefaultListModel) mySoupList.getModel();
				listModel.removeElementAt(selectedIndex);
			}
		}
	}

	protected class ClearListener implements ActionListener {
		public void actionPerformed(ActionEvent _event) {
			DefaultListModel listModel = (DefaultListModel) mySoupList.getModel();
			listModel.removeAllElements();
		}
	}

	protected static abstract class AbstractTagEntry {
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

	protected static class BasicTagEntry extends AbstractTagEntry {
		public BasicTagEntry(NodeTag _tag, NodeTag _sortTag) {
			super(_tag, _sortTag);
		}

		public String toString() {
			return "By " + getTag().getDescription() + ", sorted by " + getSortTag().getDescription();
		}
	}

	protected static class FirstLetterTagEntry extends AbstractTagEntry {
		public FirstLetterTagEntry(NodeTag _tag, NodeTag _sortTag) {
			super(_tag, _sortTag);
		}

		public String toString() {
			return "First Letter of " + getTag().getDescription() + ", sorted by " + getSortTag().getDescription();
		}
	}

	protected static class RangeTagEntry extends AbstractTagEntry {
		private TextRange[] myTextRanges;

		public RangeTagEntry(NodeTag _tag, String _textRanges, NodeTag _sortTag) throws ParseException {
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

	protected static class SearchEntry {
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
			return "Results of " + myPredicate.toString() + ", sorted by " + mySortTag.getDescription();
		}
	}

	protected static class AllEntry extends SearchEntry {
		public AllEntry(NodeTag _sortTag) {
			super(new EqualsPredicate(new TagValue(DatabaseTags.TYPE_TAG), new LiteralValue(DatabaseTags.TYPE_TUNE)), _sortTag);
		}

		public String toString() {
			return "All, sorted by " + getSortTag().getDescription();
		}
	}

  protected static class SortEntry {
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
}
