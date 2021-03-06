package org.jempeg.manager.ui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.UIManager;

import org.jempeg.JEmplodeProperties;
import org.jempeg.manager.util.EmplodeClipboard;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.model.FIDPlaylistTreeNode;

import com.inzyme.properties.PropertiesManager;
import com.inzyme.util.Debug;

/**
 * NodeColorizer is used by the table and tree to set the
 * color of each node that is displayed.
 * 
 * @author Mike Schrag
 */
public class NodeColorizer {
	private static Color DIRTY       = new Color(255, 0, 0);
	private static Color LIGHT_DIRTY = new Color(255, 200, 200);
	private static Color DARK_DIRTY  = DIRTY;
	
	private static Color COLORED       = new Color(100, 100, 255);
	private static Color LIGHT_COLORED = new Color(200, 200, 255);
	private static Color DARK_COLORED  = COLORED;
	
	private static Color DIRTY_AND_COLORED       = new Color(255, 0, 255);
	private static Color LIGHT_DIRTY_AND_COLORED = new Color(255, 200, 255);
	private static Color DARK_DIRTY_AND_COLORED  = DIRTY_AND_COLORED;
	
//	private static Color SOUP_COLOR = Color.darkGray;
	
	private static int myUseDarkSelectionColor = 0;
	
	private static boolean myRecursiveColors;
	
	private static boolean myRecursiveSoupColors;
	
	static {
		reloadColorProperties();
	}
	
	/**
	 * Reloads the configuration options that affect colorization of playlists.
	 */
	public static void reloadColorProperties() {
		try {
			PropertiesManager propertiesManager = PropertiesManager.getInstance();
			myRecursiveColors = propertiesManager.getBooleanProperty(JEmplodeProperties.RECURSIVE_COLORS_PROPERTY);
			myRecursiveSoupColors = propertiesManager.getBooleanProperty(JEmplodeProperties.RECURSIVE_SOUP_COLORS_PROPERTY);
		}
		catch (Throwable t) {
			Debug.println(t);
		}
	}
	
	protected static boolean shouldUseDarkSelectionColor(Component _comp) {
		if (myUseDarkSelectionColor == 0) {
			Color color;
			if (_comp instanceof JTree) {
				color = UIManager.getColor("Tree.selectionForeground");
				if (color == null) {
					color = _comp.getForeground();
				}
			} else if (_comp instanceof JTable) {
				color = ((JTable)_comp).getSelectionForeground();
			} else {
				color = _comp.getForeground();
			}
			shouldUseDarkSelectionColor(color);
		}
		return (myUseDarkSelectionColor == 1);
		
	}
	
	protected static boolean shouldUseDarkSelectionColor(Color _color) {
		if (myUseDarkSelectionColor == 0) {
			int y = ((299 * _color.getRed() + 587 * _color.getGreen() + 114 * _color.getGreen()) / 1000);
			if (y < 127) {
				myUseDarkSelectionColor = 1;
			} else {
				myUseDarkSelectionColor = -1;
			}
		}
		return (myUseDarkSelectionColor == 1);
	}
		
	/**
	 * Set the foreground of the component based on the given
	 * tree node (or use the default color if there isn't a 
	 * distinguising feature of the node).
	 * 
	 * @param _comp the component to set the foreground of
	 * @param _value the value to colorize
	 * @param _defaultColor the color to default to
	 */
	public static void colorize(Component _comp, Object _value, Color _defaultColor, boolean _alternate) {
		IFIDNode fidNode;
    if (_value instanceof FIDPlaylistTreeNode) {
      FIDPlaylistTreeNode playlistTreeNode = (FIDPlaylistTreeNode)_value;
      fidNode = playlistTreeNode.getPlaylist();
    } else if (_value instanceof IFIDNode) {
    	fidNode = (IFIDNode)_value;
    } else {
    	fidNode = null;
    }
    
  	Color foreground;
    if (fidNode != null) {
    	boolean soup = (fidNode instanceof FIDPlaylist && ((FIDPlaylist)fidNode).isSoup());
    	boolean transientSoup = (soup && ((FIDPlaylist)fidNode).isTransient());
    	boolean recursive = (soup && myRecursiveSoupColors) || (!soup && myRecursiveColors);
    	
    	int attributes = fidNode.getAttributes(recursive);
    	boolean dirty = (attributes & IFIDNode.ATTRIBUTE_DIRTY) != 0;
    	boolean colored = (attributes & IFIDNode.ATTRIBUTE_COLORED) != 0;
    	
      if (EmplodeClipboard.getInstance().isCut(fidNode)) {
        foreground = Color.lightGray;
      } else if (dirty && colored) {
				if (_alternate) {
					foreground = shouldUseDarkSelectionColor(_comp) ? NodeColorizer.DARK_DIRTY_AND_COLORED : NodeColorizer.LIGHT_DIRTY_AND_COLORED;
				} else {
		   		foreground = NodeColorizer.DIRTY_AND_COLORED;
				}
			} else if (dirty) {
				if (_alternate) {
					foreground = shouldUseDarkSelectionColor(_comp) ? NodeColorizer.DARK_DIRTY : NodeColorizer.LIGHT_DIRTY;
				} else {
		   		foreground = NodeColorizer.DIRTY;
				}
			} else if (colored) {
				if (_alternate) {
					foreground = shouldUseDarkSelectionColor(_comp) ? NodeColorizer.DARK_COLORED : NodeColorizer.LIGHT_COLORED;
				} else {
		   		foreground = NodeColorizer.COLORED;
				}
      } else if (soup && !transientSoup) {
      	if (_alternate) {
					foreground = _defaultColor;
      	} else {
      		foreground = NodeColorizer.COLORED;
      	}
      } else {
        foreground = _defaultColor;
      }
    } else {
      foreground = _defaultColor;
    }
    
  	_comp.setForeground(foreground);
	}
}
