/* SoupUtils - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.soup;
import java.io.IOException;

import com.inzyme.properties.PropertiesManager;
import com.inzyme.text.CollationKeyCache;
import com.inzyme.util.Debug;

import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.PlayerDatabase;

public class SoupUtils
{
    public static final String SOUP_COUNT_PROPERTY = "jempeg.soup.num";
    
    protected static class SoupInitializeRunnable implements Runnable
    {
	private SoupUpdater mySoupUpdater;
	private FIDPlaylist mySoupPlaylist;
	private ISoupListener mySoupListener;
	
	public SoupInitializeRunnable(SoupUpdater _soupUpdater,
				      FIDPlaylist _soupPlaylist,
				      ISoupListener _soupListener) {
	    mySoupUpdater = _soupUpdater;
	    mySoupPlaylist = _soupPlaylist;
	    mySoupListener = _soupListener;
	}
	
	public void run() {
	    long startTime = System.currentTimeMillis();
	    mySoupUpdater.initialize();
	    if (mySoupListener != null)
		mySoupListener.soupInitialized(mySoupUpdater, mySoupPlaylist);
	    Debug.println(4,
			  ("SoupUtils.SoupInitializeRunnable.run: Finished "
			   + SoupLayerFactory
				 .toExternalForm(mySoupUpdater.getSoupLayers())
			   + " in " + (System.currentTimeMillis() - startTime)
			   + "ms"));
	}
    }
    
    public static FIDPlaylist createPersistentSoupPlaylist
	(PlayerDatabase _playerDatabase, String _name,
	 ISoupLayer[] _soupLayers, boolean _threaded) {
	FIDPlaylist soupPlaylist
	    = createSoupPlaylist(_playerDatabase, _name, false, _soupLayers,
				 true);
	attachSoup(soupPlaylist, _soupLayers, _threaded, null);
	FIDPlaylist rootPlaylist = _playerDatabase.getRootPlaylist();
	rootPlaylist.addNode(soupPlaylist, true,
			     CollationKeyCache.createDefaultCache());
	return soupPlaylist;
    }
    
    public static FIDPlaylist createTransientSoupPlaylist
	(PlayerDatabase _playerDatabase, String _name,
	 ISoupLayer[] _soupLayers, boolean _saveForLater, boolean _temporary,
	 boolean _threaded, ISoupListener _soupListener) {
	FIDPlaylist soupPlaylist
	    = createSoupPlaylist(_playerDatabase, _name, true, _soupLayers,
				 !_temporary);
	if (_temporary) {
	    soupPlaylist.getTags().setBooleanValue("jemplode_temporary", true);
	    soupPlaylist.setIdentified(true);
	}
	if (_saveForLater) {
	    try {
		PropertiesManager propertiesManager
		    = PropertiesManager.getInstance();
		int soupNum
		    = propertiesManager.getIntProperty("jempeg.soup.num", 0);
		propertiesManager.setProperty(("jempeg.soup." + soupNum
					       + ".externalForm"),
					      soupPlaylist.getTags()
						  .getValue("soup"));
		propertiesManager.setProperty(("jempeg.soup." + soupNum
					       + ".name"),
					      soupPlaylist.getTitle());
		propertiesManager.setIntProperty("jempeg.soup.num",
						 soupNum + 1);
		propertiesManager.save();
	    } catch (IOException e) {
		Debug.println(e);
	    }
	}
	attachSoup(soupPlaylist, _soupLayers, _threaded, _soupListener);
	return soupPlaylist;
    }
    
    public static void removeTransientSoupPlaylist(int _index) {
	try {
	    PropertiesManager propertiesManager
		= PropertiesManager.getInstance();
	    int soupCount
		= propertiesManager.getIntProperty("jempeg.soup.num", 0);
	    for (int i = 0; i < soupCount; i++) {
		int oldIndex = i;
		if (oldIndex != _index) {
		    int newIndex = oldIndex > _index ? oldIndex - 1 : oldIndex;
		    String oldExternalFormKey
			= "jempeg.soup." + oldIndex + ".externalForm";
		    String newExternalFormKey
			= "jempeg.soup." + newIndex + ".externalForm";
		    String oldNameKey = "jempeg.soup." + oldIndex + ".name";
		    String newNameKey = "jempeg.soup." + newIndex + ".name";
		    String externalForm
			= propertiesManager.getProperty(oldExternalFormKey,
							"");
		    String name
			= propertiesManager.getProperty(oldNameKey, "");
		    propertiesManager.setProperty(newExternalFormKey,
						  externalForm);
		    propertiesManager.setProperty(newNameKey, name);
		}
	    }
	    propertiesManager.setIntProperty("jempeg.soup.num", soupCount - 1);
	    propertiesManager.save();
	} catch (IOException e) {
	    Debug.println(e);
	}
    }
    
    public static void loadTransientSoupPlaylists
	(PlayerDatabase _playerDatabase, boolean _threaded) {
	PropertiesManager propertiesManager = PropertiesManager.getInstance();
	int soupNum = propertiesManager.getIntProperty("jempeg.soup.num", -1);
	do {
	    if (soupNum == -1) {
		try {
		    createTransientSoupPlaylist
			(_playerDatabase, "Artists",
			 SoupLayerFactory.fromExternalForm("tag:artist"), true,
			 false, true, null);
		    createTransientSoupPlaylist
			(_playerDatabase, "Albums",
			 SoupLayerFactory.fromExternalForm("tag:source"), true,
			 false, true, null);
		    createTransientSoupPlaylist
			(_playerDatabase, "Genres",
			 SoupLayerFactory.fromExternalForm("tag:genre"), true,
			 false, true, null);
		    createTransientSoupPlaylist
			(_playerDatabase, "Years",
			 SoupLayerFactory.fromExternalForm("tag:year"), true,
			 false, true, null);
		    createTransientSoupPlaylist
			(_playerDatabase, "All",
			 SoupLayerFactory.fromExternalForm("search:type=tune"),
			 true, false, true, null);
		    break;
		} catch (Throwable t) {
		    Debug.println(t);
		    break;
		}
	    }
	    for (int i = 0; i < soupNum; i++) {
		try {
		    String query
			= propertiesManager.getProperty(("jempeg.soup." + i
							 + ".externalForm"),
							null);
		    if (query != null) {
			String name
			    = propertiesManager.getProperty(("jempeg.soup." + i
							     + ".name"),
							    query);
			createTransientSoupPlaylist
			    (_playerDatabase, name,
			     SoupLayerFactory.fromExternalForm(query), false,
			     false, _threaded, null);
		    }
		} catch (Throwable t) {
		    Debug.println(t);
		}
	    }
	} while (false);
    }
    
    private static FIDPlaylist createSoupPlaylist
	(PlayerDatabase _playerDatabase, String _name, boolean _transient,
	 ISoupLayer[] _soupLayers, boolean _identifyImmediately) {
	String soupExternalForm = SoupLayerFactory.toExternalForm(_soupLayers);
	FIDPlaylist soupPlaylist
	    = new FIDPlaylist(_playerDatabase, _transient);
	if (_name == null || _name.trim().length() == 0)
	    _name = soupExternalForm;
	soupPlaylist.getTags().setValue("title", _name);
	soupPlaylist.getTags().setValue("soup", soupExternalForm);
	soupPlaylist.setSoup(true);
	soupPlaylist.setIdentified(_identifyImmediately);
	Debug.println(4,
		      ("SoupUtils.createSoupPlaylist: Created soup playlist "
		       + soupPlaylist + " in " + _playerDatabase));
	return soupPlaylist;
    }
    
    public static void attachSoup(FIDPlaylist _soupPlaylist,
				  ISoupLayer[] _soupLayers, boolean _threaded,
				  ISoupListener _soupListener) {
	if (!PropertiesManager.getInstance()
		 .getBooleanProperty("jempeg.disableSoupUpdating")) {
	    SoupUpdater soupUpdater
		= new SoupUpdater(_soupPlaylist, _soupLayers);
	    SoupInitializeRunnable runnable
		= new SoupInitializeRunnable(soupUpdater, _soupPlaylist,
					     _soupListener);
	    if (_threaded) {
		Thread t = new Thread(runnable,
				      ("jEmplode: SoupUpdater for "
				       + SoupLayerFactory
					     .toExternalForm(_soupLayers)));
		t.start();
	    } else {
		runnable.run();
		if (_soupListener != null)
		    _soupListener.soupInitialized(soupUpdater, _soupPlaylist);
	    }
	} else
	    Debug.println(8, ("Soup updating is disabled for "
			      + SoupLayerFactory.toExternalForm(_soupLayers)));
    }
}
