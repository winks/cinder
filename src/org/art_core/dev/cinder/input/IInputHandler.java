package org.art_core.dev.cinder.input;

//import org.eclipse.core.resources.IFile;

public interface IInputHandler {
	
	public boolean isReadable();
	
	public void readFile(String ifile);
	
}
