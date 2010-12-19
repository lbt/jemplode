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
package org.jempeg.nodestore.model;

import java.text.DateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.Vector;

import org.jempeg.nodestore.DatabaseTags;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;

import com.inzyme.format.SizeFormat;
import com.inzyme.format.TimeFormat;
import com.inzyme.properties.PropertiesManager;
import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.text.StringUtils;

/**
 * NodeTag represents a single Node Tag and
 * information about it (like the type of its
 * value and how it should be sorted).
 *
 * @author Mike Schrag
 * @version $Revision: 1.6 $
 */
public class NodeTag {
  private String myName;
  private String myDescription;
  private Class myType;
  private Class mySortType;
  private int myIconType;
  private int myGroupIconType;
  private String myDerivedFrom;

  /**
   * Constructs a new NodeTag.
   *
   * @param _name the name of the tag
   * @param _type the type that is used to render this node tag
   * @param _sortType the type that is used to sort this node tag
   * @param _iconType the icon type to render this tag with
   * @param _groupIconType the icon type to render a group of this tag with
   */
  public NodeTag(String _name, Class _type, Class _sortType, int _iconType, int _groupIconType) {
    this(_name, null, _type, _sortType, _iconType, _groupIconType);
  }

  /**
   * Constructs a new NodeTag.
   *
   * @param _name the name of the tag
   * @param _description a pretty name for the tag
   * @param _type the type that is used to render this node tag
   * @param _sortType the type that is used to sort this node tag
   * @param _iconType the icon type to render this tag with
   * @param _groupIconType the icon type to render a group of this tag with
   */
  public NodeTag(String _name, String _description, Class _type, Class _sortType, int _iconType, int _groupIconType) {
    this(_name, _description, _type, _sortType, _iconType, _groupIconType, null);
  }

  /**
   * Constructs a new NodeTag.
   * 
   * @param _name the name of the tag
   * @param _type the type that is used to render this node tag
   * @param _sortType the type that is used to sort this node tag
   * @param _iconType the icon type to render this tag with
   * @param _groupIconType the icon type to render a group of this tag with
   * @param _derivedFrom the tag name that this tag was derived from
   */
  public NodeTag(String _name, Class _type, Class _sortType, int _iconType, int _groupIconType, String _derivedFrom) {
    this(_name, null, _type, _sortType, _iconType, _groupIconType, _derivedFrom);
  }

  /**
   * Constructs a new NodeTag.
   * 
   * @param _name the name of the tag
   * @param _description a pretty name for the tag
   * @param _type the type that is used to render this node tag
   * @param _sortType the type that is used to sort this node tag
   * @param _iconType the icon type to render this tag with
   * @param _groupIconType the icon type to render a group of this tag with
   * @param _derivedFrom the tag that this tag was derived from
   */
  public NodeTag(String _name, String _description, Class _type, Class _sortType, int _iconType, int _groupIconType, String _derivedFrom) {
    myName = _name;
    if (_description == null) {
      try {
        myDescription = ResourceBundle.getBundle("org.jempeg.nodestore.model.nodeTag").getString(_name);
      }
      catch (Throwable e) {
        myDescription = _name;
      }
    }
    else {
      myDescription = _description;
    }
    myType = _type;
    mySortType = _sortType;
    myIconType = _iconType;
    myGroupIconType = _groupIconType;
    myDerivedFrom = _derivedFrom;
  }

  /**
   * Returns the name of this tag (like "artist" or "title").
   * 
   * @return the name of this tag
   */
  public String getName() {
    return myName;
  }

  /**
   * Returns the pretty name of this tag (like "Track Number" or "Artist").
   * 
   * @return the pretty name of this tag
   */
  public String getDescription() {
    return myDescription;
  }

  /**
   * Returns the type that is used to render this tag.
   * 
   * @return the type that is used to render this tag
   */
  public Class getType() {
    return myType;
  }

  /**
   * Returns the type that is used to sort this tag.
   * 
   * @return the type that is used to sort this tag
   */
  public Class getSortType() {
    return mySortType;
  }

  /**
   * Returns the icon type that is used to render this tag.
   * 
   * @return the icon type that is used to render this tag
   */
  public int getIconType() {
    return myIconType;
  }

  /**
   * Returns the icon type that is used to render a group of this tag.
   * 
   * @return the icon type that is used to render a group of this tag
   */
  public int getGroupIconType() {
    return myGroupIconType;
  }

  /**
   * Returns the actual value of the tag.
   *
   * @param _parentPlaylist the parent playlist for this node
   * @param _childIndex the index of the node in the playlist
   * @returns the actual value of the tag
   */
  public String getStringValue(FIDPlaylist _parentPlaylist, int _childIndex) {
    String value = TagValueRetriever.getValue(_parentPlaylist, _childIndex, myName);
    return value;
  }

  /**
   * Returns the actual value of the tag.
   *
   * @param _node the node to lookup
   * @returns the actual value of the tag
   */
  public String getStringValue(IFIDNode _node) {
    String value = TagValueRetriever.getValue(_node, myName);
    return value;
  }

  /**
   * Returns the value of the tag with the
   * correct type (i.e. if it is type Integer, this
   * will return an Integer instance)
   *
   * @param _parentPlaylist the parent playlist for this node
   * @param _childIndex the index of the node in the playlist
   * @returns the typed value of the tag
   */
  public Object getValue(FIDPlaylist _parentPlaylist, int _childIndex) {
    Object value = toValue(getStringValue(_parentPlaylist, _childIndex));
    return value;
  }

  /**
   * Returns the value of the tag with the
   * correct type (i.e. if it is type Integer, this
   * will return an Integer instance)
   *
   * @param _node the node to lookup
   * @returns the typed value of the tag
   */
  public Object getValue(IFIDNode _node) {
    Object value = toValue(getStringValue(_node));
    return value;
  }

  /**
   * Returns a pretty display value for the tag.
   *
   * @param _parentPlaylist the parent playlist for this node
   * @param _childIndex the index of the node in the playlist
   * @returns a pretty display value of the tag
   */
  public Object getDisplayValue(FIDPlaylist _parentPlaylist, int _childIndex) {
    String originalValue = getStringValue(_parentPlaylist, _childIndex);
    return toDisplayValue(toValue(originalValue), originalValue);
  }

  /**
   * Returns a pretty display value for the tag.
   *
   * @param _node the node to lookup
   * @returns a pretty display value of the tag
   */
  public Object getDisplayValue(IFIDNode _node) {
    String originalValue = getStringValue(_node);
    return toDisplayValue(toValue(originalValue), originalValue);
  }

  /**
   * Returns a pretty display value for the tag.
   *
   * @param _value the value to convert
   * @returns a pretty display value of the tag
   */
  public Object getDisplayValue(String _value) {
    return toDisplayValue(toValue(_value), _value);
  }

  /**
   * Returns whether or not this tag is derived from the given tag name.  Every
   * tag is derived from itself, but some tags are derived from others (for
   * instance, "decade" is derived from "year".)
   * 
   * @param _tagName the tag name to check for a relationship
   * @return whether or not this tag is derived from _tagName
   */
  public boolean isDerivedFrom(NodeTag _tag) {
    return equals(_tag) || (myDerivedFrom != null && _tag != null && myDerivedFrom.equals(_tag.myName));
  }

  /**
   * OK yeah, this is hardcoded right now .. but it's better
   * than the previous version.
   */
  protected Object toDisplayValue(Object _value, String _originalValue) {
    Object displayValue;
    if (myName.equals(DatabaseTags.DURATION_TAG)) {
      long durationInMillis = ((Number) _value).longValue();
      displayValue = TimeFormat.getInstance().format(durationInMillis);
    }
    else if (myName.equals(DatabaseTags.LENGTH_TAG)) {
      displayValue = SizeFormat.getInstance().format(((Number) _value).longValue());
    }
    else if (myName.equals(DatabaseTags.PLAY_LAST_TAG) || myName.equals(DatabaseTags.CTIME_TAG)) {
      Date d = new Date(((Number) _value).longValue() * 1000);
      displayValue = DateFormat.getDateInstance().format(d);
    }
    else if (myName.equals(DatabaseTags.TRACKNR_TAG) && _value instanceof String) {
      displayValue = TagValueRetriever.getTrackNumber((String) _value);
    }
    else if (myName.equals(DatabaseTags.YEAR_TAG)) {
      displayValue = _originalValue;
    }
    else {
      displayValue = _value;
    }
    return displayValue;
  }

  public Object toValue(String _strValue) {
    Object value;
    if (mySortType == String.class) {
      value = _strValue;
    }
    else if (mySortType == Integer.class) {
      value = new Integer(StringUtils.parseIntWithoutException(_strValue));
    }
    else if (mySortType == Long.class) {
      value = new Long(StringUtils.parseLongWithoutException(_strValue));
    }
    else {
      value = _strValue;
    }
    return value;
  }

  public boolean equals(Object _obj) {
    return _obj == this || ((_obj instanceof NodeTag) && myName.equals(((NodeTag) _obj).myName));
  }

  public int hashCode() {
    return myName.hashCode();
  }

  public String toString() {
    return myDescription;
  }

  private static NodeTag[] DEFAULTS = new NodeTag[] {
      // Don't change the order of these first 6 ... There are some optimizations for this
      new NodeTag(DatabaseTags.TITLE_TAG, String.class, String.class, IFIDNode.TYPE_PLAYLIST, IFIDNode.TYPE_PLAYLIST),
      new NodeTag(DatabaseTags.ARTIST_TAG, String.class, String.class, IFIDNode.TYPE_ARTIST, IFIDNode.TYPE_ARTISTS),
      new NodeTag(DatabaseTags.SOURCE_TAG, String.class, String.class, IFIDNode.TYPE_ALBUM, IFIDNode.TYPE_ALBUMS),
      new NodeTag(DatabaseTags.GENRE_TAG, String.class, String.class, IFIDNode.TYPE_GENRE, IFIDNode.TYPE_GENRES),
      new NodeTag(DatabaseTags.YEAR_TAG, Integer.class, Integer.class, IFIDNode.TYPE_YEAR, IFIDNode.TYPE_YEARS),
      new NodeTag(DatabaseTags.TRACKNR_TAG, Integer.class, Integer.class, IFIDNode.TYPE_PLAYLIST, IFIDNode.TYPE_PLAYLIST),

      new NodeTag(DatabaseTags.LENGTH_TAG, String.class, Integer.class, IFIDNode.TYPE_PLAYLIST, IFIDNode.TYPE_PLAYLIST),
      new NodeTag(DatabaseTags.DURATION_TAG, String.class, Integer.class, IFIDNode.TYPE_PLAYLIST, IFIDNode.TYPE_PLAYLIST),
      new NodeTag(DatabaseTags.POS_TAG, Integer.class, Integer.class, IFIDNode.TYPE_PLAYLIST, IFIDNode.TYPE_PLAYLIST),
      new NodeTag(DatabaseTags.BITRATE_TAG, String.class, String.class, IFIDNode.TYPE_PLAYLIST, IFIDNode.TYPE_PLAYLIST),
      new NodeTag(DatabaseTags.SAMPLERATE_TAG, String.class, String.class, IFIDNode.TYPE_PLAYLIST, IFIDNode.TYPE_PLAYLIST),
      new NodeTag(DatabaseTags.MARKED_TAG, String.class, String.class, IFIDNode.TYPE_PLAYLIST, IFIDNode.TYPE_PLAYLIST),
      new NodeTag(DatabaseTags.PLAY_COUNT_TAG, Integer.class, Integer.class, IFIDNode.TYPE_PLAYLIST, IFIDNode.TYPE_PLAYLIST),
      new NodeTag(DatabaseTags.SKIP_COUNT_TAG, Integer.class, Integer.class, IFIDNode.TYPE_PLAYLIST, IFIDNode.TYPE_PLAYLIST),
      new NodeTag(DatabaseTags.PLAY_LAST_TAG, String.class, Long.class, IFIDNode.TYPE_PLAYLIST, IFIDNode.TYPE_PLAYLIST),
      new NodeTag(DatabaseTags.CTIME_TAG, String.class, Long.class, IFIDNode.TYPE_PLAYLIST, IFIDNode.TYPE_PLAYLIST),
      new NodeTag(DatabaseTags.COMMENT_TAG, String.class, String.class, IFIDNode.TYPE_PLAYLIST, IFIDNode.TYPE_PLAYLIST),
      new NodeTag(DatabaseTags.CODEC_TAG, String.class, String.class, IFIDNode.TYPE_PLAYLIST, IFIDNode.TYPE_PLAYLIST),
      new NodeTag(DatabaseTags.COPYRIGHT_TAG, String.class, String.class, IFIDNode.TYPE_PLAYLIST, IFIDNode.TYPE_PLAYLIST),
      new NodeTag(DatabaseTags.OFFSET_TAG, Integer.class, Integer.class, IFIDNode.TYPE_PLAYLIST, IFIDNode.TYPE_PLAYLIST),
      new NodeTag(DatabaseTags.FID_TAG, Integer.class, Integer.class, IFIDNode.TYPE_PLAYLIST, IFIDNode.TYPE_PLAYLIST),
      new NodeTag(DatabaseTags.REFS_TAG, Integer.class, Integer.class, IFIDNode.TYPE_PLAYLIST, IFIDNode.TYPE_PLAYLIST),
      new NodeTag(DatabaseTags.WENDY_TAG, String.class, String.class, IFIDNode.TYPE_PLAYLIST, IFIDNode.TYPE_PLAYLIST),
      new NodeTag(DatabaseTags.DECADE_TAG, Integer.class, Integer.class, IFIDNode.TYPE_YEAR, IFIDNode.TYPE_YEARS, DatabaseTags.YEAR_TAG),
      new NodeTag(DatabaseTags.PIN_TAG, String.class, String.class, IFIDNode.TYPE_PLAYLIST, IFIDNode.TYPE_PLAYLIST),
      new NodeTag(DatabaseTags.PLAYLIST_SIZE_TAG, Integer.class, Integer.class, IFIDNode.TYPE_PLAYLIST, IFIDNode.TYPE_PLAYLIST),
      new NodeTag(DatabaseTags.TRACKS_TAG, Integer.class, Integer.class, IFIDNode.TYPE_PLAYLIST, IFIDNode.TYPE_PLAYLIST)
  };
  public static final NodeTag TITLE_TAG = NodeTag.DEFAULTS[0];
  public static final NodeTag ARTIST_TAG = NodeTag.DEFAULTS[1];
  public static final NodeTag SOURCE_TAG = NodeTag.DEFAULTS[2];
  public static final NodeTag GENRE_TAG = NodeTag.DEFAULTS[3];
  public static final NodeTag YEAR_TAG = NodeTag.DEFAULTS[4];
  public static final NodeTag TRACKNR_TAG = NodeTag.DEFAULTS[5];

  private static String TAG_VALUE_VARIOUS;
  private static String TAG_VALUE_MISSING;
  private static Vector myNodeTags;
  private static Hashtable myNameToNodeTag;

  static {
    resetDefaultNodeTags();
  }

  public static String getMissingTagValue() {
    if (NodeTag.TAG_VALUE_MISSING == null) {
      NodeTag.TAG_VALUE_MISSING = ResourceBundleUtils.getUIString("soup.tagValueMissing");
    }
    return NodeTag.TAG_VALUE_MISSING;
  }

  public static String getVariousTagValue() {
    if (NodeTag.TAG_VALUE_VARIOUS == null) {
      NodeTag.TAG_VALUE_VARIOUS = ResourceBundleUtils.getUIString("soup.tagValueVarious");
    }
    return NodeTag.TAG_VALUE_VARIOUS;
  }

  /**
   * Returns all the NodeTags that are known in the system.
   * 
   * @return all the NodeTags that are known in the system
   */
  public static NodeTag[] getNodeTags() {
    NodeTag[] nodeTags = new NodeTag[myNodeTags.size()];
    myNodeTags.copyInto(nodeTags);
    return nodeTags;
  }

  /**
   * Resets the node tags to their default values.
   */
  protected static void resetDefaultNodeTags() {
    myNodeTags = new Vector();
    myNameToNodeTag = new Hashtable();
    for (int i = 0; i < DEFAULTS.length; i ++ ) {
      addNodeTag(DEFAULTS[i]);
    }
  }

  /**
   * This resets the NodeTags that are available as column titles
   * and search fields.  This is typically only called automatically
   * after downloading,but it's possible this could need to be called 
   * at other times.
   * 
   * @param _databaseTags the DatabaseTags to look for custom tags with
   */
  public static void resetNodeTags(DatabaseTags _databaseTags) {
    PropertiesManager propertiesManager = PropertiesManager.getInstance();
    NodeTag.resetDefaultNodeTags();
    Enumeration tagNamesEnum = _databaseTags.getTagNames();
    while (tagNamesEnum.hasMoreElements()) {
      String tagName = (String) tagNamesEnum.nextElement();
      if (!NodeTag.containsNodeTag(tagName)) {
        String description = propertiesManager.getProperty("jempeg.tag." + tagName + ".description", tagName);

        Class typeClass;
        try {
          typeClass = Class.forName(propertiesManager.getProperty("jempeg.tag." + tagName + ".typeClass", "java.lang.String"));
        }
        catch (Throwable t) {
          typeClass = String.class;
        }

        Class sortClass;
        try {
          sortClass = Class.forName(propertiesManager.getProperty("jempeg.tag." + tagName + ".sortClass", "java.lang.String"));
        }
        catch (Throwable t) {
          sortClass = String.class;
        }

        int iconType = propertiesManager.getIntProperty("jempeg.tag." + tagName + ".iconType", IFIDNode.TYPE_PLAYLIST);

        int groupIconType = propertiesManager.getIntProperty("jempeg.tag." + tagName + ".groupIconType", IFIDNode.TYPE_PLAYLIST);

        NodeTag.addNodeTag(new NodeTag(tagName, description, typeClass, sortClass, iconType, groupIconType));
      }
    }
  }

  /**
   * Adds a NodeTag to the system.
   * 
   * @param _nodeTag the NodeTag to add
   */
  public static void addNodeTag(NodeTag _nodeTag) {
    String name = _nodeTag.getName();
    if (!myNameToNodeTag.containsKey(name)) {
      myNameToNodeTag.put(name, _nodeTag);
      myNodeTags.addElement(_nodeTag);
    }
  }

  /**
   * Returns whether or not there is a NodeTag for the given tag name.
   * 
   * @param _name the tag name to return a NodeTag for
   * @return whether or not there is a NodeTag for the given tag name
   */
  public static boolean containsNodeTag(String _name) {
    boolean contains = myNameToNodeTag.containsKey(_name);
    return contains;
  }

  /**
   * Returns the NodeTag for the given tag name.
   * 
   * @param _name the tag name to return a NodeTag for
   * @return the NodeTag for the given tag name
   */
  public static NodeTag getNodeTag(String _name) {
    NodeTag nodeTag;
    if (_name == DatabaseTags.TITLE_TAG) {
      nodeTag = NodeTag.TITLE_TAG;
    }
    else if (_name == DatabaseTags.ARTIST_TAG) {
      nodeTag = NodeTag.ARTIST_TAG;
    }
    else if (_name == DatabaseTags.SOURCE_TAG) {
      nodeTag = NodeTag.SOURCE_TAG;
    }
    else if (_name == DatabaseTags.GENRE_TAG) {
      nodeTag = NodeTag.GENRE_TAG;
    }
    else if (_name == DatabaseTags.YEAR_TAG) {
      nodeTag = NodeTag.YEAR_TAG;
    }
    else if (_name == DatabaseTags.TRACKNR_TAG) {
      nodeTag = NodeTag.TRACKNR_TAG;
    }
    else {
      nodeTag = (NodeTag) myNameToNodeTag.get(_name);
      if (nodeTag == null) {
        nodeTag = new NodeTag(_name, _name, String.class, String.class, IFIDNode.TYPE_PLAYLIST, IFIDNode.TYPE_PLAYLIST);
      }
    }
    return nodeTag;
  }
}