package org.art_core.dev.cinder.input;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;


public class PropertiesInputReader implements InputHandler {
	private String sFilename;
	
	public PropertiesInputReader(String file) {
		IPath ipath = new Path(file);
		IFile ifile = ResourcesPlugin.getWorkspace().getRoot().getFile(ipath);
		
		if (ifile.exists()) {
			sFilename	= file;
			this.read();
		}
	}

	@Override
	public boolean isReadable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void read() {
		PropertiesItem pi;
		String sPath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
		System.out.println(sPath);
		
		Properties prop = new Properties();
		try {
			
			FileInputStream stream = new FileInputStream(sFilename);
			prop.load(stream);
			stream.close();
			pi = new PropertiesItem(prop);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
