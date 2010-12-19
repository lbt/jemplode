/* FuncFloor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.jcraft.jorbis;
import com.jcraft.jogg.Buffer;

abstract class FuncFloor
{
    public static FuncFloor[] floor_P = { new Floor0(), new Floor1() };
    
    abstract void pack(Object object, Buffer buffer);
    
    abstract Object unpack(Info info, Buffer buffer);
    
    abstract Object look(DspState dspstate, InfoMode infomode, Object object);
    
    abstract void free_info(Object object);
    
    abstract void free_look(Object object);
    
    abstract void free_state(Object object);
    
    abstract int forward(Block block, Object object, float[] fs, float[] fs_0_,
			 Object object_1_);
    
    abstract Object inverse1(Block block, Object object, Object object_2_);
    
    abstract int inverse2(Block block, Object object, Object object_3_,
			  float[] fs);
}
