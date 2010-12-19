/**
* Copyright (c) 2001, Mike Schrag & Daniel Zimmerman
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* Redistributions of source code must retain the above copyright notice,
* this list of conditions and the following disclaimer.
*
* Redistributions in binary form must reproduce the above copyright notice,
* this list of conditions and the following disclaimer in the documentation
* and/or other materials provided with the distribution.
*
* Neither the name of Mike Schrag, Daniel Zimmerman, nor the names of any
* other contributors may be used to endorse or promote products derived from
* this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
* "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
* TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
* PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
* LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
* SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
* CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
* ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGE.
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

import org.jempeg.manager.event.FileChooserKeyListener;
import org.jempeg.manager.util.FileChooserUtils;

import com.inzyme.util.Debug;
import com.keypoint.PngEncoder;
import com.sixlegs.image.png.PngImage;

/**
* Handy-dandy methods for managing images.
*
* @author Mike Schrag
* @revision $Revision: 1.3 $
*/
public class ImageUtils {
	/**
	* Creates an empty (black) image.
	*
	* @param _comp the component to create the image with
	* @param _width the width of the image
	* @param _height the height of the image
	* @returns the new blank Image
	*/
	public static Image createImage(Component _comp, int _width, int _height) {
		Image image = _comp.createImage(_width, _height);
		Graphics imageGraphics = image.getGraphics();
		imageGraphics.setColor(Color.black);
		imageGraphics.fillRect(0, 0, _width, _height);
		return image;
	}

	/**
	* Loads PNG, GIF, JPEG, and Empeg Logo-format images.  This will either 
	* return a java.awt.Image or an org.jempeg.empeg.logoedit.Logo.
	*
	* @param _imageFile the image file to load
	* @param _comp the component to use to create the Image
	* @returns an Image or a Logo
	* @throws IOException if the image load fails
	*/
	public static Object loadImage(File _imageFile, Component _comp) throws IOException {
		String imageFilename = _imageFile.getAbsolutePath();
		String lowercaseFileName = imageFilename.toLowerCase();

		Object image = null;
		if (lowercaseFileName.endsWith(".bin")) {
			FileInputStream fis = new FileInputStream(_imageFile);
			Logo logo = LogoFormatUtils.toLogo(_comp, fis, LogoFormatUtils.DEFAULT_GRAY_VALUES);
			fis.close();
			image = logo;
		}
		else if (lowercaseFileName.endsWith(".png")) {
			PngImage pngImage = new PngImage(imageFilename);
			image = Toolkit.getDefaultToolkit().createImage(pngImage);
		}
		else {
			image = Toolkit.getDefaultToolkit().getImage(imageFilename);
		}

		if (image instanceof Image) {
			try {
				MediaTracker mt = new MediaTracker(_comp);
				mt.addImage((Image) image, 0);
				mt.waitForAll();
			}
			catch (InterruptedException e) {
				Debug.println(e);
			}
		}

		return image;
	}

	/**
	* Shows a JFileChooser then calls loadImage.
	*
	* @param _comp the component to use to create the Image
	* @returns an Image or a Logo
	* @throws IOException if the image load fails
	*/
	public static Object loadImage(Component _comp) throws IOException {
		Object image = null;

		JFileChooser jfc = new JFileChooser();
		jfc.addKeyListener(new FileChooserKeyListener(jfc));
		jfc.setMultiSelectionEnabled(false);
		FileChooserUtils.setToLastDirectory(jfc);
		int results = jfc.showOpenDialog(_comp);
		if (results == JFileChooser.APPROVE_OPTION) {
			FileChooserUtils.saveLastDirectory(jfc);
			File imageFile = jfc.getSelectedFile();
			image = loadImage(imageFile, _comp);
		}

		return image;
	}

	/**
	* Saves a component as a PNG Image.
	*
	* @param _saveComponent the component to save
	* @throws IOException if the save fails
	*/
	public static void savePngImage(Component _comp) throws IOException {
		Dimension d = _comp.getSize();
		Image img = _comp.createImage(d.width, d.height);
		_comp.paint(img.getGraphics());
		savePngImage(img, _comp);
	}

	/**
	* Saves an image as a PNG file.
	*
	* @param _image the image to save
	* @param _saveComponent the component to save
	* @throws IOException if the save fails
	*/
	public static void savePngImage(Image _image, Component _saveComponent) throws IOException {
		JFileChooser jfc = new JFileChooser();
		jfc.addKeyListener(new FileChooserKeyListener(jfc));
		FileChooserUtils.setToLastDirectory(jfc);
		int result = jfc.showSaveDialog(_saveComponent);
		if (result == JFileChooser.APPROVE_OPTION) {
			FileChooserUtils.saveLastDirectory(jfc);
			File saveFile = jfc.getSelectedFile();
			savePngImage(_image, saveFile);
		}
	}

	/**
	* Saves a PNG encoded image of the given component.
	*
	* @param _image the image to save
	* @param _file the file to save into
	* @throws IOException if the image cannot be saved
	*/
	public static void savePngImage(Image _image, File _file) throws IOException {
		PngEncoder encoder = new PngEncoder(_image);
		byte[] pngImage = encoder.pngEncode();
		FileOutputStream fos = new FileOutputStream(_file);
		fos.write(pngImage);
		fos.close();
	}
}
