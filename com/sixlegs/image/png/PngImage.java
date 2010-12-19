/* PngImage - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.sixlegs.image.png;
import java.awt.Color;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public final class PngImage implements ImageProducer
{
    static boolean allFatal = false;
    static final int BUFFER_SIZE = 8192;
    private static boolean progressive = true;
    private static Hashtable prototypes = new Hashtable();
    static final String ASCII_ENCODING = "US-ASCII";
    static final String LATIN1_ENCODING = "8859_1";
    static final String UTF8_ENCODING = "UTF8";
    static final long DEFAULT_GAMMA = 45455L;
    private static double DISPLAY_EXPONENT = 2.2;
    private static double USER_EXPONENT = 1.0;
    Data data;
    private Vector errorList;
    public static final int COLOR_TYPE_GRAY = 0;
    public static final int COLOR_TYPE_GRAY_ALPHA = 4;
    public static final int COLOR_TYPE_PALETTE = 3;
    public static final int COLOR_TYPE_RGB = 2;
    public static final int COLOR_TYPE_RGB_ALPHA = 6;
    public static final int INTERLACE_TYPE_NONE = 0;
    public static final int INTERLACE_TYPE_ADAM7 = 1;
    public static final int FILTER_TYPE_BASE = 0;
    public static final int FILTER_TYPE_INTRAPIXEL = 64;
    public static final int COMPRESSION_TYPE_BASE = 0;
    public static final int UNIT_UNKNOWN = 0;
    public static final int UNIT_METER = 1;
    public static final int UNIT_PIXEL = 0;
    public static final int UNIT_MICROMETER = 1;
    public static final int UNIT_RADIAN = 2;
    public static final int SRGB_PERCEPTUAL = 0;
    public static final int SRGB_RELATIVE_COLORIMETRIC = 1;
    public static final int SRGB_SATURATION_PRESERVING = 2;
    public static final int SRGB_ABSOLUTE_COLORIMETRIC = 3;
    
    final class Data
    {
	private final Vector consumers = new Vector();
	private final Hashtable chunks = new Hashtable();
	private int[] pixels;
	private boolean produceFailed;
	private boolean useFlush = false;
	IDATInputStream in_idat;
	Chunk_IHDR header;
	Chunk_PLTE palette;
	final int[] gammaTable = new int[256];
	final Hashtable textChunks = new Hashtable();
	final Hashtable properties = new Hashtable();
	final Hashtable palettes = new Hashtable(1);
	final Vector gifExtensions = new Vector();
	
	private Data() {
	    /* empty */
	}
    }
    
    static {
	registerChunk(new Chunk_IHDR());
	registerChunk(new Chunk_PLTE());
	registerChunk(new Chunk_IDAT());
	registerChunk(new Chunk_IEND());
	registerChunk(new Chunk_tRNS());
    }
    
    public PngImage(String filename) throws IOException {
	data = new Data();
	FileInputStream fs = new FileInputStream(filename);
	init(new BufferedInputStream(fs, 8192));
    }
    
    public PngImage(URL url) throws IOException {
	data = new Data();
	InputStream is = url.openConnection().getInputStream();
	init(new BufferedInputStream(is, 8192));
    }
    
    public PngImage(InputStream is) {
	data = new Data();
	init(is);
    }
    
    public void addConsumer(ImageConsumer ic) {
	if (data != null && !data.consumers.contains(ic))
	    data.consumers.addElement(ic);
    }
    
    public boolean isConsumer(ImageConsumer ic) {
	if (data == null)
	    return false;
	return data.consumers.contains(ic);
    }
    
    public void removeConsumer(ImageConsumer ic) {
	if (data != null)
	    data.consumers.removeElement(ic);
    }
    
    public void startProduction(ImageConsumer ic) {
	if (data == null)
	    throw new RuntimeException("Object has been flushed.");
	addConsumer(ic);
	ImageConsumer[] ics = new ImageConsumer[data.consumers.size()];
	data.consumers.copyInto(ics);
	produceHelper(ics);
    }
    
    public void requestTopDownLeftRightResend(ImageConsumer ic) {
	if (data != null && data.pixels != null)
	    startProduction(ic);
    }
    
    public static void setUserExponent(double exponent) {
	USER_EXPONENT = exponent;
    }
    
    public static void setDisplayExponent(double exponent) {
	DISPLAY_EXPONENT = exponent;
    }
    
    public boolean hasErrors() {
	if (errorList == null)
	    return false;
	if (errorList.size() > 0)
	    return true;
	return false;
    }
    
    public boolean hasFatalError() {
	if (hasErrors() && !(errorList.elementAt(errorList.size() - 1)
			     instanceof PngExceptionSoft))
	    return true;
	return false;
    }
    
    public Enumeration getErrors() {
	return errorList.elements();
    }
    
    public static void setAllErrorsFatal(boolean allFatal) {
	PngImage.allFatal = allFatal;
    }
    
    public static void setProgressiveDisplay(boolean progressive) {
	PngImage.progressive = progressive;
    }
    
    public Color getBackgroundColor() throws IOException {
	return (Color) getProperty("background");
    }
    
    public int getWidth() throws IOException {
	readToData();
	return data.header.width;
    }
    
    public int getHeight() throws IOException {
	readToData();
	return data.header.height;
    }
    
    public int getBitDepth() throws IOException {
	readToData();
	return data.header.depth;
    }
    
    public int getInterlaceType() throws IOException {
	readToData();
	return data.header.interlace;
    }
    
    public int getColorType() throws IOException {
	readToData();
	return data.header.colorType;
    }
    
    public boolean hasAlphaChannel() throws IOException {
	readToData();
	return data.header.alphaUsed;
    }
    
    public boolean isGrayscale() throws IOException {
	readToData();
	return !data.header.colorUsed;
    }
    
    public boolean isIndexedColor() throws IOException {
	readToData();
	return data.header.paletteUsed;
    }
    
    public Object getProperty(String name) throws IOException {
	readToData();
	return data.properties.get(name);
    }
    
    public Enumeration getProperties() throws IOException {
	readToData();
	return data.properties.keys();
    }
    
    public void getEverything() {
	startProduction(new DummyImageConsumer());
    }
    
    public boolean hasChunk(String type) {
	if (data.chunks.get(new Integer(Chunk.stringToType(type))) != null)
	    return true;
	return false;
    }
    
    public static void registerChunk(ChunkHandler handler, String type)
	throws PngException {
	if (type.length() < 4)
	    throw new PngException("Invalid chunk type length.");
	int type_int = Chunk.stringToType(type);
	if (prototypes.containsKey(new Integer(type_int)))
	    throw new PngException("Chunk type already registered.");
	if ((type_int & 0x20000000) == 0)
	    throw new PngException("Chunk must be ancillary.");
	registerChunk(new UserChunk(handler, type_int));
    }
    
    public Enumeration getSuggestedPalettes() throws IOException {
	readToData();
	return data.palettes.keys();
    }
    
    public int[][] getSuggestedPalette(String name) throws IOException {
	readToData();
	return (int[][]) data.palettes.get(name);
    }
    
    public TextChunk getTextChunk(String key) throws IOException {
	readToData();
	return (TextChunk) data.textChunks.get(key);
    }
    
    public Enumeration getTextChunks() throws IOException {
	readToData();
	return data.textChunks.elements();
    }
    
    public Enumeration getGifExtensions() throws IOException {
	readToData();
	return data.gifExtensions.elements();
    }
    
    public void setFlushAfterNextProduction(boolean useFlush) {
	data.useFlush = useFlush;
    }
    
    private void flush() {
	if (data != null) {
	    try {
		data.in_idat.close();
	    } catch (IOException ioexception) {
		/* empty */
	    }
	    data = null;
	}
    }
    
    private void init(InputStream in_raw) {
	data.properties.put("gamma", new Long(45455L));
	data.in_idat = new IDATInputStream(this, in_raw);
    }
    
    private synchronized void readToData() throws IOException {
	try {
	    if (data == null)
		throw new EOFException("Object has been flushed.");
	    data.in_idat.readToData();
	} catch (PngException e) {
	    addError(e);
	    throw e;
	}
    }
    
    private static void registerChunk(Chunk proto) {
	prototypes.put(new Integer(proto.type), proto);
    }
    
    static Chunk getRegisteredChunk(int type) {
	Integer type_obj = new Integer(type);
	if (prototypes.containsKey(type_obj))
	    return ((Chunk) prototypes.get(type_obj)).copy();
	try {
	    String clsName
		= "com.sixlegs.image.png.Chunk_" + Chunk.typeToString(type);
	    registerChunk((Chunk) Class.forName(clsName).newInstance());
	    return getRegisteredChunk(type);
	} catch (Exception e) {
	    return new Chunk(type);
	}
    }
    
    Chunk getChunk(int type) {
	return (Chunk) data.chunks.get(new Integer(type));
    }
    
    void putChunk(int type, Chunk c) {
	data.chunks.put(new Integer(type), c);
    }
    
    void addError(Exception e) {
	if (errorList == null)
	    errorList = new Vector();
	errorList.addElement(e);
    }
    
    void fillGammaTable() {
	try {
	    long file_gamma = ((Long) getProperty("gamma")).longValue();
	    int max = (data.header.paletteUsed ? 255
		       : (1 << data.header.outputDepth) - 1);
	    double decoding_exponent
		= (USER_EXPONENT * 100000.0
		   / ((double) file_gamma * DISPLAY_EXPONENT));
	    for (int i = 0; i <= max; i++) {
		int v = (int) (Math.pow((double) i / (double) max,
					decoding_exponent)
			       * 255.0);
		if (!data.header.colorUsed)
		    data.gammaTable[i] = v | v << 8 | v << 16;
		else
		    data.gammaTable[i] = v;
	    }
	    if (data.palette != null)
		data.palette.calculate();
	} catch (IOException ioexception) {
	    /* empty */
	}
    }
    
    private synchronized void produceHelper(ImageConsumer[] ics) {
	try {
	    readToData();
	    for (int i = 0; i < ics.length; i++) {
		ics[i].setDimensions(data.header.width, data.header.height);
		ics[i].setProperties(data.properties);
		ics[i].setColorModel(data.header.model);
		if (data.produceFailed)
		    ics[i].imageComplete(1);
	    }
	    if (data.produceFailed)
		return;
	    if (data.pixels == null)
		firstProduction(ics);
	    else {
		setHints(ics);
		for (int i = 0; i < ics.length; i++) {
		    ics[i].setPixels(0, 0, data.header.width,
				     data.header.height, data.header.model,
				     data.pixels, 0, data.header.width);
		    ics[i].imageComplete(3);
		}
	    }
	} catch (IOException e) {
	    data.produceFailed = true;
	    addError(e);
	    for (int i = 0; i < ics.length; i++)
		ics[i].imageComplete(1);
	}
	if (data.useFlush)
	    flush();
    }
    
    private void firstProduction(ImageConsumer[] ics) throws IOException {
	UnfilterInputStream in_filter
	    = new UnfilterInputStream(this, data.in_idat);
	InputStream is = new BufferedInputStream(in_filter, 8192);
	PixelInputStream pis = new PixelInputStream(this, is);
	setHints(ics);
	if (data.header.interlace == 0)
	    produceNonInterlaced(ics, pis);
	else
	    produceInterlaced(ics, pis);
	for (int i = 0; i < ics.length; i++)
	    ics[i].imageComplete(3);
    }
    
    private void setHints(ImageConsumer[] ics) {
	for (int i = 0; i < ics.length; i++) {
	    if (progressive && data.pixels == null
		&& data.header.interlace != 0)
		ics[i].setHints(1);
	    else
		ics[i].setHints(30);
	}
    }
    
    private void produceNonInterlaced
	(ImageConsumer[] ics, PixelInputStream pis) throws IOException {
	int w = data.header.width;
	int h = data.header.height;
	if (!data.useFlush)
	    data.pixels = new int[w * h];
	int[] rowbuf = new int[w + 8];
	int pixelsWidth = w;
	int extra = w % pis.fillSize;
	if (extra > 0)
	    pixelsWidth += pis.fillSize - extra;
	for (int y = 0; y < h; y++) {
	    pis.read(rowbuf, 0, pixelsWidth);
	    for (int i = 0; i < ics.length; i++)
		ics[i].setPixels(0, y, w, 1, data.header.model, rowbuf, 0,
				 pixelsWidth);
	    if (!data.useFlush)
		System.arraycopy(rowbuf, 0, data.pixels, w * y, w);
	}
    }
    
    private void produceInterlaced
	(ImageConsumer[] ics, PixelInputStream pis) throws IOException {
	int w = data.header.width;
	int h = data.header.height;
	data.pixels = new int[w * h];
	int[] rowbuf = new int[w + 8];
	int numPasses = data.header.interlacer.numPasses();
	Interlacer lace = data.header.interlacer;
	for (int pass = 0; pass < numPasses; pass++) {
	    int passWidth = lace.getPassWidth(pass);
	    int extra = passWidth % pis.fillSize;
	    if (extra > 0)
		passWidth += pis.fillSize - extra;
	    int blockWidth = progressive ? lace.getBlockWidth(pass) : 1;
	    int blockHeight = progressive ? lace.getBlockHeight(pass) : 1;
	    int rowIncrement = lace.getSpacingY(pass);
	    int colIncrement = lace.getSpacingX(pass);
	    int offIncrement = rowIncrement * w;
	    int colStart = lace.getOffsetX(pass);
	    int row = lace.getOffsetY(pass);
	    int off = row * w;
	    for (/**/; row < h; row += rowIncrement) {
		pis.read(rowbuf, 0, passWidth);
		int col = colStart;
		int x = 0;
		for (/**/; col < w; col += colIncrement) {
		    int bw = Math.min(blockWidth, w - col);
		    int bh = Math.min(blockHeight, h - row);
		    int poff = off + col;
		    int pix = rowbuf[x++];
		    while (bh-- > 0) {
			int poffend = poff + bw;
			while (poff < poffend)
			    data.pixels[poff++] = pix;
			poff += w - bw;
		    }
		}
		off += offIncrement;
	    }
	    if (progressive) {
		for (int i = 0; i < ics.length; i++) {
		    ics[i].setPixels(0, 0, w, h, data.header.model,
				     data.pixels, 0, w);
		    ics[i].imageComplete(2);
		}
	    }
	}
	if (!progressive) {
	    for (int i = 0; i < ics.length; i++)
		ics[i].setPixels(0, 0, w, h, data.header.model, data.pixels, 0,
				 w);
	}
    }
}
