/* FibonacciFunction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.predicate;
import org.jempeg.nodestore.IFIDNode;

public class FibonacciFunction extends AbstractOneParameterFunction
{
    public FibonacciFunction(String _functionName, IPredicate _parameter) {
	super(_functionName, _parameter);
    }
    
    protected String evaluate(IFIDNode _node, String _param) {
	long x = Long.parseLong(_param);
	long y = (long) (1.0 / Math.sqrt(5.0)
			 * (Math.pow((1.0 + Math.sqrt(5.0)) / 2.0, (double) x)
			    - Math.pow((1.0 - Math.sqrt(5.0)) / 2.0,
				       (double) x)));
	String retval = String.valueOf(y);
	return retval;
    }
}
