/* SaveScreenGrabAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.logoedit.action;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import com.inzyme.util.Debug;

import org.jempeg.empeg.logoedit.EmpegScreen;
import org.jempeg.empeg.logoedit.ImageUtils;

public class SaveScreenGrabAction implements ActionListener
{
    private EmpegScreen myScreen;
    
    public SaveScreenGrabAction(EmpegScreen _screen) {
	myScreen = _screen;
    }
    
    public void actionPerformed(ActionEvent _event) {
	try {
	    ImageUtils.savePngImage(myScreen);
	} catch (IOException e) {
	    Debug.println(e);
	}
    }
}
