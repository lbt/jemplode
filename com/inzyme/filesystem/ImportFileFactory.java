/* ImportFileFactory - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.filesystem;
import java.io.IOException;

public class ImportFileFactory
{
    private static IImportFileFactory[] myImportFileFactories;
    
    static {
	addImportFileFactory(new DefaultImportFileFactory());
    }
    
    public static void addImportFileFactory
	(IImportFileFactory _importFileFactory) {
	if (myImportFileFactories == null)
	    myImportFileFactories
		= new IImportFileFactory[] { _importFileFactory };
	else {
	    IImportFileFactory[] importFileFactories
		= new IImportFileFactory[myImportFileFactories.length + 1];
	    System.arraycopy(myImportFileFactories, 0, importFileFactories, 0,
			     myImportFileFactories.length);
	    importFileFactories[importFileFactories.length - 1]
		= _importFileFactory;
	    myImportFileFactories = importFileFactories;
	}
    }
    
    public static IImportFile[] createImportFiles(Object[] _objs)
	throws IOException {
	IImportFile[] importFiles = new IImportFile[_objs.length];
	for (int i = 0; i < _objs.length; i++)
	    importFiles[i] = createImportFile(_objs[i]);
	return importFiles;
    }
    
    public static IImportFile createImportFile(Object _obj)
	throws IOException {
	return createImportFile(null, _obj);
    }
    
    public static IImportFile createImportFile(String _name, Object _obj)
	throws IOException {
	IImportFile importFile = null;
	int size = myImportFileFactories.length;
	for (int i = size - 1; importFile == null && i >= 0; i--)
	    importFile
		= myImportFileFactories[i].createImportFile(_name, _obj);
	if (importFile == null)
	    throw new IOException("Unknown ImportFile mapping for object: "
				  + _obj);
	return importFile;
    }
}
