package org.jempeg.empeg.manager.event;

import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.TreePath;

import org.jempeg.empeg.manager.dialog.SearchPanel;

/**
 * SearchTabListener keeps the tree and table synchronized
 * as you switch back and forth between the search tab and
 * the playlists tab.
 * 
 * @author Mike Schrag
 */
public class SearchTabListener implements ChangeListener {
	private JTree myTree;
	private SearchPanel mySearchPanel;
	private TreePath myLastSelection;
	
	/**
	 * Constructor for SearchTabListener.
	 */
	public SearchTabListener(JTree _tree, SearchPanel _searchPanel) {
		myTree = _tree;
		mySearchPanel = _searchPanel;
	}

	/**
	 * @see javax.swing.event.ChangeListener#stateChanged(ChangeEvent)
	 */
	public void stateChanged(ChangeEvent _event) {
		JTabbedPane tabbedPane = (JTabbedPane)_event.getSource();
		if (tabbedPane.getSelectedComponent() == mySearchPanel) {
			if (myLastSelection == null) {
				myLastSelection = myTree.getSelectionPath();
			}
			myTree.clearSelection();
			mySearchPanel.showLastSearchResults();
		} else {
			if (myLastSelection != null) {
				myTree.setSelectionPath(myLastSelection);
				myLastSelection = null;
			}
		}
	}
}
