package org.jempeg.nodestore.predicate;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import org.jempeg.nodestore.IFIDNode;

import com.inzyme.exception.ChainedRuntimeException;

public class DateFunction extends AbstractOneParameterFunction {
	public DateFunction(String _functionName, IPredicate _parameter) {
		super(_functionName, _parameter);
	}
	
	protected String evaluate(IFIDNode _node, String _param) {
		Date date;
		try {
			DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT);
			format.setLenient(true);
			date = format.parse(_param);
		}
		catch (ParseException e) {
			try {
				DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
				format.setLenient(true);
				date = format.parse(_param);
			}
			catch (ParseException e2) {
				throw new ChainedRuntimeException("Invalid date format: " + _param, e2);
			}
		}
		return String.valueOf(date.getTime() / 1000);
	}
}