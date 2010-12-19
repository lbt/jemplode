/* XMLElement - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package net.n3.nanoxml;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

public class XMLElement implements IXMLElement, Serializable
{
    static final long serialVersionUID = -2383376380548624920L;
    public static final int NO_LINE = -1;
    private IXMLElement parent;
    private Vector attributes = new Vector();
    private Vector children = new Vector(8);
    private String name;
    private String fullName;
    private String namespace;
    private String content;
    private String systemID;
    private int lineNr;
    
    public XMLElement() {
	this(null, null, null, -1);
    }
    
    public XMLElement(String _fullName) {
	this(_fullName, null, null, -1);
    }
    
    public XMLElement(String _fullName, String _systemID, int _lineNr) {
	this(_fullName, null, _systemID, _lineNr);
    }
    
    public XMLElement(String _fullName, String _namespace) {
	this(_fullName, _namespace, null, -1);
    }
    
    public XMLElement(String _fullName, String _namespace, String _systemID,
		      int _lineNr) {
	fullName = _fullName;
	if (_namespace == null)
	    name = _fullName;
	else {
	    int index = _fullName.indexOf(':');
	    if (index >= 0)
		name = _fullName.substring(index + 1);
	    else
		name = _fullName;
	}
	namespace = _namespace;
	content = null;
	lineNr = _lineNr;
	systemID = _systemID;
	parent = null;
    }
    
    public IXMLElement createPCDataElement() {
	return new XMLElement();
    }
    
    public IXMLElement createElement(String _fullName) {
	return new XMLElement(_fullName);
    }
    
    public IXMLElement createElement(String _fullName, String _systemID,
				     int _lineNr) {
	return new XMLElement(_fullName, _systemID, _lineNr);
    }
    
    public IXMLElement createElement(String _fullName, String _namespace) {
	return new XMLElement(_fullName, _namespace);
    }
    
    public IXMLElement createElement(String _fullName, String _namespace,
				     String _systemID, int _lineNr) {
	return new XMLElement(_fullName, _namespace, _systemID, _lineNr);
    }
    
    protected void finalize() throws Throwable {
	attributes.clear();
	attributes = null;
	children = null;
	fullName = null;
	name = null;
	namespace = null;
	content = null;
	systemID = null;
	parent = null;
	super.finalize();
    }
    
    public IXMLElement getParent() {
	return parent;
    }
    
    public String getFullName() {
	return fullName;
    }
    
    public String getName() {
	return name;
    }
    
    public String getNamespace() {
	return namespace;
    }
    
    public void setName(String _name) {
	name = _name;
	fullName = _name;
	namespace = null;
    }
    
    public void setName(String _fullName, String _namespace) {
	int index = _fullName.indexOf(':');
	if (_namespace == null || index < 0)
	    name = _fullName;
	else
	    name = _fullName.substring(index + 1);
	fullName = _fullName;
	namespace = _namespace;
    }
    
    public void addChild(IXMLElement child) {
	if (child == null)
	    throw new IllegalArgumentException("child must not be null");
	if (child.getName() == null && !children.isEmpty()) {
	    IXMLElement lastChild = (IXMLElement) children.lastElement();
	    if (lastChild.getName() == null) {
		lastChild
		    .setContent(lastChild.getContent() + child.getContent());
		return;
	    }
	}
	((XMLElement) child).parent = this;
	children.addElement(child);
    }
    
    public void insertChild(IXMLElement child, int index) {
	if (child == null)
	    throw new IllegalArgumentException("child must not be null");
	if (child.getName() == null && !children.isEmpty()) {
	    IXMLElement lastChild = (IXMLElement) children.lastElement();
	    if (lastChild.getName() == null) {
		lastChild
		    .setContent(lastChild.getContent() + child.getContent());
		return;
	    }
	}
	((XMLElement) child).parent = this;
	children.insertElementAt(child, index);
    }
    
    public void removeChild(IXMLElement child) {
	if (child == null)
	    throw new IllegalArgumentException("child must not be null");
	children.removeElement(child);
    }
    
    public void removeChildAtIndex(int index) {
	children.removeElementAt(index);
    }
    
    public Enumeration enumerateChildren() {
	return children.elements();
    }
    
    public boolean isLeaf() {
	return children.isEmpty();
    }
    
    public boolean hasChildren() {
	return !children.isEmpty();
    }
    
    public int getChildrenCount() {
	return children.size();
    }
    
    public Vector getChildren() {
	return children;
    }
    
    public IXMLElement getChildAtIndex(int index)
	throws ArrayIndexOutOfBoundsException {
	return (IXMLElement) children.elementAt(index);
    }
    
    public IXMLElement getFirstChildNamed(String name) {
	Enumeration enum = children.elements();
	while (enum.hasMoreElements()) {
	    IXMLElement child = (IXMLElement) enum.nextElement();
	    String childName = child.getFullName();
	    if (childName != null && childName.equals(name))
		return child;
	}
	return null;
    }
    
    public IXMLElement getFirstChildNamed(String name, String namespace) {
	Enumeration enum = children.elements();
	while (enum.hasMoreElements()) {
	    IXMLElement child = (IXMLElement) enum.nextElement();
	    String str = child.getName();
	    boolean found = str != null && str.equals(name);
	    str = child.getNamespace();
	    if (str == null)
		found = found & name == null;
	    else
		found &= str.equals(namespace);
	    if (found)
		return child;
	}
	return null;
    }
    
    public Vector getChildrenNamed(String name) {
	Vector result = new Vector(children.size());
	Enumeration enum = children.elements();
	while (enum.hasMoreElements()) {
	    IXMLElement child = (IXMLElement) enum.nextElement();
	    String childName = child.getFullName();
	    if (childName != null && childName.equals(name))
		result.addElement(child);
	}
	return result;
    }
    
    public Vector getChildrenNamed(String name, String namespace) {
	Vector result = new Vector(children.size());
	Enumeration enum = children.elements();
	while (enum.hasMoreElements()) {
	    IXMLElement child = (IXMLElement) enum.nextElement();
	    String str = child.getName();
	    boolean found = str != null && str.equals(name);
	    str = child.getNamespace();
	    if (str == null)
		found = found & name == null;
	    else
		found &= str.equals(namespace);
	    if (found)
		result.addElement(child);
	}
	return result;
    }
    
    private XMLAttribute findAttribute(String fullName) {
	Enumeration enum = attributes.elements();
	while (enum.hasMoreElements()) {
	    XMLAttribute attr = (XMLAttribute) enum.nextElement();
	    if (attr.getFullName().equals(fullName))
		return attr;
	}
	return null;
    }
    
    private XMLAttribute findAttribute(String name, String namespace) {
	Enumeration enum = attributes.elements();
	while (enum.hasMoreElements()) {
	    XMLAttribute attr = (XMLAttribute) enum.nextElement();
	    boolean found = attr.getName().equals(name);
	    if (namespace == null)
		found = found & attr.getNamespace() == null;
	    else
		found &= namespace.equals(attr.getNamespace());
	    if (found)
		return attr;
	}
	return null;
    }
    
    public int getAttributeCount() {
	return attributes.size();
    }
    
    /**
     * @deprecated
     */
    public String getAttribute(String name) {
	return getAttribute(name, (String) null);
    }
    
    public String getAttribute(String name, String defaultValue) {
	XMLAttribute attr = findAttribute(name);
	if (attr == null)
	    return defaultValue;
	return attr.getValue();
    }
    
    public String getAttribute(String name, String namespace,
			       String defaultValue) {
	XMLAttribute attr = findAttribute(name, namespace);
	if (attr == null)
	    return defaultValue;
	return attr.getValue();
    }
    
    public int getAttribute(String name, int defaultValue) {
	String value = getAttribute(name, Integer.toString(defaultValue));
	return Integer.parseInt(value);
    }
    
    public int getAttribute(String name, String namespace, int defaultValue) {
	String value
	    = getAttribute(name, namespace, Integer.toString(defaultValue));
	return Integer.parseInt(value);
    }
    
    public String getAttributeType(String name) {
	XMLAttribute attr = findAttribute(name);
	if (attr == null)
	    return null;
	return attr.getType();
    }
    
    public String getAttributeNamespace(String name) {
	XMLAttribute attr = findAttribute(name);
	if (attr == null)
	    return null;
	return attr.getNamespace();
    }
    
    public String getAttributeType(String name, String namespace) {
	XMLAttribute attr = findAttribute(name, namespace);
	if (attr == null)
	    return null;
	return attr.getType();
    }
    
    public void setAttribute(String name, String value) {
	XMLAttribute attr = findAttribute(name);
	if (attr == null) {
	    attr = new XMLAttribute(name, name, null, value, "CDATA");
	    attributes.addElement(attr);
	} else
	    attr.setValue(value);
    }
    
    public void setAttribute(String fullName, String namespace, String value) {
	int index = fullName.indexOf(':');
	String name = fullName.substring(index + 1);
	XMLAttribute attr = findAttribute(name, namespace);
	if (attr == null) {
	    attr = new XMLAttribute(fullName, name, namespace, value, "CDATA");
	    attributes.addElement(attr);
	} else
	    attr.setValue(value);
    }
    
    public void removeAttribute(String name) {
	for (int i = 0; i < attributes.size(); i++) {
	    XMLAttribute attr = (XMLAttribute) attributes.elementAt(i);
	    if (attr.getFullName().equals(name)) {
		attributes.removeElementAt(i);
		break;
	    }
	}
    }
    
    public void removeAttribute(String name, String namespace) {
	for (int i = 0; i < attributes.size(); i++) {
	    XMLAttribute attr = (XMLAttribute) attributes.elementAt(i);
	    boolean found = attr.getName().equals(name);
	    if (namespace == null)
		found = found & attr.getNamespace() == null;
	    else
		found &= attr.getNamespace().equals(namespace);
	    if (found) {
		attributes.removeElementAt(i);
		break;
	    }
	}
    }
    
    public Enumeration enumerateAttributeNames() {
	Vector result = new Vector();
	Enumeration enum = attributes.elements();
	while (enum.hasMoreElements()) {
	    XMLAttribute attr = (XMLAttribute) enum.nextElement();
	    result.addElement(attr.getFullName());
	}
	return result.elements();
    }
    
    public boolean hasAttribute(String name) {
	if (findAttribute(name) != null)
	    return true;
	return false;
    }
    
    public boolean hasAttribute(String name, String namespace) {
	if (findAttribute(name, namespace) != null)
	    return true;
	return false;
    }
    
    public Properties getAttributes() {
	Properties result = new Properties();
	Enumeration enum = attributes.elements();
	while (enum.hasMoreElements()) {
	    XMLAttribute attr = (XMLAttribute) enum.nextElement();
	    result.put(attr.getFullName(), attr.getValue());
	}
	return result;
    }
    
    public Properties getAttributesInNamespace(String namespace) {
	Properties result = new Properties();
	Enumeration enum = attributes.elements();
	while (enum.hasMoreElements()) {
	    XMLAttribute attr = (XMLAttribute) enum.nextElement();
	    if (namespace == null) {
		if (attr.getNamespace() == null)
		    result.put(attr.getName(), attr.getValue());
	    } else if (namespace.equals(attr.getNamespace()))
		result.put(attr.getName(), attr.getValue());
	}
	return result;
    }
    
    public String getSystemID() {
	return systemID;
    }
    
    public int getLineNr() {
	return lineNr;
    }
    
    public String getContent() {
	return content;
    }
    
    public void setContent(String content) {
	this.content = content;
    }
    
    public boolean equals(Object rawElement) {
	try {
	    return equalsXMLElement((IXMLElement) rawElement);
	} catch (ClassCastException e) {
	    return false;
	}
    }
    
    public boolean equalsXMLElement(IXMLElement elt) {
	if (!name.equals(elt.getName()))
	    return false;
	if (attributes.size() != elt.getAttributeCount())
	    return false;
	Enumeration enum = attributes.elements();
	while (enum.hasMoreElements()) {
	    XMLAttribute attr = (XMLAttribute) enum.nextElement();
	    if (!elt.hasAttribute(attr.getName(), attr.getNamespace()))
		return false;
	    String value
		= elt.getAttribute(attr.getName(), attr.getNamespace(),
				   (String) null);
	    if (!attr.getValue().equals(value))
		return false;
	    String type
		= elt.getAttributeType(attr.getName(), attr.getNamespace());
	    if (!attr.getType().equals(type))
		return false;
	}
	if (children.size() != elt.getChildrenCount())
	    return false;
	for (int i = 0; i < children.size(); i++) {
	    IXMLElement child1 = getChildAtIndex(i);
	    IXMLElement child2 = elt.getChildAtIndex(i);
	    if (!child1.equalsXMLElement(child2))
		return false;
	}
	return true;
    }
}
