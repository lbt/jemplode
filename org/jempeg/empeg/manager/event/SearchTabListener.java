/* SearchTabListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.event;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.TreePath;

import org.jempeg.empeg.manager.dialog.SearchPanel;

public class SearchTabListener implements ChangeListener
{
    private JTree myTree;
    private SearchPanel mySearchPanel;
    private TreePath myLastSelection;
    
    public SearchTabListener(JTree _tree, SearchPanel _searchPanel) {
	myTree = _tree;
	mySearchPanel = _searchPanel;
    }
    
    public void stateChanged(ChangeEvent _event) {
	JTabbedPane tabbedPane = (JTabbedPane) _event.getSource();
	if (tabbedPane.getSelectedComponent() == mySearchPanel) {
	    if (myLastSelection == null)
		myLastSelection = myTree.getSelectionPath();
	    myTree.clearSelection();
	    mySearchPanel.showLastSearchResults();
	} else if (myLastSelection != null) {
	    myTree.setSelectionPath(myLastSelection);
	    myLastSelection = null;
	}
    }
}
