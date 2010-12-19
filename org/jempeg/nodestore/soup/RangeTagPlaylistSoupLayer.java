package org.jempeg.nodestore.soup;

import java.text.ParseException;

import org.jempeg.nodestore.model.NodeTag;

import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.text.TextRange;

/**
 * FirstLetterTagSoupUpdater is an AbstractTagSoupUpdater 
 * implementation that uses the first letter of the tag
 * as the folder name.
 * 
 * @author Mike Schrag
 */
public class RangeTagPlaylistSoupLayer extends AbstractTagPlaylistSoupLayer {
  private TextRange[] myTextRanges;

  /**
   * Constructor for RangeTagPlaylistSoupLayer.
   * 
   * @param _tagNameTextRange the name of the tag to soup on, comma, the external form of the text range
   * @throws ParseException if the text range cannot be parsed
   */
  public RangeTagPlaylistSoupLayer(String _tagNameTextRange, int _containedType, String _sortTag) throws ParseException {
    super((NodeTag) null, _containedType, NodeTag.getNodeTag(_sortTag));
    int commaIndex = _tagNameTextRange.indexOf(',');
    if (commaIndex == -1) {
      setTag(NodeTag.getNodeTag(_tagNameTextRange));
      myTextRanges = new TextRange[0];
    }
    else {
      String tagName = _tagNameTextRange.substring(0, commaIndex);
      setTag(NodeTag.getNodeTag(tagName));
      String textRange = _tagNameTextRange.substring(commaIndex + 1);
      myTextRanges = TextRange.fromExternalForm(textRange);
    }
  }

  /**
   * Constructor for RangeTagPlaylistSoupLayer.
   * 
   * @param _tagName the name of the tag to soup on
   * @param _textRange the external form of the text range
   * @throws ParseException if the text range cannot be parsed
   */
  public RangeTagPlaylistSoupLayer(String _tagName, String _textRange, int _containedType, String _sortTag) throws ParseException {
    super(_tagName, _containedType, _sortTag);
    myTextRanges = TextRange.fromExternalForm(_textRange);
  }

  /**
   * Constructor for RangeTagPlaylistSoupLayer.
   * 
   * @param _tag the tag to soup on
   * @param _textRanges the text ranges
   */
  public RangeTagPlaylistSoupLayer(NodeTag _tag, TextRange[] _textRanges, int _containedType, NodeTag _sortTag) {
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
    char ch = Character.toUpperCase(getSortCache().getCollationKey(_value).getSourceString().charAt(0));
    
    TextRange matchingRange = null;
    for (int i = 0; matchingRange == null && i < myTextRanges.length; i ++ ) {
      if (myTextRanges[i].containsIgnoreCase(ch)) {
        matchingRange = myTextRanges[i];
      }
    }

    String value;
    if (matchingRange == null) {
      value = ResourceBundleUtils.getUIString("soup.tagValueMissing");
    }
    else {
      value = matchingRange.toString();
    }
    return value;
  }
}