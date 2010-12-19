/* FuncMapping - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.jcraft.jorbis;
import com.jcraft.jogg.Buffer;

abstract class FuncMapping
{
    public static FuncMapping[] mapping_P = { new Mapping0() };
    
    abstract void pack(Info info, Object object, Buffer buffer);
    
    abstract Object unpack(Info info, Buffer buffer);
    
    abstract Object look(DspState dspstate, InfoMode infomode, Object object);
    
    abstract void free_info(Object object);
    
    abstract void free_look(Object object);
    
    abstract int inverse(Block block, Object object);
}
