/* ImageUtils - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.logoedit;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JFileChooser;

import com.inzyme.util.Debug;
import com.keypoint.PngEncoder;
import com.sixlegs.image.png.PngImage;

import org.jempeg.manager.event.FileChooserKeyListener;
import org.jempeg.manager.util.FileChooserUtils;

public class ImageUtils
{
    public static Image createImage(Component _comp, int _width, int _height) {
	Image image = _comp.createImage(_width, _height);
	Graphics imageGraphics = image.getGraphics();
	imageGraphics.setColor(Color.black);
	imageGraphics.fillRect(0, 0, _width, _height);
	return image;
    }
    
    public static Object loadImage(File _imageFile, Component _comp)
	throws IOException {
	String imageFilename = _imageFile.getAbsolutePath();
	String lowercaseFileName = imageFilename.toLowerCase();
	Object image = null;
	if (lowercaseFileName.endsWith(".bin")) {
	    FileInputStream fis = new FileInputStream(_imageFile);
	    Logo logo
		= LogoFormatUtils.toLogo(_comp, fis,
					 LogoFormatUtils.DEFAULT_GRAY_VALUES);
	    fis.close();
	    image = logo;
	} else if (lowercaseFileName.endsWith(".png")) {
	    PngImage pngImage = new PngImage(imageFilename);
	    image = Toolkit.getDefaultToolkit().createImage(pngImage);
	} else
	    image = Toolkit.getDefaultToolkit().getImage(imageFilename);
	if (image instanceof Image) {
	    try {
		MediaTracker mt = new MediaTracker(_comp);
		mt.addImage((Image) image, 0);
		mt.waitForAll();
	    } catch (InterruptedException e) {
		Debug.println(e);
	    }
	}
	return image;
    }
    
    public static Object loadImage(Component _comp) throws IOException {
	Object image = null;
	JFileChooser jfc = new JFileChooser();
	jfc.addKeyListener(new FileChooserKeyListener(jfc));
	jfc.setMultiSelectionEnabled(false);
	FileChooserUtils.setToLastDirectory(jfc);
	int results = jfc.showOpenDialog(_comp);
	if (results == 0) {
	    FileChooserUtils.saveLastDirectory(jfc);
	    File imageFile = jfc.getSelectedFile();
	    image = loadImage(imageFile, _comp);
	}
	return image;
    }
    
    public static void savePngImage(Component _comp) throws IOException {
	Dimension d = _comp.getSize();
	Image img = _comp.createImage(d.width, d.height);
	_comp.paint(img.getGraphics());
	savePngImage(img, _comp);
    }
    
    public static void savePngImage(Image _image, Component _saveComponent)
	throws IOException {
	JFileChooser jfc = new JFileChooser();
	jfc.addKeyListener(new FileChooserKeyListener(jfc));
	FileChooserUtils.setToLastDirectory(jfc);
	int result = jfc.showSaveDialog(_saveComponent);
	if (result == 0) {
	    FileChooserUtils.saveLastDirectory(jfc);
	    File saveFile = jfc.getSelectedFile();
	    savePngImage(_image, saveFile);
	}
    }
    
    public static void savePngImage(Image _image, File _file)
	throws IOException {
	PngEncoder encoder = new PngEncoder(_image);
	byte[] pngImage = encoder.pngEncode();
	FileOutputStream fos = new FileOutputStream(_file);
	fos.write(pngImage);
	fos.close();
    }
}
