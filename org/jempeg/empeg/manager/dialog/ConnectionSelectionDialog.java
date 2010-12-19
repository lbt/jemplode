/**
 * Copyright (c) 2001, Mike Schrag & Daniel Zimmerman
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of Mike Schrag, Daniel Zimmerman, nor the names of any
 * other contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.jempeg.empeg.manager.dialog;

import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jempeg.ApplicationContext;
import org.jempeg.empeg.manager.DiscoveryManager;
import org.jempeg.empeg.protocol.EmpegSynchronizeClient;
import org.jempeg.manager.SynchronizeUI;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.protocol.IConnectionFactory;
import org.jempeg.protocol.ISynchronizeClient;
import org.jempeg.protocol.NoConnectionFactory;
import org.jempeg.protocol.ProtocolException;
import org.jempeg.protocol.discovery.BasicDevice;
import org.jempeg.protocol.discovery.IDevice;
import org.jempeg.protocol.discovery.IDiscoverer;
import org.jempeg.protocol.discovery.IDiscoveryListener;
import org.jempeg.protocol.discovery.SSDPDevice;

import com.inzyme.ui.DialogUtils;
import com.inzyme.util.Debug;
import com.rio.protocol2.PearlSynchronizeClient;

/**
 * This is the dialog that lets the user select a new empeg
 * connection from a list of discovered connections.
 *
 * @version 2.0a1 $Date: 2004/09/21 03:10:46 $
 * @author Mike Schrag
 * @author Daniel M. Zimmerman
 **/
public class ConnectionSelectionDialog extends JDialog {
  private JList myConnectionList;
  private JButton myOKButton;
  private JButton myRefreshButton;
  private boolean mySelectionAllowed;
  private boolean myConnectionSelected;
  private DiscoveryManager myDiscoveryManager;

  public ConnectionSelectionDialog(JFrame _frame) throws ProtocolException {
    super(_frame, "Select Connection", true);
    ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    myDiscoveryManager = new DiscoveryManager();
    setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

    myConnectionList = new JList();
    myConnectionList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    myConnectionList.addListSelectionListener(new ListSelectionHandler());
    myConnectionList.addMouseListener(new DoubleClickAction());

    JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 5, 0));
    myOKButton = new JButton("OK");
    setSelectionAllowed(false);
    myOKButton.addActionListener(new OKAction());

    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(new CancelAction());

    myRefreshButton = new JButton("Refresh");
    myRefreshButton.addActionListener(new RefreshAction());

    JButton optionsButton = new JButton("Options...");
    optionsButton.addActionListener(new OptionsAction());
    buttonPanel.add("OK", myOKButton);
    buttonPanel.add("Cancel", cancelButton);
    buttonPanel.add("Refresh", myRefreshButton);
    buttonPanel.add("Options...", optionsButton);

    Box vbox = Box.createVerticalBox();
    vbox.add(new JScrollPane(myConnectionList));
    vbox.add(Box.createVerticalStrut(15));
    vbox.add(buttonPanel);
    getContentPane().add(vbox);

    getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    pack();

    DialogUtils.centerWindow(this);
    addWindowListener(new WindowCloseListener());
  }

  /**
   * Shows a ConnectionSelectionDialog and sets the selected
   * connection on the EmplodeContext.
   * 
   * @param _context the context to save connections to
   * @throws IOException if the connection cannot be changed
   */
  public static void showConnectionSelectionDialog(ApplicationContext _context) throws ProtocolException {
    ConnectionSelectionDialog csd = new ConnectionSelectionDialog(_context.getFrame());
    csd.refreshInBackground();
    csd.setVisible(true);
    boolean isConnectionSelected = csd.isConnectionSelected();
    csd.cancel();
    if (isConnectionSelected) {
      IConnectionFactory connFactory = csd.getConnectionFactory();
      if (connFactory != null) {
        PlayerDatabase playerDatabase = new PlayerDatabase();
        IDevice device = csd.getDevice();
        ISynchronizeClient synchronizeClient;
        if (device instanceof SSDPDevice) {
          synchronizeClient = new PearlSynchronizeClient(connFactory);
          playerDatabase.setCreateUnattachedItems(false);
        }
        else {
          synchronizeClient = new EmpegSynchronizeClient(connFactory);
          playerDatabase.setCreateUnattachedItems(true);
        }
        _context.setSynchronizeClient(playerDatabase, synchronizeClient);
        new SynchronizeUI(playerDatabase, synchronizeClient, _context.getFrame()).downloadInBackground(_context.getDownloadProgressListener());
      }
    }
  }

  /**
   * Sets whether or not the user is allowed to select a connection
   * from the list.  If selection is not allowed, then the OK button
   * will be grayed out.  This would be disabled if there are not yet
   * any valid connections discovered.
   * 
   * @param _selectionAllowed whether or not selection is allowed
   */
  protected void setSelectionAllowed(boolean _selectionAllowed) {
    if (!_selectionAllowed) {
      myOKButton.setEnabled(false);
    }
    mySelectionAllowed = _selectionAllowed;
  }

  /**
   * Returns whether or not the user is allowed to select a connection.
   * 
   * @return whether or not the user is allowed to select a connection
   */
  protected boolean isSelectionAllowed() {
    return mySelectionAllowed && !myConnectionList.isSelectionEmpty();
  }

  /**
   * Returns whether or not the user actually selected a connection.
   * 
   * @return whether or not the user actually selected a connection
   */
  public boolean isConnectionSelected() {
    return myConnectionSelected;
  }

  /**
   * Returns the connection that the user selected.  You should
   * check isConnectionSelected() first.
   * 
   * @return the connection that the user selected
   */
  public IConnectionFactory getConnectionFactory() {
    return getDevice().getConnectionFactory();
  }

  /**
   * Returns the currently selected device.
   */
  public IDevice getDevice() {
    int selectedIndex = myConnectionList.getSelectedIndex();
    ConnectionListModel listModel = (ConnectionListModel) myConnectionList.getModel();
    IDevice device = listModel.getDeviceAt(selectedIndex);
    return device;
  }

  /**
   * Called when the user selects a connection.
   */
  protected void selectConnection() {
    myConnectionSelected = true;
  }

  /**
   * Called when the user cancels the discovery process.
   */
  protected void cancel() {
    myConnectionSelected = false;
    myDiscoveryManager.cancel();
  }

  /**
   * Called when the user selects to view the options dialog.
   */
  protected void options() {
    ConnectionOptionsDialog optionsDialog = new ConnectionOptionsDialog((JFrame) getParent(), myDiscoveryManager);
    optionsDialog.setVisible(true);

    if (optionsDialog.isApproved()) {
      refreshInBackground();
    }
  }

  /**
   * Refreshes the set of connections by kicking off another
   * discovery.
   */
  protected void refresh() {
    getGlassPane().setVisible(true);
    setTitle("Looking for Devices...");
    try {
      if (myDiscoveryManager == null) {
        myDiscoveryManager = new DiscoveryManager();
      }

      ConnectionListModel listModel = new ConnectionListModel();
      myConnectionList.setModel(listModel);
      myDiscoveryManager.refresh(new AllowSelectionListener(listModel));
      if (!myDiscoveryManager.isCancelled() && isVisible()) {
        int empegsFound = myConnectionList.getModel().getSize();
        if (empegsFound == 0) {
          listModel.addElement(new BasicDevice("No Devices Found", new NoConnectionFactory()));
          setTitle("No Devices Found");
          setSelectionAllowed(false);
        }
        else {
          setTitle("Select a Device");
          if (empegsFound == 1 && myDiscoveryManager.isAutoSelectEnabled()) {
            myConnectionList.setSelectedIndex(0);
            myOKButton.doClick();
          }
        }
      }
    }
    catch (Exception e) {
      Debug.println(e);
    }
    finally {
      getGlassPane().setVisible(false);
    }
  }

  /**
   * Calls refresh() in a background thread.
   */
  public void refreshInBackground() {
    Thread t = new Thread(new RefreshRunnable(), "jEmplode: Refresh Connections");
    t.start();
  }

  /**
   * Listens for discovered devices and enables
   * user selection when at least one is available.  This
   * also proxies a ConnectionListModel and passes the
   * event through.  The only reason this proxies is
   * because we want events to be scoped to the single refresh
   * call rather than globally scoped to the DiscoveryManager.
   */
  protected class AllowSelectionListener implements IDiscoveryListener {
    private ConnectionListModel myListModel;

    public AllowSelectionListener(ConnectionListModel _listModel) {
      myListModel = _listModel;
    }

    public void deviceDiscovered(IDiscoverer _discoverer, IDevice _device) {
      setSelectionAllowed(true);
      myListModel.deviceDiscovered(_discoverer, _device);
    }
  }

  /**
   * The OK Button is enabled if 1) there is at least one device
   * available to be selected and 2) the user has actually selected
   * one of those valid devices.  It's this listener's job to
   * turn on/off the OK button.
   */
  protected class ListSelectionHandler implements ListSelectionListener {
    public void valueChanged(ListSelectionEvent _event) {
      myOKButton.setEnabled(isSelectionAllowed());
    }
  }

  /**
   * Refreshes in a background thread.
   */
  protected class RefreshRunnable implements Runnable {
    public void run() {
      refresh();
    }
  }

  /**
   * Handles a user double-clicking on a connection..
   */
  protected class DoubleClickAction extends MouseAdapter {
    public void mousePressed(MouseEvent _event) {
      if (isSelectionAllowed() && _event.getClickCount() == 2) {
        selectConnection();
        setVisible(false);
      }
    }
  }

  /**
   * Handles a user pressing the OK button.
   */
  protected class OKAction extends AbstractAction {
    public void actionPerformed(ActionEvent _event) {
      if (isSelectionAllowed()) {
        selectConnection();
        setVisible(false);
      }
    }
  }

  /**
   * Handles the user pressing the Cancel button.
   */
  protected class CancelAction extends AbstractAction {
    public void actionPerformed(ActionEvent _event) {
      cancel();
      setVisible(false);
    }
  }

  /**
   * Handles the user pressing the Refresh button.
   */
  protected class RefreshAction extends AbstractAction {
    public void actionPerformed(ActionEvent _eent) {
      refreshInBackground();
    }
  }

  /**
   * Handles the user pressing the Options button.
   */
  protected class OptionsAction extends AbstractAction {
    public void actionPerformed(ActionEvent _event) {
      options();
    }
  }

  /**
   * Handles the user hitting the X on the dialog.
   */
  protected class WindowCloseListener extends WindowAdapter {
    public void windowClosing(WindowEvent _event) {
      cancel();
      setVisible(false);
    }
  }
}