package org.jempeg.manager.action;

import java.awt.event.ActionEvent;
import java.text.ParseException;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.jempeg.ApplicationContext;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.predicate.IPredicate;
import org.jempeg.nodestore.predicate.PredicateParser;
import org.jempeg.nodestore.soup.ISoupLayer;
import org.jempeg.nodestore.soup.SearchSoupLayer;
import org.jempeg.nodestore.soup.SoupUtils;

import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.util.Debug;

public class NewSearchSoupAction extends AbstractAction {
	private ApplicationContext myContext;
	private boolean myAllowPlaylists;

	public NewSearchSoupAction(ApplicationContext _context, boolean _allowPlaylists) {
		myContext = _context;
		myAllowPlaylists = _allowPlaylists;
	}

	public static FIDPlaylist createSearchSoup(ApplicationContext _context, boolean _allowPlaylists) throws ParseException {
		String query = (String) JOptionPane.showInputDialog(_context.getFrame(), ResourceBundleUtils.getUIString("newSearchSoup.prompt"), ResourceBundleUtils.getUIString("newSearchSoup.frameTitle"), JOptionPane.QUESTION_MESSAGE, null, null, ResourceBundleUtils.getUIString("newSearchSoup.title"));
		FIDPlaylist newPlaylist;
		if (query != null) {
			String playlistTitle = query;
			if (!_allowPlaylists) {
				query = "type = tune and (" + query + ")";
			}
			IPredicate predicate = new PredicateParser().parse(query);
			playlistTitle = (String) JOptionPane.showInputDialog(_context.getFrame(), ResourceBundleUtils.getUIString("newPlaylist.prompt"), ResourceBundleUtils.getUIString("newPlaylist.frameTitle"), JOptionPane.QUESTION_MESSAGE, null, null, playlistTitle);
			if (playlistTitle == null) {
				newPlaylist = null;
			} else {
				ISoupLayer[] soupLayers = new ISoupLayer[] { new SearchSoupLayer(predicate) };
				newPlaylist = SoupUtils.createPersistentSoupPlaylist(_context.getPlayerDatabase(), query, soupLayers, false);
			}
		} else {
			newPlaylist = null;
		}
		return newPlaylist;
	}
			
	public void actionPerformed(ActionEvent _event) {
		try {
			NewSearchSoupAction.createSearchSoup(myContext, myAllowPlaylists);
		}
		catch (Throwable t) {
			Debug.handleError(myContext.getFrame(), t, true);
		}
	}
}
