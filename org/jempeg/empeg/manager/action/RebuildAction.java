package org.jempeg.empeg.manager.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.jempeg.ApplicationContext;
import org.jempeg.empeg.protocol.EmpegProtocolClient;

import com.inzyme.progress.SilentProgressListener;
import com.inzyme.util.Debug;

public class RebuildAction extends AbstractAction {
  private ApplicationContext myContext;

  public RebuildAction(ApplicationContext _context) {
    myContext = _context;
  }

  public void actionPerformed(ActionEvent _event) {
    Thread t = new Thread(new Runnable() {
      public void run() {
        try {
          EmpegProtocolClient empegProtocolClient = (EmpegProtocolClient) myContext.getSynchronizeClient().getProtocolClient(new SilentProgressListener());
          empegProtocolClient.rebuildPlayerDatabase(0);
        }
        catch (Exception e) {
          Debug.println(e);
        }
      }
    });
    t.start();
  }
}