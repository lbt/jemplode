/* Logo - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.logoedit;
import java.awt.Image;

public class Logo
{
    public static final String TYPE_EMPEG_NOCUSTOM = "emp ";
    public static final String TYPE_EMPEG_CUSTOM = "empg";
    public static final String TYPE_RIO_NOCUSTOM = "rio ";
    public static final String TYPE_RIO_CUSTOM = "rioc";
    private String myType;
    private Image myCarImage;
    private Image myHomeImage;
    
    public Logo() {
	/* empty */
    }
    
    public Logo(String _type, Image _carImage, Image _homeImage) {
	myType = _type;
	myCarImage = _carImage;
	myHomeImage = _homeImage;
    }
    
    public String getType() {
	return myType;
    }
    
    public void setType(String _type) {
	myType = _type;
    }
    
    public Image getCarImage() {
	return myCarImage;
    }
    
    public Image getHomeImage() {
	return myHomeImage;
    }
}
