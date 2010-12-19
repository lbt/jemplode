/* XMLAttribute - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package net.n3.nanoxml;

class XMLAttribute
{
    private String fullName;
    private String name;
    private String namespace;
    private String value;
    private String type;
    
    XMLAttribute(String fullName, String name, String namespace, String value,
		 String type) {
	this.fullName = fullName;
	this.name = name;
	this.namespace = namespace;
	this.value = value;
	this.type = type;
    }
    
    String getFullName() {
	return fullName;
    }
    
    String getName() {
	return name;
    }
    
    String getNamespace() {
	return namespace;
    }
    
    String getValue() {
	return value;
    }
    
    void setValue(String value) {
	this.value = value;
    }
    
    String getType() {
	return type;
    }
}
