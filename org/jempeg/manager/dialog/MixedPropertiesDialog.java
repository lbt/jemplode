/* MixedPropertiesDialog - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.dialog;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import com.inzyme.ui.ConfirmationPanel;
import com.inzyme.ui.DialogUtils;

import org.jempeg.nodestore.IFIDNode;

public class MixedPropertiesDialog extends AbstractPropertiesDialog
{
    private FIDNodeEditorPanel myNodeEditorPanel;
    private FIDNodeTagsPanel myNodeTagsPanel;
    
    public MixedPropertiesDialog(JFrame _frame) {
	super(_frame, "Mixed");
	DialogUtils.setInitiallyFocusedComponent(this,
						 myNodeEditorPanel
						     .getFirstComponent());
    }
    
    protected ConfirmationPanel createConfirmationPanel
	(JTabbedPane _tabbedPane) {
	return createRecursiveConfirmationPanel(_tabbedPane);
    }
    
    public void setNodes(IFIDNode[] _nodes) {
	super.setNodes(_nodes);
	myNodeEditorPanel.setNodes(_nodes);
	myNodeTagsPanel.setNodes(_nodes);
	setVisible(false);
    }
    
    public void ok(boolean _recursive) {
	myNodeEditorPanel.ok(_recursive);
	myNodeTagsPanel.ok(_recursive);
	setVisible(false);
    }
    
    public void cancel() {
	/* empty */
    }
    
    protected void addTabs(JTabbedPane _tabbedPane) {
	myNodeEditorPanel = new FIDNodeEditorPanel();
	_tabbedPane.addTab("Details", myNodeEditorPanel);
	myNodeTagsPanel = new FIDNodeTagsPanel();
	_tabbedPane.addTab("Advanced", myNodeTagsPanel);
    }
}
