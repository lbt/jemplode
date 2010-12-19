/* TagValueRetriever - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.model;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.NodeTags;
import org.jempeg.nodestore.WendyFlags;
import org.jempeg.tags.Genre;

public class TagValueRetriever
{
    public static String getValue(FIDPlaylist _parentPlaylist, int _childIndex,
				  String _tagName) {
	IFIDNode node = _parentPlaylist.getNodeAt(_childIndex);
	String value;
	if (node != null) {
	    if (_tagName.equals("pos")) {
		int pos;
		if (_parentPlaylist != null)
		    pos = _childIndex;
		else
		    pos = 0;
		value = String.valueOf(pos + 1);
	    } else if (_tagName.equals("size")) {
		if (node instanceof FIDPlaylist) {
		    FIDPlaylist playlist = (FIDPlaylist) node;
		    value = String.valueOf(playlist.getSize());
		} else
		    value = "";
	    } else if (_tagName.equals("tracks")) {
		if (node instanceof FIDPlaylist) {
		    FIDPlaylist playlist = (FIDPlaylist) node;
		    value = String.valueOf(playlist.getTrackCount());
		} else
		    value = "";
	    } else if (_tagName.equals("tracknr")
		       || _tagName.equals("tracknrpos")) {
		value = getValue(node, "tracknr");
		try {
		    value = getTrackNumber(value);
		    if (value.length() == 0 || Integer.parseInt(value) <= 0)
			value = null;
		} catch (NumberFormatException e) {
		    value = null;
		}
		if (value == null) {
		    if (_tagName.equals("tracknrpos"))
			value = getValue(_parentPlaylist, _childIndex, "pos");
		    else
			value = "";
		}
	    } else
		value = getValue(node, _tagName);
	} else
	    value = "";
	return value;
    }
    
    public static String getValue(IFIDNode _node, String _tagName) {
	String value;
	if (_tagName.equals("refs"))
	    value = String.valueOf(_node.getSouplessReferenceCount());
	else if (_tagName.equals("decade")) {
	    int year = _node.getTags().getIntValue("year", -1);
	    if (year == -1)
		value = "";
	    else {
		year -= year % 10;
		value = String.valueOf(year);
	    }
	} else if (_tagName.equals("dirty"))
	    value = String.valueOf(_node.isDirty());
	else if (_tagName.equals("fid"))
	    value = String.valueOf(_node.getFID());
	else if (_tagName.equals("ext")) {
	    value = _node.getTags().getValue("codec");
	    if (value != null && value.length() > 0
		&& !value.equalsIgnoreCase("taxi")) {
		value = value.toLowerCase();
		if ("vorbis".equals(value))
		    value = "ogg";
		else if ("flac".equals(value))
		    value = "flac";
		else if (value.length() > 3)
		    value = value.substring(0, 3);
		value = "." + value;
	    } else
		value = "";
	} else if (_tagName.equals("wendy")) {
	    int wendy = _node.getTags().getIntValue("wendy", 0);
	    if (wendy > 0) {
		StringBuffer wendyFlagsSB = new StringBuffer();
		WendyFlags wendyFlags = new WendyFlags();
		wendyFlags.readFlags(_node.getDB().getDeviceSettings());
		String[] flags = wendyFlags.getFlags();
		for (int i = 0; i < flags.length; i++) {
		    if (wendyFlags.isWendyFlagSet(wendy, i)) {
			wendyFlagsSB.append(flags[i]);
			wendyFlagsSB.append(",");
		    }
		}
		if (wendyFlagsSB.length() > 0)
		    wendyFlagsSB.setLength(wendyFlagsSB.length() - 1);
		value = wendyFlagsSB.toString();
	    } else
		value = "";
	} else if (_tagName.equals("jemplode_issoup"))
	    value = String.valueOf(_node instanceof FIDPlaylist
				   && ((FIDPlaylist) _node).isSoup());
	else {
	    NodeTags tags = _node.getTags();
	    value = tags.getValue(_tagName);
	    if (_tagName.equals("genre"))
		value = Genre.getGenre(value);
	}
	return value;
    }
    
    public static String getTrackNumber(String _value) {
	String trackNumber;
	if (_value != null) {
	    int firstNonDigit = -1;
	    int size = _value.length();
	    for (int i = 0; firstNonDigit == -1 && i < size; i++) {
		if (!Character.isDigit(_value.charAt(i)))
		    firstNonDigit = i;
	    }
	    if (firstNonDigit == -1)
		trackNumber = _value;
	    else
		trackNumber = _value.substring(0, firstNonDigit);
	} else
	    trackNumber = null;
	return trackNumber;
    }
}
