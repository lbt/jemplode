/* PluginsManager - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.plugins;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import com.inzyme.properties.PropertiesManager;
import com.inzyme.util.Debug;
import com.inzyme.util.ReflectionUtils;

import org.jempeg.ApplicationContext;

public class PluginsManager
{
    /*synthetic*/ static Class class$0;
    
    public static JEmplodePlugin[] getPluginActions
	(ApplicationContext _context) {
	Vector actionsVec = new Vector();
	try {
	    String pluginsDirStr
		= PropertiesManager.getInstance()
		      .getProperty("jempeg.pluginsDir", "plugins");
	    File pluginsDir = new File(pluginsDirStr);
	    File[] pluginFiles = pluginsDir.listFiles();
	    if (pluginFiles != null) {
		Vector pluginURLsVec = new Vector();
		for (int i = 0; i < pluginFiles.length; i++) {
		    if (pluginFiles[i].getName().endsWith(".jar"))
			pluginURLsVec.addElement(pluginFiles[i].toURL());
		}
		URL[] pluginURLs = new URL[pluginURLsVec.size()];
		pluginURLsVec.copyInto(pluginURLs);
		URLClassLoader pluginClassLoader
		    = URLClassLoader.newInstance(pluginURLs);
		for (int i = 0; i < pluginURLs.length; i++) {
		    try {
			String name = pluginURLs[i].getPath();
			boolean done = false;
			int actionCount = 1;
			while (!done) {
			    String jmp
				= (name.substring(name.lastIndexOf('/') + 1,
						  name.lastIndexOf('.'))
				   + "-" + actionCount + ".jmp");
			    InputStream is
				= pluginClassLoader.getResourceAsStream(jmp);
			    if (is == null)
				done = true;
			    else {
				Properties pluginProperties = new Properties();
				pluginProperties.load(is);
				String actionClassName
				    = pluginProperties
					  .getProperty("Action-Class");
				if (actionClassName != null) {
				    Class actionClass
					= pluginClassLoader
					      .loadClass(actionClassName);
				    Constructor[] constructors
					= actionClass.getConstructors();
				    JEmplodePlugin action = null;
				    for (int j = 0;
					 (action == null
					  && j < constructors.length);
					 j++) {
					Class[] constructorParams
					    = constructors[j]
						  .getParameterTypes();
					if (constructorParams.length == 1) {
					    Class var_class
						= constructorParams[0];
					    Class var_class_0_ = class$0;
					    if (var_class_0_ == null) {
						Class var_class_1_;
						try {
						    var_class_1_
							= (Class.forName
							   ("org.jempeg.ApplicationContext"));
						} catch (ClassNotFoundException classnotfoundexception) {
						    NoClassDefFoundError noclassdeffounderror
							= new NoClassDefFoundError;
						    ((UNCONSTRUCTED)
						     noclassdeffounderror)
							.NoClassDefFoundError
							(classnotfoundexception
							     .getMessage());
						    throw noclassdeffounderror;
						}
						var_class_0_ = class$0
						    = var_class_1_;
					    }
					    if (var_class == var_class_0_)
						action = ((JEmplodePlugin)
							  (constructors[j]
							       .newInstance
							   (new Object[]
							    { _context })));
					}
				    }
				    if (action == null)
					action = ((JEmplodePlugin)
						  actionClass.newInstance());
				    Enumeration keys = pluginProperties.keys();
				    while (keys.hasMoreElements()) {
					String key
					    = (String) keys.nextElement();
					String value = pluginProperties
							   .getProperty(key);
					action.putValue(key, value);
				    }
				    if (pluginProperties.getProperty("Name")
					== null)
					action.putValue
					    ("Name",
					     ReflectionUtils
						 .getShortName(actionClass));
				    actionsVec.addElement(action);
				}
			    }
			    actionCount++;
			}
		    } catch (Throwable t) {
			Debug.println(t);
		    }
		}
	    }
	} catch (Throwable t) {
	    Debug.println(t);
	}
	JEmplodePlugin[] actions = new JEmplodePlugin[actionsVec.size()];
	actionsVec.copyInto(actions);
	return actions;
    }
    
    public static void main(String[] args) {
	ApplicationContext context = new ApplicationContext();
	JEmplodePlugin[] plugins = getPluginActions(context);
	for (int i = 0; i < plugins.length; i++)
	    System.out.println(plugins[i].getValue("Text"));
    }
}
