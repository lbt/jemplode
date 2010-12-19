/* IMutableTypeContainer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.container;

public interface IMutableTypeContainer extends IContainer
{
    public static final int TYPE_ROOT = 0;
    public static final int TYPE_FOLDER = 1;
    
    public int getType();
    
    public void setType(int i);
    
    public void setContainedType(int i);
    
    public int getContainedType();
}
