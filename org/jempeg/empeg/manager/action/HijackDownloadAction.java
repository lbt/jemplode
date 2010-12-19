package org.jempeg.empeg.manager.action;

import org.jempeg.ApplicationContext;
import org.jempeg.empeg.util.HijackUtils;
import org.jempeg.manager.action.DownloadAction;
import org.jempeg.protocol.IConnection;

/**
 * @author Mike Schrag
 */
public class HijackDownloadAction extends DownloadAction {

	/**
	 * Constructor for HijackDownloadAction.
	 * @param _context
	 */
	public HijackDownloadAction(ApplicationContext _context) {
		super(_context);
	}

	protected boolean shouldUseHijack(IConnection _conn) {
		boolean useHijack = (HijackUtils.shouldUseHijack(_conn) == null);
		return useHijack;
	}

}
