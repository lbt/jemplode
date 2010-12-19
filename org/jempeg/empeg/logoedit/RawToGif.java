package org.jempeg.empeg.logoedit;

import java.awt.Frame;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import com.inzyme.typeconv.LittleEndianInputStream;

/**
* RawToGif is a commandline utility for converting .raw animation
* files in to animated gifs.
*
* This requires the PJA toolkit to be installed (http://www.eteks.com/pja/en/)
*
* Usage: RawToGif <input .raw file> <output .gif file>
*
* @author Mike Schrag
* @revision $Revision: 1.1 $
*/
public class RawToGif {
  public static void main(String[] _args) throws Throwable {
    if (_args.length != 2) {
      System.out.println("Usage: RawToGif <input .raw file> <output .gif file>");
      System.exit(0);
    }

    File inFile = new File(_args[0]);
    File outFile = new File(_args[1]);

    Properties props = System.getProperties();
    props.put("awt.toolkit", "com.eteks.awt.PJAToolkit");
    props.put("java.awt.graphicsenv", "com.eteks.java2d.PJAGraphicsEnvironment");
    System.setProperties(props);

    Frame frame = new Frame();
    frame.addNotify();

    Animation animation = new Animation(frame, LogoFormatUtils.DEFAULT_WIDTH, LogoFormatUtils.DEFAULT_HEIGHT);

    System.out.println("Reading animation from " + inFile + "...");
    FileInputStream fis = new FileInputStream(inFile);
    LittleEndianInputStream eis = new LittleEndianInputStream(fis);
    animation.load(eis, inFile.length());
    fis.close();

    System.out.println("Writing animation to " + outFile + "...");
    FileOutputStream fos = new FileOutputStream(outFile);
    animation.saveAnimatedGif(fos);
    fos.close();

    System.out.println("Done.");
  }
}
