/* PredicateParser - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.predicate;
import java.lang.reflect.Constructor;
import java.text.ParseException;

import com.inzyme.util.Debug;

import org.jempeg.nodestore.DatabaseTags;

public class PredicateParser
{
    private static InterpretedToken[] INTERPRETED_TOKENS;
    /*synthetic*/ static Class class$0;
    /*synthetic*/ static Class class$1;
    /*synthetic*/ static Class class$2;
    /*synthetic*/ static Class class$3;
    /*synthetic*/ static Class class$4;
    /*synthetic*/ static Class class$5;
    /*synthetic*/ static Class class$6;
    /*synthetic*/ static Class class$7;
    /*synthetic*/ static Class class$8;
    /*synthetic*/ static Class class$9;
    /*synthetic*/ static Class class$10;
    /*synthetic*/ static Class class$11;
    /*synthetic*/ static Class class$12;
    /*synthetic*/ static Class class$13;
    /*synthetic*/ static Class class$14;
    /*synthetic*/ static Class class$15;
    /*synthetic*/ static Class class$16;
    /*synthetic*/ static Class class$17;
    /*synthetic*/ static Class class$18;
    /*synthetic*/ static Class class$19;
    /*synthetic*/ static Class class$20;
    /*synthetic*/ static Class class$21;
    
    private static class InterpretedToken
    {
	private String[] myNames;
	private boolean myBinary;
	private Class myPredicateClass;
	private boolean mySkipIfParameterMismatch;
	private boolean myFunction;
	private int myPrecedence;
	/*synthetic*/ static Class class$0;
	/*synthetic*/ static Class class$1;
	
	public InterpretedToken(String[] _names, Class _predicateClass,
				int _precedence, boolean _binary) {
	    this(_names, _predicateClass, _precedence, _binary, false, false);
	}
	
	public InterpretedToken(String[] _names, Class _predicateClass,
				int _precedence, boolean _binary,
				boolean _skipIfParameterMismatch,
				boolean _function) {
	    myNames = _names;
	    myBinary = _binary;
	    myPredicateClass = _predicateClass;
	    myPrecedence = _precedence;
	    mySkipIfParameterMismatch = _skipIfParameterMismatch;
	    myFunction = _function;
	}
	
	public int getPrecedence() {
	    return myPrecedence;
	}
	
	public boolean isFunction() {
	    return myFunction;
	}
	
	public boolean matches(IPredicate _leftValue, String _name)
	    throws ParseException {
	    boolean matches = false;
	    for (int i = 0; !matches && i < myNames.length; i++)
		matches = myNames[i].equals(_name);
	    if (matches) {
		if (myBinary) {
		    if (_leftValue == null) {
			if (mySkipIfParameterMismatch)
			    matches = false;
			else
			    throw new ParseException
				      (("You used the binary token " + _name
					+ " without an lvalue."),
				       0);
		    }
		} else if (_leftValue != null) {
		    if (mySkipIfParameterMismatch)
			matches = false;
		    else
			throw new ParseException(("You used the unary token "
						  + _name
						  + " with an lvalue of "
						  + _leftValue + "."),
						 0);
		}
	    }
	    return matches;
	}
	
	public IPredicate createPredicate
	    (String _name, IPredicate _leftValue, IPredicate _rightValue)
	    throws ParseException {
	    try {
		IPredicate predicate;
		if (myFunction) {
		    if (myBinary) {
			Class var_class = myPredicateClass;
			Class[] var_classes = new Class[3];
			int i = 0;
			Class var_class_0_ = class$0;
			if (var_class_0_ == null) {
			    Class var_class_1_;
			    try {
				var_class_1_
				    = Class.forName("java.lang.String");
			    } catch (ClassNotFoundException classnotfoundexception) {
				NoClassDefFoundError noclassdeffounderror
				    = new NoClassDefFoundError;
				((UNCONSTRUCTED)noclassdeffounderror)
				    .NoClassDefFoundError
				    (classnotfoundexception.getMessage());
				throw noclassdeffounderror;
			    }
			    var_class_0_ = class$0 = var_class_1_;
			}
			var_classes[i] = var_class_0_;
			int i_2_ = 1;
			Class var_class_3_ = class$1;
			if (var_class_3_ == null) {
			    Class var_class_4_;
			    try {
				var_class_4_
				    = (Class.forName
				       ("org.jempeg.nodestore.predicate.IPredicate"));
			    } catch (ClassNotFoundException classnotfoundexception) {
				NoClassDefFoundError noclassdeffounderror
				    = new NoClassDefFoundError;
				((UNCONSTRUCTED)noclassdeffounderror)
				    .NoClassDefFoundError
				    (classnotfoundexception.getMessage());
				throw noclassdeffounderror;
			    }
			    var_class_3_ = class$1 = var_class_4_;
			}
			var_classes[i_2_] = var_class_3_;
			int i_5_ = 2;
			Class var_class_6_ = class$1;
			if (var_class_6_ == null) {
			    Class var_class_7_;
			    try {
				var_class_7_
				    = (Class.forName
				       ("org.jempeg.nodestore.predicate.IPredicate"));
			    } catch (ClassNotFoundException classnotfoundexception) {
				NoClassDefFoundError noclassdeffounderror
				    = new NoClassDefFoundError;
				((UNCONSTRUCTED)noclassdeffounderror)
				    .NoClassDefFoundError
				    (classnotfoundexception.getMessage());
				throw noclassdeffounderror;
			    }
			    var_class_6_ = class$1 = var_class_7_;
			}
			var_classes[i_5_] = var_class_6_;
			Constructor constructor
			    = var_class.getConstructor(var_classes);
			predicate
			    = ((IPredicate)
			       constructor.newInstance(new Object[]
						       { _name, _leftValue,
							 _rightValue }));
		    } else {
			Class var_class = myPredicateClass;
			Class[] var_classes = new Class[2];
			int i = 0;
			Class var_class_8_ = class$0;
			if (var_class_8_ == null) {
			    Class var_class_9_;
			    try {
				var_class_9_
				    = Class.forName("java.lang.String");
			    } catch (ClassNotFoundException classnotfoundexception) {
				NoClassDefFoundError noclassdeffounderror
				    = new NoClassDefFoundError;
				((UNCONSTRUCTED)noclassdeffounderror)
				    .NoClassDefFoundError
				    (classnotfoundexception.getMessage());
				throw noclassdeffounderror;
			    }
			    var_class_8_ = class$0 = var_class_9_;
			}
			var_classes[i] = var_class_8_;
			int i_10_ = 1;
			Class var_class_11_ = class$1;
			if (var_class_11_ == null) {
			    Class var_class_12_;
			    try {
				var_class_12_
				    = (Class.forName
				       ("org.jempeg.nodestore.predicate.IPredicate"));
			    } catch (ClassNotFoundException classnotfoundexception) {
				NoClassDefFoundError noclassdeffounderror
				    = new NoClassDefFoundError;
				((UNCONSTRUCTED)noclassdeffounderror)
				    .NoClassDefFoundError
				    (classnotfoundexception.getMessage());
				throw noclassdeffounderror;
			    }
			    var_class_11_ = class$1 = var_class_12_;
			}
			var_classes[i_10_] = var_class_11_;
			Constructor constructor
			    = var_class.getConstructor(var_classes);
			predicate = ((IPredicate)
				     constructor.newInstance(new Object[]
							     { _name,
							       _rightValue }));
		    }
		} else if (myBinary) {
		    Class var_class = myPredicateClass;
		    Class[] var_classes = new Class[2];
		    int i = 0;
		    Class var_class_13_ = class$1;
		    if (var_class_13_ == null) {
			Class var_class_14_;
			try {
			    var_class_14_
				= (Class.forName
				   ("org.jempeg.nodestore.predicate.IPredicate"));
			} catch (ClassNotFoundException classnotfoundexception) {
			    NoClassDefFoundError noclassdeffounderror
				= new NoClassDefFoundError;
			    ((UNCONSTRUCTED)noclassdeffounderror)
				.NoClassDefFoundError
				(classnotfoundexception.getMessage());
			    throw noclassdeffounderror;
			}
			var_class_13_ = class$1 = var_class_14_;
		    }
		    var_classes[i] = var_class_13_;
		    int i_15_ = 1;
		    Class var_class_16_ = class$1;
		    if (var_class_16_ == null) {
			Class var_class_17_;
			try {
			    var_class_17_
				= (Class.forName
				   ("org.jempeg.nodestore.predicate.IPredicate"));
			} catch (ClassNotFoundException classnotfoundexception) {
			    NoClassDefFoundError noclassdeffounderror
				= new NoClassDefFoundError;
			    ((UNCONSTRUCTED)noclassdeffounderror)
				.NoClassDefFoundError
				(classnotfoundexception.getMessage());
			    throw noclassdeffounderror;
			}
			var_class_16_ = class$1 = var_class_17_;
		    }
		    var_classes[i_15_] = var_class_16_;
		    Constructor constructor
			= var_class.getConstructor(var_classes);
		    predicate = ((IPredicate)
				 constructor.newInstance(new Object[]
							 { _leftValue,
							   _rightValue }));
		} else {
		    Class var_class = myPredicateClass;
		    Class[] var_classes = new Class[1];
		    int i = 0;
		    Class var_class_18_ = class$1;
		    if (var_class_18_ == null) {
			Class var_class_19_;
			try {
			    var_class_19_
				= (Class.forName
				   ("org.jempeg.nodestore.predicate.IPredicate"));
			} catch (ClassNotFoundException classnotfoundexception) {
			    NoClassDefFoundError noclassdeffounderror
				= new NoClassDefFoundError;
			    ((UNCONSTRUCTED)noclassdeffounderror)
				.NoClassDefFoundError
				(classnotfoundexception.getMessage());
			    throw noclassdeffounderror;
			}
			var_class_18_ = class$1 = var_class_19_;
		    }
		    var_classes[i] = var_class_18_;
		    Constructor constructor
			= var_class.getConstructor(var_classes);
		    predicate = ((IPredicate)
				 constructor.newInstance(new Object[]
							 { _rightValue }));
		}
		return predicate;
	    } catch (Throwable t) {
		Debug.println(t);
		throw new ParseException("Failed to create predicate.", 0);
	    }
	}
    }
    
    static {
	InterpretedToken[] interpretedtokens = new InterpretedToken[22];
	int i = 0;
	InterpretedToken interpretedtoken = new InterpretedToken;
	String[] strings = { "and", "&&", "&" };
	Class var_class = class$0;
	if (var_class == null) {
	    Class var_class_20_;
	    try {
		var_class_20_
		    = (Class.forName
		       ("org.jempeg.nodestore.predicate.AndPredicate"));
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class = class$0 = var_class_20_;
	}
	((UNCONSTRUCTED)interpretedtoken).InterpretedToken(strings, var_class,
							   0, true);
	interpretedtokens[i] = interpretedtoken;
	int i_21_ = 1;
	InterpretedToken interpretedtoken_22_ = new InterpretedToken;
	String[] strings_23_ = { "or", "||", "|" };
	Class var_class_24_ = class$1;
	if (var_class_24_ == null) {
	    Class var_class_25_;
	    try {
		var_class_25_
		    = (Class.forName
		       ("org.jempeg.nodestore.predicate.OrPredicate"));
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_24_ = class$1 = var_class_25_;
	}
	((UNCONSTRUCTED)interpretedtoken_22_)
	    .InterpretedToken(strings_23_, var_class_24_, 0, true);
	interpretedtokens[i_21_] = interpretedtoken_22_;
	int i_26_ = 2;
	InterpretedToken interpretedtoken_27_ = new InterpretedToken;
	String[] strings_28_ = { "not", "!" };
	Class var_class_29_ = class$2;
	if (var_class_29_ == null) {
	    Class var_class_30_;
	    try {
		var_class_30_
		    = (Class.forName
		       ("org.jempeg.nodestore.predicate.NotPredicate"));
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_29_ = class$2 = var_class_30_;
	}
	((UNCONSTRUCTED)interpretedtoken_27_)
	    .InterpretedToken(strings_28_, var_class_29_, 7, false);
	interpretedtokens[i_26_] = interpretedtoken_27_;
	int i_31_ = 3;
	InterpretedToken interpretedtoken_32_ = new InterpretedToken;
	String[] strings_33_ = { "=", "equals", "==", "is" };
	Class var_class_34_ = class$3;
	if (var_class_34_ == null) {
	    Class var_class_35_;
	    try {
		var_class_35_
		    = (Class.forName
		       ("org.jempeg.nodestore.predicate.EqualsPredicate"));
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_34_ = class$3 = var_class_35_;
	}
	((UNCONSTRUCTED)interpretedtoken_32_)
	    .InterpretedToken(strings_33_, var_class_34_, 7, true);
	interpretedtokens[i_31_] = interpretedtoken_32_;
	int i_36_ = 4;
	InterpretedToken interpretedtoken_37_ = new InterpretedToken;
	String[] strings_38_ = { "!=", "<>" };
	Class var_class_39_ = class$4;
	if (var_class_39_ == null) {
	    Class var_class_40_;
	    try {
		var_class_40_
		    = (Class.forName
		       ("org.jempeg.nodestore.predicate.NotEqualsPredicate"));
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_39_ = class$4 = var_class_40_;
	}
	((UNCONSTRUCTED)interpretedtoken_37_)
	    .InterpretedToken(strings_38_, var_class_39_, 7, true);
	interpretedtokens[i_36_] = interpretedtoken_37_;
	int i_41_ = 5;
	InterpretedToken interpretedtoken_42_ = new InterpretedToken;
	String[] strings_43_ = { "like", "contains" };
	Class var_class_44_ = class$5;
	if (var_class_44_ == null) {
	    Class var_class_45_;
	    try {
		var_class_45_
		    = (Class.forName
		       ("org.jempeg.nodestore.predicate.LikePredicate"));
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_44_ = class$5 = var_class_45_;
	}
	((UNCONSTRUCTED)interpretedtoken_42_)
	    .InterpretedToken(strings_43_, var_class_44_, 7, true);
	interpretedtokens[i_41_] = interpretedtoken_42_;
	int i_46_ = 6;
	InterpretedToken interpretedtoken_47_ = new InterpretedToken;
	String[] strings_48_ = { "regex" };
	Class var_class_49_ = class$6;
	if (var_class_49_ == null) {
	    Class var_class_50_;
	    try {
		var_class_50_
		    = (Class.forName
		       ("org.jempeg.nodestore.predicate.RegexPredicate"));
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_49_ = class$6 = var_class_50_;
	}
	((UNCONSTRUCTED)interpretedtoken_47_)
	    .InterpretedToken(strings_48_, var_class_49_, 7, true);
	interpretedtokens[i_46_] = interpretedtoken_47_;
	int i_51_ = 7;
	InterpretedToken interpretedtoken_52_ = new InterpretedToken;
	String[] strings_53_ = { "<" };
	Class var_class_54_ = class$7;
	if (var_class_54_ == null) {
	    Class var_class_55_;
	    try {
		var_class_55_
		    = (Class.forName
		       ("org.jempeg.nodestore.predicate.LessThanPredicate"));
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_54_ = class$7 = var_class_55_;
	}
	((UNCONSTRUCTED)interpretedtoken_52_)
	    .InterpretedToken(strings_53_, var_class_54_, 5, true);
	interpretedtokens[i_51_] = interpretedtoken_52_;
	int i_56_ = 8;
	InterpretedToken interpretedtoken_57_ = new InterpretedToken;
	String[] strings_58_ = { ">" };
	Class var_class_59_ = class$8;
	if (var_class_59_ == null) {
	    Class var_class_60_;
	    try {
		var_class_60_
		    = (Class.forName
		       ("org.jempeg.nodestore.predicate.GreaterThanPredicate"));
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_59_ = class$8 = var_class_60_;
	}
	((UNCONSTRUCTED)interpretedtoken_57_)
	    .InterpretedToken(strings_58_, var_class_59_, 5, true);
	interpretedtokens[i_56_] = interpretedtoken_57_;
	int i_61_ = 9;
	InterpretedToken interpretedtoken_62_ = new InterpretedToken;
	String[] strings_63_ = { "<=" };
	Class var_class_64_ = class$9;
	if (var_class_64_ == null) {
	    Class var_class_65_;
	    try {
		var_class_65_
		    = (Class.forName
		       ("org.jempeg.nodestore.predicate.LessThanOrEqualPredicate"));
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_64_ = class$9 = var_class_65_;
	}
	((UNCONSTRUCTED)interpretedtoken_62_)
	    .InterpretedToken(strings_63_, var_class_64_, 5, true);
	interpretedtokens[i_61_] = interpretedtoken_62_;
	int i_66_ = 10;
	InterpretedToken interpretedtoken_67_ = new InterpretedToken;
	String[] strings_68_ = { ">=" };
	Class var_class_69_ = class$10;
	if (var_class_69_ == null) {
	    Class var_class_70_;
	    try {
		var_class_70_
		    = (Class.forName
		       ("org.jempeg.nodestore.predicate.GreaterThanOrEqualPredicate"));
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_69_ = class$10 = var_class_70_;
	}
	((UNCONSTRUCTED)interpretedtoken_67_)
	    .InterpretedToken(strings_68_, var_class_69_, 5, true);
	interpretedtokens[i_66_] = interpretedtoken_67_;
	int i_71_ = 11;
	InterpretedToken interpretedtoken_72_ = new InterpretedToken;
	String[] strings_73_ = { "-" };
	Class var_class_74_ = class$11;
	if (var_class_74_ == null) {
	    Class var_class_75_;
	    try {
		var_class_75_
		    = (Class.forName
		       ("org.jempeg.nodestore.predicate.NegatePredicate"));
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_74_ = class$11 = var_class_75_;
	}
	((UNCONSTRUCTED)interpretedtoken_72_).InterpretedToken(strings_73_,
							       var_class_74_,
							       7, false, true,
							       false);
	interpretedtokens[i_71_] = interpretedtoken_72_;
	int i_76_ = 12;
	InterpretedToken interpretedtoken_77_ = new InterpretedToken;
	String[] strings_78_ = { "-" };
	Class var_class_79_ = class$12;
	if (var_class_79_ == null) {
	    Class var_class_80_;
	    try {
		var_class_80_
		    = (Class.forName
		       ("org.jempeg.nodestore.predicate.SubtractPredicate"));
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_79_ = class$12 = var_class_80_;
	}
	((UNCONSTRUCTED)interpretedtoken_77_)
	    .InterpretedToken(strings_78_, var_class_79_, 10, true);
	interpretedtokens[i_76_] = interpretedtoken_77_;
	int i_81_ = 13;
	InterpretedToken interpretedtoken_82_ = new InterpretedToken;
	String[] strings_83_ = { "+" };
	Class var_class_84_ = class$13;
	if (var_class_84_ == null) {
	    Class var_class_85_;
	    try {
		var_class_85_
		    = (Class.forName
		       ("org.jempeg.nodestore.predicate.AddPredicate"));
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_84_ = class$13 = var_class_85_;
	}
	((UNCONSTRUCTED)interpretedtoken_82_)
	    .InterpretedToken(strings_83_, var_class_84_, 10, true);
	interpretedtokens[i_81_] = interpretedtoken_82_;
	int i_86_ = 14;
	InterpretedToken interpretedtoken_87_ = new InterpretedToken;
	String[] strings_88_ = { "*" };
	Class var_class_89_ = class$14;
	if (var_class_89_ == null) {
	    Class var_class_90_;
	    try {
		var_class_90_
		    = (Class.forName
		       ("org.jempeg.nodestore.predicate.MultiplyPredicate"));
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_89_ = class$14 = var_class_90_;
	}
	((UNCONSTRUCTED)interpretedtoken_87_)
	    .InterpretedToken(strings_88_, var_class_89_, 15, true);
	interpretedtokens[i_86_] = interpretedtoken_87_;
	int i_91_ = 15;
	InterpretedToken interpretedtoken_92_ = new InterpretedToken;
	String[] strings_93_ = { "/" };
	Class var_class_94_ = class$15;
	if (var_class_94_ == null) {
	    Class var_class_95_;
	    try {
		var_class_95_
		    = (Class.forName
		       ("org.jempeg.nodestore.predicate.DividePredicate"));
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_94_ = class$15 = var_class_95_;
	}
	((UNCONSTRUCTED)interpretedtoken_92_)
	    .InterpretedToken(strings_93_, var_class_94_, 15, true);
	interpretedtokens[i_91_] = interpretedtoken_92_;
	int i_96_ = 16;
	InterpretedToken interpretedtoken_97_ = new InterpretedToken;
	String[] strings_98_ = { "date" };
	Class var_class_99_ = class$16;
	if (var_class_99_ == null) {
	    Class var_class_100_;
	    try {
		var_class_100_
		    = (Class.forName
		       ("org.jempeg.nodestore.predicate.DateFunction"));
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_99_ = class$16 = var_class_100_;
	}
	((UNCONSTRUCTED)interpretedtoken_97_).InterpretedToken(strings_98_,
							       var_class_99_,
							       20, false,
							       false, true);
	interpretedtokens[i_96_] = interpretedtoken_97_;
	int i_101_ = 17;
	InterpretedToken interpretedtoken_102_ = new InterpretedToken;
	String[] strings_103_ = { "daysago" };
	Class var_class_104_ = class$17;
	if (var_class_104_ == null) {
	    Class var_class_105_;
	    try {
		var_class_105_
		    = (Class.forName
		       ("org.jempeg.nodestore.predicate.DaysAgoFunction"));
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_104_ = class$17 = var_class_105_;
	}
	((UNCONSTRUCTED)interpretedtoken_102_).InterpretedToken(strings_103_,
								var_class_104_,
								20, false,
								false, true);
	interpretedtokens[i_101_] = interpretedtoken_102_;
	int i_106_ = 18;
	InterpretedToken interpretedtoken_107_ = new InterpretedToken;
	String[] strings_108_ = { "fibonacci" };
	Class var_class_109_ = class$18;
	if (var_class_109_ == null) {
	    Class var_class_110_;
	    try {
		var_class_110_
		    = (Class.forName
		       ("org.jempeg.nodestore.predicate.FibonacciFunction"));
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_109_ = class$18 = var_class_110_;
	}
	((UNCONSTRUCTED)interpretedtoken_107_).InterpretedToken(strings_108_,
								var_class_109_,
								20, false,
								false, true);
	interpretedtokens[i_106_] = interpretedtoken_107_;
	int i_111_ = 19;
	InterpretedToken interpretedtoken_112_ = new InterpretedToken;
	String[] strings_113_ = { "hasoption" };
	Class var_class_114_ = class$19;
	if (var_class_114_ == null) {
	    Class var_class_115_;
	    try {
		var_class_115_
		    = (Class.forName
		       ("org.jempeg.nodestore.predicate.HasOptionFunction"));
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_114_ = class$19 = var_class_115_;
	}
	((UNCONSTRUCTED)interpretedtoken_112_).InterpretedToken(strings_113_,
								var_class_114_,
								20, false,
								false, true);
	interpretedtokens[i_111_] = interpretedtoken_112_;
	int i_116_ = 20;
	InterpretedToken interpretedtoken_117_ = new InterpretedToken;
	String[] strings_118_ = { "now" };
	Class var_class_119_ = class$20;
	if (var_class_119_ == null) {
	    Class var_class_120_;
	    try {
		var_class_120_
		    = (Class.forName
		       ("org.jempeg.nodestore.predicate.NowFunction"));
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_119_ = class$20 = var_class_120_;
	}
	((UNCONSTRUCTED)interpretedtoken_117_).InterpretedToken(strings_118_,
								var_class_119_,
								20, false,
								false, true);
	interpretedtokens[i_116_] = interpretedtoken_117_;
	int i_121_ = 21;
	InterpretedToken interpretedtoken_122_ = new InterpretedToken;
	String[] strings_123_ = { "secondsago" };
	Class var_class_124_ = class$21;
	if (var_class_124_ == null) {
	    Class var_class_125_;
	    try {
		var_class_125_
		    = (Class.forName
		       ("org.jempeg.nodestore.predicate.SecondsAgoFunction"));
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_124_ = class$21 = var_class_125_;
	}
	((UNCONSTRUCTED)interpretedtoken_122_).InterpretedToken(strings_123_,
								var_class_124_,
								20, false,
								false, true);
	interpretedtokens[i_121_] = interpretedtoken_122_;
	INTERPRETED_TOKENS = interpretedtokens;
    }
    
    public IPredicate parse(String _string) throws ParseException {
	Tokenizer.IToken token = new Tokenizer(_string).tokenize();
	IPredicate predicate
	    = (parseToken
	       (null,
		new PeekableEnumeration(new OneElementEnumeration(token))));
	if (predicate == null)
	    predicate = new TruePredicate();
	return predicate;
    }
    
    private IPredicate parseToken
	(IPredicate _leftValue, PeekableEnumeration _membersEnum)
	throws ParseException {
	requireMoreTokens(_membersEnum);
	Tokenizer.IToken token = (Tokenizer.IToken) _membersEnum.nextElement();
	IPredicate value;
	if (token instanceof Tokenizer.Group) {
	    checkUnaryOperator(_leftValue, token);
	    Tokenizer.Group group = (Tokenizer.Group) token;
	    PeekableEnumeration membersEnum
		= new PeekableEnumeration(group.tokens());
	    value = null;
	    while (membersEnum.hasMoreElements())
		value = parseToken(value, membersEnum);
	} else if (token instanceof Tokenizer.LiteralToken) {
	    checkUnaryOperator(_leftValue, token);
	    if (token instanceof Tokenizer.NumericLiteral)
		value = new LiteralValue(((Tokenizer.NumericLiteral) token)
					     .getValue(),
					 true);
	    else
		value = new LiteralValue(((Tokenizer.LiteralToken) token)
					     .getValue());
	} else if (token instanceof Tokenizer.KeywordToken) {
	    String name = ((Tokenizer.KeywordToken) token).getName();
	    InterpretedToken interpretedToken
		= interpretToken(_leftValue, name);
	    if (interpretedToken != null) {
		if (interpretedToken.isFunction())
		    value
			= (interpretedToken.createPredicate
			   (name, _leftValue, parseToken(null, _membersEnum)));
		else
		    value = (interpretedToken.createPredicate
			     (name, _leftValue,
			      parseTokenWithPrecedence(token, _leftValue,
						       _membersEnum)));
	    } else {
		checkUnaryOperator(_leftValue, token);
		if (DatabaseTags.isDatabaseTag(name)
		    && !"playlist".equals(name))
		    value = new TagValue(name);
		else
		    value = new LiteralValue(name);
	    }
	} else
	    throw new ParseException(("Unable to process the current token "
				      + token + "."),
				     -1);
	return value;
    }
    
    private int getPrecedence
	(IPredicate _leftValue, Tokenizer.IToken _token)
	throws ParseException {
	int precedence = -1;
	if (_token instanceof Tokenizer.KeywordToken) {
	    String name = ((Tokenizer.KeywordToken) _token).getName();
	    InterpretedToken interpretedToken
		= interpretToken(_leftValue, name);
	    if (interpretedToken != null)
		precedence = interpretedToken.getPrecedence();
	}
	return precedence;
    }
    
    private void requireMoreTokens(PeekableEnumeration _membersEnum)
	throws ParseException {
	if (!_membersEnum.hasMoreElements())
	    throw new ParseException
		      ("The current function or operator requires more tokens than are available.",
		       -1);
    }
    
    private IPredicate parseTokenWithPrecedence
	(Tokenizer.IToken _leftOperator, IPredicate _leftValue,
	 PeekableEnumeration _membersEnum)
	throws ParseException {
	requireMoreTokens(_membersEnum);
	Tokenizer.IToken nextToken = (Tokenizer.IToken) _membersEnum.peek();
	IPredicate predicate = parseToken(null, _membersEnum);
	Tokenizer.IToken rightOperator
	    = (Tokenizer.IToken) _membersEnum.peek();
	if (rightOperator != null) {
	    int leftPrecedence = getPrecedence(_leftValue, _leftOperator);
	    int rightPrecedence = getPrecedence(null, nextToken);
	    if (rightPrecedence == -1)
		rightPrecedence = getPrecedence(predicate, rightOperator);
	    if (leftPrecedence < rightPrecedence)
		predicate = parseToken(predicate, _membersEnum);
	}
	return predicate;
    }
    
    private InterpretedToken interpretToken
	(IPredicate _leftValue, String _name) throws ParseException {
	InterpretedToken interpretedToken = null;
	for (int i = 0;
	     interpretedToken == null && i < INTERPRETED_TOKENS.length; i++) {
	    if (INTERPRETED_TOKENS[i].matches(_leftValue, _name))
		interpretedToken = INTERPRETED_TOKENS[i];
	}
	return interpretedToken;
    }
    
    private void checkUnaryOperator
	(IPredicate _leftValue, Tokenizer.IToken _token)
	throws ParseException {
	if (_leftValue != null)
	    throw new ParseException(("You used the unary token " + _token
				      + " with an lvalue of " + _leftValue
				      + "."),
				     0);
    }
}
