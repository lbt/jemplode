/* IPlaylistSoupLayer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.soup;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;

public interface IPlaylistSoupLayer extends ISoupLayer
{
    public FIDPlaylist[] getPlaylists(FIDPlaylist fidplaylist,
				      IFIDNode ifidnode, boolean bool);
}
