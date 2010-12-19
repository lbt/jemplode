/* ID3Tag - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.music.tag;

public interface ID3Tag
{
    public String getTitle();
    
    public String getArtist();
    
    public String getAlbum();
    
    public String getComment();
    
    public byte getTrackNumber();
    
    public byte getGenre();
    
    public short getYear();
    
    public void setTitle(String string);
    
    public void setArtist(String string);
    
    public void setAlbum(String string);
    
    public void setComment(String string);
    
    public void setTrackNumber(byte i);
    
    public void setGenre(byte i);
    
    public void setYear(short i);
    
    public boolean isChanged();
    
    public void setChanged(boolean bool);
    
    public String toString();
    
    public byte[] getBytes();
}
