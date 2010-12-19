/* FuncTime - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.jcraft.jorbis;
import com.jcraft.jogg.Buffer;

abstract class FuncTime
{
    public static FuncTime[] time_P = { new Time0() };
    
    abstract void pack(Object object, Buffer buffer);
    
    abstract Object unpack(Info info, Buffer buffer);
    
    abstract Object look(DspState dspstate, InfoMode infomode, Object object);
    
    abstract void free_info(Object object);
    
    abstract void free_look(Object object);
    
    abstract int forward(Block block, Object object);
    
    abstract int inverse(Block block, Object object, float[] fs,
			 float[] fs_0_);
}
