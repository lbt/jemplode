/**
* Copyright (c) 2001, Mike Schrag & Daniel Zimmerman
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* Redistributions of source code must retain the above copyright notice,
* this list of conditions and the following disclaimer.
*
* Redistributions in binary form must reproduce the above copyright notice,
* this list of conditions and the following disclaimer in the documentation
* and/or other materials provided with the distribution.
*
* Neither the name of Mike Schrag, Daniel Zimmerman, nor the names of any
* other contributors may be used to endorse or promote products derived from
* this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
* "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
* TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
* PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
* LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
* SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
* CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
* ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGE.
*/
package com.inzyme.text;

import java.text.CollationKey;
import java.text.Collator;

/**
* IgnoreArticlesCollator collates text by moving "The " (and friends) to the end of the string
*
* @author Mike Schrag
*/
public class IgnoreArticlesCollator extends Collator {
	private static final String[] ARTICLES = new String[] {
		"the ",//.toCharArray(),
		"un ",//.toCharArray(),
		"une ",//.toCharArray(),
		"le ",//.toCharArray(),
		"la ",//.toCharArray(),
		"l'",//.toCharArray(),
		"les ",//.toCharArray(),
		"der ",//.toCharArray(),
		"die ",//.toCharArray(),
		"das ",//.toCharArray(),
		"ein ",//.toCharArray(),
		"eine ",//.toCharArray(),
		"einen "//.toCharArray()
	};
			
  private Collator myCollator;

  public IgnoreArticlesCollator(Collator _collator) {
    myCollator = _collator;
  }

  public CollationKey getCollationKey(String _source) {
    return myCollator.getCollationKey(putArticleOnEnd(_source));
  }

  public int compare(String _source, String _target) {
    return myCollator.compare(putArticleOnEnd(_source), putArticleOnEnd(_target));
  }

  public static String putArticleOnEnd(String _value) {
		String trimmedValue = _value.trim().toLowerCase();
		String fixedValue = null;
		for (int articleNum = 0; fixedValue == null && articleNum < IgnoreArticlesCollator.ARTICLES.length; articleNum ++) {
			String article = IgnoreArticlesCollator.ARTICLES[articleNum];
			if (trimmedValue.startsWith(article)) {
				StringBuffer sb = new StringBuffer(trimmedValue.length() + 1);
				int articleLength = article.length();
				char[] chars = trimmedValue.toCharArray();
				sb.append(chars, articleLength, chars.length - articleLength);
				sb.append(' ');
				sb.append(article);
				fixedValue = sb.toString();
			}
		}

		if (fixedValue == null) {
			fixedValue = trimmedValue;
		}
		
    return fixedValue;
  }

  public int hashCode() {
    return getClass().getName().hashCode();
  }
}
