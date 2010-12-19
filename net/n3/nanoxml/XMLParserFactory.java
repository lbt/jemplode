/* XMLParserFactory - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package net.n3.nanoxml;

public class XMLParserFactory
{
    public static final String DEFAULT_CLASS = "net.n3.nanoxml.StdXMLParser";
    public static final String CLASS_KEY = "net.n3.nanoxml.XMLParser";
    
    public static IXMLParser createDefaultXMLParser()
	throws ClassNotFoundException, InstantiationException,
	       IllegalAccessException {
	String className = System.getProperty("net.n3.nanoxml.XMLParser",
					      "net.n3.nanoxml.StdXMLParser");
	return createXMLParser(className, new StdXMLBuilder());
    }
    
    public static IXMLParser createDefaultXMLParser(IXMLBuilder builder)
	throws ClassNotFoundException, InstantiationException,
	       IllegalAccessException {
	String className = System.getProperty("net.n3.nanoxml.XMLParser",
					      "net.n3.nanoxml.StdXMLParser");
	return createXMLParser(className, builder);
    }
    
    public static IXMLParser createXMLParser
	(String className, IXMLBuilder builder)
	throws ClassNotFoundException, InstantiationException,
	       IllegalAccessException {
	Class cls = Class.forName(className);
	IXMLParser parser = (IXMLParser) cls.newInstance();
	parser.setBuilder(builder);
	parser.setValidator(new NonValidator());
	return parser;
    }
}
