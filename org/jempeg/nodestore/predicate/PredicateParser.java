package org.jempeg.nodestore.predicate;

import java.lang.reflect.Constructor;
import java.text.ParseException;

import org.jempeg.nodestore.DatabaseTags;

import com.inzyme.util.Debug;

public class PredicateParser {
	private static InterpretedToken[] INTERPRETED_TOKENS =
		new InterpretedToken[] {
			new InterpretedToken(new String[] { "and", "&&", "&" }, AndPredicate.class, 0, true),
			new InterpretedToken(new String[] { "or", "||", "|" }, OrPredicate.class, 0, true),
			new InterpretedToken(new String[] { "not", "!" }, NotPredicate.class, 7, false),
			new InterpretedToken(new String[] { "=", "equals", "==", "is" }, EqualsPredicate.class, 7, true),
			new InterpretedToken(new String[] { "!=", "<>" }, NotEqualsPredicate.class, 7, true),
			new InterpretedToken(new String[] { "like", "contains" }, LikePredicate.class, 7, true),
      new InterpretedToken(new String[] { "regex" }, RegexPredicate.class, 7, true),
			new InterpretedToken(new String[] { "<" }, LessThanPredicate.class, 5, true),
			new InterpretedToken(new String[] { ">" }, GreaterThanPredicate.class, 5, true),
			new InterpretedToken(new String[] { "<=" }, LessThanOrEqualPredicate.class, 5, true),
			new InterpretedToken(new String[] { ">=" }, GreaterThanOrEqualPredicate.class, 5, true),
			new InterpretedToken(new String[] { "-" }, NegatePredicate.class, 7, false, true, false),
			new InterpretedToken(new String[] { "-" }, SubtractPredicate.class, 10, true),
			new InterpretedToken(new String[] { "+" }, AddPredicate.class, 10, true),
			new InterpretedToken(new String[] { "*" }, MultiplyPredicate.class, 15, true),
			new InterpretedToken(new String[] { "/" }, DividePredicate.class, 15, true),
			new InterpretedToken(new String[] { "date" }, DateFunction.class, 20, false, false, true),
			new InterpretedToken(new String[] { "daysago" }, DaysAgoFunction.class, 20, false, false, true),
			new InterpretedToken(new String[] { "fibonacci" }, FibonacciFunction.class, 20, false, false, true),
			new InterpretedToken(new String[] { "hasoption" }, HasOptionFunction.class, 20, false, false, true),
			new InterpretedToken(new String[] { "now" }, NowFunction.class, 20, false, false, true),
			new InterpretedToken(new String[] { "secondsago" }, SecondsAgoFunction.class, 20, false, false, true),
			};

	public PredicateParser() {
	}

	public IPredicate parse(String _string) throws ParseException {
		Tokenizer.IToken token = new Tokenizer(_string).tokenize();
		IPredicate predicate = parseToken(null, new PeekableEnumeration(new OneElementEnumeration(token)));
		if (predicate == null) {
			predicate = new TruePredicate();
		}
		return predicate;
	}

	private IPredicate parseToken(IPredicate _leftValue, PeekableEnumeration _membersEnum) throws ParseException {
		requireMoreTokens(_membersEnum);

		IPredicate value;
		Tokenizer.IToken token = (Tokenizer.IToken) _membersEnum.nextElement();
		if (token instanceof Tokenizer.Group) {
			checkUnaryOperator(_leftValue, token);
			Tokenizer.Group group = (Tokenizer.Group) token;
			PeekableEnumeration membersEnum = new PeekableEnumeration(group.tokens());
			value = null;
			while (membersEnum.hasMoreElements()) {
				value = parseToken(value, membersEnum);
			}
		}
		else if (token instanceof Tokenizer.LiteralToken) {
			checkUnaryOperator(_leftValue, token);
			if (token instanceof Tokenizer.NumericLiteral) {
				value = new LiteralValue(((Tokenizer.NumericLiteral) token).getValue(), true);
			}
			else {
				value = new LiteralValue(((Tokenizer.LiteralToken) token).getValue());
			}
		}
		else if (token instanceof Tokenizer.KeywordToken) {
			String name = ((Tokenizer.KeywordToken) token).getName();
			InterpretedToken interpretedToken = interpretToken(_leftValue, name);
			if (interpretedToken != null) {
				// functions ALWAYS have a parameter, so they don't need to check precedence of their token
				if (interpretedToken.isFunction()) {
					value = interpretedToken.createPredicate(name, _leftValue, parseToken(null, _membersEnum));
				}
				else {
					value = interpretedToken.createPredicate(name, _leftValue, parseTokenWithPrecedence(token, _leftValue, _membersEnum));
				}
			}
			else {
				checkUnaryOperator(_leftValue, token);
				// "playlist" is a common rvalue literal and a rare tag value, so we toss it out
				if (DatabaseTags.isDatabaseTag(name) && !DatabaseTags.PLAYLIST_TAG.equals(name)) {
					value = new TagValue(name);
				}
				else {
					value = new LiteralValue(name);
				}
			}
		}
		else {
			throw new ParseException("Unable to process the current token " + token + ".", -1);
		}
		return value;
	}

	private int getPrecedence(IPredicate _leftValue, Tokenizer.IToken _token) throws ParseException {
		int precedence = -1;
		if (_token instanceof Tokenizer.KeywordToken) {
			String name = ((Tokenizer.KeywordToken) _token).getName();
			InterpretedToken interpretedToken = interpretToken(_leftValue, name);
			if (interpretedToken != null) {
				precedence = interpretedToken.getPrecedence();
			}
		}
		return precedence;
	}

	private void requireMoreTokens(PeekableEnumeration _membersEnum) throws ParseException {
		if (!_membersEnum.hasMoreElements()) {
			throw new ParseException("The current function or operator requires more tokens than are available.", -1);
		}
	}

	private IPredicate parseTokenWithPrecedence(Tokenizer.IToken _leftOperator, IPredicate _leftValue, PeekableEnumeration _membersEnum) throws ParseException {
		requireMoreTokens(_membersEnum);
		
		Tokenizer.IToken nextToken = (Tokenizer.IToken) _membersEnum.peek();
		IPredicate predicate = parseToken(null, _membersEnum);
		Tokenizer.IToken rightOperator = (Tokenizer.IToken) _membersEnum.peek();

		if (rightOperator != null) {
			int leftPrecedence = getPrecedence(_leftValue, _leftOperator);
			int rightPrecedence = getPrecedence(null, nextToken);

			if (rightPrecedence == -1) {
				rightPrecedence = getPrecedence(predicate, rightOperator);
			}

			if (leftPrecedence < rightPrecedence) {
				predicate = parseToken(predicate, _membersEnum);
			}
		}

		return predicate;
	}

	private InterpretedToken interpretToken(IPredicate _leftValue, String _name) throws ParseException {
		InterpretedToken interpretedToken = null;
		for (int i = 0; interpretedToken == null && i < PredicateParser.INTERPRETED_TOKENS.length; i++) {
			if (PredicateParser.INTERPRETED_TOKENS[i].matches(_leftValue, _name)) {
				interpretedToken = PredicateParser.INTERPRETED_TOKENS[i];
			}
		}
		return interpretedToken;
	}

	private void checkUnaryOperator(IPredicate _leftValue, Tokenizer.IToken _token) throws ParseException {
		if (_leftValue != null) {
			throw new ParseException("You used the unary token " + _token + " with an lvalue of " + _leftValue + ".", 0);
		}
	}

	private static class InterpretedToken {
		private String[] myNames;
		private boolean myBinary;
		private Class myPredicateClass;
		private boolean mySkipIfParameterMismatch;
		private boolean myFunction;
		private int myPrecedence;

		public InterpretedToken(String[] _names, Class _predicateClass, int _precedence, boolean _binary) {
			this(_names, _predicateClass, _precedence, _binary, false, false);
		}

		public InterpretedToken(String[] _names, Class _predicateClass, int _precedence, boolean _binary, boolean _skipIfParameterMismatch, boolean _function) {
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

		public boolean matches(IPredicate _leftValue, String _name) throws ParseException {
			boolean matches = false;
			for (int i = 0; !matches && i < myNames.length; i++) {
				matches = myNames[i].equals(_name);
			}
			if (matches) {
				if (myBinary) {
					if (_leftValue == null) {
						if (mySkipIfParameterMismatch) {
							matches = false;
						}
						else {
							throw new ParseException("You used the binary token " + _name + " without an lvalue.", 0);
						}
					}
				}
				else {
					if (_leftValue != null) {
						if (mySkipIfParameterMismatch) {
							matches = false;
						}
						else {
							throw new ParseException("You used the unary token " + _name + " with an lvalue of " + _leftValue + ".", 0);
						}
					}
				}
			}
			return matches;
		}

		public IPredicate createPredicate(String _name, IPredicate _leftValue, IPredicate _rightValue) throws ParseException {
			try {
				IPredicate predicate;
				if (myFunction) {
					if (myBinary) {
						Constructor constructor = myPredicateClass.getConstructor(new Class[] { String.class, IPredicate.class, IPredicate.class });
						predicate = (IPredicate) constructor.newInstance(new Object[] { _name, _leftValue, _rightValue });
					}
					else {
						Constructor constructor = myPredicateClass.getConstructor(new Class[] { String.class, IPredicate.class });
						predicate = (IPredicate) constructor.newInstance(new Object[] { _name, _rightValue });
					}
				}
				else if (myBinary) {
					Constructor constructor = myPredicateClass.getConstructor(new Class[] { IPredicate.class, IPredicate.class });
					predicate = (IPredicate) constructor.newInstance(new Object[] { _leftValue, _rightValue });
				}
				else {
					Constructor constructor = myPredicateClass.getConstructor(new Class[] { IPredicate.class });
					predicate = (IPredicate) constructor.newInstance(new Object[] { _rightValue });
				}
				return predicate;
			}
			catch (Throwable t) {
				Debug.println(t);
				throw new ParseException("Failed to create predicate.", 0);
			}
		}
	}
}
