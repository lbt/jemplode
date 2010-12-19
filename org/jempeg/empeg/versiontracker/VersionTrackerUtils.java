/* VersionTrackerUtils - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.versiontracker;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.inzyme.model.Reason;
import com.inzyme.util.Debug;

import org.jempeg.ApplicationContext;
import org.jempeg.empeg.manager.dialog.VersionChangeDialog;

public class VersionTrackerUtils
{
    public static boolean upgrade(ApplicationContext _context,
				  boolean _autoUpdate) {
	IVersionTracker[] versionTrackers
	    = { new HijackVersionTracker(_context.getDefaultProgressListener(),
					 _context.getSynchronizeClient()
					     .getConnectionFactory()) };
	boolean restartRequired
	    = upgrade(_context, versionTrackers, _autoUpdate);
	return restartRequired;
    }
    
    public static boolean upgrade(ApplicationContext _context,
				  IVersionTracker[] _versionTrackers,
				  boolean _autoUpdate) {
	boolean restartRequired = false;
	for (int i = 0; !restartRequired && i < _versionTrackers.length; i++) {
	    try {
		restartRequired
		    = upgrade(_context, _versionTrackers[i], _autoUpdate);
	    } catch (Throwable t) {
		Debug.handleError(_context.getFrame(), t, true);
	    }
	}
	if (restartRequired) {
	    if (_autoUpdate)
		JOptionPane.showMessageDialog
		    (_context.getFrame(),
		     "The previous update requires that jEmplode be restarted.  jEmplode will now quit.  Please restart to continue using jEmplode.");
	    else
		JOptionPane.showMessageDialog
		    (_context.getFrame(),
		     "The previous update requires that jEmplode be restarted (or things will break).  Other updates will be skipped until you restart.");
	}
	return restartRequired;
    }
    
    public static boolean upgrade(ApplicationContext _context,
				  IVersionTracker _versionTracker,
				  boolean _autoUpdate) {
	JFrame frame = _context.getFrame();
	boolean restartRequired = false;
	try {
	    try {
		if (!_autoUpdate || _versionTracker.shouldAutoUpdate()) {
		    if (_versionTracker.isInstalled()) {
			if (_versionTracker.isNewerVersionAvailable()) {
			    boolean shouldUpgrade = false;
			    if (_versionTracker.shouldPromptBeforeUpdate()) {
				VersionChangeDialog versionChangeDialog
				    = new VersionChangeDialog(frame,
							      _versionTracker);
				versionChangeDialog.setVisible(true);
				shouldUpgrade
				    = versionChangeDialog.shouldUpgrade();
			    } else
				shouldUpgrade = true;
			    if (shouldUpgrade) {
				_versionTracker.installLatestVersion();
				restartRequired
				    = _versionTracker.isRestartRequired();
				String latestVersion
				    = _versionTracker.getLatestVersion();
				String message
				    = ("Your "
				       + _versionTracker.getProductName()
				       + " was updated to version '"
				       + latestVersion + "'.");
				JOptionPane.showMessageDialog(frame, message);
			    }
			} else if (!_autoUpdate)
			    JOptionPane.showMessageDialog
				(frame,
				 ("Your " + _versionTracker.getProductName()
				  + " is already up-to-date."));
		    } else if (!_autoUpdate) {
			Reason r = (_versionTracker.getFailureReason
				    ("upgrade "
				     + _versionTracker.getProductName()));
			JOptionPane.showMessageDialog(frame, r.getReason());
		    }
		}
	    } catch (Throwable t) {
		Debug.handleError(frame, t, true);
	    }
	} catch (Object object) {
	    if (!_autoUpdate)
		_context.getDefaultProgressListener().progressCompleted();
	    throw object;
	}
	if (!_autoUpdate)
	    _context.getDefaultProgressListener().progressCompleted();
	return restartRequired;
    }
}
