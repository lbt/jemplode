package org.jempeg.empeg.logoedit.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import org.jempeg.empeg.logoedit.EmpegScreen;
import org.jempeg.empeg.logoedit.ImageUtils;

import com.inzyme.util.Debug;

/**
* Saves an EmpegScreen to a file.
*
* @author Mike Schrag
*/
public class SaveScreenGrabAction implements ActionListener {
  private EmpegScreen myScreen;
  
  public SaveScreenGrabAction(EmpegScreen _screen) {
    myScreen = _screen;
  }
  
  public void actionPerformed(ActionEvent _event) {
    try {
      ImageUtils.savePngImage(myScreen);
    }
    catch (IOException e) {
      Debug.println(e);
    }
  }
}
