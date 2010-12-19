/* PlaylistTreeCellRenderer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.ui;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import com.inzyme.container.IMutableTypeContainer;
import com.inzyme.text.ResourceBundleUtils;

public class PlaylistTreeCellRenderer implements TreeCellRenderer
{
    private TreeCellRenderer myOriginalRenderer;
    public static Icon[] ICONS;
    /*synthetic*/ static Class class$0;
    
    static {
	Icon[] icons = new Icon[14];
	int i = 0;
	ImageIcon imageicon = new ImageIcon;
	Class var_class = class$0;
	if (var_class == null) {
	    Class var_class_0_;
	    try {
		var_class_0_
		    = (Class.forName
		       ("org.jempeg.manager.ui.PlaylistTreeCellRenderer"));
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class = class$0 = var_class_0_;
	}
	((UNCONSTRUCTED)imageicon).ImageIcon
	    (var_class.getResource(ResourceBundleUtils
				       .getUIString("fidNodeType.root.icon")));
	icons[i] = imageicon;
	int i_1_ = 1;
	ImageIcon imageicon_2_ = new ImageIcon;
	Class var_class_3_ = class$0;
	if (var_class_3_ == null) {
	    Class var_class_4_;
	    try {
		var_class_4_
		    = (Class.forName
		       ("org.jempeg.manager.ui.PlaylistTreeCellRenderer"));
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_3_ = class$0 = var_class_4_;
	}
	((UNCONSTRUCTED)imageicon_2_).ImageIcon
	    (var_class_3_.getResource
	     (ResourceBundleUtils.getUIString("fidNodeType.folder.icon")));
	icons[i_1_] = imageicon_2_;
	int i_5_ = 2;
	ImageIcon imageicon_6_ = new ImageIcon;
	Class var_class_7_ = class$0;
	if (var_class_7_ == null) {
	    Class var_class_8_;
	    try {
		var_class_8_
		    = (Class.forName
		       ("org.jempeg.manager.ui.PlaylistTreeCellRenderer"));
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_7_ = class$0 = var_class_8_;
	}
	((UNCONSTRUCTED)imageicon_6_).ImageIcon
	    (var_class_7_.getResource
	     (ResourceBundleUtils.getUIString("fidNodeType.playlist.icon")));
	icons[i_5_] = imageicon_6_;
	int i_9_ = 3;
	ImageIcon imageicon_10_ = new ImageIcon;
	Class var_class_11_ = class$0;
	if (var_class_11_ == null) {
	    Class var_class_12_;
	    try {
		var_class_12_
		    = (Class.forName
		       ("org.jempeg.manager.ui.PlaylistTreeCellRenderer"));
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_11_ = class$0 = var_class_12_;
	}
	((UNCONSTRUCTED)imageicon_10_).ImageIcon
	    (var_class_11_.getResource
	     (ResourceBundleUtils.getUIString("fidNodeType.tune.icon")));
	icons[i_9_] = imageicon_10_;
	int i_13_ = 4;
	ImageIcon imageicon_14_ = new ImageIcon;
	Class var_class_15_ = class$0;
	if (var_class_15_ == null) {
	    Class var_class_16_;
	    try {
		var_class_16_
		    = (Class.forName
		       ("org.jempeg.manager.ui.PlaylistTreeCellRenderer"));
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_15_ = class$0 = var_class_16_;
	}
	((UNCONSTRUCTED)imageicon_14_).ImageIcon
	    (var_class_15_.getResource
	     (ResourceBundleUtils.getUIString("fidNodeType.tunes.icon")));
	icons[i_13_] = imageicon_14_;
	int i_17_ = 5;
	ImageIcon imageicon_18_ = new ImageIcon;
	Class var_class_19_ = class$0;
	if (var_class_19_ == null) {
	    Class var_class_20_;
	    try {
		var_class_20_
		    = (Class.forName
		       ("org.jempeg.manager.ui.PlaylistTreeCellRenderer"));
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_19_ = class$0 = var_class_20_;
	}
	((UNCONSTRUCTED)imageicon_18_).ImageIcon
	    (var_class_19_.getResource
	     (ResourceBundleUtils.getUIString("fidNodeType.genre.icon")));
	icons[i_17_] = imageicon_18_;
	int i_21_ = 6;
	ImageIcon imageicon_22_ = new ImageIcon;
	Class var_class_23_ = class$0;
	if (var_class_23_ == null) {
	    Class var_class_24_;
	    try {
		var_class_24_
		    = (Class.forName
		       ("org.jempeg.manager.ui.PlaylistTreeCellRenderer"));
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_23_ = class$0 = var_class_24_;
	}
	((UNCONSTRUCTED)imageicon_22_).ImageIcon
	    (var_class_23_.getResource
	     (ResourceBundleUtils.getUIString("fidNodeType.genres.icon")));
	icons[i_21_] = imageicon_22_;
	int i_25_ = 7;
	ImageIcon imageicon_26_ = new ImageIcon;
	Class var_class_27_ = class$0;
	if (var_class_27_ == null) {
	    Class var_class_28_;
	    try {
		var_class_28_
		    = (Class.forName
		       ("org.jempeg.manager.ui.PlaylistTreeCellRenderer"));
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_27_ = class$0 = var_class_28_;
	}
	((UNCONSTRUCTED)imageicon_26_).ImageIcon
	    (var_class_27_.getResource
	     (ResourceBundleUtils.getUIString("fidNodeType.year.icon")));
	icons[i_25_] = imageicon_26_;
	int i_29_ = 8;
	ImageIcon imageicon_30_ = new ImageIcon;
	Class var_class_31_ = class$0;
	if (var_class_31_ == null) {
	    Class var_class_32_;
	    try {
		var_class_32_
		    = (Class.forName
		       ("org.jempeg.manager.ui.PlaylistTreeCellRenderer"));
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_31_ = class$0 = var_class_32_;
	}
	((UNCONSTRUCTED)imageicon_30_).ImageIcon
	    (var_class_31_.getResource
	     (ResourceBundleUtils.getUIString("fidNodeType.years.icon")));
	icons[i_29_] = imageicon_30_;
	int i_33_ = 9;
	ImageIcon imageicon_34_ = new ImageIcon;
	Class var_class_35_ = class$0;
	if (var_class_35_ == null) {
	    Class var_class_36_;
	    try {
		var_class_36_
		    = (Class.forName
		       ("org.jempeg.manager.ui.PlaylistTreeCellRenderer"));
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_35_ = class$0 = var_class_36_;
	}
	((UNCONSTRUCTED)imageicon_34_).ImageIcon
	    (var_class_35_.getResource
	     (ResourceBundleUtils.getUIString("fidNodeType.album.icon")));
	icons[i_33_] = imageicon_34_;
	int i_37_ = 10;
	ImageIcon imageicon_38_ = new ImageIcon;
	Class var_class_39_ = class$0;
	if (var_class_39_ == null) {
	    Class var_class_40_;
	    try {
		var_class_40_
		    = (Class.forName
		       ("org.jempeg.manager.ui.PlaylistTreeCellRenderer"));
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_39_ = class$0 = var_class_40_;
	}
	((UNCONSTRUCTED)imageicon_38_).ImageIcon
	    (var_class_39_.getResource
	     (ResourceBundleUtils.getUIString("fidNodeType.albums.icon")));
	icons[i_37_] = imageicon_38_;
	int i_41_ = 11;
	ImageIcon imageicon_42_ = new ImageIcon;
	Class var_class_43_ = class$0;
	if (var_class_43_ == null) {
	    Class var_class_44_;
	    try {
		var_class_44_
		    = (Class.forName
		       ("org.jempeg.manager.ui.PlaylistTreeCellRenderer"));
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_43_ = class$0 = var_class_44_;
	}
	((UNCONSTRUCTED)imageicon_42_).ImageIcon
	    (var_class_43_.getResource
	     (ResourceBundleUtils.getUIString("fidNodeType.artist.icon")));
	icons[i_41_] = imageicon_42_;
	int i_45_ = 12;
	ImageIcon imageicon_46_ = new ImageIcon;
	Class var_class_47_ = class$0;
	if (var_class_47_ == null) {
	    Class var_class_48_;
	    try {
		var_class_48_
		    = (Class.forName
		       ("org.jempeg.manager.ui.PlaylistTreeCellRenderer"));
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_47_ = class$0 = var_class_48_;
	}
	((UNCONSTRUCTED)imageicon_46_).ImageIcon
	    (var_class_47_.getResource
	     (ResourceBundleUtils.getUIString("fidNodeType.artists.icon")));
	icons[i_45_] = imageicon_46_;
	int i_49_ = 13;
	ImageIcon imageicon_50_ = new ImageIcon;
	Class var_class_51_ = class$0;
	if (var_class_51_ == null) {
	    Class var_class_52_;
	    try {
		var_class_52_
		    = (Class.forName
		       ("org.jempeg.manager.ui.PlaylistTreeCellRenderer"));
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_51_ = class$0 = var_class_52_;
	}
	((UNCONSTRUCTED)imageicon_50_).ImageIcon
	    (var_class_51_.getResource
	     (ResourceBundleUtils.getUIString("fidNodeType.playlist.icon")));
	icons[i_49_] = imageicon_50_;
	ICONS = icons;
    }
    
    public PlaylistTreeCellRenderer(TreeCellRenderer _originalRenderer) {
	myOriginalRenderer = _originalRenderer;
    }
    
    public Component getTreeCellRendererComponent
	(JTree _tree, Object _value, boolean _sel, boolean _expanded,
	 boolean _leaf, int _row, boolean _hasFocus) {
	Component comp
	    = myOriginalRenderer.getTreeCellRendererComponent(_tree, _value,
							      _sel, _expanded,
							      _leaf, _row,
							      _hasFocus);
	if (comp instanceof JLabel
	    && _value instanceof IMutableTypeContainer) {
	    IMutableTypeContainer playlistTreeNode
		= (IMutableTypeContainer) _value;
	    int type = playlistTreeNode.getType();
	    JLabel label = (JLabel) comp;
	    label.setIcon(ICONS[type]);
	}
	NodeColorizer.colorize(comp, _value, comp.getForeground(), _sel);
	return comp;
    }
}
