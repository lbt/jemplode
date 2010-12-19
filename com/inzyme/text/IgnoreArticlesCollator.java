/* IgnoreArticlesCollator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.text;
import java.text.CollationKey;
import java.text.Collator;

public class IgnoreArticlesCollator extends Collator
{
    private static final String[] ARTICLES
	= { "the ", "un ", "une ", "le ", "la ", "l'", "les ", "der ", "die ",
	    "das ", "ein ", "eine ", "einen " };
    private Collator myCollator;
    
    public IgnoreArticlesCollator(Collator _collator) {
	myCollator = _collator;
    }
    
    public CollationKey getCollationKey(String _source) {
	return myCollator.getCollationKey(putArticleOnEnd(_source));
    }
    
    public int compare(String _source, String _target) {
	return myCollator.compare(putArticleOnEnd(_source),
				  putArticleOnEnd(_target));
    }
    
    public static String putArticleOnEnd(String _value) {
	String trimmedValue = _value.trim().toLowerCase();
	String fixedValue = null;
	for (int articleNum = 0;
	     fixedValue == null && articleNum < ARTICLES.length;
	     articleNum++) {
	    String article = ARTICLES[articleNum];
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
	if (fixedValue == null)
	    fixedValue = trimmedValue;
	return fixedValue;
    }
    
    public int hashCode() {
	return this.getClass().getName().hashCode();
    }
}
