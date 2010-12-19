/* TunePropertiesDialog - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.dialog;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.ui.ConfirmationPanel;
import com.inzyme.ui.DialogUtils;

import org.jempeg.nodestore.IFIDNode;

public class TunePropertiesDialog extends AbstractPropertiesDialog
{
    private FIDNodeEditorPanel myNodeEditorPanel;
    private FIDNodeInfoPanel myNodeInfoPanel;
    private FIDNodeStatisticsPanel myNodeStatsPanel;
    private FIDNodeWendyPanel myNodeWendyPanel;
    private FIDNodeTagsPanel myNodeTagsPanel;
    
    public TunePropertiesDialog(JFrame _frame) {
	super(_frame,
	      ResourceBundleUtils.getUIString("tuneProperties.frameTitle"));
	DialogUtils.setInitiallyFocusedComponent(this,
						 myNodeEditorPanel
						     .getFirstComponent());
    }
    
    protected ConfirmationPanel createConfirmationPanel
	(JTabbedPane _tabbedPane) {
	ConfirmationPanel confirmationPanel
	    = new ConfirmationPanel(_tabbedPane);
	return confirmationPanel;
    }
    
    public void setNodes(IFIDNode[] _nodes) {
	super.setNodes(_nodes);
	if (myNodeEditorPanel != null)
	    myNodeEditorPanel.setNodes(_nodes);
	if (myNodeInfoPanel != null)
	    myNodeInfoPanel.setNodes(_nodes);
	if (myNodeStatsPanel != null)
	    myNodeStatsPanel.setNodes(_nodes);
	if (myNodeWendyPanel != null)
	    myNodeWendyPanel.setNodes(_nodes);
	if (myNodeTagsPanel != null)
	    myNodeTagsPanel.setNodes(_nodes);
    }
    
    public void ok(boolean _recursive) {
	if (myNodeEditorPanel != null)
	    myNodeEditorPanel.ok(_recursive);
	if (myNodeInfoPanel != null)
	    myNodeInfoPanel.ok();
	if (myNodeWendyPanel != null)
	    myNodeWendyPanel.ok();
	if (myNodeTagsPanel != null)
	    myNodeTagsPanel.ok(_recursive);
	setVisible(false);
    }
    
    public void cancel() {
	setVisible(false);
    }
    
    protected void addTabs(JTabbedPane _tabbedPane) {
	String tabs = ResourceBundleUtils.getUIString("tuneProperties.tabs");
	if (tabs.indexOf("details") != -1) {
	    myNodeEditorPanel = new FIDNodeEditorPanel();
	    _tabbedPane.addTab(ResourceBundleUtils
				   .getUIString("properties.tab.details.text"),
			       myNodeEditorPanel);
	}
	if (tabs.indexOf("information") != -1) {
	    myNodeInfoPanel = new FIDNodeInfoPanel();
	    _tabbedPane.addTab((ResourceBundleUtils.getUIString
				("properties.tab.information.text")),
			       myNodeInfoPanel);
	}
	if (tabs.indexOf("statistics") != -1) {
	    myNodeStatsPanel = new FIDNodeStatisticsPanel();
	    _tabbedPane.addTab((ResourceBundleUtils.getUIString
				("properties.tab.statistics.text")),
			       myNodeStatsPanel);
	}
	if (tabs.indexOf("wendy") != -1) {
	    myNodeWendyPanel = new FIDNodeWendyPanel();
	    _tabbedPane.addTab(ResourceBundleUtils
				   .getUIString("properties.tab.wendy.text"),
			       myNodeWendyPanel);
	}
	if (tabs.indexOf("advanced") != -1) {
	    myNodeTagsPanel = new FIDNodeTagsPanel();
	    _tabbedPane.addTab((ResourceBundleUtils.getUIString
				("properties.tab.advanced.text")),
			       myNodeTagsPanel);
	}
    }
}
