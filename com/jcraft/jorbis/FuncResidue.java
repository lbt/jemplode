/* FuncResidue - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.jcraft.jorbis;
import com.jcraft.jogg.Buffer;

abstract class FuncResidue
{
    public static FuncResidue[] residue_P
	= { new Residue0(), new Residue1(), new Residue2() };
    
    abstract void pack(Object object, Buffer buffer);
    
    abstract Object unpack(Info info, Buffer buffer);
    
    abstract Object look(DspState dspstate, InfoMode infomode, Object object);
    
    abstract void free_info(Object object);
    
    abstract void free_look(Object object);
    
    abstract int forward(Block block, Object object, float[][] fs, int i);
    
    abstract int inverse(Block block, Object object, float[][] fs, int[] is,
			 int i);
}
