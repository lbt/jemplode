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

public interface ITagExtractorListener {
  public void tagExtracted(String _tagName, String _tagValue);
  
  public void tagExtracted(String _tagName, int _tagValue);
  
  public void tagExtracted(String _tagName, long _tagValue);
}
