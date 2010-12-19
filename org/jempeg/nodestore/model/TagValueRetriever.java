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

import org.jempeg.nodestore.DatabaseTags;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.NodeTags;
import org.jempeg.nodestore.WendyFlags;
import org.jempeg.tags.Genre;

/**
 * TagValueRetriever providfes an layer above the 
 * NodeTags of a Node to expose "virtual tags" like
 * "decade" that are not actually contained in the 
 * tags themselves.
 * 
 * @author Mike Schrag
 * @version $Revision: 1.6 $
 */
public class TagValueRetriever {
	/**
	 * Returns the value for tags that require the context of
	 * the container FIDPlaylist.
	 * 
	 * @param _parentPlaylist the parent playlist
	 * @param _childIndex the index of the child node
	 * @param _tagName the tag name to retrieve
	 * @return the value of a tag or a virtual tag
	 */
	public static String getValue(FIDPlaylist _parentPlaylist, int _childIndex, String _tagName) {
		IFIDNode node = _parentPlaylist.getNodeAt(_childIndex);
		String value;
		if (node != null) {
			if (_tagName.equals(DatabaseTags.POS_TAG)) {
				int pos;
				if (_parentPlaylist != null) {
					pos = _childIndex;
				}
				else {
					pos = 0;
				}
				value = String.valueOf(pos + 1);
			}
			else if (_tagName.equals(DatabaseTags.PLAYLIST_SIZE_TAG)) {
				if (node instanceof FIDPlaylist) {
					FIDPlaylist playlist = (FIDPlaylist) node;
					value = String.valueOf(playlist.getSize());
				}
				else {
					value = "";
				}
			}
			else if (_tagName.equals(DatabaseTags.TRACKS_TAG)) {
				if (node instanceof FIDPlaylist) {
					FIDPlaylist playlist = (FIDPlaylist) node;
					value = String.valueOf(playlist.getTrackCount());
				}
				else {
					value = "";
				}
			}
			else if (_tagName.equals(DatabaseTags.TRACKNR_TAG) || _tagName.equals(DatabaseTags.TRACKNR_OR_POS_TAG)) {
				value = getValue(node, DatabaseTags.TRACKNR_TAG);
				try {
					value = TagValueRetriever.getTrackNumber(value);
					if (value.length() == 0 || Integer.parseInt(value) <= 0) {
						value = null;
					}
				}
				catch (NumberFormatException e) {
					value = null;
				}
				if (value == null) {
					if (_tagName.equals(DatabaseTags.TRACKNR_OR_POS_TAG)) {
						value = getValue(_parentPlaylist, _childIndex, DatabaseTags.POS_TAG);
					}
					else {
						value = "";
					}
				}
			}
			else {
				value = getValue(node, _tagName);
			}
		}
		else {
			value = "";
		}
		return value;
	}

	/**
	 * Returns the value of a tag or a virtual tag.
	 * 
	 * @param _node the node to retrieve the value from
	 * @param _tagName the name of the tag to retrieve
	 * @return the value of a tag or a virtual tag
	 */
	public static String getValue(IFIDNode _node, String _tagName) {
		String value;
		if (_tagName.equals(DatabaseTags.REFS_TAG)) {
			value = String.valueOf(_node.getSouplessReferenceCount());
		}
		else if (_tagName.equals(DatabaseTags.DECADE_TAG)) {
			int year = _node.getTags().getIntValue(DatabaseTags.YEAR_TAG, -1);
			if (year == -1) {
				value = "";
			}
			else {
				year -= (year % 10);
				value = String.valueOf(year);
			}
		}
		else if (_tagName.equals(DatabaseTags.DIRTY_TAG)) {
			value = String.valueOf(_node.isDirty());
		}
		else if (_tagName.equals(DatabaseTags.FID_TAG)) {
			value = String.valueOf(_node.getFID());
		}
		else if (_tagName.equals(DatabaseTags.EXTENSION_TAG)) {
			value = String.valueOf(_node.getTags().getValue(DatabaseTags.CODEC_TAG));
			if (value != null && value.length() > 0 && !value.equalsIgnoreCase(DatabaseTags.CODEC_TAXI)) {
				value = value.toLowerCase();
				if (DatabaseTags.CODEC_OGG.equals(value)) {
					value = "ogg";
				}
				else if (DatabaseTags.CODEC_FLAC.equals(value)) {
					value = "flac";
				}
				else if (value.length() > 3) {
					value = value.substring(0, 3);
				}
				value = "." + value;
			}
			else {
				value = "";
			}
		}
		else if (_tagName.equals(DatabaseTags.WENDY_TAG)) {
			// This is innefficient right now .. PlayerDatabase probably needs to
			// cache wendy flags so they don't have to be recomputed each time
			int wendy = _node.getTags().getIntValue(DatabaseTags.WENDY_TAG, 0);
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
				if (wendyFlagsSB.length() > 0) {
					wendyFlagsSB.setLength(wendyFlagsSB.length() - 1);
				}
				value = wendyFlagsSB.toString();
			}
			else {
				value = "";
			}
		}
		else if (_tagName.equals(DatabaseTags.JEMPLODE_IS_SOUP_TAG)) {
			value = String.valueOf(_node instanceof FIDPlaylist && ((FIDPlaylist)_node).isSoup());
		}
		else {
			NodeTags tags = _node.getTags();
			value = tags.getValue(_tagName);
			if (_tagName.equals(DatabaseTags.GENRE_TAG)) {
				value = Genre.getGenre(value);
			}
		}
		return value;
	}

	public static String getTrackNumber(String _value) {
		String trackNumber;
		if (_value != null) {
			int firstNonDigit = -1;
			int size = _value.length();
			for (int i = 0; firstNonDigit == -1 && i < size; i++) {
				if (!Character.isDigit(_value.charAt(i))) {
					firstNonDigit = i;
				}
			}
			if (firstNonDigit == -1) {
				trackNumber = _value;
			}
			else {
				trackNumber = _value.substring(0, firstNonDigit);
			}
		}
		else {
			trackNumber = null;
		}
		return trackNumber;
	}

}
