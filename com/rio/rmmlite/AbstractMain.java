/* AbstractMain - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.rmmlite;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;

import com.inzyme.container.ContainerUtils;
import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.util.Debug;
import com.rio.PearlUtils;
import com.rio.protocol2.PearlSynchronizeClient;

import org.jempeg.ApplicationContext;
import org.jempeg.manager.IManagerUI;
import org.jempeg.manager.SynchronizeUI;
import org.jempeg.manager.dialog.ProgressDialog;
import org.jempeg.manager.event.AutoSyncSynchronizeQueueListener;
import org.jempeg.manager.ui.Splash;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.protocol.ConnectionException;
import org.jempeg.protocol.ProtocolException;
import org.jempeg.protocol.SocketConnectionFactory;

public abstract class AbstractMain
{
    protected abstract URL getSplashImage();
    
    protected abstract void initializeTagExtractors();
    
    protected abstract void initializeTagWriters();
    
    protected abstract IManagerUI createUI
	(ApplicationContext applicationcontext, boolean bool);
    
    public void init(String _hostAddress, boolean _applet)
	throws IOException, ConnectionException, ProtocolException {
	Thread collatorLoader = new Thread(new Runnable() {
	    public void run() {
		ContainerUtils.getDefaultCollator();
	    }
	});
	collatorLoader.start();
	boolean hasPrivileges = PearlUtils.initializeApplication();
	Splash splash = null;
	try {
	    splash = new Splash(getSplashImage());
	    splash.show();
	} catch (Throwable t) {
	    t.printStackTrace();
	}
	ApplicationContext context = new ApplicationContext();
	initializeTagExtractors();
	IManagerUI managerUI = createUI(context, _applet);
	if (splash != null)
	    splash.hide();
	managerUI.getFrame().setVisible(true);
	if (!hasPrivileges)
	    Debug.handleError(managerUI.getFrame(),
			      ResourceBundleUtils
				  .getUIString("insufficientPermissions"),
			      true);
	InetAddress hostAddress
	    = PearlUtils.findPearl(new ProgressDialog(managerUI.getFrame(),
						      false, true));
	if (hostAddress == null) {
	    if (!_applet)
		System.exit(0);
	} else {
	    try {
		org.jempeg.protocol.IConnectionFactory connectionFactory
		    = new SocketConnectionFactory(hostAddress, 8302);
		PlayerDatabase playerDatabase = new PlayerDatabase();
		playerDatabase.setNestedPlaylistAllowed(false);
		org.jempeg.protocol.ISynchronizeClient synchronizeClient
		    = new PearlSynchronizeClient(connectionFactory);
		playerDatabase.getSynchronizeQueue()
		    .addSynchronizeQueueListener
		    (new AutoSyncSynchronizeQueueListener(context));
		context.setSynchronizeClient(playerDatabase,
					     synchronizeClient);
		new SynchronizeUI
		    (playerDatabase, synchronizeClient, context.getFrame())
		    .downloadInBackground
		    (context.getDownloadProgressListener());
	    } catch (Throwable t) {
		Debug.handleError(context.getFrame(), t, true);
	    }
	}
    }
}
