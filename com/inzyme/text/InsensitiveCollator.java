/* InsensitiveCollator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.text;
import java.text.CollationKey;
import java.text.Collator;

public class InsensitiveCollator extends Collator
{
    private Collator myCollator;
    
    public InsensitiveCollator(Collator _collator) {
	myCollator = _collator;
    }
    
    public CollationKey getCollationKey(String _source) {
	return myCollator.getCollationKey(process(_source));
    }
    
    public int compare(String _source, String _target) {
	return myCollator.compare(process(_source), process(_target));
    }
    
    public static String process(String _value) {
	String trimmedValue = _value.trim().toLowerCase();
	return trimmedValue;
    }
    
    public int hashCode() {
	return this.getClass().getName().hashCode();
    }
}
