/* RangeTagPlaylistSoupLayer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.soup;
import java.text.ParseException;

import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.text.TextRange;

import org.jempeg.nodestore.model.NodeTag;

public class RangeTagPlaylistSoupLayer extends AbstractTagPlaylistSoupLayer
{
    private TextRange[] myTextRanges;
    
    public RangeTagPlaylistSoupLayer
	(String _tagNameTextRange, int _containedType, String _sortTag)
	throws ParseException {
	super(null, _containedType, NodeTag.getNodeTag(_sortTag));
	int commaIndex = _tagNameTextRange.indexOf(',');
	if (commaIndex == -1) {
	    setTag(NodeTag.getNodeTag(_tagNameTextRange));
	    myTextRanges = new TextRange[0];
	} else {
	    String tagName = _tagNameTextRange.substring(0, commaIndex);
	    setTag(NodeTag.getNodeTag(tagName));
	    String textRange = _tagNameTextRange.substring(commaIndex + 1);
	    myTextRanges = TextRange.fromExternalForm(textRange);
	}
    }
    
    public RangeTagPlaylistSoupLayer(String _tagName, String _textRange,
				     int _containedType,
				     String _sortTag) throws ParseException {
	super(_tagName, _containedType, _sortTag);
	myTextRanges = TextRange.fromExternalForm(_textRange);
    }
    
    public RangeTagPlaylistSoupLayer(NodeTag _tag, TextRange[] _textRanges,
				     int _containedType, NodeTag _sortTag) {
	super(_tag, _containedType, _sortTag);
	myTextRanges = _textRanges;
    }
    
    public String toExternalForm() {
	StringBuffer sb = new StringBuffer();
	sb.append(getTag().getName());
	sb.append(",");
	sb.append(TextRange.toExternalForm(myTextRanges));
	return sb.toString();
    }
    
    protected String getPlaylistName(String _value) {
	char ch = Character.toUpperCase(getSortCache().getCollationKey
					    (_value).getSourceString
					    ().charAt(0));
	TextRange matchingRange = null;
	for (int i = 0; matchingRange == null && i < myTextRanges.length;
	     i++) {
	    if (myTextRanges[i].containsIgnoreCase(ch))
		matchingRange = myTextRanges[i];
	}
	String value;
	if (matchingRange == null)
	    value = ResourceBundleUtils.getUIString("soup.tagValueMissing");
	else
	    value = matchingRange.toString();
	return value;
    }
}
