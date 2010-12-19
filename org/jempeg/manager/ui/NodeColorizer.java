/* NodeColorizer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.ui;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.UIManager;

import com.inzyme.properties.PropertiesManager;
import com.inzyme.util.Debug;

import org.jempeg.manager.util.EmplodeClipboard;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.model.FIDPlaylistTreeNode;

public class NodeColorizer
{
    private static Color DIRTY = new Color(255, 0, 0);
    private static Color LIGHT_DIRTY = new Color(255, 200, 200);
    private static Color DARK_DIRTY = DIRTY;
    private static Color COLORED = new Color(100, 100, 255);
    private static Color LIGHT_COLORED = new Color(200, 200, 255);
    private static Color DARK_COLORED = COLORED;
    private static Color DIRTY_AND_COLORED = new Color(255, 0, 255);
    private static Color LIGHT_DIRTY_AND_COLORED = new Color(255, 200, 255);
    private static Color DARK_DIRTY_AND_COLORED = DIRTY_AND_COLORED;
    private static int myUseDarkSelectionColor = 0;
    private static boolean myRecursiveColors;
    private static boolean myRecursiveSoupColors;
    
    static {
	reloadColorProperties();
    }
    
    public static void reloadColorProperties() {
	try {
	    PropertiesManager propertiesManager
		= PropertiesManager.getInstance();
	    myRecursiveColors
		= propertiesManager
		      .getBooleanProperty("jempeg.recursiveColors");
	    myRecursiveSoupColors
		= propertiesManager
		      .getBooleanProperty("jempeg.recursiveSoupColors");
	} catch (Throwable t) {
	    Debug.println(t);
	}
    }
    
    protected static boolean shouldUseDarkSelectionColor(Component _comp) {
	if (myUseDarkSelectionColor == 0) {
	    Color color;
	    if (_comp instanceof JTree) {
		color = UIManager.getColor("Tree.selectionForeground");
		if (color == null)
		    color = _comp.getForeground();
	    } else if (_comp instanceof JTable)
		color = ((JTable) _comp).getSelectionForeground();
	    else
		color = _comp.getForeground();
	    shouldUseDarkSelectionColor(color);
	}
	if (myUseDarkSelectionColor == 1)
	    return true;
	return false;
    }
    
    protected static boolean shouldUseDarkSelectionColor(Color _color) {
	if (myUseDarkSelectionColor == 0) {
	    int y = ((299 * _color.getRed() + 587 * _color.getGreen()
		      + 114 * _color.getGreen())
		     / 1000);
	    if (y < 127)
		myUseDarkSelectionColor = 1;
	    else
		myUseDarkSelectionColor = -1;
	}
	if (myUseDarkSelectionColor == 1)
	    return true;
	return false;
    }
    
    public static void colorize(Component _comp, Object _value,
				Color _defaultColor, boolean _alternate) {
	IFIDNode fidNode;
	if (_value instanceof FIDPlaylistTreeNode) {
	    FIDPlaylistTreeNode playlistTreeNode
		= (FIDPlaylistTreeNode) _value;
	    fidNode = playlistTreeNode.getPlaylist();
	} else if (_value instanceof IFIDNode)
	    fidNode = (IFIDNode) _value;
	else
	    fidNode = null;
	Color foreground;
	if (fidNode != null) {
	    boolean soup = (fidNode instanceof FIDPlaylist
			    && ((FIDPlaylist) fidNode).isSoup());
	    boolean transientSoup
		= soup && ((FIDPlaylist) fidNode).isTransient();
	    boolean recursive
		= soup && myRecursiveSoupColors || !soup && myRecursiveColors;
	    int attributes = fidNode.getAttributes(recursive);
	    boolean dirty = (attributes & 0x1) != 0;
	    boolean colored = (attributes & 0x4) != 0;
	    if (EmplodeClipboard.getInstance().isCut(fidNode))
		foreground = Color.lightGray;
	    else if (dirty && colored) {
		if (_alternate)
		    foreground
			= (shouldUseDarkSelectionColor(_comp)
			   ? DARK_DIRTY_AND_COLORED : LIGHT_DIRTY_AND_COLORED);
		else
		    foreground = DIRTY_AND_COLORED;
	    } else if (dirty) {
		if (_alternate)
		    foreground = (shouldUseDarkSelectionColor(_comp)
				  ? DARK_DIRTY : LIGHT_DIRTY);
		else
		    foreground = DIRTY;
	    } else if (colored) {
		if (_alternate)
		    foreground = (shouldUseDarkSelectionColor(_comp)
				  ? DARK_COLORED : LIGHT_COLORED);
		else
		    foreground = COLORED;
	    } else if (soup && !transientSoup) {
		if (_alternate)
		    foreground = _defaultColor;
		else
		    foreground = COLORED;
	    } else
		foreground = _defaultColor;
	} else
	    foreground = _defaultColor;
	_comp.setForeground(foreground);
    }
}
