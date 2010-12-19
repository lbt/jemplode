/* Main - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager;
import java.io.File;
import java.net.URL;

import com.inzyme.properties.PropertiesManager;
import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.ui.UIUtils;
import com.inzyme.util.ApplicationUtils;
import com.inzyme.util.Debug;

import org.jempeg.ApplicationContext;
import org.jempeg.JEmplodeProperties;
import org.jempeg.empeg.manager.dialog.ConnectionSelectionDialog;
import org.jempeg.empeg.tags.JOrbisTagExtractor;
import org.jempeg.empeg.tags.TFFID3TagWriter;
import org.jempeg.manager.ui.Splash;
import org.jempeg.protocol.ProtocolException;
import org.jempeg.tags.ASFTagExtractor;
import org.jempeg.tags.ITagExtractor;
import org.jempeg.tags.TagExtractorFactory;
import org.jempeg.tags.TagWriterFactory;
import org.jempeg.tags.id3.ID3TagExtractor;

public class Main
{
    /*synthetic*/ static Class class$0;
    
    protected static void initializeTagExtractors() {
	try {
	    Class.forName("com.rio.rmmlite.RmmlMain");
	    TagExtractorFactory.addTagExtractor
		((ITagExtractor)
		 Class.forName("com.rio.tags.FlacTagExtractor").newInstance());
	    TagExtractorFactory.addTagExtractor(new ID3TagExtractor());
	    TagExtractorFactory.addTagExtractor(new ASFTagExtractor());
	    TagExtractorFactory.addTagExtractor
		((ITagExtractor)
		 Class.forName("com.rio.tags.OggTagExtractor").newInstance());
	} catch (Throwable t) {
	    TagExtractorFactory.addTagExtractor(new ID3TagExtractor());
	    TagExtractorFactory.addTagExtractor(new ASFTagExtractor());
	    TagExtractorFactory.addTagExtractor(new JOrbisTagExtractor());
	}
    }
    
    protected static void initializeTagWriters() {
	TagWriterFactory.addTagWriter(new TFFID3TagWriter());
    }
    
    public static void start() {
	final ApplicationContext context = new ApplicationContext();
	final EmplodeUI emplodeUI = new EmplodeUI(context);
	emplodeUI.getFrame().setVisible(true);
	Thread t = new Thread(new Runnable() {
	    public void run() {
		try {
		    ConnectionSelectionDialog
			.showConnectionSelectionDialog(context);
		} catch (ProtocolException e) {
		    Debug.handleError(emplodeUI.getFrame(), e, true);
		}
	    }
	}, "Connection Selection");
	t.start();
    }
    
    public static void main(String[] _args) throws Throwable {
	PropertiesManager.initializeInstance("jEmplode Properties",
					     new File(ApplicationUtils
							  .getSettingsFolder(),
						      "jempegrc"));
	JEmplodeProperties.initializeDefaults();
	UIUtils.initializeOSX("jEmplode");
	ResourceBundleUtils.putBaseName("errors",
					"org.jempeg.empeg.manager.errors");
	ResourceBundleUtils.putBaseName("ui", "org.jempeg.empeg.manager.ui");
	Splash splash = null;
	try {
	    Class var_class = class$0;
	    if (var_class == null) {
		Class var_class_0_;
		try {
		    var_class_0_
			= Class.forName("org.jempeg.empeg.manager.Main");
		} catch (ClassNotFoundException classnotfoundexception) {
		    NoClassDefFoundError noclassdeffounderror
			= new NoClassDefFoundError;
		    ((UNCONSTRUCTED)noclassdeffounderror).NoClassDefFoundError
			(classnotfoundexception.getMessage());
		    throw noclassdeffounderror;
		}
		var_class = class$0 = var_class_0_;
	    }
	    URL splashImageURL
		= (var_class.getResource
		   ("/org/jempeg/empeg/manager/icons/splash.jpg"));
	    splash = new Splash(splashImageURL);
	    splash.show();
	} catch (Throwable t) {
	    t.printStackTrace();
	}
	for (int i = 0; i < _args.length; i++) {
	    if (_args[i].equalsIgnoreCase("-nometal"))
		PropertiesManager.getInstance()
		    .setProperty("jempeg.lookAndFeel", "system");
	}
	initializeTagExtractors();
	initializeTagWriters();
	start();
	if (splash != null)
	    splash.hide();
    }
}
