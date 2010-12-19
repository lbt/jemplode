package org.jempeg.nodestore.soup;

import org.jempeg.nodestore.model.NodeTag;

/**
 * FirstLetterTagSoupUpdater is an AbstractTagSoupUpdater 
 * implementation that uses the first letter of the tag
 * as the folder name.
 * 
 * @author Mike Schrag
 */
public class FirstLetterTagPlaylistSoupLayer extends AbstractTagPlaylistSoupLayer {
  public FirstLetterTagPlaylistSoupLayer(String _tagName, int _containedType, String _sortTag) {
    super(_tagName, _containedType, _sortTag);
  }

  public FirstLetterTagPlaylistSoupLayer(NodeTag _tag, int _containedType, NodeTag _sortTag) {
    super(_tag, _containedType, _sortTag);
  }

  public String toExternalForm() {
    return getTag().getName();
  }

  protected String getPlaylistName(String _value) {
    String value = String.valueOf(Character.toUpperCase(getSortCache().getCollationKey(_value).getSourceString().charAt(0)));
    return value;
  }
}