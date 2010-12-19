/* NodeTag - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.model;
import java.text.DateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.Vector;

import com.inzyme.format.SizeFormat;
import com.inzyme.format.TimeFormat;
import com.inzyme.properties.PropertiesManager;
import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.text.StringUtils;

import org.jempeg.nodestore.DatabaseTags;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;

public class NodeTag
{
    private String myName;
    private String myDescription;
    private Class myType;
    private Class mySortType;
    private int myIconType;
    private int myGroupIconType;
    private String myDerivedFrom;
    private static NodeTag[] DEFAULTS;
    public static final NodeTag TITLE_TAG;
    public static final NodeTag ARTIST_TAG;
    public static final NodeTag SOURCE_TAG;
    public static final NodeTag GENRE_TAG;
    public static final NodeTag YEAR_TAG;
    public static final NodeTag TRACKNR_TAG;
    private static String TAG_VALUE_VARIOUS;
    private static String TAG_VALUE_MISSING;
    private static Vector myNodeTags;
    private static Hashtable myNameToNodeTag;
    /*synthetic*/ static Class class$0;
    /*synthetic*/ static Class class$1;
    /*synthetic*/ static Class class$2;
    
    static {
	NodeTag[] nodetags = new NodeTag[27];
	int i = 0;
	NodeTag nodetag = new NodeTag;
	String string = "title";
	Class var_class = class$0;
	if (var_class == null) {
	    Class var_class_0_;
	    try {
		var_class_0_ = Class.forName("java.lang.String");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class = class$0 = var_class_0_;
	}
	Class var_class_1_ = class$0;
	if (var_class_1_ == null) {
	    Class var_class_2_;
	    try {
		var_class_2_ = Class.forName("java.lang.String");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_1_ = class$0 = var_class_2_;
	}
	((UNCONSTRUCTED)nodetag).NodeTag(string, var_class, var_class_1_, 2,
					 2);
	nodetags[i] = nodetag;
	int i_3_ = 1;
	NodeTag nodetag_4_ = new NodeTag;
	String string_5_ = "artist";
	Class var_class_6_ = class$0;
	if (var_class_6_ == null) {
	    Class var_class_7_;
	    try {
		var_class_7_ = Class.forName("java.lang.String");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_6_ = class$0 = var_class_7_;
	}
	Class var_class_8_ = class$0;
	if (var_class_8_ == null) {
	    Class var_class_9_;
	    try {
		var_class_9_ = Class.forName("java.lang.String");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_8_ = class$0 = var_class_9_;
	}
	((UNCONSTRUCTED)nodetag_4_).NodeTag(string_5_, var_class_6_,
					    var_class_8_, 11, 12);
	nodetags[i_3_] = nodetag_4_;
	int i_10_ = 2;
	NodeTag nodetag_11_ = new NodeTag;
	String string_12_ = "source";
	Class var_class_13_ = class$0;
	if (var_class_13_ == null) {
	    Class var_class_14_;
	    try {
		var_class_14_ = Class.forName("java.lang.String");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_13_ = class$0 = var_class_14_;
	}
	Class var_class_15_ = class$0;
	if (var_class_15_ == null) {
	    Class var_class_16_;
	    try {
		var_class_16_ = Class.forName("java.lang.String");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_15_ = class$0 = var_class_16_;
	}
	((UNCONSTRUCTED)nodetag_11_).NodeTag(string_12_, var_class_13_,
					     var_class_15_, 9, 10);
	nodetags[i_10_] = nodetag_11_;
	int i_17_ = 3;
	NodeTag nodetag_18_ = new NodeTag;
	String string_19_ = "genre";
	Class var_class_20_ = class$0;
	if (var_class_20_ == null) {
	    Class var_class_21_;
	    try {
		var_class_21_ = Class.forName("java.lang.String");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_20_ = class$0 = var_class_21_;
	}
	Class var_class_22_ = class$0;
	if (var_class_22_ == null) {
	    Class var_class_23_;
	    try {
		var_class_23_ = Class.forName("java.lang.String");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_22_ = class$0 = var_class_23_;
	}
	((UNCONSTRUCTED)nodetag_18_).NodeTag(string_19_, var_class_20_,
					     var_class_22_, 5, 6);
	nodetags[i_17_] = nodetag_18_;
	int i_24_ = 4;
	NodeTag nodetag_25_ = new NodeTag;
	String string_26_ = "year";
	Class var_class_27_ = class$1;
	if (var_class_27_ == null) {
	    Class var_class_28_;
	    try {
		var_class_28_ = Class.forName("java.lang.Integer");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_27_ = class$1 = var_class_28_;
	}
	Class var_class_29_ = class$1;
	if (var_class_29_ == null) {
	    Class var_class_30_;
	    try {
		var_class_30_ = Class.forName("java.lang.Integer");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_29_ = class$1 = var_class_30_;
	}
	((UNCONSTRUCTED)nodetag_25_).NodeTag(string_26_, var_class_27_,
					     var_class_29_, 7, 8);
	nodetags[i_24_] = nodetag_25_;
	int i_31_ = 5;
	NodeTag nodetag_32_ = new NodeTag;
	String string_33_ = "tracknr";
	Class var_class_34_ = class$1;
	if (var_class_34_ == null) {
	    Class var_class_35_;
	    try {
		var_class_35_ = Class.forName("java.lang.Integer");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_34_ = class$1 = var_class_35_;
	}
	Class var_class_36_ = class$1;
	if (var_class_36_ == null) {
	    Class var_class_37_;
	    try {
		var_class_37_ = Class.forName("java.lang.Integer");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_36_ = class$1 = var_class_37_;
	}
	((UNCONSTRUCTED)nodetag_32_).NodeTag(string_33_, var_class_34_,
					     var_class_36_, 2, 2);
	nodetags[i_31_] = nodetag_32_;
	int i_38_ = 6;
	NodeTag nodetag_39_ = new NodeTag;
	String string_40_ = "length";
	Class var_class_41_ = class$0;
	if (var_class_41_ == null) {
	    Class var_class_42_;
	    try {
		var_class_42_ = Class.forName("java.lang.String");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_41_ = class$0 = var_class_42_;
	}
	Class var_class_43_ = class$1;
	if (var_class_43_ == null) {
	    Class var_class_44_;
	    try {
		var_class_44_ = Class.forName("java.lang.Integer");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_43_ = class$1 = var_class_44_;
	}
	((UNCONSTRUCTED)nodetag_39_).NodeTag(string_40_, var_class_41_,
					     var_class_43_, 2, 2);
	nodetags[i_38_] = nodetag_39_;
	int i_45_ = 7;
	NodeTag nodetag_46_ = new NodeTag;
	String string_47_ = "duration";
	Class var_class_48_ = class$0;
	if (var_class_48_ == null) {
	    Class var_class_49_;
	    try {
		var_class_49_ = Class.forName("java.lang.String");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_48_ = class$0 = var_class_49_;
	}
	Class var_class_50_ = class$1;
	if (var_class_50_ == null) {
	    Class var_class_51_;
	    try {
		var_class_51_ = Class.forName("java.lang.Integer");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_50_ = class$1 = var_class_51_;
	}
	((UNCONSTRUCTED)nodetag_46_).NodeTag(string_47_, var_class_48_,
					     var_class_50_, 2, 2);
	nodetags[i_45_] = nodetag_46_;
	int i_52_ = 8;
	NodeTag nodetag_53_ = new NodeTag;
	String string_54_ = "pos";
	Class var_class_55_ = class$1;
	if (var_class_55_ == null) {
	    Class var_class_56_;
	    try {
		var_class_56_ = Class.forName("java.lang.Integer");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_55_ = class$1 = var_class_56_;
	}
	Class var_class_57_ = class$1;
	if (var_class_57_ == null) {
	    Class var_class_58_;
	    try {
		var_class_58_ = Class.forName("java.lang.Integer");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_57_ = class$1 = var_class_58_;
	}
	((UNCONSTRUCTED)nodetag_53_).NodeTag(string_54_, var_class_55_,
					     var_class_57_, 2, 2);
	nodetags[i_52_] = nodetag_53_;
	int i_59_ = 9;
	NodeTag nodetag_60_ = new NodeTag;
	String string_61_ = "bitrate";
	Class var_class_62_ = class$0;
	if (var_class_62_ == null) {
	    Class var_class_63_;
	    try {
		var_class_63_ = Class.forName("java.lang.String");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_62_ = class$0 = var_class_63_;
	}
	Class var_class_64_ = class$0;
	if (var_class_64_ == null) {
	    Class var_class_65_;
	    try {
		var_class_65_ = Class.forName("java.lang.String");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_64_ = class$0 = var_class_65_;
	}
	((UNCONSTRUCTED)nodetag_60_).NodeTag(string_61_, var_class_62_,
					     var_class_64_, 2, 2);
	nodetags[i_59_] = nodetag_60_;
	int i_66_ = 10;
	NodeTag nodetag_67_ = new NodeTag;
	String string_68_ = "samplerate";
	Class var_class_69_ = class$0;
	if (var_class_69_ == null) {
	    Class var_class_70_;
	    try {
		var_class_70_ = Class.forName("java.lang.String");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_69_ = class$0 = var_class_70_;
	}
	Class var_class_71_ = class$0;
	if (var_class_71_ == null) {
	    Class var_class_72_;
	    try {
		var_class_72_ = Class.forName("java.lang.String");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_71_ = class$0 = var_class_72_;
	}
	((UNCONSTRUCTED)nodetag_67_).NodeTag(string_68_, var_class_69_,
					     var_class_71_, 2, 2);
	nodetags[i_66_] = nodetag_67_;
	int i_73_ = 11;
	NodeTag nodetag_74_ = new NodeTag;
	String string_75_ = "marked";
	Class var_class_76_ = class$0;
	if (var_class_76_ == null) {
	    Class var_class_77_;
	    try {
		var_class_77_ = Class.forName("java.lang.String");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_76_ = class$0 = var_class_77_;
	}
	Class var_class_78_ = class$0;
	if (var_class_78_ == null) {
	    Class var_class_79_;
	    try {
		var_class_79_ = Class.forName("java.lang.String");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_78_ = class$0 = var_class_79_;
	}
	((UNCONSTRUCTED)nodetag_74_).NodeTag(string_75_, var_class_76_,
					     var_class_78_, 2, 2);
	nodetags[i_73_] = nodetag_74_;
	int i_80_ = 12;
	NodeTag nodetag_81_ = new NodeTag;
	String string_82_ = "play_count";
	Class var_class_83_ = class$1;
	if (var_class_83_ == null) {
	    Class var_class_84_;
	    try {
		var_class_84_ = Class.forName("java.lang.Integer");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_83_ = class$1 = var_class_84_;
	}
	Class var_class_85_ = class$1;
	if (var_class_85_ == null) {
	    Class var_class_86_;
	    try {
		var_class_86_ = Class.forName("java.lang.Integer");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_85_ = class$1 = var_class_86_;
	}
	((UNCONSTRUCTED)nodetag_81_).NodeTag(string_82_, var_class_83_,
					     var_class_85_, 2, 2);
	nodetags[i_80_] = nodetag_81_;
	int i_87_ = 13;
	NodeTag nodetag_88_ = new NodeTag;
	String string_89_ = "skip_count";
	Class var_class_90_ = class$1;
	if (var_class_90_ == null) {
	    Class var_class_91_;
	    try {
		var_class_91_ = Class.forName("java.lang.Integer");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_90_ = class$1 = var_class_91_;
	}
	Class var_class_92_ = class$1;
	if (var_class_92_ == null) {
	    Class var_class_93_;
	    try {
		var_class_93_ = Class.forName("java.lang.Integer");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_92_ = class$1 = var_class_93_;
	}
	((UNCONSTRUCTED)nodetag_88_).NodeTag(string_89_, var_class_90_,
					     var_class_92_, 2, 2);
	nodetags[i_87_] = nodetag_88_;
	int i_94_ = 14;
	NodeTag nodetag_95_ = new NodeTag;
	String string_96_ = "play_last";
	Class var_class_97_ = class$0;
	if (var_class_97_ == null) {
	    Class var_class_98_;
	    try {
		var_class_98_ = Class.forName("java.lang.String");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_97_ = class$0 = var_class_98_;
	}
	Class var_class_99_ = class$2;
	if (var_class_99_ == null) {
	    Class var_class_100_;
	    try {
		var_class_100_ = Class.forName("java.lang.Long");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_99_ = class$2 = var_class_100_;
	}
	((UNCONSTRUCTED)nodetag_95_).NodeTag(string_96_, var_class_97_,
					     var_class_99_, 2, 2);
	nodetags[i_94_] = nodetag_95_;
	int i_101_ = 15;
	NodeTag nodetag_102_ = new NodeTag;
	String string_103_ = "ctime";
	Class var_class_104_ = class$0;
	if (var_class_104_ == null) {
	    Class var_class_105_;
	    try {
		var_class_105_ = Class.forName("java.lang.String");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_104_ = class$0 = var_class_105_;
	}
	Class var_class_106_ = class$2;
	if (var_class_106_ == null) {
	    Class var_class_107_;
	    try {
		var_class_107_ = Class.forName("java.lang.Long");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_106_ = class$2 = var_class_107_;
	}
	((UNCONSTRUCTED)nodetag_102_).NodeTag(string_103_, var_class_104_,
					      var_class_106_, 2, 2);
	nodetags[i_101_] = nodetag_102_;
	int i_108_ = 16;
	NodeTag nodetag_109_ = new NodeTag;
	String string_110_ = "comment";
	Class var_class_111_ = class$0;
	if (var_class_111_ == null) {
	    Class var_class_112_;
	    try {
		var_class_112_ = Class.forName("java.lang.String");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_111_ = class$0 = var_class_112_;
	}
	Class var_class_113_ = class$0;
	if (var_class_113_ == null) {
	    Class var_class_114_;
	    try {
		var_class_114_ = Class.forName("java.lang.String");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_113_ = class$0 = var_class_114_;
	}
	((UNCONSTRUCTED)nodetag_109_).NodeTag(string_110_, var_class_111_,
					      var_class_113_, 2, 2);
	nodetags[i_108_] = nodetag_109_;
	int i_115_ = 17;
	NodeTag nodetag_116_ = new NodeTag;
	String string_117_ = "codec";
	Class var_class_118_ = class$0;
	if (var_class_118_ == null) {
	    Class var_class_119_;
	    try {
		var_class_119_ = Class.forName("java.lang.String");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_118_ = class$0 = var_class_119_;
	}
	Class var_class_120_ = class$0;
	if (var_class_120_ == null) {
	    Class var_class_121_;
	    try {
		var_class_121_ = Class.forName("java.lang.String");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_120_ = class$0 = var_class_121_;
	}
	((UNCONSTRUCTED)nodetag_116_).NodeTag(string_117_, var_class_118_,
					      var_class_120_, 2, 2);
	nodetags[i_115_] = nodetag_116_;
	int i_122_ = 18;
	NodeTag nodetag_123_ = new NodeTag;
	String string_124_ = "copyright";
	Class var_class_125_ = class$0;
	if (var_class_125_ == null) {
	    Class var_class_126_;
	    try {
		var_class_126_ = Class.forName("java.lang.String");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_125_ = class$0 = var_class_126_;
	}
	Class var_class_127_ = class$0;
	if (var_class_127_ == null) {
	    Class var_class_128_;
	    try {
		var_class_128_ = Class.forName("java.lang.String");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_127_ = class$0 = var_class_128_;
	}
	((UNCONSTRUCTED)nodetag_123_).NodeTag(string_124_, var_class_125_,
					      var_class_127_, 2, 2);
	nodetags[i_122_] = nodetag_123_;
	int i_129_ = 19;
	NodeTag nodetag_130_ = new NodeTag;
	String string_131_ = "offset";
	Class var_class_132_ = class$1;
	if (var_class_132_ == null) {
	    Class var_class_133_;
	    try {
		var_class_133_ = Class.forName("java.lang.Integer");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_132_ = class$1 = var_class_133_;
	}
	Class var_class_134_ = class$1;
	if (var_class_134_ == null) {
	    Class var_class_135_;
	    try {
		var_class_135_ = Class.forName("java.lang.Integer");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_134_ = class$1 = var_class_135_;
	}
	((UNCONSTRUCTED)nodetag_130_).NodeTag(string_131_, var_class_132_,
					      var_class_134_, 2, 2);
	nodetags[i_129_] = nodetag_130_;
	int i_136_ = 20;
	NodeTag nodetag_137_ = new NodeTag;
	String string_138_ = "fid";
	Class var_class_139_ = class$1;
	if (var_class_139_ == null) {
	    Class var_class_140_;
	    try {
		var_class_140_ = Class.forName("java.lang.Integer");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_139_ = class$1 = var_class_140_;
	}
	Class var_class_141_ = class$1;
	if (var_class_141_ == null) {
	    Class var_class_142_;
	    try {
		var_class_142_ = Class.forName("java.lang.Integer");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_141_ = class$1 = var_class_142_;
	}
	((UNCONSTRUCTED)nodetag_137_).NodeTag(string_138_, var_class_139_,
					      var_class_141_, 2, 2);
	nodetags[i_136_] = nodetag_137_;
	int i_143_ = 21;
	NodeTag nodetag_144_ = new NodeTag;
	String string_145_ = "refs";
	Class var_class_146_ = class$1;
	if (var_class_146_ == null) {
	    Class var_class_147_;
	    try {
		var_class_147_ = Class.forName("java.lang.Integer");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_146_ = class$1 = var_class_147_;
	}
	Class var_class_148_ = class$1;
	if (var_class_148_ == null) {
	    Class var_class_149_;
	    try {
		var_class_149_ = Class.forName("java.lang.Integer");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_148_ = class$1 = var_class_149_;
	}
	((UNCONSTRUCTED)nodetag_144_).NodeTag(string_145_, var_class_146_,
					      var_class_148_, 2, 2);
	nodetags[i_143_] = nodetag_144_;
	int i_150_ = 22;
	NodeTag nodetag_151_ = new NodeTag;
	String string_152_ = "wendy";
	Class var_class_153_ = class$0;
	if (var_class_153_ == null) {
	    Class var_class_154_;
	    try {
		var_class_154_ = Class.forName("java.lang.String");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_153_ = class$0 = var_class_154_;
	}
	Class var_class_155_ = class$0;
	if (var_class_155_ == null) {
	    Class var_class_156_;
	    try {
		var_class_156_ = Class.forName("java.lang.String");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_155_ = class$0 = var_class_156_;
	}
	((UNCONSTRUCTED)nodetag_151_).NodeTag(string_152_, var_class_153_,
					      var_class_155_, 2, 2);
	nodetags[i_150_] = nodetag_151_;
	int i_157_ = 23;
	NodeTag nodetag_158_ = new NodeTag;
	String string_159_ = "decade";
	Class var_class_160_ = class$1;
	if (var_class_160_ == null) {
	    Class var_class_161_;
	    try {
		var_class_161_ = Class.forName("java.lang.Integer");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_160_ = class$1 = var_class_161_;
	}
	Class var_class_162_ = class$1;
	if (var_class_162_ == null) {
	    Class var_class_163_;
	    try {
		var_class_163_ = Class.forName("java.lang.Integer");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_162_ = class$1 = var_class_163_;
	}
	((UNCONSTRUCTED)nodetag_158_).NodeTag(string_159_, var_class_160_,
					      var_class_162_, 7, 8, "year");
	nodetags[i_157_] = nodetag_158_;
	int i_164_ = 24;
	NodeTag nodetag_165_ = new NodeTag;
	String string_166_ = "pin";
	Class var_class_167_ = class$0;
	if (var_class_167_ == null) {
	    Class var_class_168_;
	    try {
		var_class_168_ = Class.forName("java.lang.String");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_167_ = class$0 = var_class_168_;
	}
	Class var_class_169_ = class$0;
	if (var_class_169_ == null) {
	    Class var_class_170_;
	    try {
		var_class_170_ = Class.forName("java.lang.String");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_169_ = class$0 = var_class_170_;
	}
	((UNCONSTRUCTED)nodetag_165_).NodeTag(string_166_, var_class_167_,
					      var_class_169_, 2, 2);
	nodetags[i_164_] = nodetag_165_;
	int i_171_ = 25;
	NodeTag nodetag_172_ = new NodeTag;
	String string_173_ = "size";
	Class var_class_174_ = class$1;
	if (var_class_174_ == null) {
	    Class var_class_175_;
	    try {
		var_class_175_ = Class.forName("java.lang.Integer");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_174_ = class$1 = var_class_175_;
	}
	Class var_class_176_ = class$1;
	if (var_class_176_ == null) {
	    Class var_class_177_;
	    try {
		var_class_177_ = Class.forName("java.lang.Integer");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_176_ = class$1 = var_class_177_;
	}
	((UNCONSTRUCTED)nodetag_172_).NodeTag(string_173_, var_class_174_,
					      var_class_176_, 2, 2);
	nodetags[i_171_] = nodetag_172_;
	int i_178_ = 26;
	NodeTag nodetag_179_ = new NodeTag;
	String string_180_ = "tracks";
	Class var_class_181_ = class$1;
	if (var_class_181_ == null) {
	    Class var_class_182_;
	    try {
		var_class_182_ = Class.forName("java.lang.Integer");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_181_ = class$1 = var_class_182_;
	}
	Class var_class_183_ = class$1;
	if (var_class_183_ == null) {
	    Class var_class_184_;
	    try {
		var_class_184_ = Class.forName("java.lang.Integer");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_183_ = class$1 = var_class_184_;
	}
	((UNCONSTRUCTED)nodetag_179_).NodeTag(string_180_, var_class_181_,
					      var_class_183_, 2, 2);
	nodetags[i_178_] = nodetag_179_;
	DEFAULTS = nodetags;
	TITLE_TAG = DEFAULTS[0];
	ARTIST_TAG = DEFAULTS[1];
	SOURCE_TAG = DEFAULTS[2];
	GENRE_TAG = DEFAULTS[3];
	YEAR_TAG = DEFAULTS[4];
	TRACKNR_TAG = DEFAULTS[5];
	resetDefaultNodeTags();
    }
    
    public NodeTag(String _name, Class _type, Class _sortType, int _iconType,
		   int _groupIconType) {
	this(_name, null, _type, _sortType, _iconType, _groupIconType);
    }
    
    public NodeTag(String _name, String _description, Class _type,
		   Class _sortType, int _iconType, int _groupIconType) {
	this(_name, _description, _type, _sortType, _iconType, _groupIconType,
	     null);
    }
    
    public NodeTag(String _name, Class _type, Class _sortType, int _iconType,
		   int _groupIconType, String _derivedFrom) {
	this(_name, null, _type, _sortType, _iconType, _groupIconType,
	     _derivedFrom);
    }
    
    public NodeTag(String _name, String _description, Class _type,
		   Class _sortType, int _iconType, int _groupIconType,
		   String _derivedFrom) {
	myName = _name;
	do {
	    if (_description == null) {
		try {
		    myDescription = ResourceBundle.getBundle
					("org.jempeg.nodestore.model.nodeTag")
					.getString(_name);
		    break;
		} catch (Throwable e) {
		    myDescription = _name;
		    break;
		}
	    }
	    myDescription = _description;
	} while (false);
	myType = _type;
	mySortType = _sortType;
	myIconType = _iconType;
	myGroupIconType = _groupIconType;
	myDerivedFrom = _derivedFrom;
    }
    
    public String getName() {
	return myName;
    }
    
    public String getDescription() {
	return myDescription;
    }
    
    public Class getType() {
	return myType;
    }
    
    public Class getSortType() {
	return mySortType;
    }
    
    public int getIconType() {
	return myIconType;
    }
    
    public int getGroupIconType() {
	return myGroupIconType;
    }
    
    public String getStringValue(FIDPlaylist _parentPlaylist,
				 int _childIndex) {
	String value
	    = TagValueRetriever.getValue(_parentPlaylist, _childIndex, myName);
	return value;
    }
    
    public String getStringValue(IFIDNode _node) {
	String value = TagValueRetriever.getValue(_node, myName);
	return value;
    }
    
    public Object getValue(FIDPlaylist _parentPlaylist, int _childIndex) {
	Object value = toValue(getStringValue(_parentPlaylist, _childIndex));
	return value;
    }
    
    public Object getValue(IFIDNode _node) {
	Object value = toValue(getStringValue(_node));
	return value;
    }
    
    public Object getDisplayValue(FIDPlaylist _parentPlaylist,
				  int _childIndex) {
	String originalValue = getStringValue(_parentPlaylist, _childIndex);
	return toDisplayValue(toValue(originalValue), originalValue);
    }
    
    public Object getDisplayValue(IFIDNode _node) {
	String originalValue = getStringValue(_node);
	return toDisplayValue(toValue(originalValue), originalValue);
    }
    
    public Object getDisplayValue(String _value) {
	return toDisplayValue(toValue(_value), _value);
    }
    
    public boolean isDerivedFrom(NodeTag _tag) {
	if (!equals(_tag) && (myDerivedFrom == null || _tag == null
			      || !myDerivedFrom.equals(_tag.myName)))
	    return false;
	return true;
    }
    
    protected Object toDisplayValue(Object _value, String _originalValue) {
	Object displayValue;
	if (myName.equals("duration")) {
	    long durationInMillis = ((Number) _value).longValue();
	    displayValue = TimeFormat.getInstance().format(durationInMillis);
	} else if (myName.equals("length"))
	    displayValue = SizeFormat.getInstance()
			       .format((double) ((Number) _value).longValue());
	else if (myName.equals("play_last") || myName.equals("ctime")) {
	    Date d = new Date(((Number) _value).longValue() * 1000L);
	    displayValue = DateFormat.getDateInstance().format(d);
	} else if (myName.equals("tracknr") && _value instanceof String)
	    displayValue = TagValueRetriever.getTrackNumber((String) _value);
	else if (myName.equals("year"))
	    displayValue = _originalValue;
	else
	    displayValue = _value;
	return displayValue;
    }
    
    public Object toValue(String _strValue) {
	Class var_class = mySortType;
	Class var_class_185_ = class$0;
	if (var_class_185_ == null) {
	    Class var_class_186_;
	    try {
		var_class_186_ = Class.forName("java.lang.String");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_185_ = class$0 = var_class_186_;
	}
	Object value;
	if (var_class == var_class_185_)
	    value = _strValue;
	else {
	    Class var_class_187_ = mySortType;
	    Class var_class_188_ = class$1;
	    if (var_class_188_ == null) {
		Class var_class_189_;
		try {
		    var_class_189_ = Class.forName("java.lang.Integer");
		} catch (ClassNotFoundException classnotfoundexception) {
		    NoClassDefFoundError noclassdeffounderror
			= new NoClassDefFoundError;
		    ((UNCONSTRUCTED)noclassdeffounderror).NoClassDefFoundError
			(classnotfoundexception.getMessage());
		    throw noclassdeffounderror;
		}
		var_class_188_ = class$1 = var_class_189_;
	    }
	    if (var_class_187_ == var_class_188_)
		value = new Integer(StringUtils
					.parseIntWithoutException(_strValue));
	    else {
		Class var_class_190_ = mySortType;
		Class var_class_191_ = class$2;
		if (var_class_191_ == null) {
		    Class var_class_192_;
		    try {
			var_class_192_ = Class.forName("java.lang.Long");
		    } catch (ClassNotFoundException classnotfoundexception) {
			NoClassDefFoundError noclassdeffounderror
			    = new NoClassDefFoundError;
			((UNCONSTRUCTED)noclassdeffounderror)
			    .NoClassDefFoundError
			    (classnotfoundexception.getMessage());
			throw noclassdeffounderror;
		    }
		    var_class_191_ = class$2 = var_class_192_;
		}
		if (var_class_190_ == var_class_191_)
		    value
			= new Long(StringUtils
				       .parseLongWithoutException(_strValue));
		else
		    value = _strValue;
	    }
	}
	return value;
    }
    
    public boolean equals(Object _obj) {
	if (_obj != this && (!(_obj instanceof NodeTag)
			     || !myName.equals(((NodeTag) _obj).myName)))
	    return false;
	return true;
    }
    
    public int hashCode() {
	return myName.hashCode();
    }
    
    public String toString() {
	return myDescription;
    }
    
    public static String getMissingTagValue() {
	if (TAG_VALUE_MISSING == null)
	    TAG_VALUE_MISSING
		= ResourceBundleUtils.getUIString("soup.tagValueMissing");
	return TAG_VALUE_MISSING;
    }
    
    public static String getVariousTagValue() {
	if (TAG_VALUE_VARIOUS == null)
	    TAG_VALUE_VARIOUS
		= ResourceBundleUtils.getUIString("soup.tagValueVarious");
	return TAG_VALUE_VARIOUS;
    }
    
    public static NodeTag[] getNodeTags() {
	NodeTag[] nodeTags = new NodeTag[myNodeTags.size()];
	myNodeTags.copyInto(nodeTags);
	return nodeTags;
    }
    
    protected static void resetDefaultNodeTags() {
	myNodeTags = new Vector();
	myNameToNodeTag = new Hashtable();
	for (int i = 0; i < DEFAULTS.length; i++)
	    addNodeTag(DEFAULTS[i]);
    }
    
    public static void resetNodeTags(DatabaseTags _databaseTags) {
	PropertiesManager propertiesManager = PropertiesManager.getInstance();
	resetDefaultNodeTags();
	Enumeration tagNamesEnum = _databaseTags.getTagNames();
	while (tagNamesEnum.hasMoreElements()) {
	    String tagName = (String) tagNamesEnum.nextElement();
	    if (!containsNodeTag(tagName)) {
		String description
		    = propertiesManager.getProperty(("jempeg.tag." + tagName
						     + ".description"),
						    tagName);
		Class typeClass;
		try {
		    typeClass = Class.forName(propertiesManager.getProperty
					      (("jempeg.tag." + tagName
						+ ".typeClass"),
					       "java.lang.String"));
		} catch (Throwable t) {
		    Class var_class = class$0;
		    if (var_class == null) {
			Class var_class_193_;
			try {
			    var_class_193_ = Class.forName("java.lang.String");
			} catch (ClassNotFoundException classnotfoundexception) {
			    NoClassDefFoundError noclassdeffounderror
				= new NoClassDefFoundError;
			    ((UNCONSTRUCTED)noclassdeffounderror)
				.NoClassDefFoundError
				(classnotfoundexception.getMessage());
			    throw noclassdeffounderror;
			}
			var_class = class$0 = var_class_193_;
		    }
		    typeClass = var_class;
		}
		Class sortClass;
		try {
		    sortClass = Class.forName(propertiesManager.getProperty
					      (("jempeg.tag." + tagName
						+ ".sortClass"),
					       "java.lang.String"));
		} catch (Throwable t) {
		    Class var_class = class$0;
		    if (var_class == null) {
			Class var_class_194_;
			try {
			    var_class_194_ = Class.forName("java.lang.String");
			} catch (ClassNotFoundException classnotfoundexception) {
			    NoClassDefFoundError noclassdeffounderror
				= new NoClassDefFoundError;
			    ((UNCONSTRUCTED)noclassdeffounderror)
				.NoClassDefFoundError
				(classnotfoundexception.getMessage());
			    throw noclassdeffounderror;
			}
			var_class = class$0 = var_class_194_;
		    }
		    sortClass = var_class;
		}
		int iconType
		    = propertiesManager.getIntProperty(("jempeg.tag." + tagName
							+ ".iconType"),
						       2);
		int groupIconType
		    = propertiesManager.getIntProperty(("jempeg.tag." + tagName
							+ ".groupIconType"),
						       2);
		addNodeTag(new NodeTag(tagName, description, typeClass,
				       sortClass, iconType, groupIconType));
	    }
	}
    }
    
    public static void addNodeTag(NodeTag _nodeTag) {
	String name = _nodeTag.getName();
	if (!myNameToNodeTag.containsKey(name)) {
	    myNameToNodeTag.put(name, _nodeTag);
	    myNodeTags.addElement(_nodeTag);
	}
    }
    
    public static boolean containsNodeTag(String _name) {
	boolean contains = myNameToNodeTag.containsKey(_name);
	return contains;
    }
    
    public static NodeTag getNodeTag(String _name) {
	NodeTag nodeTag;
	if (_name == "title")
	    nodeTag = TITLE_TAG;
	else if (_name == "artist")
	    nodeTag = ARTIST_TAG;
	else if (_name == "source")
	    nodeTag = SOURCE_TAG;
	else if (_name == "genre")
	    nodeTag = GENRE_TAG;
	else if (_name == "year")
	    nodeTag = YEAR_TAG;
	else if (_name == "tracknr")
	    nodeTag = TRACKNR_TAG;
	else {
	    nodeTag = (NodeTag) myNameToNodeTag.get(_name);
	    if (nodeTag == null) {
		NodeTag nodetag = new NodeTag;
		String string = _name;
		String string_195_ = _name;
		Class var_class = class$0;
		if (var_class == null) {
		    Class var_class_196_;
		    try {
			var_class_196_ = Class.forName("java.lang.String");
		    } catch (ClassNotFoundException classnotfoundexception) {
			NoClassDefFoundError noclassdeffounderror
			    = new NoClassDefFoundError;
			((UNCONSTRUCTED)noclassdeffounderror)
			    .NoClassDefFoundError
			    (classnotfoundexception.getMessage());
			throw noclassdeffounderror;
		    }
		    var_class = class$0 = var_class_196_;
		}
		Class var_class_197_ = class$0;
		if (var_class_197_ == null) {
		    Class var_class_198_;
		    try {
			var_class_198_ = Class.forName("java.lang.String");
		    } catch (ClassNotFoundException classnotfoundexception) {
			NoClassDefFoundError noclassdeffounderror
			    = new NoClassDefFoundError;
			((UNCONSTRUCTED)noclassdeffounderror)
			    .NoClassDefFoundError
			    (classnotfoundexception.getMessage());
			throw noclassdeffounderror;
		    }
		    var_class_197_ = class$0 = var_class_198_;
		}
		((UNCONSTRUCTED)nodetag).NodeTag(string, string_195_,
						 var_class, var_class_197_, 2,
						 2);
		nodeTag = nodetag;
	    }
	}
	return nodeTag;
    }
}
