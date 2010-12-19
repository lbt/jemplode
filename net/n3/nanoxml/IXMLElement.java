/* IXMLElement - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package net.n3.nanoxml;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

public interface IXMLElement
{
    public static final int NO_LINE = -1;
    
    public IXMLElement createPCDataElement();
    
    public IXMLElement createElement(String string);
    
    public IXMLElement createElement(String string, String string_0_, int i);
    
    public IXMLElement createElement(String string, String string_1_);
    
    public IXMLElement createElement(String string, String string_2_,
				     String string_3_, int i);
    
    public IXMLElement getParent();
    
    public String getFullName();
    
    public String getName();
    
    public String getNamespace();
    
    public void setName(String string);
    
    public void setName(String string, String string_4_);
    
    public void addChild(IXMLElement ixmlelement_5_);
    
    public void removeChild(IXMLElement ixmlelement_6_);
    
    public void removeChildAtIndex(int i);
    
    public Enumeration enumerateChildren();
    
    public boolean isLeaf();
    
    public boolean hasChildren();
    
    public int getChildrenCount();
    
    public Vector getChildren();
    
    public IXMLElement getChildAtIndex(int i)
	throws ArrayIndexOutOfBoundsException;
    
    public IXMLElement getFirstChildNamed(String string);
    
    public IXMLElement getFirstChildNamed(String string, String string_7_);
    
    public Vector getChildrenNamed(String string);
    
    public Vector getChildrenNamed(String string, String string_8_);
    
    public int getAttributeCount();
    
    /**
     * @deprecated
     */
    public String getAttribute(String string);
    
    public String getAttribute(String string, String string_9_);
    
    public String getAttribute(String string, String string_10_,
			       String string_11_);
    
    public int getAttribute(String string, int i);
    
    public int getAttribute(String string, String string_12_, int i);
    
    public String getAttributeType(String string);
    
    public String getAttributeNamespace(String string);
    
    public String getAttributeType(String string, String string_13_);
    
    public void setAttribute(String string, String string_14_);
    
    public void setAttribute(String string, String string_15_,
			     String string_16_);
    
    public void removeAttribute(String string);
    
    public void removeAttribute(String string, String string_17_);
    
    public Enumeration enumerateAttributeNames();
    
    public boolean hasAttribute(String string);
    
    public boolean hasAttribute(String string, String string_18_);
    
    public Properties getAttributes();
    
    public Properties getAttributesInNamespace(String string);
    
    public String getSystemID();
    
    public int getLineNr();
    
    public String getContent();
    
    public void setContent(String string);
    
    public boolean equals(Object object);
    
    public boolean equalsXMLElement(IXMLElement ixmlelement_19_);
}
