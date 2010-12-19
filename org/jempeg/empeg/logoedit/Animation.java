/* Animation - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.logoedit;
import java.awt.Component;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import com.fmsoftware.AnimatedGifEncoder;
import com.fmsoftware.GifDecoder;
import com.inzyme.io.MemorySeekableInputStream;
import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.util.Debug;

public class Animation
{
    private Component myComponent;
    private Vector myFrames;
    private Vector mySequence;
    private int myWidth;
    private int myHeight;
    
    public Animation(Component _component, int _width, int _height) {
	myComponent = _component;
	myWidth = _width;
	myHeight = _height;
	myFrames = new Vector();
	mySequence = new Vector();
    }
    
    public int getWidth() {
	return myWidth;
    }
    
    public int getHeight() {
	return myHeight;
    }
    
    public Vector getFrames() {
	return myFrames;
    }
    
    public Vector getSequence() {
	return mySequence;
    }
    
    public void clear() {
	mySequence.removeAllElements();
	myFrames.removeAllElements();
    }
    
    public void save(LittleEndianOutputStream _os) throws IOException {
	int maxAni = getSequenceCount() + 1;
	for (int i = 0; i < maxAni - 1; i++) {
	    int frame = getSequenceIndex(i);
	    int offset = frame * 1024 + maxAni * 4;
	    _os.writeUnsigned32((long) offset);
	}
	_os.writeUnsigned32(0L);
	int maxFrame = getFrameCount();
	for (int frame = 0; frame < maxFrame; frame++) {
	    Image frameImage = getFrameAt(frame);
	    byte[] frameBytes
		= LogoFormatUtils.toLogoFormat(frameImage, myWidth, myHeight,
					       (LogoFormatUtils
						.DEFAULT_CUTOFFS));
	    MemorySeekableInputStream is
		= new MemorySeekableInputStream(frameBytes);
	    for (int i = 0; i < 1024; i++) {
		int p1 = is.read();
		int p2 = is.read();
		int animValue = ((p1 & 0x30) << 2 | (p1 & 0x3) << 4
				 | (p2 & 0x30) >> 2 | p2 & 0x3);
		_os.writeUnsigned8(animValue);
	    }
	    is.close();
	}
    }
    
    public void load(LittleEndianInputStream _is, long _totalLength)
	throws IOException {
	myFrames.removeAllElements();
	mySequence.removeAllElements();
	boolean done = false;
	do {
	    int offset = (int) _is.readUnsigned32();
	    if (offset == 0)
		done = true;
	    else {
		Integer offsetInteger = new Integer(offset);
		mySequence.addElement(offsetInteger);
	    }
	} while (!done);
	int maxFrame;
	if (_totalLength >= 0L)
	    maxFrame = (int) (_totalLength
			      - (long) ((mySequence.size() + 1) * 4)) / 1024;
	else
	    maxFrame = -1;
	done = false;
	int[] animBytes = new int[1024];
	int frame = 0;
	for (;;) {
	    if (maxFrame == -1) {
		if (done)
		    break;
	    } else if (frame >= maxFrame)
		break;
	    int byteNum = 0;
	    for (byteNum = 0; !done && byteNum < 1024; byteNum++) {
		animBytes[byteNum] = _is.read();
		if (animBytes[byteNum] == -1)
		    done = true;
	    }
	    if (!done) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
		for (int i = 0; i < 1024; i++) {
		    int b = animBytes[i];
		    int p1 = (b & 0xc0) >> 2 | (b & 0x30) >> 4;
		    int p2 = (b & 0xc) << 2 | b & 0x3;
		    baos.write(p1);
		    baos.write(p2);
		}
		baos.close();
		byte[] frameBytes = baos.toByteArray();
		Image frameImage
		    = LogoFormatUtils.fromLogoFormat(myComponent, frameBytes,
						     myWidth, myHeight,
						     (LogoFormatUtils
						      .DEFAULT_GRAY_VALUES));
		myFrames.addElement(frameImage);
	    }
	    frame++;
	}
	if (maxFrame == -1)
	    maxFrame = myFrames.size();
	int maxAni = mySequence.size() + 1;
	for (int i = 0; i < maxAni - 1; i++) {
	    int offset = ((Integer) mySequence.elementAt(i)).intValue();
	    int frame_0_ = (offset - maxAni * 4) / 1024;
	    Image frameImage = (Image) myFrames.elementAt(frame_0_);
	    mySequence.setElementAt(frameImage, i);
	}
    }
    
    public int[] getPixels(Image _img) {
	int[] tempPixels
	    = (new int
	       [_img.getWidth(myComponent) * _img.getHeight(myComponent)]);
	PixelGrabber pg
	    = new PixelGrabber(_img, 0, 0, _img.getWidth(myComponent),
			       _img.getHeight(myComponent), tempPixels, 0,
			       _img.getWidth(myComponent));
	try {
	    pg.grabPixels();
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
	return tempPixels;
    }
    
    public void loadAnimatedGif(InputStream _is) throws IOException {
	clear();
	GifDecoder decoder = new GifDecoder();
	decoder.read(_is);
	int gifFrameCount = decoder.getFrameCount();
	for (int i = 0; i < gifFrameCount; i++) {
	    Image gifFrame = decoder.getFrame(i);
	    int[] gifPixels = getPixels(gifFrame);
	    boolean foundMatch = false;
	    int animFrameCount = getFrameCount();
	    for (int j = 0; !foundMatch && j < animFrameCount; j++) {
		Image animFrame = getFrameAt(j);
		int[] animPixels = getPixels(animFrame);
		boolean pixelsMatch = gifPixels.length == animPixels.length;
		for (int k = 0; pixelsMatch && k < animPixels.length; k++) {
		    if (animPixels[k] != gifPixels[k])
			pixelsMatch = false;
		}
		if (pixelsMatch) {
		    gifFrame = animFrame;
		    Debug.println(4, "Sequence " + i + " matches frame " + j);
		    foundMatch = true;
		}
	    }
	    if (!foundMatch)
		addFrame(gifFrame);
	    addSequence(gifFrame);
	}
    }
    
    public void saveAnimatedGif(OutputStream _os) throws IOException {
	AnimatedGifEncoder e = new AnimatedGifEncoder();
	e.start(_os);
	e.setRepeat(0);
	e.setDelay(83);
	int width = getWidth();
	int height = getHeight();
	int size = getSequenceCount();
	for (int i = 0; i < size; i++) {
	    Image img = getSequenceAt(i);
	    BufferedImage bi = new BufferedImage(width, height, 5);
	    bi.getGraphics().drawImage(img, 0, 0, null);
	    e.addFrame(bi);
	}
	e.finish();
    }
    
    public int getSequenceIndex(int _sequenceIndex) {
	int index = -1;
	if (_sequenceIndex != -1) {
	    Image image = (Image) mySequence.elementAt(_sequenceIndex);
	    if (image != null)
		index = myFrames.indexOf(image);
	}
	return index;
    }
    
    public int getSequenceCount() {
	return mySequence.size();
    }
    
    public Image getSequenceAt(int _index) {
	Image img = (Image) mySequence.elementAt(_index);
	return img;
    }
    
    public void removeSequenceAt(int _index) {
	mySequence.removeElementAt(_index);
    }
    
    public void addSequence(Image _img) {
	mySequence.addElement(_img);
    }
    
    public void insertSequenceAt(Image _img, int _index) {
	mySequence.insertElementAt(_img, _index);
    }
    
    public int getFrameCount() {
	return myFrames.size();
    }
    
    public void addFrame(Image _image) {
	myFrames.addElement(_image);
    }
    
    public void addFrameAt(int _index) {
	myFrames.insertElementAt("", _index);
    }
    
    public void removeFrameAt(int _index) {
	myFrames.removeElementAt(_index);
    }
    
    public void insertFrameAt(Image _img, int _index) {
	myFrames.insertElementAt(_img, _index);
    }
    
    public void setFrameAt(Image _image, int _index) {
	myFrames.setElementAt(_image, _index);
    }
    
    public int getFrameIndex(Object _image) {
	int index = myFrames.indexOf(_image);
	return index;
    }
    
    public Image getFrameAt(int _index) {
	Object obj = myFrames.elementAt(_index);
	Image img;
	if (obj instanceof Image)
	    img = (Image) obj;
	else {
	    img = ImageUtils.createImage(myComponent, myWidth, myHeight);
	    myFrames.setElementAt(img, _index);
	}
	return img;
    }
}
