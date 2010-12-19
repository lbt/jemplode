/* IPlaylistMemberDatabaseChange - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore;

public interface IPlaylistMemberDatabaseChange extends INodeDatabaseChange
{
    public PlaylistPair[] getPlaylistPairs();
    
    public void setPlaylistPairs(PlaylistPair[] playlistpairs);
}
