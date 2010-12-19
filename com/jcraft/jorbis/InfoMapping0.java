/* InfoMapping0 - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.jcraft.jorbis;

class InfoMapping0
{
    int submaps;
    int[] chmuxlist = new int[256];
    int[] timesubmap = new int[16];
    int[] floorsubmap = new int[16];
    int[] residuesubmap = new int[16];
    int[] psysubmap = new int[16];
    int coupling_steps;
    int[] coupling_mag = new int[256];
    int[] coupling_ang = new int[256];
    
    void free() {
	chmuxlist = null;
	timesubmap = null;
	floorsubmap = null;
	residuesubmap = null;
	psysubmap = null;
	coupling_mag = null;
	coupling_ang = null;
    }
}
