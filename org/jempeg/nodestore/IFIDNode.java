/* IFIDNode - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore;
import javax.swing.tree.TreePath;

import com.inzyme.model.Reason;

public interface IFIDNode
{
    public static final int TYPE_PLAYLIST = 2;
    public static final int TYPE_TUNE = 3;
    public static final int TYPE_TUNES = 4;
    public static final int TYPE_GENRE = 5;
    public static final int TYPE_GENRES = 6;
    public static final int TYPE_YEAR = 7;
    public static final int TYPE_YEARS = 8;
    public static final int TYPE_ALBUM = 9;
    public static final int TYPE_ALBUMS = 10;
    public static final int TYPE_ARTIST = 11;
    public static final int TYPE_ARTISTS = 12;
    public static final int TYPE_PLAYLISTS = 13;
    public static final int ATTRIBUTE_DIRTY = 1;
    public static final int ATTRIBUTE_MARKED = 2;
    public static final int ATTRIBUTE_COLORED = 4;
    public static final int ATTRIBUTE_TRANSIENT = 8;
    
    public int getType();
    
    public long getFID();
    
    public int getGeneration();
    
    public int getAttributes(boolean bool);
    
    public boolean isColored();
    
    public boolean isTransient();
    
    public void setColored(boolean bool);
    
    public boolean isIdentified();
    
    public void setIdentified(boolean bool);
    
    public boolean isDirty();
    
    public void setDirty(boolean bool);
    
    public boolean isMarkedForDeletion();
    
    public PlayerDatabase getDB();
    
    public NodeTags getTags();
    
    public String getTitle();
    
    public int getLength();
    
    public boolean hasOption(int i);
    
    public void setOption(int i, boolean bool);
    
    public Reason[] checkForProblems(boolean bool, TreePath treepath);
    
    public void delete();
    
    public String toVerboseString(int i);
    
    public int getReferenceCount();
    
    public int getSouplessReferenceCount();
    
    public void nodeAddedToPlaylist(FIDPlaylist fidplaylist);
    
    public void nodeRemovedFromPlaylist(FIDPlaylist fidplaylist);
    
    public void clearParentPlaylists();
    
    public FIDPlaylist[] getParentPlaylists();
}
