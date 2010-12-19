package org.jempeg.nodestore;

import java.text.DecimalFormat;

import org.jempeg.nodestore.model.TagValueRetriever;

public class NodeTagStringEvaluator {
	private IFIDNode myNode;
	
	private FIDPlaylist myParentPlaylist;
	private int myIndex;

	public NodeTagStringEvaluator(IFIDNode _node) {
		myNode = _node;
	}
	
	public NodeTagStringEvaluator(FIDPlaylist _parentPlaylist, int _index) {
		myParentPlaylist = _parentPlaylist;
		myIndex = _index;
	}

	public String evaluate(String _str) {
		String parsedStr;
		if (_str == null) {
			parsedStr = null;
		}
		else {
			int leftBracketPos = _str.indexOf('{');
			if (leftBracketPos == -1) {
				parsedStr = _str;
			}
			else {
				StringBuffer parsedStrBuf = new StringBuffer();
				int length = _str.length();
				if (leftBracketPos > 0) {
					parsedStrBuf.append(_str.substring(0, leftBracketPos));
				}
				for (int i = leftBracketPos; i < length; i++) {
					char ch = _str.charAt(i);
					switch (ch) {
						case '{' :
							leftBracketPos = i;
							break;
						case '}' :
							if (leftBracketPos > -1) {
								String tagName = _str.substring(leftBracketPos + 1, i);
								int colonIndex = tagName.indexOf(':');

								int pad = -1;
								if (colonIndex > -1) {
									String padStr = tagName.substring(colonIndex + 1);
									try {
										pad = Integer.parseInt(padStr);
										tagName = tagName.substring(0, colonIndex);
									}
									catch (NumberFormatException e) {
										pad = -1;
									}
								}

								String value;
								if (myParentPlaylist == null) {
									value = TagValueRetriever.getValue(myNode, tagName);
								} else {
									value = TagValueRetriever.getValue(myParentPlaylist, myIndex, tagName);
								}
								if (value == null) {
									value = "";
								}

								try {
									if (pad >= 0) {
										StringBuffer formatBuf = new StringBuffer();
										for (int padNum = 0; padNum < pad; padNum++) {
											formatBuf.append("0");
										}
										DecimalFormat df = new DecimalFormat(formatBuf.toString());
										value = df.format(Integer.parseInt(value));
									}
								}
								catch (Throwable e) {
									// Oh well...
								}

								if (value != null) {
									parsedStrBuf.append(value);
								}
								leftBracketPos = -1;
							}
							break;
						default :
							if (leftBracketPos == -1) {
								parsedStrBuf.append(ch);
							}
							break;
					}
				}
				parsedStr = parsedStrBuf.toString();
			}
		}
		return parsedStr;
	}
}
