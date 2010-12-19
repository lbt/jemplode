package org.jempeg.empeg.manager.dialog;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jempeg.nodestore.WendyFilters;

/**
 * WendyPanel contains a TabbedPane with
 * a WendyFlagsPanel and a WendyFiltersPanel.
 * 
 * @author Mike Schrag
 */
public class WendyPanel extends JPanel {
	private WendyFlagsPanel myFlagsPanel;
	private WendyFiltersPanel myFiltersPanel;
	
	public WendyPanel(WendyFilters _filters) {
		setLayout(new BorderLayout());
		
		myFlagsPanel = new WendyFlagsPanel(_filters);
		myFiltersPanel = new WendyFiltersPanel(_filters);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Flags", myFlagsPanel);
		tabbedPane.addTab("Filters", myFiltersPanel);
		
		add(tabbedPane, BorderLayout.CENTER);
		
		tabbedPane.addChangeListener(new TabChangeListener());
	}
	
	public void setWendyFilters(WendyFilters _filters) {
		myFlagsPanel.setWendyFilters(_filters);
		myFiltersPanel.setWendyFilters(_filters);
	}
	
	public WendyFilters getWendyFilters() {
		return myFiltersPanel.getWendyFilters();
	}
	
	protected class TabChangeListener implements ChangeListener {
		public void stateChanged(ChangeEvent _event) {
			myFiltersPanel.reloadCurrentFilter();
		}
	}
}
