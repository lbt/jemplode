/**
* This file is licensed under the GPL.
*
* See the LICENSE0 file included in this release, or
* http://www.opensource.org/licenses/gpl-license.html
* for the details of the license.
*
* Any references to com.tffenterprises code is 
* referenced under the BSD license as defined in
* LICENSE1.
*/
package org.jempeg.tags;

import java.util.Properties;

public class PropertiesTagExtractorListener implements ITagExtractorListener {
	private Properties myTags;

	public PropertiesTagExtractorListener() {
		myTags = new Properties();
	}

	public Properties getTags() {
		return myTags;
	}

	public String getTagValue(String _tagName) {
		return (String) myTags.get(_tagName);
	}

	public void tagExtracted(String _tagName, String _tagValue) {
		if (_tagValue != null) {
			myTags.put(_tagName, _tagValue);
		}
	}

	public void tagExtracted(String _tagName, int _tagValue) {
		myTags.put(_tagName, String.valueOf(_tagValue));
	}

	public void tagExtracted(String _tagName, long _tagValue) {
		myTags.put(_tagName, String.valueOf(_tagValue));
	}
}