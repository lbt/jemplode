/* PropertiesTagExtractorListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.tags;
import java.util.Properties;

public class PropertiesTagExtractorListener implements ITagExtractorListener
{
    private Properties myTags = new Properties();
    
    public Properties getTags() {
	return myTags;
    }
    
    public String getTagValue(String _tagName) {
	return (String) myTags.get(_tagName);
    }
    
    public void tagExtracted(String _tagName, String _tagValue) {
	if (_tagValue != null)
	    myTags.put(_tagName, _tagValue);
    }
    
    public void tagExtracted(String _tagName, int _tagValue) {
	myTags.put(_tagName, String.valueOf(_tagValue));
    }
    
    public void tagExtracted(String _tagName, long _tagValue) {
	myTags.put(_tagName, String.valueOf(_tagValue));
    }
}
