package org.jempeg.manager.dialog;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jempeg.ApplicationContext;
import org.jempeg.manager.action.DatabaseChangeCancelAction;
import org.jempeg.manager.action.PauseSynchronizeAction;
import org.jempeg.manager.action.SynchronizeAction;
import org.jempeg.manager.event.DefaultPopupListener;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.nodestore.SynchronizeQueueTableModel;
import org.jempeg.nodestore.event.IContextListener;
import org.jempeg.protocol.ISynchronizeClient;

import com.inzyme.container.ContainerSelection;
import com.inzyme.container.IContainer;
import com.inzyme.progress.IProgressListener;
import com.inzyme.ui.UIUtils;

public class SynchronizeQueuePanel extends JPanel implements IContextListener {
	private ApplicationContext myContext;
	private JTable mySynchronizeQueueTable;
	private JComponent mySynchronizeProgressBar;
	private JScrollPane myScrollPane;
	private JPanel myButtonPanel;
	private boolean myKeepHistory;

	public SynchronizeQueuePanel(ApplicationContext _context, boolean _dualProgressBars) {
		myContext = _context;
		mySynchronizeQueueTable = new JTable();
		
		JPopupMenu synchronizeQueueMenu = new JPopupMenu();
		final JMenuItem cancelDatabaseChange = UIUtils.createMenuItem("cancel");
		cancelDatabaseChange.addActionListener(new DatabaseChangeCancelAction(myContext, mySynchronizeQueueTable));
		synchronizeQueueMenu.add(cancelDatabaseChange);
		mySynchronizeQueueTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent _event) {
				if (!_event.getValueIsAdjusting()) {
					cancelDatabaseChange.setEnabled(mySynchronizeQueueTable.getSelectedRowCount() > 0);
				}
			}
		});
		mySynchronizeQueueTable.addMouseListener(new DefaultPopupListener(synchronizeQueueMenu));
		myScrollPane = new JScrollPane(mySynchronizeQueueTable);
		
		setLayout(new BorderLayout());
		add(myScrollPane, BorderLayout.CENTER);

		JPanel syncControlPanel = new JPanel();
		myButtonPanel = new JPanel();
		JButton transferButton = new JButton();
		UIUtils.initializeButton(transferButton, "transferButton");

		JButton cancelButton = new JButton();
		UIUtils.initializeButton(cancelButton, "cancel");

		transferButton.addActionListener(new SynchronizeAction(myContext));
		cancelButton.addActionListener(new PauseSynchronizeAction(myContext));
		myButtonPanel.add(transferButton);
		myButtonPanel.add(cancelButton);
		
		syncControlPanel.setLayout(new BorderLayout());
		syncControlPanel.add(myButtonPanel, BorderLayout.NORTH);
		
		if (_dualProgressBars) {
			mySynchronizeProgressBar = new ProgressPanel(true, false);
			mySynchronizeProgressBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			_context.setSynchronizeProgressListener((IProgressListener)mySynchronizeProgressBar);
		} else {
			mySynchronizeProgressBar = new JProgressBar();
		}
		syncControlPanel.add(mySynchronizeProgressBar, BorderLayout.SOUTH);
		add(syncControlPanel, BorderLayout.SOUTH);
		
		myContext.addContextListener(this);
	}
	
	public JPanel getButtonPanel() {
		return myButtonPanel;
	}
	
	public SynchronizeQueueTableModel getSynchronizeQueueTableModel() {
		return (SynchronizeQueueTableModel)mySynchronizeQueueTable.getModel();
	}
	
	public void databaseChanged(PlayerDatabase _oldPlayerDatabase, PlayerDatabase _newPlayerDatabase) {
		if (_newPlayerDatabase != null) {
			ISynchronizeClient synchronizeClient = myContext.getSynchronizeClient();
			SynchronizeQueueTableModel syncTableModel = new SynchronizeQueueTableModel(_newPlayerDatabase.getSynchronizeQueue(), synchronizeClient);
			syncTableModel.setKeepHistory(myKeepHistory);
			mySynchronizeQueueTable.setModel(syncTableModel);
			if (mySynchronizeProgressBar instanceof JProgressBar) {
				((JProgressBar)mySynchronizeProgressBar).setModel(syncTableModel.getCompletionModel());
			}
		}
	}
	
	public void selectedContainerChanged(IContainer _oldSelectedContainer, IContainer _newSelectedContainer) {
	}
	
	public void selectionChanged(Object _source, ContainerSelection _oldSelection, ContainerSelection _newSelection) {
	}
	
	public void synchronizeClientChanged(ISynchronizeClient _oldSynchronizeClient, ISynchronizeClient _newSynchronizeClient) {
	}
	
	public void setKeepHistory(boolean _keepHistory) {
		myKeepHistory = _keepHistory;
		myScrollPane.getVerticalScrollBar().setValue(0);
		Object tableModel = mySynchronizeQueueTable.getModel();
		if (tableModel instanceof SynchronizeQueueTableModel) {
			SynchronizeQueueTableModel syncQueueTableModel = (SynchronizeQueueTableModel) tableModel;
			syncQueueTableModel.setKeepHistory(_keepHistory);
		}
	}
}
